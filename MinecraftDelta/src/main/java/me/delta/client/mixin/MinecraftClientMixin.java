package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        // Tick all modules
        DeltaClient.MODULE_MANAGER.getEnabledModules().forEach(Module::onTick);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickReturn(CallbackInfo ci) {
        // Post-tick cleanup if needed
    }
}
