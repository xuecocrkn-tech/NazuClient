package shame.nazuna.mixin;
 
 import net.minecraft.class_10042;
 import net.minecraft.class_10055;
 import net.minecraft.class_1297;
 import net.minecraft.class_1657;
 import net.minecraft.class_310;
 import net.minecraft.class_3882;
 import net.minecraft.class_3883;
 import net.minecraft.class_3887;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_583;
 import net.minecraft.class_976;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.client.modules.impl.render.Chams;
 
 @Mixin({class_976.class})
 public abstract class HeadFeatureRendererMixin<S extends class_10042, M extends class_583<S> & class_3882>
   extends class_3887<S, M> {
   public HeadFeatureRendererMixin(class_3883<S, M> context) {
     super(context);
   }
 
 
   
   @Inject(method = {"method_17159"}, at = {@At("HEAD")}, cancellable = true)
   private void onRenderHead(class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g, CallbackInfo ci) {
     class_10055 playerState;
     class_1657 player;
     if (livingEntityRenderState instanceof class_10055) { playerState = (class_10055)livingEntityRenderState; }
     else { return; }
      class_310 mc = class_310.method_1551();
     if (mc == null || mc.field_1687 == null)
       return; 
     class_1297 entity = mc.field_1687.method_8469(playerState.field_53528);
     if (entity instanceof class_1657) { player = (class_1657)entity; }
     else { return; }
      if (Chams.INSTANCE != null && Chams.INSTANCE.shouldHideItemsAndCape(player))
       ci.cancel(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\HeadFeatureRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */