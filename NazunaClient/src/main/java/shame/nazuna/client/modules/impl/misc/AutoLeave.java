package shame.nazuna.client.modules.impl.misc;
 
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Locale;
 import java.util.Set;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Text;
 import net.minecraft.Team;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AutoLeave extends Module {
   public static final AutoLeave INSTANCE = new AutoLeave();
   
   private static final Set<String> STAFF_PREFIXES = new HashSet<>(Arrays.asList(new String[] { "supp", "mod", "der", "adm", "wne", "curat", "dev", "yt", "мод", "помо", "адм", "владе", "курато", "сапп", "ютуб", "стажер", "сотрудник" }));
 
 
 
   
   private final FloatSetting leaveDistance = new FloatSetting("Дистанция срабатывания", 5.0F, 3.0F, 50.0F, 1.0F);
   private final ListSetting leaveIfSeen = new ListSetting("Выходить если замечен", new BooleanSetting[] { new BooleanSetting("Игрок", true), new BooleanSetting("Модератор", false) });
 
 
   
   private final ModeSetting leaveType = new ModeSetting("Тип выхода", "В мейн меню", new String[] { "В мейн меню", "/hub", "/home", "/spawn" });
   private final BooleanSetting stopBaritone = new BooleanSetting("Выключать баритон", false);
   private final BooleanSetting leaveDisable = new BooleanSetting("Выключать после выхода", true);
   
   private int cooldownTicks;
   
   public AutoLeave() {
     super("AutoLeave", "Выходит с сервера, когда замечает поблизости игрока", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.leaveDistance, (Setting)this.leaveIfSeen, (Setting)this.leaveType, (Setting)this.stopBaritone, (Setting)this.leaveDisable });
   }
 
   
   public void onEnable() {
     this.cooldownTicks = 0;
     super.onEnable();
   }
 
   
   public void onDisable() {
     this.cooldownTicks = 0;
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     if (this.cooldownTicks > 0) {
       this.cooldownTicks--;
       
       return;
     } 
     float maxDistance = this.leaveDistance.get();
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (player == null || player == mc.field_1724) {
         continue;
       }
       
       if (mc.field_1724.method_5739((Entity)player) <= maxDistance && shouldLeaveFor(player)) {
         triggerLeave();
         break;
       } 
     } 
   }
   
   private boolean shouldLeaveFor(PlayerEntity player) {
     if (isModerator(player)) {
       return this.leaveIfSeen.is("Модератор");
     }
     return this.leaveIfSeen.is("Игрок");
   }
   
   private boolean isModerator(PlayerEntity player) {
     if (player == null) {
       return false;
     }
     
     String name = player.method_5477().getString();
     if (astra.INSTANCE != null && astra.INSTANCE.staffStorage != null && astra.INSTANCE.staffStorage.isStaff(name)) {
       return true;
     }
     
     Team team = player.method_5781();
     if (team == null) {
       return false;
     }
     
     String prefix = team.method_1144().getString().toLowerCase(Locale.ROOT);
     for (String candidate : STAFF_PREFIXES) {
       if (prefix.contains(candidate)) {
         return true;
       }
     } 
     
     return false;
   }
   
   private void triggerLeave() {
     tryStopBaritone();
     
     switch (this.leaveType.getCurrent()) { case "В мейн меню":
         disconnectLeave(); break;
       case "/hub": commandLeave("hub"); break;
       case "/home": commandLeave("home home"); break;
       case "/spawn": commandLeave("spawn");
         break; }
   
   }
   private void tryStopBaritone() {
     if (!this.stopBaritone.isState() || mc.method_1562() == null) {
       return;
     }
     mc.method_1562().method_45729("#stop");
   }
   
   private void disconnectLeave() {
     if (mc.method_1562() == null) {
       ChatUtils.sendMessage("Модуль не работает в одиночном мире");
       
       return;
     } 
     mc.method_1562().method_48296().method_10747((Text)Text.method_43470("AutoLeave"));
     if (this.leaveDisable.isState()) {
       toggle();
     }
   }
   
   private void commandLeave(String command) {
     if (mc.method_1562() == null) {
       ChatUtils.sendMessage("AutoLeave нельзя использовать в одиночной игре!");
       
       return;
     } 
     mc.method_1562().method_45730(command);
     this.cooldownTicks = this.leaveDisable.isState() ? 10 : 30;
     
     if (this.leaveDisable.isState())
       toggle(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\AutoLeave.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */