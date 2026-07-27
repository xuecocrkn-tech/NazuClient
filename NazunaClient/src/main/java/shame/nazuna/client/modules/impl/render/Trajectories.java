package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.Optional;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.CrossbowItem;
 import net.minecraft.Item;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.Enchantments;
 import net.minecraft.Blocks;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.Position;
 import net.minecraft.Box;
 import net.minecraft.HitResult;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.RaycastContext;
 import net.minecraft.BlockHitResult;
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
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
   private static final Identifier GLOW_TEXTURE = Identifier.method_60655("astra", "textures/trajectories/glow.png");
   
   private final FloatSetting lineWidth = new FloatSetting("Ширина линии", 2.2F, 0.5F, 5.0F, 0.1F);
   
   public Trajectories() {
     super("Trajectories", "Показывает траекторию предмета в руке", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.lineWidth });
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     ItemStack stack = getHeldProjectileStack();
     if (stack.method_7960())
       return; 
     ProjectileParams params = getParams(stack);
     if (params == null)
       return; 
     float tickDelta = event.getTickDelta();
     Vec3d startPos = mc.field_1724.method_5836(tickDelta);
     Vec3d[] directions = getShotDirections(stack, tickDelta);
     PredictionResult[] results = new PredictionResult[directions.length];
     int resultCount = 0;
     for (Vec3d direction : directions) {
       PredictionResult result = predict((PlayerEntity)mc.field_1724, params, startPos, direction);
       if (result != null && result.points.length >= 2) {
         results[resultCount++] = result;
       }
     } 
     if (resultCount == 0)
       return; 
     MatrixStack matrices = event.getMatrices();
     Camera camera = event.getCamera();
     Vec3d cameraPos = camera.method_19326();
     int themeColor = ColorUtils.getThemeColor();
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
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
       
       if (stack.method_31574(Items.field_8436) && result.hitPos != null) {
         drawPotionRadiusGlow(matrices, result.hitPos, themeColor);
       }
     } 
     
     matrices.method_22909();
     
     RenderSystem.depthMask(true);
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
   }
   
   private ItemStack getHeldProjectileStack() {
     ItemStack main = mc.field_1724.method_6047();
     if (!main.method_7960() && getParams(main) != null) return main; 
     ItemStack off = mc.field_1724.method_6079();
     if (!off.method_7960() && getParams(off) != null) return off; 
     return ItemStack.field_8037;
   }
   
   private ProjectileParams getParams(ItemStack stack) {
     Item item = stack.method_7909();
     if (item == Items.field_8634 || item == Items.field_8543 || item == Items.field_8803) {
       return new ProjectileParams(1.5D, 0.03D, 0.99D);
     }
     if (item == Items.field_8436 || item == Items.field_8150) {
       return new ProjectileParams(0.5D, 0.05D, 0.99D);
     }
     if (item instanceof net.minecraft.BowItem) {
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
     if (item instanceof CrossbowItem) {
       if (!CrossbowItem.method_7781(stack)) return null; 
       return new ProjectileParams(3.15D, 0.05D, 0.99D);
     } 
     if (item instanceof net.minecraft.TridentItem) {
       return new ProjectileParams(2.5D, 0.05D, 0.99D);
     }
     return null;
   }
   
   private Vec3d[] getShotDirections(ItemStack stack, float tickDelta) {
     Vec3d baseDir = mc.field_1724.method_5828(tickDelta).method_1029();
     if (!(stack.method_7909() instanceof CrossbowItem) || InventoryUtils.getEnchantmentLevel(stack, Enchantments.field_9108) <= 0) {
       return new Vec3d[] { baseDir };
     }
     
     float baseYaw = (float)(MathHelper.method_15349(baseDir.field_1350, baseDir.field_1352) * 57.29577951308232D) - 90.0F;
     float basePitch = (float)-(MathHelper.method_15349(baseDir.field_1351, MathHelper.method_15355((float)(baseDir.field_1352 * baseDir.field_1352 + baseDir.field_1350 * baseDir.field_1350))) * 57.29577951308232D);
     return new Vec3d[] {
         getDirectionFromYawPitch(baseYaw - 10.0F, basePitch), baseDir, 
         
         getDirectionFromYawPitch(baseYaw + 10.0F, basePitch)
       };
   }
   
   private Vec3d getDirectionFromYawPitch(float yawDeg, float pitchDeg) {
     float yaw = yawDeg * 0.017453292F;
     float pitch = pitchDeg * 0.017453292F;
     float x = MathHelper.method_15374(-yaw - 3.1415927F) * -MathHelper.method_15362(-pitch);
     float y = MathHelper.method_15374(-pitch);
     float z = MathHelper.method_15362(-yaw - 3.1415927F) * -MathHelper.method_15362(-pitch);
     return (new Vec3d(x, y, z)).method_1029();
   }
   
   private PredictionResult predict(PlayerEntity player, ProjectileParams params, Vec3d startPos, Vec3d direction) {
     Vec3d pos = startPos;
     Vec3d motion = direction.method_1029().method_1021(params.velocity);
     Vec3d[] points = new Vec3d[441];
     int count = 0;
     points[count++] = pos;
     
     Entity entityHit = null;
     Vec3d entityHitPos = null;
     
     for (int i = 0; i < 440; i++) {
       Vec3d prev = pos;
       Vec3d next = pos.method_1019(motion.method_1021(0.5D));
       
       if (entityHit == null) {
         EntityHit hit = rayTraceEntities(prev, next, (Entity)player);
         if (hit != null) {
           entityHit = hit.entity;
           entityHitPos = hit.hitPos;
         } 
       } 
       
       BlockHitResult blockHit = mc.field_1687.method_17742(new RaycastContext(prev, next, RaycastContext.class_3960.field_17558, RaycastContext.class_242.field_1348, (Entity)player));
 
 
 
 
 
 
       
       if (blockHit.method_17783() == HitResult.class_240.field_1332) {
         points[count++] = blockHit.method_17784();
         return new PredictionResult(copyPoints(points, count), blockHit, blockHit.method_17784(), entityHit, entityHitPos);
       } 
       
       points[count++] = next;
       pos = next;
       
       boolean inWater = mc.field_1687.method_8320(BlockPos.method_49638((Position)pos)).method_27852(Blocks.field_10382);
       double drag = Math.pow(inWater ? 0.8D : params.drag, 0.5D);
       motion = motion.method_1021(drag).method_1023(0.0D, params.gravity * 0.5D, 0.0D);
       if (pos.field_1351 <= mc.field_1687.method_31607())
         break; 
     } 
     Vec3d hitPos = (entityHitPos != null) ? entityHitPos : points[count - 1];
     return new PredictionResult(copyPoints(points, count), null, hitPos, entityHit, entityHitPos);
   }
   
   private Vec3d[] copyPoints(Vec3d[] points, int count) {
     Vec3d[] out = new Vec3d[count];
     System.arraycopy(points, 0, out, 0, count);
     return out;
   }
   
   private EntityHit rayTraceEntities(Vec3d from, Vec3d to, Entity owner) {
     Box search = (new Box(from, to)).method_1014(1.0D);
     Entity closest = null;
     Vec3d closestHit = null;
     double closestDistance = Double.MAX_VALUE;
     
     for (Entity entity : mc.field_1687.method_8333(owner, search, entity -> (entity != null && entity.method_5805() && entity.method_5863()))) {
       Optional<Vec3d> hit = entity.method_5829().method_1014(0.3D).method_992(from, to);
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
   
   private void drawTrajectoryLine(Matrix4f matrix, Vec3d[] points, int color) {
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     for (int i = 0; i < points.length - 1; i++) {
       Vec3d start = points[i];
       Vec3d end = points[i + 1];
       buffer.method_22918(matrix, (float)start.field_1352, (float)start.field_1351, (float)start.field_1350).method_1336(r, g, b, a);
       buffer.method_22918(matrix, (float)end.field_1352, (float)end.field_1351, (float)end.field_1350).method_1336(r, g, b, a);
     } 
     BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private void drawImpactMarker(Matrix4f matrix, Vec3d pos, Direction side, int color) {
     Vec3d normal = Vec3d.method_24954(side.method_62675()).method_1029();
 
     
     Vec3d u = (side == Direction.field_11036 || side == Direction.field_11033) ? new Vec3d(1.0D, 0.0D, 0.0D) : normal.method_1036(new Vec3d(0.0D, 1.0D, 0.0D)).method_1029();
     Vec3d v = normal.method_1036(u).method_1029();
     Vec3d center = pos.method_1019(normal.method_1021(0.004D));
     double radius = 0.35D;
     
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     
     int segments = 48;
     Vec3d previous = null;
     for (int i = 0; i <= segments; i++) {
       double angle = 6.283185307179586D * i / segments;
       Vec3d point = center.method_1019(u.method_1021(Math.cos(angle) * radius)).method_1019(v.method_1021(Math.sin(angle) * radius));
       if (previous != null) {
         buffer.method_22918(matrix, (float)previous.field_1352, (float)previous.field_1351, (float)previous.field_1350).method_1336(r, g, b, a);
         buffer.method_22918(matrix, (float)point.field_1352, (float)point.field_1351, (float)point.field_1350).method_1336(r, g, b, a);
       } 
       previous = point;
     } 
     
     Vec3d left = center.method_1019(u.method_1021(-radius));
     Vec3d right = center.method_1019(u.method_1021(radius));
     Vec3d down = center.method_1019(v.method_1021(-radius));
     Vec3d up = center.method_1019(v.method_1021(radius));
     buffer.method_22918(matrix, (float)left.field_1352, (float)left.field_1351, (float)left.field_1350).method_1336(r, g, b, a);
     buffer.method_22918(matrix, (float)right.field_1352, (float)right.field_1351, (float)right.field_1350).method_1336(r, g, b, a);
     buffer.method_22918(matrix, (float)down.field_1352, (float)down.field_1351, (float)down.field_1350).method_1336(r, g, b, a);
     buffer.method_22918(matrix, (float)up.field_1352, (float)up.field_1351, (float)up.field_1350).method_1336(r, g, b, a);
     
     BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private void drawEntityBox(Matrix4f matrix, Entity entity, int color) {
     Box box = entity.method_5829();
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     vertexBox(buffer, matrix, box, r, g, b, a);
     BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private void vertexBox(BufferBuilder buffer, Matrix4f matrix, Box box, int r, int g, int b, int a) {
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
   
   private void line(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
     buffer.method_22918(matrix, x1, y1, z1).method_1336(r, g, b, a);
     buffer.method_22918(matrix, x2, y2, z2).method_1336(r, g, b, a);
   }
   
   private void drawPotionRadiusGlow(MatrixStack matrices, Vec3d pos, int themeColor) {
     int color = ColorUtils.setAlphaColor(themeColor, 82);
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     int a = color >> 24 & 0xFF;
     float radius = 4.0F;
     
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     RenderSystem.setShaderTexture(0, GLOW_TEXTURE);
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     matrices.method_22903();
     matrices.method_22904(pos.field_1352, pos.field_1351 + 0.012D, pos.field_1350);
     Matrix4f matrix = matrices.method_23760().method_23761();
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     buffer.method_22918(matrix, -radius, 0.0F, -radius).method_22913(0.0F, 0.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, -radius, 0.0F, radius).method_22913(0.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, radius, 0.0F, radius).method_22913(1.0F, 1.0F).method_1336(r, g, b, a);
     buffer.method_22918(matrix, radius, 0.0F, -radius).method_22913(1.0F, 0.0F).method_1336(r, g, b, a);
     BufferRenderer.method_43433(buffer.method_60800());
     matrices.method_22909();
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
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
   private static final class EntityHit extends Record { private final Entity entity; private final Vec3d hitPos;
     private EntityHit(Entity entity, Vec3d hitPos) { this.entity = entity; this.hitPos = hitPos; } public final String toString() { // Byte code:
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
       //   0	8	1	o	Ljava/lang/Object; } public Entity entity() { return this.entity; } public Vec3d hitPos() { return this.hitPos; }
      }
   private static final class PredictionResult extends Record { private final Vec3d[] points; private final BlockHitResult blockHit; private final Vec3d hitPos; private final Entity entityHit; private final Vec3d entityHitPos;
     private PredictionResult(Vec3d[] points, BlockHitResult blockHit, Vec3d hitPos, Entity entityHit, Vec3d entityHitPos) { this.points = points; this.blockHit = blockHit; this.hitPos = hitPos; this.entityHit = entityHit; this.entityHitPos = entityHitPos; } public final String toString() { // Byte code:
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
       //   0	8	1	o	Ljava/lang/Object; } public Vec3d[] points() { return this.points; } public BlockHitResult blockHit() { return this.blockHit; } public Vec3d hitPos() { return this.hitPos; } public Entity entityHit() { return this.entityHit; } public Vec3d entityHitPos() { return this.entityHitPos; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Trajectories.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */