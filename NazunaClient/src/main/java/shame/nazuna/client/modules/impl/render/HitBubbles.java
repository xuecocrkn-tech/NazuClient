package shame.nazuna.client.modules.impl.render;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.concurrent.CopyOnWriteArrayList;
 import net.minecraft.class_10142;
 import net.minecraft.class_1297;
 import net.minecraft.class_1309;
 import net.minecraft.class_243;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_2960;
 import net.minecraft.class_4587;
 import net.minecraft.class_7833;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventAttackEntity;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.client.modules.Module;
 
 public class HitBubbles
   extends Module
 {
   public static HitBubbles INSTANCE = new HitBubbles();
   
   private static final long LIFE_MS = 1600L;
   
   private final CopyOnWriteArrayList<HitBubble> bubbles = new CopyOnWriteArrayList<>();
   private final class_2960 bubbleTexture = class_2960.method_60655("astra", "textures/hitbubble/bubble.png");
   
   public HitBubbles() {
     super("HitBubbles", "Круг при ударе игрока", Module.ModuleCategory.RENDER);
   }
 
   
   public void onDisable() {
     this.bubbles.clear();
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     long now = System.currentTimeMillis();
     this.bubbles.removeIf(b -> (now - b.spawnTime() >= 1600L));
   }
   @EventLink
   public void onAttack(EventAttackEntity event) {
     class_1309 living;
     if (event == null || event.getTarget() == null)
       return;  class_1297 class_1297 = event.getTarget(); if (class_1297 instanceof class_1309) { living = (class_1309)class_1297; } else { return; }
      if (event.getPlayer() == null)
       return; 
     class_243 sideDir = getHitSideDirection(living, event.getPlayer().method_19538());
     class_243 pos = getHitPosition(living, sideDir);
     float sideYaw = (float)Math.toDegrees(Math.atan2(sideDir.field_1352, sideDir.field_1350));
     this.bubbles.add(new HitBubble(pos, System.currentTimeMillis(), (float)(Math.random() * 360.0D), sideYaw));
   }
   
   @EventLink
   public void onWorldRender(Event3DRender event) {
     if (this.bubbles.isEmpty() || mc.field_1724 == null)
       return; 
     class_4587 stack = event.getMatrices();
     class_243 cameraPos = event.getCamera().method_19326();
     
     RenderSystem.enableBlend();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.setShader(class_10142.field_53880);
     RenderSystem.setShaderTexture(0, this.bubbleTexture);
     
     long now = System.currentTimeMillis();
     for (HitBubble bubble : this.bubbles) {
       renderSingleBubble(stack, cameraPos, bubble, now);
     }
     
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(true);
     RenderSystem.enableCull();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   private void renderSingleBubble(class_4587 stack, class_243 cameraPos, HitBubble bubble, long now) {
     float progress = (float)(now - bubble.spawnTime()) / 1600.0F;
     if (progress >= 1.0F)
       return; 
     float inPhase = Math.max(0.0F, Math.min(1.0F, progress / 0.22F));
     float outPhase = Math.max(0.0F, Math.min(1.0F, (progress - 0.225F) / 0.4F));
     float scaleIn = inPhase * inPhase * (3.0F - 2.0F * inPhase);
     float scaleOut = 1.0F - outPhase * outPhase;
     float scale = 0.02F + 1.55F * scaleIn * scaleOut;
     float alpha = 1.0F - outPhase * outPhase * outPhase;
     float rotation = (float)(now - bubble.spawnTime()) / 1.5F + bubble.spinSeed();
     
     class_243 rel = bubble.pos().method_1020(cameraPos);
     int color = ColorUtils.multAlpha(ColorUtils.getThemeColor(), alpha);
     
     stack.method_22903();
     stack.method_22904(rel.field_1352, rel.field_1351, rel.field_1350);
     stack.method_22907(class_7833.field_40716.rotationDegrees(bubble.sideYaw()));
     stack.method_22907(class_7833.field_40714.rotationDegrees(-210.0F));
     stack.method_22907(class_7833.field_40718.rotationDegrees(rotation));
     drawTexturedQuad(stack, -scale * 0.5F, -scale * 0.5F, scale, scale, color);
     stack.method_22909();
   }
   
   private void drawTexturedQuad(class_4587 stack, float x, float y, float width, float height, int color) {
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     if (a <= 0)
       return; 
     Matrix4f mat = stack.method_23760().method_23761();
     class_287 buffer = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1575);
     buffer.method_22918(mat, x, y, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, a);
     buffer.method_22918(mat, x, y + height, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(mat, x + width, y + height, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(mat, x + width, y, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, a);
     class_286.method_43433(buffer.method_60800());
   }
   
   private class_243 getHitSideDirection(class_1309 target, class_243 attackerPos) {
     class_243 dir = attackerPos.method_1020(target.method_19538());
     dir = new class_243(dir.field_1352, 0.0D, dir.field_1350);
     if (dir.method_1027() < 1.0E-4D) {
       class_243 fallback = target.method_5720();
       dir = new class_243(fallback.field_1352, 0.0D, fallback.field_1350);
     } 
     if (dir.method_1027() < 1.0E-4D) dir = new class_243(0.0D, 0.0D, 1.0D); 
     return dir.method_1029();
   }
   
   private class_243 getHitPosition(class_1309 target, class_243 sideDir) {
     class_243 head = new class_243(target.method_23317(), target.method_23318() + target.method_17682() + 0.18D, target.method_23321());
     return head.method_1019(sideDir.method_1021(0.1D));
   }
   private static final class HitBubble extends Record { private final class_243 pos; private final long spawnTime; private final float spinSeed; private final float sideYaw;
     private HitBubble(class_243 pos, long spawnTime, float spinSeed, float sideYaw) { this.pos = pos; this.spawnTime = spawnTime; this.spinSeed = spinSeed; this.sideYaw = sideYaw; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #146	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble; } public class_243 pos() { return this.pos; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #146	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #146	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/render/HitBubbles$HitBubble;
       //   0	8	1	o	Ljava/lang/Object; } public long spawnTime() { return this.spawnTime; } public float spinSeed() { return this.spinSeed; } public float sideYaw() { return this.sideYaw; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\HitBubbles.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */