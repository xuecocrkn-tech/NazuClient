package shame.nazuna.mixin;
 
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import net.minecraft.EntityPositionSyncS2CPacket;
 import net.minecraft.Formatting;
 import net.minecraft.Packet;
 import net.minecraft.ExplosionS2CPacket;
 import net.minecraft.GameJoinS2CPacket;
 import net.minecraft.EntityVelocityUpdateS2CPacket;
 import net.minecraft.MinecraftClient;
 import net.minecraft.ClientPlayNetworkHandler;
 import net.minecraft.ClientWorld;
 import net.minecraft.GameMessageS2CPacket;
 import org.jetbrains.annotations.NotNull;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.baritone.BaritoneAntiStuck;
 import shame.nazuna.api.utils.bot.BotSessionManager;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.astra;
 
 @Mixin({ClientPlayNetworkHandler.class})
 public abstract class ClientPlayNetworkHandlerMixin {
   @Shadow
   private ClientWorld field_3699;
   
   @Inject(method = {"method_45729"}, at = {@At("HEAD")}, cancellable = true)
   public void sendChatMessage(@NotNull String message, CallbackInfo ci) {
     if (message.startsWith(NazunaClient.INSTANCE.commandStorage.getPrefix())) {
       try {
         NazunaClient.INSTANCE.commandStorage.getDispatcher().execute(message.substring(NazunaClient.INSTANCE.commandStorage.getPrefix().length()), NazunaClient.INSTANCE.commandStorage.getSource());
       } catch (CommandSyntaxException e) {
         ChatUtils.sendMessage(String.valueOf(Formatting.field_1061) + "Ошибка в использовании!");
       } 
       ci.cancel();
       return;
     } 
   }
   
   @Inject(method = {"method_11132"}, at = {@At("HEAD")}, cancellable = true)
   private void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
     EventPacket event = new EventPacket((Packet)packet, EventPacket.Type.RECEIVE);
     event.call();
     if (event.isCancelled()) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_11124"}, at = {@At("HEAD")}, cancellable = true)
   private void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
     EventPacket event = new EventPacket((Packet)packet, EventPacket.Type.RECEIVE);
     event.call();
     if (event.isCancelled()) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_64553"}, at = {@At("HEAD")}, cancellable = true)
   private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
     MinecraftClient mc = MinecraftClient.method_1551();
     if (this.field_3699 == null || mc.field_1724 == null || mc.field_1687 == null) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_43596"}, at = {@At("HEAD")})
   private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
     BaritoneAntiStuck.onGameMessage(packet.comp_763().getString());
   }
   
   @Inject(method = {"method_11120"}, at = {@At("HEAD")})
   private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
     BotSessionManager.finishBotConnectStage();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ClientPlayNetworkHandlerMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */