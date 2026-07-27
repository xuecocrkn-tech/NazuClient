package shame.nazuna.client.modules.impl.combat.components;
 
 import net.minecraft.class_1309;
 import net.minecraft.class_238;
 import net.minecraft.class_241;
 import net.minecraft.class_243;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.combat.PredictUtils;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 
 public abstract class RotationsSystem
   implements QClient {
   public class_241 rotate = class_241.field_1340;
   
   public abstract void updateRotations(class_1309 paramclass_1309);
   
   public static class_241 correctRotation(float yaw, float pitch) {
     if ((yaw == -90.0F && pitch == 90.0F) || yaw == -180.0F) return new class_241(mc.field_1724.method_36454(), mc.field_1724.method_36455());
     
     float gcd = GCDUtil.getGCD();
     yaw -= yaw % gcd;
     pitch -= pitch % gcd;
     
     return new class_241(yaw, pitch);
   }
   
   protected boolean shouldUseElytraPredict(class_1309 target) {
     return (mc.field_1724 != null && target != null && mc.field_1724
       
       .method_6128() && target
       .method_6128() && ModuleClass.elytraTarget != null && ModuleClass.elytraTarget
       
       .isEnable());
   }
   
   protected int getElytraPredictTicks() {
     if (ModuleClass.elytraTarget == null) {
       return 0;
     }
     return Math.max(0, ModuleClass.elytraTarget.forward.getValue().intValue());
   }
   
   protected class_243 getPredictedPoint(class_1309 target, class_243 point) {
     if (!shouldUseElytraPredict(target)) {
       return point;
     }
     
     return PredictUtils.bypasselytrahacking(target);
   }
   
   protected class_238 getPredictedBox(class_1309 target) {
     class_238 box = target.method_5829();
     if (!shouldUseElytraPredict(target)) {
       return box;
     }
     class_243 currentCenter = box.method_1005();
     class_243 predictedCenter = getPredictedPoint(target, currentCenter);
     return box.method_997(predictedCenter.method_1020(currentCenter));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\RotationsSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */