package shame.nazuna.api.utils.math;
 
 
 public class TimerUtils {
   private long millis;
   
   public TimerUtils() {
     reset();
   }
   
   public boolean finished(float delay) {
     return ((float)System.currentTimeMillis() - delay >= (float)this.millis);
   }
   
   public boolean finished(long delay) {
     return (System.currentTimeMillis() - this.millis >= delay);
   }
   
   public void reset() {
     this.millis = System.currentTimeMillis();
   }
   
   public long getElapsedTime() {
     return System.currentTimeMillis() - this.millis;
   }
   
   public long getMillis() {
     return this.millis;
   }
   
   public void setMillis(long millis) {
     this.millis = millis;
   }
 }

