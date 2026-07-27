package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.modules.movement.Sprint;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    // Только Sprint — просто, безопасно, работает
    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void onCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        Sprint sprint = DeltaClient.MODULE_MANAGER.getModule(Sprint.class);
        if (sprint != null && sprint.isEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
