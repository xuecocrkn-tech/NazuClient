package shame.nazuna.client.modules.impl.player;
 
 import net.minecraft.class_1799;
 import net.minecraft.class_1802;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.mixin.IMinecraftClientAccessor;
 
 public class FastExp
   extends Module {
   public static FastExp INSTANCE = new FastExp();
   
   public FastExp() {
     super("FastExp", "Позволяет бросать пузырьки опыта без задержки", Module.ModuleCategory.PLAYER);
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null) {
       return;
     }
     
     class_1799 stack = mc.field_1724.method_6047();
     if (stack.method_31574(class_1802.field_8287))
       ((IMinecraftClientAccessor)mc).setItemUseCooldown(0); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\FastExp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */