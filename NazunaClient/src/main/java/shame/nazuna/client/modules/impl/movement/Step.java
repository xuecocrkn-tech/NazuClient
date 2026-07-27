package shame.nazuna.client.modules.impl.movement;
 import net.minecraft.BlockView;
 import net.minecraft.BlockPos;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.VoxelShape;
 import net.minecraft.BlockState;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class Step extends Module {
   public static Step INSTANCE = new Step();
   
   public ModeSetting mode = new ModeSetting("Режим", "Vanilla", new String[] { "Vanilla", "NCP", "Motion" });
   public FloatSetting height = new FloatSetting("Высота", 1.0F, 1.0F, 10.0F, 0.5F);
   public BooleanSetting reverse = new BooleanSetting("Reverse", false);
   public FloatSetting reverseHeight = new FloatSetting("Высота Reverse", 1.0F, 1.0F, 10.0F, 0.5F);
   
   private int timer = 0;
   
   public Step() {
     super("Step", "Моментально взбирается на блок", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.height, (Setting)this.reverse, (Setting)this.reverseHeight });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.timer = 0;
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (this.reverse.isState() && mc.field_1724.method_24828() && !mc.field_1690.field_1903.method_1434() && 
       !mc.field_1724.method_5715() && !isBlockAbove()) {
       
       float fallDistance = this.reverseHeight.get();
       
       if (canFall(fallDistance)) {
         Vec3d vel = mc.field_1724.method_18798();
         mc.field_1724.method_18800(vel.field_1352, -fallDistance, vel.field_1350);
       } 
     } 
     
     if (!mc.field_1724.field_5976 || !mc.field_1724.method_24828() || mc.field_1690.field_1903.method_1434()) {
       this.timer = 0;
       
       return;
     } 
     float stepHeight = getStepHeight();
     
     if (stepHeight > 0.6F && stepHeight <= this.height.get()) {
       if (this.mode.is("Vanilla")) {
         handleVanillaStep(stepHeight);
       }
       
       if (this.mode.is("NCP")) {
         handleNCPStep(stepHeight);
       }
       
       if (this.mode.is("Motion")) {
         handleMotionStep(stepHeight);
       }
     } 
   }
   
   private void handleVanillaStep(float stepHeight) {
     mc.field_1724.method_5814(mc.field_1724
         .method_23317(), mc.field_1724
         .method_23318() + stepHeight, mc.field_1724
         .method_23321());
   }
 
   
   private void handleNCPStep(float stepHeight) {
     double[] offsets = null;
     double baseY = mc.field_1724.method_23318();
     
     if (stepHeight <= 1.0F) {
       offsets = new double[] { 0.42D, 0.753D };
     } else if (stepHeight <= 1.5F) {
       offsets = new double[] { 0.42D, 0.75D, 1.0D, 1.16D, 1.23D, 1.2D };
     } else if (stepHeight <= 2.0F) {
       offsets = new double[] { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D };
     } else if (stepHeight <= 2.5F) {
       offsets = new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D };
     } else if (stepHeight <= 3.0F) {
       offsets = new double[] { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 2.1D, 2.4D, 2.7D };
     } 
     
     if (offsets != null) {
       for (double offset : offsets) {
         mc.field_1724.method_5814(mc.field_1724
             .method_23317(), baseY + offset, mc.field_1724
             
             .method_23321());
       }
     }
   }
 
   
   private void handleMotionStep(float stepHeight) {
     Vec3d velocity = mc.field_1724.method_18798();
     double motionY = 0.42D;
     
     if (stepHeight <= 1.0F) {
       motionY = 0.42D;
     } else if (stepHeight <= 1.5F) {
       motionY = 0.52D;
     } else if (stepHeight <= 2.0F) {
       motionY = 0.62D;
     } else if (stepHeight <= 2.5F) {
       motionY = 0.72D;
     } else if (stepHeight <= 3.0F) {
       motionY = 0.82D;
     } 
     
     mc.field_1724.method_18800(velocity.field_1352, motionY, velocity.field_1350);
   }
   
   private float getStepHeight() {
     Box box = mc.field_1724.method_5829();
     float maxY = 0.0F;
     
     double checkDistance = 0.3D;
     double playerYaw = Math.toRadians(mc.field_1724.method_36454());
     double offsetX = -Math.sin(playerYaw) * checkDistance;
     double offsetZ = Math.cos(playerYaw) * checkDistance;
     double y;
     for (y = 0.6D; y <= this.height.get() + 0.6D; y += 0.1D) {
       Box testBox = box.method_989(offsetX, y, offsetZ);
       
       for (BlockPos pos : BlockPos.method_10094(
           (int)Math.floor(testBox.field_1323), 
           (int)Math.floor(testBox.field_1322), 
           (int)Math.floor(testBox.field_1321), 
           (int)Math.floor(testBox.field_1320), 
           (int)Math.floor(testBox.field_1325), 
           (int)Math.floor(testBox.field_1324))) {
         
         BlockState state = mc.field_1687.method_8320(pos);
         if (state.method_26215())
           continue; 
         VoxelShape shape = state.method_26220((BlockView)mc.field_1687, pos);
         if (shape.method_1110())
           continue; 
         for (Box collisionBox : shape.method_1090()) {
           Box offsetBox = collisionBox.method_996(pos);
           float blockHeight = (float)(offsetBox.field_1325 - mc.field_1724.method_23318());
           
           if (blockHeight > 0.6F && blockHeight <= this.height.get()) {
             maxY = Math.max(maxY, blockHeight);
           }
         } 
       } 
     } 
     
     return maxY;
   }
   
   private boolean isBlockAbove() {
     Box box = mc.field_1724.method_5829().method_989(0.0D, 1.0D, 0.0D);
     
     for (BlockPos pos : BlockPos.method_10094(
         (int)Math.floor(box.field_1323), 
         (int)Math.floor(box.field_1322), 
         (int)Math.floor(box.field_1321), 
         (int)Math.floor(box.field_1320), 
         (int)Math.floor(box.field_1325), 
         (int)Math.floor(box.field_1324))) {
       
       if (!mc.field_1687.method_8320(pos).method_26215()) {
         return true;
       }
     } 
     
     return false;
   }
   
   private boolean canFall(float distance) {
     Box box = mc.field_1724.method_5829();
     
     for (double y = 0.1D; y <= distance; y += 0.1D) {
       Box testBox = box.method_989(0.0D, -y, 0.0D);
       
       for (BlockPos pos : BlockPos.method_10094(
           (int)Math.floor(testBox.field_1323), 
           (int)Math.floor(testBox.field_1322), 
           (int)Math.floor(testBox.field_1321), 
           (int)Math.floor(testBox.field_1320), 
           (int)Math.floor(testBox.field_1325), 
           (int)Math.floor(testBox.field_1324))) {
         
         if (!mc.field_1687.method_8320(pos).method_26215()) {
           return false;
         }
       } 
     } 
     
     return true;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\Step.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */