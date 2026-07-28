package shame.nazuna.client.modules.impl.player;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.mixin.ILivingEntity;
 
 public class NoJumpDelay
   extends Module {
   public static NoJumpDelay INSTANCE = new NoJumpDelay();
   
   public NoJumpDelay() {
     super("NoJumpDelay", "Убирает задержку на прыжок", Module.ModuleCategory.PLAYER);
   }
   
   @EventLink
   public void onEvent(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     ((ILivingEntity)mc.field_1724).setJumpingCooldown(0);
   }
 }

