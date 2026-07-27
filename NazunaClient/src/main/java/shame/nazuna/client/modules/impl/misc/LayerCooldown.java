package shame.nazuna.client.modules.impl.misc;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import net.minecraft.ItemStack;
 import net.minecraft.ItemConvertible;
 import net.minecraft.Blocks;
 import net.minecraft.Block;
 import net.minecraft.BlockPos;
 import net.minecraft.Position;
 import net.minecraft.Vec3i;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.BlockState;
 import net.minecraft.PlaySoundS2CPacket;
 import net.minecraft.DrawContext;
 import net.minecraft.MatrixStack;
 import net.minecraft.Registries;
 import org.joml.Matrix4f;
 import org.joml.Matrix4fc;
 import org.joml.Quaternionf;
 import org.joml.Quaternionfc;
 import org.joml.Vector3f;
 import org.joml.Vector4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.client.modules.Module;
 
 public class LayerCooldown extends Module {
   public static LayerCooldown INSTANCE = new LayerCooldown();
   
   private static final long DELAYED_SCAN_MS = 250L;
   private static final int SEARCH_RADIUS = 4;
   private static final int SEARCH_HEIGHT = 4;
   private static final int MAX_TIMERS = 100;
   private static final float TIMER_SECONDS = 19.5F;
   private static final float MAX_DISTANCE = 96.0F;
   private static final double TIMER_Y_OFFSET = 0.6D;
   private static final ItemStack LAYER_ICON = new ItemStack((ItemConvertible)Items.field_8551);
   
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private Vec3d lastCameraPos = Vec3d.field_1353;
   
   private boolean hasProjection;
   private final List<LayerTimer> timers = new ArrayList<>();
   private final List<PendingScan> pendingScans = new ArrayList<>();
   
   public LayerCooldown() {
     super("LayerCooldown", "Показывает таймер возле поставленного пласта", Module.ModuleCategory.MISC);
   }
 
   
   public void onDisable() {
     this.timers.clear();
     this.pendingScans.clear();
     this.hasProjection = false;
     super.onDisable();
   }
   @EventLink
   public void onPacket(EventPacket event) {
     PlaySoundS2CPacket packet;
     if (event.getType() != EventPacket.Type.RECEIVE || mc.field_1687 == null || mc.field_1724 == null)
       return;  Packet Packet = event.getPacket(); if (Packet instanceof PlaySoundS2CPacket) { packet = (PlaySoundS2CPacket)Packet; }
     else { return; }
      String sound = getSoundPath(packet);
     if (sound == null)
       return; 
     Vec3d soundPos = new Vec3d(packet.method_11890(), packet.method_11889(), packet.method_11893());
     BlockPos blockPos = BlockPos.method_49638((Position)soundPos);
     
     if ("block.piston.extend".equals(sound)) {
       addTimer(blockPos, soundPos);
       
       return;
     } 
     if (isDelayedTrapSound(sound)) {
       this.pendingScans.add(new PendingScan(blockPos, System.currentTimeMillis() + 250L));
     }
   }
   
   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
     if (mc.field_1687 == null || mc.field_1724 == null)
       return; 
     this.hasProjection = true;
     this.lastProjectionMatrix.set((Matrix4fc)event.getProjectionMatrix());
     this.lastCameraRotation.set((Quaternionfc)event.getCamera().method_23767());
     this.lastCameraPos = event.getCamera().method_19326();
     
     processPendingScans();
   }
   
   @EventLink(priority = 100)
   public void onRender2D(EventRender.Default event) {
     if (!this.hasProjection || mc.field_1687 == null || mc.field_1724 == null)
       return; 
     long now = System.currentTimeMillis();
     this.timers.removeIf(timer -> (timer.endTime <= now));
     while (this.timers.size() > 100) {
       this.timers.remove(0);
     }
     
     if (this.timers.isEmpty())
       return; 
     MatrixStack matrices = event.getContext().method_51448();
     Font font = Fonts.getFont("sf_regular", 13);
     if (font == null)
       return; 
     float maxDistSq = 9216.0F;
     for (int i = 0; i < this.timers.size(); i++) {
       LayerTimer timer = this.timers.get(i);
       if (mc.field_1724.method_5707(timer.pos) <= maxDistSq) {
         
         Vec3d screen = worldToScreen(timer.pos);
         if (screen != null) {
           
           float seconds = Math.max(0.0F, (float)(timer.endTime - now) / 1000.0F);
           drawTimer(event.getContext(), matrices, font, (float)screen.field_1352, (float)screen.field_1351, seconds);
         } 
       } 
     } 
   } private void processPendingScans() {
     if (this.pendingScans.isEmpty() || mc.field_1687 == null)
       return; 
     long now = System.currentTimeMillis();
     Iterator<PendingScan> iterator = this.pendingScans.iterator();
     while (iterator.hasNext()) {
       PendingScan scan = iterator.next();
       if (scan.runAt > now)
         continue; 
       BlockPos found = findLayerLikeBlock(scan.center);
 
       
       Vec3d pos = (found == null) ? Vec3d.method_24953((Vec3i)scan.center) : new Vec3d(found.method_10263() + 0.5D, found.method_10264() + 0.65D, found.method_10260() + 0.5D);
       addTimer((found == null) ? scan.center : found, pos);
       iterator.remove();
     } 
   }
   
   private BlockPos findLayerLikeBlock(BlockPos center) {
     BlockPos best = null;
     double bestDistance = Double.MAX_VALUE;
     
     for (int x = -4; x <= 4; x++) {
       for (int y = -4; y <= 4; y++) {
         for (int z = -4; z <= 4; z++) {
           BlockPos pos = center.method_10069(x, y, z);
           BlockState state = mc.field_1687.method_8320(pos);
           if (isLayerLikeBlock(state)) {
             
             double distance = pos.method_10262((Vec3i)center);
             if (distance < bestDistance) {
               bestDistance = distance;
               best = pos;
             } 
           } 
         } 
       } 
     } 
     return best;
   }
   
   private boolean isLayerLikeBlock(BlockState state) {
     if (state == null || state.method_26215()) return false;
     
     Block block = state.method_26204();
     return (block == Blocks.field_10560 || block == Blocks.field_10615 || block == Blocks.field_10008 || block == Blocks.field_10342 || block == Blocks.field_10535 || block == Blocks.field_10105 || block == Blocks.field_10414);
   }
 
 
 
 
 
 
   
   private void addTimer(BlockPos blockPos, Vec3d renderPos) {
     long endTime = System.currentTimeMillis() + 19500L;
     
     for (int i = 0; i < this.timers.size(); i++) {
       LayerTimer timer = this.timers.get(i);
       if (timer.blockPos.method_10262((Vec3i)blockPos) <= 2.25D) {
         this.timers.set(i, new LayerTimer(blockPos, renderPos.method_1031(0.0D, 0.6D, 0.0D), endTime));
         
         return;
       } 
     } 
     this.timers.add(new LayerTimer(blockPos, renderPos.method_1031(0.0D, 0.6D, 0.0D), endTime));
   }
   
   private boolean isDelayedTrapSound(String sound) {
     return ("block.anvil.place".equals(sound) || "entity.zombie_horse.death"
       .equals(sound) || "entity.ender_dragon.growl"
       .equals(sound));
   }
   
   private String getSoundPath(PlaySoundS2CPacket packet) {
     try {
       return Registries.field_41172.method_10221(packet.method_11894().comp_349()).method_12832();
     } catch (Exception ignored) {
       return null;
     } 
   }
   
   private void drawTimer(DrawContext context, MatrixStack matrices, Font font, float x, float y, float seconds) {
     String text = formatOneDecimal(seconds) + "с";
     float textWidth = font.getStringWidth(text);
     float iconSize = 10.0F;
     float iconScale = 0.62F;
     float gap = 3.0F;
     float boxWidth = iconSize + gap + textWidth + 8.0F;
     float boxHeight = 12.5F;
     float boxX = x - boxWidth * 0.5F;
     float boxY = y - boxHeight * 0.5F;
     int themeColor = ColorUtils.getThemeColor();
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderUtils.drawDefaultHudThemedPanel(matrices, boxX, boxY, boxWidth, boxHeight, 2.0F, 3.0F, themeColor);
     drawItemIcon(context, matrices, boxX + 4.0F, boxY + 1.25F, iconScale);
     font.drawString(matrices, text, boxX + 4.0F + iconSize + gap, boxY + 4.55F, -1);
     RenderSystem.disableBlend();
   }
   
   private String formatOneDecimal(float value) {
     int scaled = Math.round(value * 10.0F);
     return "" + scaled / 10 + "." + scaled / 10;
   }
   
   private void drawItemIcon(DrawContext context, MatrixStack matrices, float x, float y, float scale) {
     if (context == null)
       return; 
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     matrices.method_22903();
     matrices.method_46416(x, y, 0.0F);
     matrices.method_22905(scale, scale, 1.0F);
     context.method_51427(LAYER_ICON, 0, 0);
     matrices.method_22909();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
   }
   
   private Vec3d worldToScreen(Vec3d worldPos) {
     if (mc == null || mc.method_22683() == null) return null;
     
     Vector3f relative = new Vector3f((float)(worldPos.field_1352 - this.lastCameraPos.field_1352), (float)(worldPos.field_1351 - this.lastCameraPos.field_1351), (float)(worldPos.field_1350 - this.lastCameraPos.field_1350));
 
 
 
 
     
     Quaternionf invCameraRot = (new Quaternionf((Quaternionfc)this.lastCameraRotation)).conjugate();
     relative.rotate((Quaternionfc)invCameraRot);
     
     Vector4f clip = new Vector4f(relative.x, relative.y, relative.z, 1.0F);
     this.lastProjectionMatrix.transform(clip);
     
     float w = clip.w;
     if (w <= 1.0E-5F) return null;
     
     float ndcX = clip.x / w;
     float ndcY = clip.y / w;
     float ndcZ = clip.z / w;
     
     float screenX = (ndcX * 0.5F + 0.5F) * mc.method_22683().method_4486();
     float screenY = (1.0F - ndcY * 0.5F + 0.5F) * mc.method_22683().method_4502();
     
     if (Float.isNaN(screenX) || Float.isNaN(screenY) || Float.isInfinite(screenX) || Float.isInfinite(screenY)) {
       return null;
     }
     if (screenX < -400.0F || screenY < -400.0F || screenX > (mc
       .method_22683().method_4486() + 400) || screenY > (mc
       .method_22683().method_4502() + 400)) {
       return null;
     }
     
     return new Vec3d(screenX, screenY, ndcZ);
   }
   private static final class LayerTimer extends Record { private final BlockPos blockPos; private final Vec3d pos; private final long endTime;
     private LayerTimer(BlockPos blockPos, Vec3d pos, long endTime) { this.blockPos = blockPos; this.pos = pos; this.endTime = endTime; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #289	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer; } public BlockPos blockPos() { return this.blockPos; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #289	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #289	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$LayerTimer;
       //   0	8	1	o	Ljava/lang/Object; } public Vec3d pos() { return this.pos; } public long endTime() { return this.endTime; }
      }
   private static final class PendingScan extends Record { private final BlockPos center; private final long runAt;
     private PendingScan(BlockPos center, long runAt) { this.center = center; this.runAt = runAt; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/LayerCooldown$PendingScan;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #292	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$PendingScan; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/LayerCooldown$PendingScan;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #292	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$PendingScan; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/LayerCooldown$PendingScan;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #292	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/misc/LayerCooldown$PendingScan;
       //   0	8	1	o	Ljava/lang/Object; } public BlockPos center() { return this.center; } public long runAt() { return this.runAt; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\LayerCooldown.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */