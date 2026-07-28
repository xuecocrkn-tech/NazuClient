package shame.nazuna.mixin;
 
 import net.minecraft.EntityRenderState;
 import net.minecraft.WorldView;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.EntityRenderDispatcher;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Removals;
 
 @Mixin({EntityRenderDispatcher.class})
 public class EntityRenderDispatcherMixin
 {
   @Inject(method = {"method_23166"}, at = {@At("HEAD")}, cancellable = true)
   private static void astra$renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Тени"))
       ci.cancel(); 
   }
 }

