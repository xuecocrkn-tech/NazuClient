package shame.nazuna.client.modules.impl.movement;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.combat.Aura;
 
 public class AutoJump
   extends Module {
   public static AutoJump INSTANCE = new AutoJump();
   
   public AutoJump() {
     super("AutoJump", "Прыгает автоматически при ауре", Module.ModuleCategory.MOVEMENT);
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     Aura aura = ModuleClass.aura;
     
     if (aura == null || !aura.isEnable())
       return; 
     if (aura.getTarget() != null && 
       mc.field_1724.method_24828())
       mc.field_1724.method_6043(); 
   }
 }

