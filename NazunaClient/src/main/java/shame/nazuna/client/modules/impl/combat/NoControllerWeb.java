package shame.nazuna.client.modules.impl.combat;
 
 import net.minecraft.Blocks;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBlockCollide;
 import shame.nazuna.client.modules.Module;
 
 public class NoControllerWeb
   extends Module {
   public static NoControllerWeb INSTANCE = new NoControllerWeb();
   
   public NoControllerWeb() {
     super("NoControllerWeb", "Позволяет ломать и бить сквозь паутину", Module.ModuleCategory.COMBAT);
   }
   
   @EventLink
   public void onBlockCollide(EventBlockCollide e) {
     if (mc.field_1687 == null || e.getPos() == null)
       return;  if (mc.field_1687.method_8320(e.getPos()).method_26204() == Blocks.field_10343)
       e.setCancelled(true); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\NoControllerWeb.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */