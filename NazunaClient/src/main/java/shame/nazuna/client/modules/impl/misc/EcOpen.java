package shame.nazuna.client.modules.impl.misc;
 import net.minecraft.class_1268;
 import net.minecraft.class_2246;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_2382;
 import net.minecraft.class_243;
 import net.minecraft.class_3532;
 import net.minecraft.class_3965;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventGameUpdate;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class EcOpen extends Module {
   public static EcOpen INSTANCE = new EcOpen();
   
   private final BindSetting openKey = new BindSetting("Открыть", -1);
   private final FloatSetting range = new FloatSetting("Дистанция", 6.0F, 3.0F, 6.0F, 0.1F);
   
   private class_2338 targetChest = null;
   private boolean shouldRotate = false;
   private int rotationTicks = 0; private float currentYaw;
   private float currentPitch;
   
   public EcOpen() {
     super("EcOpen", "Открывает эндер сундук по бинду", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.openKey, (Setting)this.range });
   }
 
   
   public void onEnable() {
     reset();
     super.onEnable();
   }
 
   
   public void onDisable() {
     reset();
     super.onDisable();
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1755 != null || mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (event.getKey() == this.openKey.getKey()) {
       findEnderChest();
     }
   }
   
   @EventLink
   public void onGameUpdate(EventGameUpdate event) {
     if (!this.shouldRotate || this.targetChest == null || mc.field_1724 == null)
       return; 
     if (!mc.field_1687.method_8320(this.targetChest).method_27852(class_2246.field_10443)) {
       reset();
       
       return;
     } 
     class_243 target = class_243.method_24953((class_2382)this.targetChest);
     float[] rotations = calculateRotation(target);
     
     float deltaYaw = class_3532.method_15393(rotations[0] - this.currentYaw);
     float deltaPitch = rotations[1] - this.currentPitch;
     
     this.currentYaw += deltaYaw * 0.8F;
     this.currentPitch = class_3532.method_15363(this.currentPitch + deltaPitch * 0.8F, -90.0F, 90.0F);
     
     RotationStorage.update(new Rotation(this.currentYaw, this.currentPitch), 360.0F, 360.0F, 360.0F, 360.0F, 1, 1, false);
     this.rotationTicks++;
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (!this.shouldRotate || this.targetChest == null || mc.field_1724 == null)
       return; 
     if (this.rotationTicks >= 2) {
       class_243 hitVec = class_243.method_24953((class_2382)this.targetChest).method_1031(0.0D, 0.5D, 0.0D);
       class_3965 hitResult = new class_3965(hitVec, class_2350.field_11036, this.targetChest, false);
       
       mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hitResult);
       mc.field_1724.method_6104(class_1268.field_5808);
       reset();
     } 
     
     if (this.rotationTicks > 20) reset(); 
   }
   
   private void findEnderChest() {
     class_2338 playerPos = mc.field_1724.method_24515();
     int r = this.range.getValue().intValue();
     double maxDist = (this.range.getValue().floatValue() * this.range.getValue().floatValue());
     double closestDist = Double.MAX_VALUE;
     class_2338 closest = null;
     
     for (int x = -r; x <= r; x++) {
       for (int y = -r; y <= r; y++) {
         for (int z = -r; z <= r; z++) {
           class_2338 pos = playerPos.method_10069(x, y, z);
           if (mc.field_1687.method_8320(pos).method_27852(class_2246.field_10443)) {
             double dist = mc.field_1724.method_33571().method_1025(class_243.method_24953((class_2382)pos));
             if (dist < closestDist && dist <= maxDist) {
               closestDist = dist;
               closest = pos;
             } 
           } 
         } 
       } 
     } 
     
     if (closest != null) {
       this.targetChest = closest;
       this.shouldRotate = true;
       this.rotationTicks = 0;
       this.currentYaw = mc.field_1724.method_36454();
       this.currentPitch = mc.field_1724.method_36455();
     } 
   }
   
   private float[] calculateRotation(class_243 target) {
     class_243 eye = mc.field_1724.method_33571();
     double dx = target.field_1352 - eye.field_1352;
     double dy = target.field_1351 - eye.field_1351;
     double dz = target.field_1350 - eye.field_1350;
     double dist = Math.sqrt(dx * dx + dz * dz);
     
     float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
     float pitch = (float)-Math.toDegrees(Math.atan2(dy, dist));
     
     return new float[] { yaw, class_3532.method_15363(pitch, -90.0F, 90.0F) };
   }
   
   private void reset() {
     this.targetChest = null;
     this.shouldRotate = false;
     this.rotationTicks = 0;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\EcOpen.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */