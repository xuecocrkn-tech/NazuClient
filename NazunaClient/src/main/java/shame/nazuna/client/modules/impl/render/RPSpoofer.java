package shame.nazuna.client.modules.impl.render;
 
 import java.util.UUID;
 import net.minecraft.class_2596;
 import net.minecraft.class_2720;
 import net.minecraft.class_2856;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.bot.BotSessionManager;
 import shame.nazuna.client.modules.Module;
 
 public class RPSpoofer
   extends Module {
   public static RPSpoofer INSTANCE = new RPSpoofer();
   public RPSpoofer() {
     super("RPSpoofer", "Убирает ресурс-пак сервера", Module.ModuleCategory.PLAYER);
   }
   
   @EventLink
   public void onReceivePacket(EventPacket e) {
     class_2596 class_2596 = e.getPacket(); if (class_2596 instanceof class_2720) { class_2720 packet = (class_2720)class_2596; if (isEnable() || BotSessionManager.shouldBypassResourcePacks()) {
         UUID packId = packet.comp_2158();
         mc.method_1562().method_52787((class_2596)new class_2856(packId, class_2856.class_2857.field_13016));
         mc.method_1562().method_52787((class_2596)new class_2856(packId, class_2856.class_2857.field_13017));
         e.setCancelled(true);
       }  }
   
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\RPSpoofer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */