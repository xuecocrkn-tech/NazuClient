package me.delta.client.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "getOutlineColor", at = @At("HEAD"), cancellable = true)
    private void onGetOutlineColor(Entity entity, float tickDelta, float alpha, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0x9C27B0); // Delta purple
    }
}
