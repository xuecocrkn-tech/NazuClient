package shame.nazuna.mixin;
 
 import io.netty.channel.ChannelHandlerContext;
 import java.lang.reflect.InvocationTargetException;
 import net.minecraft.ClientConnection;
 import net.minecraft.Packet;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.events.Event;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.network.NetworkUtils;
 
 @Mixin({ClientConnection.class})
 public abstract class ClientConnectionMixin {
   @Inject(method = {"method_10770"}, at = {@At("HEAD")}, cancellable = true)
   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     EventPacket eventReceive = new EventPacket(packet, EventPacket.Type.RECEIVE);
     EventInvoker.invoke((Event)eventReceive);
     if (eventReceive.isCancelled()) ci.cancel(); 
   }
   
   @Inject(method = {"method_10743"}, at = {@At("HEAD")}, cancellable = true)
   public void send(Packet<?> packet, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     if (NetworkUtils.getSilentPackets().contains(packet)) {
       NetworkUtils.getSilentPackets().remove(packet);
       
       return;
     } 
     EventPacket eventSend = new EventPacket(packet, EventPacket.Type.SEND);
     EventInvoker.invoke((Event)eventSend);
     if (eventSend.isCancelled()) ci.cancel(); 
   }
 }

