package shame.nazuna.client.modules.impl.player;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 
 public class NoClip
   extends Module
 {
   public static NoClip INSTANCE = new NoClip();
   public NoClip() {
     super("NoClip", "Позволяте проходить через блоки", Module.ModuleCategory.PLAYER);
   }
 
   
   @EventLink
   public void onUpdate(EventUpdate ignored) {
     if (mc.field_1724 == null)
       return; 
     if (mc.field_1724.field_6012 % 35 == 0) {
       mc.field_1724.field_3944.method_45729("/gmsp");
     } else if (mc.field_1724.field_6012 % 35 == 2) {
       mc.field_1724.field_3944.method_45729("/gms");
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\NoClip.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */