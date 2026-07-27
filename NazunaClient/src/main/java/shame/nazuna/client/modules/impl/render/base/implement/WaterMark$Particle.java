package shame.nazuna.client.modules.impl.render.base.implement;
 
 import java.util.Random;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class Particle
 {
   float x;
   float y;
   float velX;
   float velY;
   float life;
   float maxLife;
   float size;
   
   Particle(float x, float y) {
     this.x = x;
     this.y = y;
     Random r = new Random();
     this.velX = (r.nextFloat() - 0.5F) * 0.5F;
     this.velY = -r.nextFloat() * 1.5F - 0.5F;
     this.maxLife = r.nextFloat() * 2.0F + 1.0F;
     this.life = this.maxLife;
     this.size = r.nextFloat() * 1.5F + 0.5F;
   }
   
   void update(float deltaTime) {
     this.x += this.velX * deltaTime * 10.0F;
     this.y += this.velY * deltaTime * 10.0F;
     this.life -= deltaTime;
   }
   
   boolean isDead() {
     return (this.life <= 0.0F);
   }
   
   float getAlpha() {
     return Math.min(1.0F, this.life / this.maxLife);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\WaterMark$Particle.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */