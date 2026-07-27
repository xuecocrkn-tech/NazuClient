package shame.nazuna.mixin;
 import net.minecraft.Entity;
 import net.minecraft.Camera;
 import net.minecraft.FogShape;
 import net.minecraft.BackgroundRenderer;
 import net.minecraft.Fog;
 import org.joml.Vector4f;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.client.modules.impl.render.Removals;
 import shame.nazuna.client.modules.impl.render.WorldTweaks;
 
 @Mixin({BackgroundRenderer.class})
 public class BackgroundRendererMixin {
   @Inject(method = {"method_42588"}, at = {@At("HEAD")}, cancellable = true)
   private static void astra$getFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> cir) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Плохие эффекты")) {
       cir.setReturnValue(null);
     }
   }
 
 
 
 
 
 
 
 
   
   @Inject(method = {"method_3211"}, at = {@At("RETURN")}, cancellable = true)
   private static void astra$applyFog(Camera camera, BackgroundRenderer.class_4596 fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<Fog> cir) {
     if (ModuleClass.INSTANCE == null)
       return; 
     WorldTweaks tweaks = ModuleClass.worldTweaks;
     if (tweaks == null || !tweaks.isFogEnabled())
       return; 
     float fogDistance = Math.max(12.0F, tweaks.getFogDistance());
     float fogEnd = Math.min(viewDistance, fogDistance);
     float fogStart = Math.max(0.0F, fogEnd * 0.05F);
     int color1 = tweaks.getFogColor();
     
     cir.setReturnValue(new Fog(fogStart, fogEnd, FogShape.field_36350, 
 
 
           
           ColorUtils.redf(color1), 
           ColorUtils.greenf(color1), 
           ColorUtils.bluef(color1), 1.0F));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\BackgroundRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */