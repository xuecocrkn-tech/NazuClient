package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.Packet;
 import net.minecraft.PlayerMoveC2SPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 
 public class GrimNoFall extends Module {
   public static GrimNoFall INSTANCE = new GrimNoFall();
   
   public GrimNoFall() {
     super("NoFall", "Убирает урон от падения", Module.ModuleCategory.MOVEMENT);
   }
   
   @EventLink
   public void onUpdate(EventUpdate ignored) {
     if (mc.field_1724 == null || mc.method_1562() == null)
       return;  if (!mc.field_1724.method_24828() && mc.field_1724.field_6017 > 1.0F) {
       mc.method_1562().method_52787((Packet)new PlayerMoveC2SPacket.class_2830(mc.field_1724.method_23317(), mc.field_1724.method_23318() + 1.0E-9D, mc.field_1724.method_23321(), mc.field_1724.method_36454(), mc.field_1724.method_36455(), true, false));
       mc.field_1724.method_38785();
     } 
   }
 }

