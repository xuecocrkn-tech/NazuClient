package shame.nazuna.client.modules.impl.misc;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.regex.Pattern;
 import net.minecraft.class_1657;
 import net.minecraft.class_1713;
 import net.minecraft.class_243;
 import net.minecraft.class_2596;
 import net.minecraft.class_437;
 import net.minecraft.class_476;
 import net.minecraft.class_640;
 import net.minecraft.class_7439;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AutoDuel extends Module {
   public static AutoDuel INSTANCE = new AutoDuel();
   
   public ModeSetting mode = new ModeSetting("Режим", "Шары", new String[] { "Щит", "Шипы", "Лук", "Тотемы", "Нодебафф", "Шары", "Классик", "Читер", "Незер" });
 
   
   private static final Pattern NAME_PATTERN = Pattern.compile("^\\w{3,16}$");
   
   private final List<String> sent = new ArrayList<>();
   private final TimerUtils duelT = new TimerUtils();
   private final TimerUtils clrT = new TimerUtils();
   private final TimerUtils pickT = new TimerUtils();
   private final TimerUtils setT = new TimerUtils();
   
   private class_243 lastPos;
   private boolean inDuel;
   
   public AutoDuel() {
     super("AutoDuel", "Автоматически кидает дуель", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.mode });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.sent.clear();
     this.inDuel = false;
     if (mc.field_1724 != null) {
       this.lastPos = mc.field_1724.method_19538();
     }
     this.duelT.reset();
     this.clrT.reset();
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.sent.clear();
     this.inDuel = false;
   }
 
   
   @EventLink
   public void onUpdate(EventUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null || this.inDuel)
       return; 
     if (this.lastPos != null && mc.field_1724.method_19538().method_1022(this.lastPos) > 500.0D) {
       toggle();
       return;
     } 
     this.lastPos = mc.field_1724.method_19538();
     
     if (this.clrT.getElapsedTime() >= 30000L) {
       this.sent.clear();
       this.clrT.reset();
     } 
     
     if (this.duelT.getElapsedTime() >= 1000L) {
       sendDuel();
       this.duelT.reset();
     } 
     
     handleGui();
   }
   
   @EventLink
   public void onPacket(EventPacket e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (e.getType() == EventPacket.Type.RECEIVE) { class_2596 class_2596 = e.getPacket(); if (class_2596 instanceof class_7439) { class_7439 p = (class_7439)class_2596;
         String msg = p.comp_763().getString().toLowerCase();
         if ((msg.contains("начало") && msg.contains("через") && msg.contains("секунд")) || msg
           .contains("поединок начался") || msg
           .contains("во время поединка")) {
           this.inDuel = true;
           toggle();
         }  }
        }
   
   }
   
   private void sendDuel() {
     for (String p : getPlayers()) {
       if (!this.sent.contains(p) && !p.equals(mc.field_1724.method_5477().getString())) {
         mc.method_1562().method_45730("duel " + p);
         this.sent.add(p);
         break;
       } 
     } 
   }
   private void handleGui() {
     class_476 s;
     class_437 class_437 = mc.field_1755; if (class_437 instanceof class_476) { s = (class_476)class_437; } else { return; }
      int id = ((class_1707)s.method_17577()).field_7763;
     String t = s.method_25440().getString();
     
     if (t.contains("Выбор набора") && this.pickT.getElapsedTime() >= 150L) {
       mc.field_1761.method_2906(id, getModeSlot(), 0, class_1713.field_7794, (class_1657)mc.field_1724);
       this.pickT.reset();
     } else if (t.contains("Настройка поединка") && this.setT.getElapsedTime() >= 150L) {
       mc.field_1761.method_2906(id, 0, 0, class_1713.field_7794, (class_1657)mc.field_1724);
       this.setT.reset();
     } 
   }
   
   private int getModeSlot() {
     if (this.mode.is("Щит")) return 0; 
     if (this.mode.is("Шипы")) return 1; 
     if (this.mode.is("Лук")) return 2; 
     if (this.mode.is("Тотемы")) return 3; 
     if (this.mode.is("Нодебафф")) return 4; 
     if (this.mode.is("Шары")) return 5; 
     if (this.mode.is("Классик")) return 6; 
     if (this.mode.is("Читер")) return 7; 
     if (this.mode.is("Незер")) return 8; 
     return 5;
   }
   
   private List<String> getPlayers() {
     List<String> list = new ArrayList<>();
     if (mc.method_1562() == null) return list; 
     for (class_640 e : mc.method_1562().method_2880()) {
       String n = e.method_2966().getName();
       if (NAME_PATTERN.matcher(n).matches()) {
         list.add(n);
       }
     } 
     return list;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\AutoDuel.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */