package shame.nazuna.mixin;
 
 import net.minecraft.MinecraftClient;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.api.utils.render.fonts.ttf.Fonts;
 
 @Mixin({MinecraftClient.class})
 public class FontInitMixin {
   @Inject(method = {"method_53465"}, at = {@At("TAIL")})
   private void onFinishedLoading(CallbackInfo ci) {
     Fonts.init();
     Fonts.init();
   }
 }

