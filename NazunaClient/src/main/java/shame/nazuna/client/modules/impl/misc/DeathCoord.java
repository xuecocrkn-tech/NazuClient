package shame.nazuna.client.modules.impl.misc;
 
 import net.minecraft.BlockPos;
 import net.minecraft.Text;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class DeathCoord extends Module {
   public static DeathCoord INSTANCE = new DeathCoord();
   
   private final BooleanSetting copyToClipboard = new BooleanSetting("Копировать в буфер", true);
   
   private BlockPos deathPos = null;
   private boolean isDead = false;
   
   public DeathCoord() {
     super("DeathCoord", "Показывает координаты смерти", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.copyToClipboard });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.isDead = false;
     this.deathPos = null;
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (mc.field_1724.method_6032() <= 0.0F && !this.isDead) {
       this.isDead = true;
       this.deathPos = mc.field_1724.method_24515();
       
       String coords = "X: " + this.deathPos.method_10263() + " Y: " + this.deathPos.method_10264() + " Z: " + this.deathPos.method_10260();
       String dimension = getDimension();
       String message = "§cВы умерли! §f" + coords + " §7(" + dimension + ")";
       
       mc.field_1724.method_7353((Text)Text.method_43470(message), false);
       
       if (this.copyToClipboard.isState()) {
         mc.field_1774.method_1455("" + this.deathPos.method_10263() + " " + this.deathPos.method_10263() + " " + this.deathPos.method_10264());
       }
     } 
     
     if (mc.field_1724.method_6032() > 0.0F && this.isDead) {
       this.isDead = false;
     }
   }
   
   private String getDimension() {
     if (mc.field_1687 == null) return "Unknown";
     
     String dimension = mc.field_1687.method_27983().method_29177().toString();
     
     if (dimension.contains("overworld")) return "Overworld"; 
     if (dimension.contains("nether")) return "Nether"; 
     if (dimension.contains("end")) return "End";
     
     return dimension;
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.isDead = false;
     this.deathPos = null;
   }
 }

