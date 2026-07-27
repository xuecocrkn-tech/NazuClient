package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.class_2246;
 import net.minecraft.class_2338;
 import net.minecraft.class_2680;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 
 public class HighJump
   extends Module
 {
   public static HighJump INSTANCE = new HighJump();
   
   private final ModeSetting mode = new ModeSetting("Режим", "Shulker", new String[] { "Shulker", "Slime", "Boat" });
   private final FloatSetting slimeMultiplier = new FloatSetting("Множитель", 2.0F, 1.1F, 5.0F, 0.1F);
   
   private boolean wasInBoat;
   private double lastVelY;
   private int cooldown;
   
   public HighJump() {
     super("HighJump", "Высокий прыжок от различных источников", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.slimeMultiplier });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.wasInBoat = false;
     this.lastVelY = 0.0D;
     this.cooldown = 0;
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.wasInBoat = false;
     this.lastVelY = 0.0D;
     this.cooldown = 0;
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (this.cooldown > 0) this.cooldown--;
     
     if (this.mode.is("Shulker")) {
       handleShulker();
     }
     
     if (this.mode.is("Slime")) {
       handleSlime();
     }
     
     if (this.mode.is("Boat")) {
       handleBoat();
     }
   }
   
   private void handleShulker() {
     if (!(mc.field_1755 instanceof net.minecraft.class_495))
       return; 
     class_2338 playerPos = mc.field_1724.method_24515();
 
 
 
 
 
 
     
     class_2338[] checkPositions = { playerPos.method_10074(), playerPos, playerPos.method_10095(), playerPos.method_10072(), playerPos.method_10078(), playerPos.method_10067() };
 
     
     boolean onShulker = false;
     for (class_2338 pos : checkPositions) {
       class_2680 state = mc.field_1687.method_8320(pos);
       if (state.method_26204() instanceof net.minecraft.class_2480) {
         onShulker = true;
         
         break;
       } 
     } 
     if (onShulker) {
       mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, 2.0D, (mc.field_1724.method_18798()).field_1350);
       mc.field_1724.method_7346();
     } 
   }
   
   private void handleSlime() {
     double velY = (mc.field_1724.method_18798()).field_1351;
     
     class_2338 below = mc.field_1724.method_24515().method_10074();
     class_2338 belowTwo = mc.field_1724.method_24515().method_10087(2);
 
     
     boolean onSlime = (mc.field_1687.method_8320(below).method_27852(class_2246.field_10030) || mc.field_1687.method_8320(belowTwo).method_27852(class_2246.field_10030));
     
     if (this.lastVelY < -0.1D && velY > 0.1D && onSlime && this.cooldown == 0) {
       double boostedVel = velY * this.slimeMultiplier.get();
       mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, boostedVel, (mc.field_1724.method_18798()).field_1350);
       this.cooldown = 5;
     } 
     
     this.lastVelY = velY;
   }
   
   private void handleBoat() {
     boolean inBoat = mc.field_1724.method_5854() instanceof net.minecraft.class_1690;
     
     if (this.wasInBoat && !inBoat && this.cooldown == 0) {
       mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, 1.5D, (mc.field_1724.method_18798()).field_1350);
       this.cooldown = 20;
     } 
     
     this.wasInBoat = inBoat;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\HighJump.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */