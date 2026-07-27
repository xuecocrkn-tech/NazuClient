package shame.nazuna.client.modules.impl.combat;
 
 import net.minecraft.MathHelper;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 
 public class TpsSync
   extends Module {
   public static TpsSync INSTANCE = new TpsSync();
   
   public TpsSync() {
     super("TpsSync", "Синхронизация с TPS сервера", Module.ModuleCategory.COMBAT);
   }
   
   public float getCurrentTPS() {
     if (NazunaClient.INSTANCE == null || NazunaClient.INSTANCE.tpsCalc == null) {
       return 20.0F;
     }
     float tps = NazunaClient.INSTANCE.tpsCalc.getTPS();
     return MathHelper.method_15363(tps, 0.1F, 20.0F);
   }
   
   public long getAdjustedCooldown(long baseCooldown) {
     if (!isEnable()) {
       return baseCooldown;
     }
     
     float tps = getCurrentTPS();
     if (tps >= 20.0F) {
       return baseCooldown;
     }
     
     float multiplier = 20.0F / tps;
     float additionalFactor = 1.0F + (20.0F - tps) * 0.05F;
     long adjusted = (long)((float)baseCooldown * multiplier * additionalFactor);
     
     return Math.min(adjusted, 3000L);
   }
   
   public boolean canAttack(long lastAttackTime, long baseCooldown, long currentTime) {
     if (!isEnable()) {
       return (currentTime >= lastAttackTime + baseCooldown);
     }
     
     long adjustedCooldown = getAdjustedCooldown(baseCooldown);
     return (currentTime >= lastAttackTime + adjustedCooldown);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\TpsSync.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */