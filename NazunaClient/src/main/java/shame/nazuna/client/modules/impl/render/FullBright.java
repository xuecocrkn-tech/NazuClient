package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.StatusEffectInstance;
 import net.minecraft.StatusEffects;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 
 public class FullBright
   extends Module {
   public static FullBright INSTANCE = new FullBright();
   
   public FullBright() {
     super("FullBright", "Всегда светло", Module.ModuleCategory.RENDER);
   }
   
   @EventLink
   public void onUpdate(EventUpdate ignored) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  mc.field_1724.method_6092(new StatusEffectInstance(StatusEffects.field_5925, 777, 1));
   }
 
   
   public void onDisable() {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  mc.field_1724.method_6016(StatusEffects.field_5925);
     super.onDisable();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\FullBright.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */