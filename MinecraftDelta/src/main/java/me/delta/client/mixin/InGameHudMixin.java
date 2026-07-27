package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Render our custom HUD overlay
        DeltaClient.HUD.render(context, tickDelta);
    }
}
