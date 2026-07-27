package shame.nazuna.api.utils.network;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.Packet;
 import shame.nazuna.api.QClient;
 
 public final class NetworkUtils implements QClient {
   private NetworkUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   
   public static void sendSilentPacket(Packet<?> packet) {
     silentPackets.add(packet);
     mc.method_1562().method_52787(packet);
   }
   
   public static void sendPacket(Packet<?> packet) {
     mc.method_1562().method_52787(packet);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\network\NetworkUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */