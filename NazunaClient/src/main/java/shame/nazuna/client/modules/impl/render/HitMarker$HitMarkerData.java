package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.Vec3d;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class HitMarkerData
 {
   Vec3d position;
   long birthTime;
   long fadeInTime;
   long displayTime;
   long fadeOutTime;
   
   HitMarkerData(Vec3d position, long birthTime, long fadeInTime, long displayTime, long fadeOutTime) {
     this.position = position;
     this.birthTime = birthTime;
     this.fadeInTime = fadeInTime;
     this.displayTime = displayTime;
     this.fadeOutTime = fadeOutTime;
   }
   
   boolean isDead() {
     return (System.currentTimeMillis() - this.birthTime >= this.fadeInTime + this.displayTime + this.fadeOutTime);
   }
   
   float getAlpha() {
     long elapsed = System.currentTimeMillis() - this.birthTime;
     
     if (elapsed < this.fadeInTime) {
       float f = (float)elapsed / (float)this.fadeInTime;
       return easeOutCubic(f);
     }  if (elapsed < this.fadeInTime + this.displayTime) {
       return 1.0F;
     }
     long fadeOutElapsed = elapsed - this.fadeInTime - this.displayTime;
     float progress = Math.min(1.0F, (float)fadeOutElapsed / (float)this.fadeOutTime);
     return 1.0F - easeInCubic(progress);
   }
 
   
   float getScaleMultiplier() {
     long elapsed = System.currentTimeMillis() - this.birthTime;
     
     if (elapsed < this.fadeInTime) {
       float f = (float)elapsed / (float)this.fadeInTime;
       return 0.5F + 0.5F * easeOutBack(f);
     }  if (elapsed < this.fadeInTime + this.displayTime) {
       return 1.0F;
     }
     long fadeOutElapsed = elapsed - this.fadeInTime - this.displayTime;
     float progress = Math.min(1.0F, (float)fadeOutElapsed / (float)this.fadeOutTime);
     return 1.0F - 0.3F * easeInCubic(progress);
   }
 
   
   private float easeOutCubic(float x) {
     return 1.0F - (float)Math.pow(1.0D - x, 3.0D);
   }
   
   private float easeInCubic(float x) {
     return x * x * x;
   }
   
   private float easeOutBack(float x) {
     float c1 = 1.70158F;
     float c3 = c1 + 1.0F;
     return 1.0F + c3 * (float)Math.pow(x - 1.0D, 3.0D) + c1 * (float)Math.pow(x - 1.0D, 2.0D);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\HitMarker$HitMarkerData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */