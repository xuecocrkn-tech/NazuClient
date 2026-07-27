package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.modules.render.Chams;
import me.delta.client.module.modules.render.ESP;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void onHasLabel(T entity, CallbackInfoReturnable<Boolean> cir) {
        // Could add nametag ESP here
    }

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void onIsVisible(T entity, CallbackInfoReturnable<Boolean> cir) {
        Chams chams = DeltaClient.MODULE_MANAGER.getModule(Chams.class);
        if (chams != null && chams.isEnabled() && chams.isThroughWalls()) {
            cir.setReturnValue(true);
        }
    }
}
