package shame.nazuna.api.utils.combat;
 
 import net.minecraft.class_243;
 
 public class PositionData {
   private double serverX;
   private double serverY;
   private double serverZ;
   private double prevServerX;
   private double prevServerY;
   private double prevServerZ;
   private double backUpX;
   private double backUpY;
   private double backUpZ;
   private double lastSpeed;
   private double prevSpeed;
   private long lastUpdate;
   
   public long getLastUpdate() { return this.lastUpdate; }
   
   public class_243 getResolvedPos() {
     return new class_243(this.serverX, this.serverY, this.serverZ);
   }
   
   public class_243 getResolvedForward() {
     return new class_243(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
   }
 
 
 
 
   
   public void update(double x, double y, double z) {
     this.backUpX = this.prevServerX;
     this.backUpY = this.prevServerY;
     this.backUpZ = this.prevServerZ;
     
     this.prevServerX = this.serverX;
     this.prevServerY = this.serverY;
     this.prevServerZ = this.serverZ;
     this.serverX = x;
     this.serverY = y;
     this.serverZ = z;
     
     this.prevSpeed = this.lastSpeed;
     this.lastSpeed = getResolvedForward().method_1033() * 20.0D;
     this.lastUpdate = System.currentTimeMillis();
   }
   
   public boolean isSpeedChanged() {
     return (this.lastSpeed >= 20.0D || (this.lastSpeed != this.prevSpeed && this.lastSpeed == 0.0D));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\combat\PredictUtils$PositionData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */