package shame.nazuna.client.modules.impl.movement;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.EntityPose;
 import net.minecraft.MatrixStack;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventMove;
 import shame.nazuna.api.events.implement.EventMoveInput;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.client.modules.Module;
 
 public class FreeCam extends Module {
   public static FreeCam INSTANCE = new FreeCam();
   
   public Vec3d pos;
   
   public FreeCam() {
     super("FreeCam", "Обзор местности за фейк игрока", Module.ModuleCategory.MOVEMENT);
   }
 
   
   public void onEnable() {
     super.onEnable();
     if (mc.field_1724 != null) {
       this.pos = mc.field_1724.method_19538();
     }
   }
 
   
   public void onDisable() {
     super.onDisable();
     if (mc.field_1724 != null && this.pos != null) {
       mc.field_1724.method_33574(this.pos);
     }
   }
   
   @EventLink
   public void onEvent(EventPacket event) {
     Packet<?> packet = event.getPacket();
     
     if (packet instanceof net.minecraft.PlayerMoveC2SPacket) {
       event.cancel();
     } else if (packet instanceof net.minecraft.PlayerRespawnS2CPacket || packet instanceof net.minecraft.GameJoinS2CPacket) {
       toggle();
     } 
   }
   
   @EventLink
   public void onEvent(Event3DRender event) {
     if (this.pos == null || mc.field_1724 == null)
       return; 
     float width = mc.field_1724.method_17681() / 2.0F;
     float height = mc.field_1724.method_17682();
     
     Box box = new Box(this.pos.field_1352 - width, this.pos.field_1351, this.pos.field_1350 - width, this.pos.field_1352 + width, this.pos.field_1351 + height, this.pos.field_1350 + width);
 
 
 
 
 
 
 
     
     drawHitbox(event.getMatrices(), box, event.getCamera().method_19326());
   }
   
   private void drawHitbox(MatrixStack matrices, Box box, Vec3d camera) {
     double x1 = box.field_1323 - camera.field_1352;
     double y1 = box.field_1322 - camera.field_1351;
     double z1 = box.field_1321 - camera.field_1350;
     double x2 = box.field_1320 - camera.field_1352;
     double y2 = box.field_1325 - camera.field_1351;
     double z2 = box.field_1324 - camera.field_1350;
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     Tessellator tessellator = Tessellator.method_1348();
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.disableDepthTest();
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     RenderSystem.lineWidth(1.5F);
     
     BufferBuilder buffer = tessellator.method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     
     float r = 1.0F;
     float g = 1.0F;
     float b = 1.0F;
     float a = 1.0F;
     
     buffer.method_22918(matrix, (float)x1, (float)y1, (float)z1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x2, (float)y1, (float)z1).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x2, (float)y1, (float)z1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x2, (float)y1, (float)z2).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x2, (float)y1, (float)z2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x1, (float)y1, (float)z2).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x1, (float)y1, (float)z2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x1, (float)y1, (float)z1).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x1, (float)y2, (float)z1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x2, (float)y2, (float)z1).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x2, (float)y2, (float)z1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x2, (float)y2, (float)z2).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x2, (float)y2, (float)z2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x1, (float)y2, (float)z2).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x1, (float)y2, (float)z2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x1, (float)y2, (float)z1).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x1, (float)y1, (float)z1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x1, (float)y2, (float)z1).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x2, (float)y1, (float)z1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x2, (float)y2, (float)z1).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x2, (float)y1, (float)z2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x2, (float)y2, (float)z2).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, (float)x1, (float)y1, (float)z2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, (float)x1, (float)y2, (float)z2).method_22915(r, g, b, a);
     
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.enableDepthTest();
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
   }
   
   @EventLink
   public void onEvent(EventMove event) {
     if (mc.field_1724 == null)
       return; 
     mc.field_1724.field_5960 = true;
     
     double speed = 1.0D;
     double forward = mc.field_1724.field_3913.field_3905;
     double strafe = mc.field_1724.field_3913.field_3907;
     
     double yaw = Math.toRadians(mc.field_1724.method_36454());
     
     double motionX = 0.0D;
     double motionZ = 0.0D;
     
     if (forward != 0.0D || strafe != 0.0D) {
       double angle = yaw + Math.atan2(-strafe, forward);
       motionX = -Math.sin(angle) * speed;
       motionZ = Math.cos(angle) * speed;
     } 
     
     double motionY = 0.0D;
     if (mc.field_1690.field_1903.method_1434()) {
       motionY = speed;
     } else if (mc.field_1690.field_1832.method_1434()) {
       motionY = -speed;
     } 
     
     event.setMovePos(new Vec3d(motionX, motionY, motionZ));
   }
   
   @EventLink
   public void onEvent(EventMoveInput event) {
     if (mc.field_1724 == null)
       return; 
     if (mc.field_1724.method_18376() == EntityPose.field_18081 || mc.field_1724.method_18376() == EntityPose.field_18079)
       event.setStrafe(event.getStrafe() * 5.0F); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\FreeCam.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */