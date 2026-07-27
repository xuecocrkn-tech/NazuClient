package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.modules.combat.Velocity;
import me.delta.client.module.modules.movement.NoFall;
import me.delta.client.module.modules.movement.Sprint;
import me.delta.client.module.modules.movement.Step;
import me.delta.client.module.modules.player.NoSlow;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    // Sprint override
    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void onCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        Sprint sprint = DeltaClient.MODULE_MANAGER.getModule(Sprint.class);
        if (sprint != null && sprint.isEnabled()) {
            cir.setReturnValue(true);
        }
    }

    // NoFall — prevent fall damage
    @ModifyVariable(method = "sendMovementPackets", at = @At("STORE"), ordinal = 0)
    private boolean modifyOnGround(boolean onGround) {
        NoFall noFall = DeltaClient.MODULE_MANAGER.getModule(NoFall.class);
        if (noFall != null && noFall.isEnabled() && "Packet".equals(noFall.getCurrentMode())) {
            return true;
        }
        return onGround;
    }

    // Step height modifier
    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setStepHeight(F)V"))
    private float modifyStepHeight(float original) {
        Step step = DeltaClient.MODULE_MANAGER.getModule(Step.class);
        if (step != null && step.isEnabled()) {
            return step.getStepHeight();
        }
        return 0.6f;
    }

    // NoSlow — prevent slowdown from items
    @ModifyVariable(method = "tickMovement", at = @At("HEAD"))
    private boolean modifySprintState(boolean sprinting) {
        NoSlow noSlow = DeltaClient.MODULE_MANAGER.getModule(NoSlow.class);
        if (noSlow != null && noSlow.isEnabled() && sprinting) {
            // Keep sprinting even when using items
        }
        return sprinting;
    }

    @ModifyVariable(method = "tickMovement", at = @At("STORE"), ordinal = 2)
    private boolean modifyItemSlowdown(boolean usingItem) {
        NoSlow noSlow = DeltaClient.MODULE_MANAGER.getModule(NoSlow.class);
        if (noSlow != null && noSlow.isEnabled()) {
            return false;
        }
        return usingItem;
    }
}
