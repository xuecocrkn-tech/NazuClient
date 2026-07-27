package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.modules.movement.Sprint;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Shadow
    private void setStepHeight(float height) {}

    // ✅ Sprint — простой cancellable inject, работает везде
    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void onCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        Sprint sprint = DeltaClient.MODULE_MANAGER.getModule(Sprint.class);
        if (sprint != null && sprint.isEnabled()) {
            cir.setReturnValue(true);
        }
    }

    // ✅ Step — напрямую устанавливаем stepHeight после каждого tick
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovementTail(CallbackInfo ci) {
        me.delta.client.module.modules.movement.Step step =
                DeltaClient.MODULE_MANAGER.getModule(me.delta.client.module.modules.movement.Step.class);
        if (step != null && step.isEnabled()) {
            setStepHeight(step.getStepHeight());
        }
    }

    // ✅ NoSlow после tick — восстанавливаем скорость если slowed
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onNoSlowTick(CallbackInfo ci) {
        me.delta.client.module.modules.player.NoSlow noSlow =
                DeltaClient.MODULE_MANAGER.getModule(me.delta.client.module.modules.player.NoSlow.class);
        if (noSlow != null && noSlow.isEnabled()) {
            ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
            if (self.isUsingItem() && self.isSprinting()) {
                self.setVelocity(
                        self.getVelocity().x * 1.4,
                        self.getVelocity().y,
                        self.getVelocity().z * 1.4
                );
            }
        }
    }
}
