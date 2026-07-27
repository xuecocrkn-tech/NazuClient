package shame.nazuna.api.utils.animation;
 
 import net.minecraft.MathHelper;
 
 public class AnimationUtils {
   private float currentValue;
   private float targetValue;
   private float fromValue;
   private float speed;
   private Easing easing;
   private long startTime;
   private double duration;
   private boolean isRunning;
   
   public AnimationUtils(float initialValue, float speed, Easing easing) {
     this.currentValue = initialValue;
     this.targetValue = initialValue;
     this.fromValue = initialValue;
     this.speed = speed;
     this.easing = (easing != null) ? easing : Easings.LINEAR;
     this.startTime = 0L;
     this.duration = 0.0D;
     this.isRunning = false;
   }
   
   public AnimationUtils(float initialValue, float speed) {
     this(initialValue, speed, Easings.LINEAR);
   }
   
   public void update(float target) {
     if (this.targetValue != target || !this.isRunning) {
       this.targetValue = target;
       this.fromValue = this.currentValue;
       this.startTime = System.nanoTime();
       this.duration = 1.0D / this.speed * 2.0D;
       this.isRunning = true;
     } 
     
     if (isDone()) {
       this.currentValue = this.targetValue;
       this.isRunning = false;
       
       return;
     } 
     double part = calculatePart();
     float easedPart = (float)this.easing.ease(part);
     this.currentValue = MathHelper.method_16439(easedPart, this.fromValue, this.targetValue);
   }
   
   private double calculatePart() {
     if (!this.isRunning) return 1.0D; 
     long now = System.nanoTime();
     double elapsed = (now - this.startTime) / 1.0E9D;
     return MathHelper.method_15350(elapsed / this.duration, 0.0D, 1.0D);
   }
   
   public float getValue() {
     return this.currentValue;
   }
   
   public void setValue(float value) {
     this.currentValue = value;
     this.targetValue = value;
     this.fromValue = value;
     this.isRunning = false;
   }
   
   public float getTarget() {
     return this.targetValue;
   }
   
   public void setSpeed(float speed) {
     this.speed = speed;
     this.duration = 1.0D / speed;
   }
   
   public void setEasing(Easing easing) {
     this.easing = (easing != null) ? easing : Easings.LINEAR;
   }
   
   public boolean isDone() {
     return (calculatePart() >= 1.0D);
   }
   
   public boolean isAlive() {
     return !isDone();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\animation\AnimationUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */