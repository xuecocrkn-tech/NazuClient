package shame.nazuna.mixin;
 
 import net.minecraft.LivingEntityRenderState;
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.MinecraftClient;
 import net.minecraft.ModelWithHead;
 import net.minecraft.FeatureRendererContext;
 import net.minecraft.FeatureRenderer;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.EntityModel;
 import net.minecraft.HeadFeatureRenderer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.client.modules.impl.render.Chams;
 
 @Mixin({HeadFeatureRenderer.class})
 public abstract class HeadFeatureRendererMixin<S extends LivingEntityRenderState, M extends EntityModel<S> & ModelWithHead>
   extends FeatureRenderer<S, M> {
   public HeadFeatureRendererMixin(FeatureRendererContext<S, M> context) {
     super(context);
   }
 
 
   
   @Inject(method = {"method_17159"}, at = {@At("HEAD")}, cancellable = true)
   private void onRenderHead(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g, CallbackInfo ci) {
     PlayerEntityRenderState playerState;
     PlayerEntity player;
     if (livingEntityRenderState instanceof PlayerEntityRenderState) { playerState = (PlayerEntityRenderState)livingEntityRenderState; }
     else { return; }
      MinecraftClient mc = MinecraftClient.method_1551();
     if (mc == null || mc.field_1687 == null)
       return; 
     Entity entity = mc.field_1687.method_8469(playerState.field_53528);
     if (entity instanceof PlayerEntity) { player = (PlayerEntity)entity; }
     else { return; }
      if (Chams.INSTANCE != null && Chams.INSTANCE.shouldHideItemsAndCape(player))
       ci.cancel(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\HeadFeatureRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */