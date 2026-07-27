package shame.nazuna.api.utils.bot;
 
 import io.netty.channel.ChannelDuplexHandler;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.util.ReferenceCountUtil;
 import net.minecraft.class_10264;
 import net.minecraft.class_2596;
 import net.minecraft.class_2670;
 import net.minecraft.class_2708;
 import net.minecraft.class_2720;
 import net.minecraft.class_2749;
 import net.minecraft.class_2793;
 import net.minecraft.class_2827;
 import net.minecraft.class_2828;
 import net.minecraft.class_2856;
 import net.minecraft.class_634;
 import net.minecraft.class_6373;
 import net.minecraft.class_6374;
 import net.minecraft.class_746;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class null
   extends ChannelDuplexHandler
 {
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
     if (msg instanceof class_2670) { class_2670 packet = (class_2670)msg;
       handler.method_48296().method_10743((class_2596)new class_2827(packet.method_11517()));
       if (botPlayer != null) {
         handler.method_52787((class_2596)new class_2828.class_5911(botPlayer.method_24828(), botPlayer.field_5976));
       }
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof class_6373) { class_6373 packet = (class_6373)msg;
       handler.method_48296().method_10743((class_2596)new class_6374(packet.method_36950()));
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof class_2720) { class_2720 packet = (class_2720)msg;
       handler.method_52787((class_2596)new class_2856(packet.comp_2158(), class_2856.class_2857.field_13016));
       handler.method_52787((class_2596)new class_2856(packet.comp_2158(), class_2856.class_2857.field_13017));
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof class_2708) { class_2708 packet = (class_2708)msg;
       BotSessionManager.applyFrozenPositionLook(botPlayer, packet);
       handler.method_52787((class_2596)new class_2793(packet.comp_3133()));
       if (botPlayer != null) {
         handler.method_52787((class_2596)new class_2828.class_2830(botPlayer
               .method_23317(), botPlayer
               .method_23318(), botPlayer
               .method_23321(), botPlayer
               .method_36454(), botPlayer
               .method_36455(), botPlayer
               .method_24828(), botPlayer.field_5976));
       }
 
       
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof class_10264) { class_10264 packet = (class_10264)msg;
       BotSessionManager.applyFrozenEntityPositionSync(botPlayer, packet);
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof class_2749) { class_2749 packet = (class_2749)msg;
       if (botPlayer != null) {
         botPlayer.method_6033(packet.method_11833());
       }
       ReferenceCountUtil.release(msg);
       
       return; }
     
     if (msg instanceof net.minecraft.class_2661) {
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