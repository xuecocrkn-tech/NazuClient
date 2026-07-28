package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.MinecraftClient;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 
 public class Sprint
   extends Module
 {
   public static Sprint INSTANCE = new Sprint();
   private static final MinecraftClient CLIENT = MinecraftClient.method_1551();
   private final BooleanSetting keepInWater = new BooleanSetting("Сохранять в воде", false); private static boolean sprinting;
   
   public Sprint() {
     super("Sprint", "Автоматический бег", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.keepInWater });
   }
   public static void setSprinting(boolean sprinting) { Sprint.sprinting = sprinting; }
   public static void setTime(long time) { Sprint.time = time; }
    private static long time = 0L;
   private static int pauseDepth = 0;
   
   private static boolean restoreAfterPause = false;
   private ClientPlayerEntity lastPlayer;
   
   public void onEnable() {
     resetPauseState();
     sprinting = true;
     super.onEnable();
   }
 
   
   public void onDisable() {
     resetPauseState();
     sprinting = false;
     this.lastPlayer = null;
     if (mc.field_1690 != null) {
       mc.field_1690.field_1867.method_23481(false);
     }
     if (mc.field_1724 != null) {
       mc.field_1724.method_5728(false);
     }
     super.onDisable();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   @EventLink
   public void onEvent(EventUpdate ignored) {
}
}
