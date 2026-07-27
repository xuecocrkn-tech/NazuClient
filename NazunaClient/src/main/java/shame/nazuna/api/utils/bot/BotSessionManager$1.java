package shame.nazuna.api.utils.bot;
 
 import io.netty.channel.ChannelDuplexHandler;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.util.ReferenceCountUtil;
 import net.minecraft.EntityPositionSyncS2CPacket;
 import net.minecraft.Packet;
 import net.minecraft.KeepAliveS2CPacket;
 import net.minecraft.PlayerPositionLookS2CPacket;
 import net.minecraft.ResourcePackSendS2CPacket;
 import net.minecraft.HealthUpdateS2CPacket;
 import net.minecraft.TeleportConfirmC2SPacket;
 import net.minecraft.KeepAliveC2SPacket;
 import net.minecraft.PlayerMoveC2SPacket;
 import net.minecraft.ResourcePackStatusC2SPacket;
 import net.minecraft.ClientPlayNetworkHandler;
 import net.minecraft.CommonPingS2CPacket;
 import net.minecraft.CommonPongC2SPacket;
 import net.minecraft.ClientPlayerEntity;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class null
   extends ChannelDuplexHandler
 {
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
     if (msg instanceof KeepAliveS2CPacket) { KeepAliveS2CPacket packet = (KeepAliveS2CPacket)msg;
       handler.method_48296().method_10743((Packet)new KeepAliveC2SPacket(packet.method_11517()));
       if (botPlayer != null) {
         handler.method_52787((Packet)new PlayerMoveC2SPacket.class_5911(botPlayer.method_24828(), botPlayer.field_5976));
       }
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof CommonPingS2CPacket) { CommonPingS2CPacket packet = (CommonPingS2CPacket)msg;
       handler.method_48296().method_10743((Packet)new CommonPongC2SPacket(packet.method_36950()));
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof ResourcePackSendS2CPacket) { ResourcePackSendS2CPacket packet = (ResourcePackSendS2CPacket)msg;
       handler.method_52787((Packet)new ResourcePackStatusC2SPacket(packet.comp_2158(), ResourcePackStatusC2SPacket.class_2857.field_13016));
       handler.method_52787((Packet)new ResourcePackStatusC2SPacket(packet.comp_2158(), ResourcePackStatusC2SPacket.class_2857.field_13017));
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof PlayerPositionLookS2CPacket) { PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)msg;
       BotSessionManager.applyFrozenPositionLook(botPlayer, packet);
       handler.method_52787((Packet)new TeleportConfirmC2SPacket(packet.comp_3133()));
       if (botPlayer != null) {
         handler.method_52787((Packet)new PlayerMoveC2SPacket.class_2830(botPlayer
               .method_23317(), botPlayer
               .method_23318(), botPlayer
               .method_23321(), botPlayer
               .method_36454(), botPlayer
               .method_36455(), botPlayer
               .method_24828(), botPlayer.field_5976));
       }
 
       
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof EntityPositionSyncS2CPacket) { EntityPositionSyncS2CPacket packet = (EntityPositionSyncS2CPacket)msg;
       BotSessionManager.applyFrozenEntityPositionSync(botPlayer, packet);
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof HealthUpdateS2CPacket) { HealthUpdateS2CPacket packet = (HealthUpdateS2CPacket)msg;
       if (botPlayer != null) {
         botPlayer.method_6033(packet.method_11833());
       }
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof net.minecraft.DisconnectS2CPacket) {
       BotSessionManager.connections.removeIf(bot -> BotSessionManager.matchesName(bot.name(), name));
       ctx.close();
       ReferenceCountUtil.release(msg);
       
       return;
     } 
     String packetName = msg.getClass().getSimpleName();
     if (packetName.contains("Sound") || packetName
       .contains("Particle") || packetName
       .contains("Screen") || (BotSessionManager.ignoreBotMessages && 
       BotSessionManager.isBotMessagePacket(packetName)) || packetName
       .contains("Explosion") || packetName
       .contains("BossBar") || packetName
       .contains("Scoreboard") || packetName
       .contains("OverlayMessage")) {
       ReferenceCountUtil.release(msg);
       return;
     } 
     super.channelRead(ctx, msg);
   }
 
   
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
     BotSessionManager.connections.removeIf(bot -> BotSessionManager.matchesName(bot.name(), name));
     super.channelInactive(ctx);
   }
 
   
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
     BotSessionManager.connections.removeIf(bot -> BotSessionManager.matchesName(bot.name(), name));
     ctx.close();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\bot\BotSessionManager$1.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */