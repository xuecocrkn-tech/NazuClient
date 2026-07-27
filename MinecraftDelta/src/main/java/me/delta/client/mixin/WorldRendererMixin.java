package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.gui.HUD;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderWorld(CallbackInfo ci) {
        // World render hook for ESP and tracers
    }
}
