package shame.nazuna.mixin;
 
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import net.minecraft.class_10264;
 import net.minecraft.class_124;
 import net.minecraft.class_2596;
 import net.minecraft.class_2664;
 import net.minecraft.class_2678;
 import net.minecraft.class_2743;
 import net.minecraft.class_310;
 import net.minecraft.class_634;
 import net.minecraft.class_638;
 import net.minecraft.class_7439;
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
 
 @Mixin({class_634.class})
 public abstract class ClientPlayNetworkHandlerMixin {
   @Shadow
   private class_638 field_3699;
   
   @Inject(method = {"method_45729"}, at = {@At("HEAD")}, cancellable = true)
   public void sendChatMessage(@NotNull String message, CallbackInfo ci) {
     if (message.startsWith(astra.INSTANCE.commandStorage.getPrefix())) {
       try {
         astra.INSTANCE.commandStorage.getDispatcher().execute(message.substring(astra.INSTANCE.commandStorage.getPrefix().length()), astra.INSTANCE.commandStorage.getSource());
       } catch (CommandSyntaxException e) {
         ChatUtils.sendMessage(String.valueOf(class_124.field_1061) + "Ошибка в использовании!");
       } 
       ci.cancel();
       return;
     } 
   }
   
   @Inject(method = {"method_11132"}, at = {@At("HEAD")}, cancellable = true)
   private void onVelocityUpdate(class_2743 packet, CallbackInfo ci) {
     EventPacket event = new EventPacket((class_2596)packet, EventPacket.Type.RECEIVE);
     event.call();
     if (event.isCancelled()) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_11124"}, at = {@At("HEAD")}, cancellable = true)
   private void onExplosion(class_2664 packet, CallbackInfo ci) {
     EventPacket event = new EventPacket((class_2596)packet, EventPacket.Type.RECEIVE);
     event.call();
     if (event.isCancelled()) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_64553"}, at = {@At("HEAD")}, cancellable = true)
   private void onEntityPositionSync(class_10264 packet, CallbackInfo ci) {
     class_310 mc = class_310.method_1551();
     if (this.field_3699 == null || mc.field_1724 == null || mc.field_1687 == null) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_43596"}, at = {@At("HEAD")})
   private void onGameMessage(class_7439 packet, CallbackInfo ci) {
     BaritoneAntiStuck.onGameMessage(packet.comp_763().getString());
   }
   
   @Inject(method = {"method_11120"}, at = {@At("HEAD")})
   private void onGameJoin(class_2678 packet, CallbackInfo ci) {
     BotSessionManager.finishBotConnectStage();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ClientPlayNetworkHandlerMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */