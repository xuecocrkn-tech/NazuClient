package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.Optional;
 import net.minecraft.class_10142;
 import net.minecraft.class_1297;
 import net.minecraft.class_1657;
 import net.minecraft.class_1764;
 import net.minecraft.class_1792;
 import net.minecraft.class_1799;
 import net.minecraft.class_1802;
 import net.minecraft.class_1893;
 import net.minecraft.class_2246;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_2374;
 import net.minecraft.class_238;
 import net.minecraft.class_239;
 import net.minecraft.class_243;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_2960;
 import net.minecraft.class_3532;
 import net.minecraft.class_3959;
 import net.minecraft.class_3965;
 import net.minecraft.class_4184;
 import net.minecraft.class_4587;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class Trajectories extends Module {
   public static Trajectories INSTANCE = new Trajectories();
   
   private static final int MAX_STEPS = 440;
   private static final double SIMULATION_STEP = 0.5D;
   private static final double SPLASH_RADIUS = 4.0D;
   private static final class_2960 GLOW_TEXTURE = class_2960.method_60655("astra", "textures/trajectories/glow.png");
   
   private final FloatSetting lineWidth = new FloatSetting("Ширина линии", 2.2F, 0.5F, 5.0F, 0.1F);
   
   public Trajectories() {
     super("Trajectories", "Показывает траекторию предмета в руке", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.lineWidth });
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     class_1799 stack = getHeldProjectileStack();
     if (stack.method_7960())
       return; 
     ProjectileParams params = getParams(stack);
     if (params == null)
       return; 
     float tickDelta = event.getTickDelta();
     class_243 startPos = mc.field_1724.method_5836(tickDelta);
     class_243[] directions = getShotDirections(stack, tickDelta);
     PredictionResult[] results = new PredictionResult[directions.length];
     int resultCount = 0;
     for (class_243 direction : directions) {
       PredictionResult result = predict((class_1657)mc.field_1724, params, startPos, direction);
       if (result != null && result.points.length >= 2) {
         results[resultCount++] = result;
       }
     } 
     if (resultCount == 0)
       return; 
     class_4587 matrices = event.getMatrices();
     class_4184 camera = event.getCamera();
     class_243 cameraPos = camera.method_19326();
     int themeColor = ColorUtils.getThemeColor();
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(class_10142.field_53876);
     RenderSystem.lineWidth(this.lineWidth.getValue().floatValue());
     
     matrices.method_22903();
     matrices.method_22904(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350);
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     for (int i = 0; i < resultCount; i++) {
       PredictionResult result = results[i];
       
       drawTrajectoryLine(matrix, result.points, ColorUtils.setAlphaColor(themeColor, 190));
       
       if (result.entityHit != null && result.entityHit.method_5805()) {
         drawEntityBox(matrix, result.entityHit, ColorUtils.rgba(255, 70, 70, 210));
       } else if (result.blockHit != null) {
         drawImpactMarker(matrix, result.hitPos, result.blockHit.method_17780(), ColorUtils.setAlphaColor(themeColor, 230));
       } 
       
       if (stack.method_31574(class_1802.field_8436) && result.hitPos != null) {
         drawPotionRadiusGlow(matrices, result.hitPos, themeColor);
       }
     } 
     
     matrices.method_22909();
     
     RenderSystem.depthMask(true);
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
   }
   
   private class_1799 getHeldProjectileStack() {
     class_1799 main = mc.field_1724.method_6047();
     if (!main.method_7960() && getParams(main) != null) return main; 
     class_1799 off = mc.field_1724.method_6079();
     if (!off.method_7960() && getParams(off) != null) return off; 
     return class_1799.field_8037;
   }
   
   private ProjectileParams getParams(class_1799 stack) {
     class_1792 item = stack.method_7909();
     if (item == class_1802.field_8634 || item == class_1802.field_8543 || item == class_1802.field_8803) {
       return new ProjectileParams(1.5D, 0.03D, 0.99D);
     }
     if (item == class_1802.field_8436 || item == class_1802.field_8150) {
       return new ProjectileParams(0.5D, 0.05D, 0.99D);
     }
     if (item instanceof net.minecraft.class_1753) {
       float power = 1.0F;
       if (mc.field_1724.method_6115() && mc.field_1724.method_6030() == stack) {
         float use = mc.field_1724.method_6048();
         float f = use / 20.0F;
         f = (f * f + f * 2.0F) / 3.0F;
         power = Math.min(f, 1.0F);
       } 
       double velocity = 3.0D * power;
       return (velocity <= 0.01D) ? null : new ProjectileParams(velocity, 0.05D, 0.99D);
     } 
     if (item instanceof class_1764) {
       if (!class_1764.method_7781(stack)) return null; 
       return new ProjectileParams(3.15D, 0.05D, 0.99D);
     } 
     if (item instanceof net.minecraft.class_1835) {
       return new ProjectileParams(2.5D, 0.05D, 0.99D);
     }
     return null;
   }
   
   private class_243[] getShotDirections(class_1799 stack, float tickDelta) {
     class_243 baseDir = mc.field_1724.method_5828(tickDelta).method_1029();
     if (!(stack.method_7909() instanceof class_1764) || InventoryUtils.getEnchantmentLevel(stack, class_1893.field_9108) <= 0) {
       return new class_243[] { baseDir };
     }
     
     float baseYaw = (float)(class_3532.method_15349(baseDir.field_1350, baseDir.field_1352) * 57.29577951308232D) - 90.0F;
     float basePitch = (float)-(class_3532.method_15349(baseDir.field_1351, class_3532.method_15355((float)(baseDir.field_1352 * baseDir.field_1352 + baseDir.field_1350 * baseDir.field_1350))) * 57.29577951308232D);
     return new class_243[] {
         getDirectionFromYawPitch(baseYaw - 10.0F, basePitch), baseDir, 
         
         getDirectionFromYawPitch(baseYaw + 10.0F, basePitch)
       };
   }
   
   private class_243 getDirectionFromYawPitch(float yawDeg, float pitchDeg) {
     float yaw = yawDeg * 0.017453292F;
     float pitch = pitchDeg * 0.017453292F;
     float x = class_3532.method_15374(-yaw - 3.1415927F) * -class_3532.method_15362(-pitch);
     float y = class_3532.method_15374(-pitch);
     float z = class_3532.method_15362(-yaw - 3.1415927F) * -class_3532.method_15362(-pitch);
     return (new class_243(x, y, z)).method_1029();
   }
   
   private PredictionResult predict(class_1657 player, ProjectileParams params, class_243 startPos, class_243 direction) {
     class_243 pos = startPos;
     class_243 motion = direction.method_1029().method_1021(params.velocity);
     class_243[] points = new class_243[441];
     int count = 0;
     points[count++] = pos;
     
     class_1297 entityHit = null;
     class_243 entityHitPos = null;
     
     for (int i = 0; i < 440; i++) {
       class_243 prev = pos;
       class_243 next = pos.method_1019(motion.method_1021(0.5D));
       
       if (entityHit == null) {
         EntityHit hit = rayTraceEntities(prev, next, (class_1297)player);
         if (hit != null) {
           entityHit = hit.entity;
           entityHitPos = hit.hitPos;
         } 
       } 
       
       class_3965 blockHit = mc.field_1687.method_17742(new class_3959(prev, next, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)player));
 
 
 
 
 
 
       
       if (blockHit.method_17783() == class_239.class_240.field_1332) {
         points[count++] = blockHit.method_17784();
         return new PredictionResult(copyPoints(points, count), blockHit, blockHit.method_17784(), entityHit, entityHitPos);
       } 
       
       points[count++] = next;
       pos = next;
       
       boolean inWater = mc.field_1687.method_8320(class_2338.method_49638((class_2374)pos)).method_27852(class_2246.field_10382);
       double drag = Math.pow(inWater ? 0.8D : params.drag, 0.5D);
       motion = motion.method_1021(drag).method_1023(0.0D, params.gravity * 0.5D, 0.0D);
       if (pos.field_1351 <= mc.field_1687.method_31607())
         break; 
     } 
     class_243 hitPos = (entityHitPos != null) ? entityHitPos : points[count - 1];
     return new PredictionResult(copyPoints(points, count), null, hitPos, entityHit, entityHitPos);
   }
   
   private class_243[] copyPoints(class_243[] points, int count) {
     class_243[] out = new class_243[count];
     System.arraycopy(points, 0, out, 0, count);
     return out;
   }
   
   private EntityHit rayTraceEntities(class_243 from, class_243 to, class_1297 owner) {
     class_238 search = (new class_238(from, to)).method_1014(1.0D);
     class_1297 closest = null;
     class_243 closestHit = null;
     double closestDistance = Double.MAX_VALUE;
     
     for (class_1297 entity : mc.field_1687.method_8333(owner, search, entity -> (entity != null && entity.method_5805() && entity.method_5863()))) {
       Optional<class_243> hit = entity.method_5829().method_1014(0.3D).method_992(from, to);
       if (hit.isEmpty())
         continue; 
       double distance = from.method_1025(hit.get());
       if (distance < closestDistance) {
         closestDistance = distance;
         closest = entity;
         closestHit = hit.get();
       } 
     } 
     
     return (closest == null) ? null : new EntityHit(closest, closestHit);
   }
   
   private void drawTrajectoryLine(Matrix4f matrix, class_243[] points, int color) {
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     class_287 buffer = class_289.method_1348().method_60827(class_293.class_5596.field_29344, class_290.field_1576);
     for (int i = 0; i < points.length - 1; i++) {
       class_243 start = points[i];
       class_243 end = points[i + 1];
       buffer.method_22918(matrix, (float)start.field_1352, (float)start.field_1351, (float)start.field_1350).method_1336(r, g, b, a);
       buffer.method_22918(matrix, (float)end.field_1352, (float)end.field_1351, (float)end.field_1350).method_1336(r, g, b, a);
     } 
     class_286.method_43433(buffer.method_60800());
   }
   
   private void drawImpactMarker(Matrix4f matrix, class_243 pos, class_2350 side, int color) {
     class_243 normal = class_243.method_24954(side.method_62675()).method_1029();
 
     
     class_243 u = (side == class_2350.field_11036 || side == class_2350.field_11033) ? new class_243(1.0D, 0.0D, 0.0D) : normal.method_1036(new class_243(0.0D, 1.0D, 0.0D)).method_1029();
     class_243 v = normal.method_1036(u).method_1029();
     class_243 center = pos.method_1019(normal.method_1021(0.004D));
     double radius = 0.35D;
     
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     class_287 buffer = class_289.method_1348().method_60827(class_293.class_5596.field_29344, class_290.field_1576);
     
     int segments = 48;
     class_243 previous = null;
     for (int i = 0; i <= segments; i++) {
       double angle = 6.283185307179586D * i / segments;
       class_243 point = center.method_1019(u.method_1021(Math.cos(angle) * radius)).method_1019(v.method_1021(Math.sin(angle) * radius));
       if (previous != null) {
         buffer.method_22918(matrix, (float)previous.field_1352, (float)previous.field_1351, (float)previous.field_1350).method_1336(r, g, b, a);
         buffer.method_22918(matrix, (float)point.field_1352, (float)point.field_1351, (float)point.field_1350).method_1336(r, g, b, a);
       } 
       previous = point;
     } 
     
     class_243 left = center.method_1019(u.method_1021(-radius));
     class_243 right = center.method_1019(u.method_1021(radius));
     class_243 down = center.method_1019(v.method_1021(-radius));
     class_243 up = center.method_1019(v.method_1021(radius));
     buffer.method_22918(matrix, (float)left.field_1352, (float)left.field_1351, (float)left.field_1350).method_1336(r, g, b, a);
     buffer.method_22918(matrix, (float)right.field_1352, (float)right.field_1351, (float)right.field_1350).method_1336(r, g, b, a);
     buffer.method_22918(matrix, (float)down.field_1352, (float)down.field_1351, (float)down.field_1350).method_1336(r, g, b, a);
     buffer.method_22918(matrix, (float)up.field_1352, (float)up.field_1351, (float)up.field_1350).method_1336(r, g, b, a);
     
     class_286.method_43433(buffer.method_60800());
   }
   
   private void drawEntityBox(Matrix4f matrix, class_1297 entity, int color) {
     class_238 box = entity.method_5829();
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     
     class_287 buffer = class_289.method_1348().method_60827(class_293.class_5596.field_29344, class_290.field_1576);
     vertexBox(buffer, matrix, box, r, g, b, a);
     class_286.method_43433(buffer.method_60800());
   }
   
   private void vertexBox(class_287 buffer, Matrix4f matrix, class_238 box, int r, int g, int b, int a) {
     float minX = (float)box.field_1323, minY = (float)box.field_1322, minZ = (float)box.field_1321;
     float maxX = (float)box.field_1320, maxY = (float)box.field_1325, maxZ = (float)box.field_1324;
     line(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
     line(buffer, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
     line(buffer, matrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
     line(buffer, matrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);
     line(buffer, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
     line(buffer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
     line(buffer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
     line(buffer, matrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);
     line(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
     line(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
     line(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
     line(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
   }
   
   private void line(class_287 buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
     buffer.method_22918(matrix, x1, y1, z1).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x2, y2, z2).method_1336(r, g, b, a);
   }
   
   private void drawPotionRadiusGlow(class_4587 matrices, class_243 pos, int themeColor) {
     int color = ColorUtils.setAlphaColor(themeColor, 82);
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     float radius = 4.0F;
     
     RenderSystem.setShader(class_10142.field_53880);
     RenderSystem.setShaderTexture(0, GLOW_TEXTURE);
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     matrices.method_22903();
     matrices.method_22904(pos.field_1352, pos.field_1351 + 0.012D, pos.field_1350);
     Matrix4f matrix = matrices.method_23760().method_23761();
     class_287 buffer = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1575);
     buffer.method_22918(matrix, -radius, 0.0F, -radius).method_22913(0.0F, 0.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, -radius, 0.0F, radius).method_22913(0.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, radius, 0.0F, radius).method_22913(1.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, radius, 0.0F, -radius).method_22913(1.0F, 0.0F).method_1336(r, g, b, a);
     class_286.method_43433(buffer.method_60800());
     matrices.method_22909();
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.setShader(class_10142.field_53876);
   }
   private static final class ProjectileParams extends Record { private final double velocity; private final double gravity; private final double drag;
     private ProjectileParams(double velocity, double gravity, double drag) { this.velocity = velocity; this.gravity = gravity; this.drag = drag; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/Trajectories$ProjectileParams;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #370	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$ProjectileParams; } public double velocity() { return this.velocity; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/Trajectories$ProjectileParams;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #370	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$ProjectileParams; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/Trajectories$ProjectileParams;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #370	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/render/Trajectories$ProjectileParams;
       //   0	8	1	o	Ljava/lang/Object; } public double gravity() { return this.gravity; } public double drag() { return this.drag; }
      }
   private static final class EntityHit extends Record { private final class_1297 entity; private final class_243 hitPos;
     private EntityHit(class_1297 entity, class_243 hitPos) { this.entity = entity; this.hitPos = hitPos; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #373	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$EntityHit; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #373	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$EntityHit; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #373	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/render/Trajectories$EntityHit;
       //   0	8	1	o	Ljava/lang/Object; } public class_1297 entity() { return this.entity; } public class_243 hitPos() { return this.hitPos; }
      }
   private static final class PredictionResult extends Record { private final class_243[] points; private final class_3965 blockHit; private final class_243 hitPos; private final class_1297 entityHit; private final class_243 entityHitPos;
     private PredictionResult(class_243[] points, class_3965 blockHit, class_243 hitPos, class_1297 entityHit, class_243 entityHitPos) { this.points = points; this.blockHit = blockHit; this.hitPos = hitPos; this.entityHit = entityHit; this.entityHitPos = entityHitPos; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #376	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #376	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #376	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/render/Trajectories$PredictionResult;
       //   0	8	1	o	Ljava/lang/Object; } public class_243[] points() { return this.points; } public class_3965 blockHit() { return this.blockHit; } public class_243 hitPos() { return this.hitPos; } public class_1297 entityHit() { return this.entityHit; } public class_243 entityHitPos() { return this.entityHitPos; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Trajectories.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */