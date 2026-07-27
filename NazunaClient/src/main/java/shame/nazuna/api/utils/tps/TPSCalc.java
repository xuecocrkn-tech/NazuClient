package shame.nazuna.api.utils.tps;
 
 import net.minecraft.class_310;
 import net.minecraft.class_3532;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 
 
 
 public class TPSCalc
 {
   private float TPS = 20.0F;
   public long getLastPacketTime() { return this.lastPacketTime; }
 
 
 
   
   @EventLink
   public void onPacket(EventPacket e) {
     if (e.getType() == EventPacket.Type.RECEIVE && e.getPacket() instanceof net.minecraft.class_2761) {
       updateTPS();
     }
   }
   
   public float getTPS() {
     if (this.lastPacketTime == 0L) {
       return this.TPS;
     }
     
     class_310 mc = class_310.method_1551();
     if (mc == null || mc.method_1562() == null || System.currentTimeMillis() - this.lastPacketTime > 3500L) {
       return 20.0F;
     }
     
     return this.TPS;
   }
   
   public TPSCalc() {
     this.tpsSamples = new float[20];
   public float[] getTpsSamples() { return this.tpsSamples; } private void updateTPS() {
     long now = System.nanoTime();
     this.lastPacketTime = System.currentTimeMillis();
     if (this.timestamp == 0L) {
       this.timestamp = now;
       
       return;
     } 
     long delay = now - this.timestamp;
     this.timestamp = now;
     if (delay <= 0L) {
       return;
     }
     
     float maxTPS = 20.0F;
     float rawTPS = maxTPS * 1.0E9F / (float)delay;
     float boundedTPS = class_3532.method_15363(rawTPS, 0.0F, maxTPS);
     
     this.tpsSamples[this.sampleIndex % 20] = boundedTPS;
     this.sampleIndex++;
     
     int sampleCount = Math.min(this.sampleIndex, 20);
     float sum = 0.0F;
     for (int i = 0; i < sampleCount; i++) {
       float sample = this.tpsSamples[i];
       sum += sample;
     } 
     
     this.TPS = (float)round((sum / sampleCount));
     this.adjustTicks = this.TPS - maxTPS;
   }
   
   public double round(double input) {
     return Math.round(input * 10.0D) / 10.0D;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\tps\TPSCalc.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */