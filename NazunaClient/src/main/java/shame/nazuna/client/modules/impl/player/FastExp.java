package shame.nazuna.client.modules.impl.player;
 
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
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
     
     ItemStack stack = mc.field_1724.method_6047();
     if (stack.method_31574(Items.field_8287))
       ((IMinecraftClientAccessor)mc).setItemUseCooldown(0); 
   }
 }

