package shame.nazuna.client.modules.impl.player;
 import net.minecraft.class_1268;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_239;
 import net.minecraft.class_2596;
 import net.minecraft.class_2680;
 import net.minecraft.class_2846;
 import net.minecraft.class_3965;
 import net.minecraft.class_634;
 import net.minecraft.class_636;
 import net.minecraft.class_638;
 import net.minecraft.class_746;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class FastBreak extends Module {
   public static FastBreak INSTANCE = new FastBreak();
   
   private final FloatSetting speed = new FloatSetting("Ускорение", 0.5F, 0.3F, 1.0F, 0.1F);
   
   public FastBreak() {
     super("FastBreak", "Ускоряет ломание блоков", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.speed });
   }
   @EventLink
   public void onUpdate(EventUpdate event) {
     class_3965 hit;
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
       return;
     }
     
     class_239 class_239 = mc.field_1765; if (class_239 instanceof class_3965) { hit = (class_3965)class_239; }
     else
     { return; }
     
     if (!mc.field_1690.field_1886.method_1434()) {
       return;
     }
     
     accelerateClientBreak(mc.field_1761, mc.field_1724, mc.field_1687, hit.method_17777(), hit.method_17780(), this.speed.get(), true);
   }
   
   public float getSpeed() {
     return this.speed.get();
   }
   
   public static int getExtraTicks(float speed) {
     return Math.max(1, Math.round(Math.max(0.3F, speed) / 0.35F));
   }
 
 
 
 
 
 
   
   public static boolean accelerateClientBreak(class_636 interactionManager, class_746 player, class_638 world, class_2338 pos, class_2350 side, float speed, boolean swing) {
     if (interactionManager == null || player == null || world == null || pos == null) {
       return false;
     }
     
     class_2680 state = world.method_8320(pos);
     if (state == null || state.method_26215()) {
       return false;
     }
     
     class_2350 breakSide = (side == null) ? class_2350.field_11036 : side;
     int extraTicks = getExtraTicks(speed);
     for (int i = 0; i < extraTicks; i++) {
       interactionManager.method_2902(pos, breakSide);
     }
     
     if (swing) {
       player.method_6104(class_1268.field_5808);
     }
     
     return true;
   }
 
 
 
 
   
   public static boolean packetBreak(class_634 handler, class_746 player, class_2338 pos, class_2350 side, boolean swing) {
     if (handler == null || player == null || pos == null) {
       return false;
     }
     
     class_2350 breakSide = (side == null) ? class_2350.field_11036 : side;
     handler.method_52787((class_2596)new class_2846(class_2846.class_2847.field_12968, pos, breakSide));
     handler.method_52787((class_2596)new class_2846(class_2846.class_2847.field_12973, pos, breakSide));
     
     if (swing) {
       handler.method_52787((class_2596)new class_2879(class_1268.field_5808));
     }
     
     return true;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\FastBreak.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */