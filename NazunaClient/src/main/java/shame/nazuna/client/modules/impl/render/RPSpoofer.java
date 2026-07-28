package shame.nazuna.client.modules.impl.render;
 
 import java.util.UUID;
 import net.minecraft.Packet;
 import net.minecraft.ResourcePackSendS2CPacket;
 import net.minecraft.ResourcePackStatusC2SPacket;
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
     Packet Packet = e.getPacket(); if (Packet instanceof ResourcePackSendS2CPacket) { ResourcePackSendS2CPacket packet = (ResourcePackSendS2CPacket)Packet; if (isEnable() || BotSessionManager.shouldBypassResourcePacks()) {
         UUID packId = packet.comp_2158();
         mc.method_1562().method_52787((Packet)new ResourcePackStatusC2SPacket(packId, ResourcePackStatusC2SPacket.class_2857.field_13016));
         mc.method_1562().method_52787((Packet)new ResourcePackStatusC2SPacket(packId, ResourcePackStatusC2SPacket.class_2857.field_13017));
         e.setCancelled(true);
       }  }
   
   }
 }

