package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.modules.render.ESP;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "getOutlineColor", at = @At("HEAD"), cancellable = true)
    private void onGetOutlineColor(Entity entity, CallbackInfoReturnable<Integer> cir) {
        ESP esp = DeltaClient.MODULE_MANAGER.getModule(ESP.class);
        if (esp != null && esp.shouldRender()) {
            cir.setReturnValue(0x9C27B0); // Delta purple
        }
    }
}
