package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.BlockView;
 import net.minecraft.BlockPos;
 import net.minecraft.Vec3d;
 import net.minecraft.BlockState;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MathHelper;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class ParticleData
 {
   Vec3d position;
   Vec3d velocity;
   int color;
   float size;
   long lifeTime;
   long birthTime;
   float alpha = 1.0F;
   float smoothFactor;
   long lastUpdateNs;
   double gravity;
   
   ParticleData(Vec3d position, Vec3d velocity, int color, float size, long lifeTime, float smooth, double gravity) {
     this.position = position;
     this.velocity = velocity;
     this.color = color;
     this.size = size;
     this.lifeTime = lifeTime;
     this.birthTime = System.currentTimeMillis();
     this.lastUpdateNs = System.nanoTime();
     this.smoothFactor = smooth;
     this.gravity = gravity;
   }
   
   boolean isDead() {
     return (System.currentTimeMillis() - this.birthTime >= this.lifeTime);
   }
   
   void update(MinecraftClient mc) {
     long nowNs = System.nanoTime();
     double deltaSec = (nowNs - this.lastUpdateNs) / 1.0E9D;
     this.lastUpdateNs = nowNs;
     
     float progress = Math.min(1.0F, (float)(System.currentTimeMillis() - this.birthTime) / (float)this.lifeTime);
     double factor = Math.pow(1.0D - progress, this.smoothFactor);
     
     double vx = this.velocity.field_1352;
     double vy = this.velocity.field_1351;
     double vz = this.velocity.field_1350;
     
     double newX = this.position.field_1352;
     double newY = this.position.field_1351;
     double newZ = this.position.field_1350;
     
     newX += vx * factor * deltaSec * 60.0D;
     if (!checkCollision(newX, this.position.field_1351, this.position.field_1350, this.size, mc)) {
       vx = -vx * 0.8D;
       newX = this.position.field_1352;
     } 
     
     newY += vy * factor * deltaSec * 60.0D;
     if (!checkCollision(newX, newY, this.position.field_1350, this.size, mc)) {
       vy = -vy * 1.5D;
       newY = this.position.field_1351;
     } 
     
     newZ += vz * factor * deltaSec * 60.0D;
     if (!checkCollision(newX, newY, newZ, this.size, mc)) {
       vz = -vz * 0.8D;
       newZ = this.position.field_1350;
     } 
     
     this.position = new Vec3d(newX, newY, newZ);
     this.velocity = new Vec3d(vx * 0.9999D, vy * 0.9999D - this.gravity, vz * 0.9999D);
     this.alpha = 1.0F - progress;
   }
   
   static boolean checkCollision(double x, double y, double z, float size, MinecraftClient mc) {
     if (mc.field_1687 == null) return false; 
     double half = size * 0.5D;
     int minX = MathHelper.method_15357(x - half);
     int maxX = MathHelper.method_15357(x + half);
     int minY = MathHelper.method_15357(y - half);
     int maxY = MathHelper.method_15357(y + half);
     int minZ = MathHelper.method_15357(z - half);
     int maxZ = MathHelper.method_15357(z + half);
     
     BlockPos.class_2339 pos = new BlockPos.class_2339();
     for (int bx = minX; bx <= maxX; bx++) {
       for (int by = minY; by <= maxY; by++) {
         for (int bz = minZ; bz <= maxZ; bz++) {
           pos.method_10103(bx, by, bz);
           BlockState state = mc.field_1687.method_8320((BlockPos)pos);
           if (!state.method_26215() && state.method_26212((BlockView)mc.field_1687, (BlockPos)pos)) {
             return false;
           }
         } 
       } 
     } 
     return true;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Particle$ParticleData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */