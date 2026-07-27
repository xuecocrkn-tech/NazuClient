package shame.nazuna.api.utils.math;
 
 import shame.nazuna.api.QClient;
 
 public class Timer implements QClient {
   
   public long getMillis() {
     return this.millis;
   }
   public Timer() {
     reset();
   }
   
   public static Timer create() {
     return new Timer();
   }
   
   public boolean finished(long delay) {
     return (System.currentTimeMillis() - delay >= this.millis);
   }
   
   public void reset() {
     this.millis = System.currentTimeMillis();
   }
   
   public long getElapsedTime() {
     return System.currentTimeMillis() - this.millis;
   }
   
   public double deltaTime() {
     return (mc.method_47599() > 0) ? (1.0D / mc.method_47599()) : 1.0D;
   }
   public boolean every(long ms) {
     boolean passed = (getMillis(System.nanoTime() - this.millis) >= ms);
     if (passed)
       reset(); 
     return passed;
   }
   public boolean passed(long time) {
     return (System.currentTimeMillis() - this.startTime > time);
   }
   public long getMillis(long time) {
     return time / 1000000L;
   }
   
   public long getTime() {
     return System.currentTimeMillis() - this.startTime;
   }
   public void setTime(long time) {
     this.startTime = time;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\math\Timer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */