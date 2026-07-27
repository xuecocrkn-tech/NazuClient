package shame.nazuna.mixin;
 
 import com.google.common.base.MoreObjects;
 import net.minecraft.class_1268;
 import net.minecraft.class_1306;
 import net.minecraft.class_1309;
 import net.minecraft.class_1657;
 import net.minecraft.class_1799;
 import net.minecraft.class_310;
 import net.minecraft.class_3532;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_742;
 import net.minecraft.class_746;
 import net.minecraft.class_759;
 import net.minecraft.class_7833;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Overwrite;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.ModifyArg;
 import org.spongepowered.asm.mixin.injection.Redirect;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.render.hands.ShaderHandsRenderer;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.impl.render.ShaderHands;
 import shame.nazuna.client.modules.impl.render.SwingAnimations;
 import shame.nazuna.client.modules.impl.render.ViewModel;
 
 @Mixin({class_759.class})
 public abstract class HeldItemRendererMixin {
   @Shadow
   private class_1799 field_4047;
   @Shadow
   private float field_4043;
   @Shadow
   private float field_4053;
   
   @Inject(method = {"method_22976"}, at = {@At("HEAD")})
   private void onRenderItemHead(float tickProgress, class_4587 matrices, class_4597.class_4598 immediate, class_746 player, int light, CallbackInfo ci) {
     ShaderHands shaderHands = getShaderHands();
     if (shaderHands == null || !shaderHands.isEnable())
       return;  ShaderHandsRenderer.getInstance().captureBeforeHands(); } @Shadow
   private float field_4051; @Shadow
   private float field_4052; @Shadow
   private class_1799 field_4048; @Shadow
   protected abstract void method_3228(class_742 paramclass_742, float paramFloat1, float paramFloat2, class_1268 paramclass_1268, float paramFloat3, class_1799 paramclass_1799, float paramFloat4, class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt); @Inject(method = {"method_22976"}, at = {@At("TAIL")})
   private void onRenderItemTail(float tickProgress, class_4587 matrices, class_4597.class_4598 immediate, class_746 player, int light, CallbackInfo ci) { ShaderHands shaderHands = getShaderHands();
     if (shaderHands == null || !shaderHands.isEnable())
       return;  ShaderHandsRenderer.getInstance().captureAfterHands(); }
 
 
   
   @Redirect(method = {"method_22976"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"))
   private void onRenderFirstPersonItemCall(class_759 instance, class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 stack, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light) {
     class_1268 renderHand = hand;
     SwingAnimations tweaks = getTweaks();
     if (tweaks != null && tweaks.isEnable() && !tweaks.hmiEnable.isState() && tweaks.swapHands.isState()) {
       renderHand = (hand == class_1268.field_5808) ? class_1268.field_5810 : class_1268.field_5808;
     }
     ((HeldItemRendererInvoker)instance).whylol$callRenderFirstPersonItem(player, tickDelta, pitch, renderHand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light);
   }
 
 
 
 
 
 
 
   
   @ModifyArg(method = {"method_3228"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_759;method_3219(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IFFLnet/minecraft/class_1306;)V"), index = 5)
   private class_1306 swapEmptyHandArm(class_1306 arm) {
     SwingAnimations tweaks = getTweaks();
     if (tweaks != null && tweaks.isEnable() && !tweaks.hmiEnable.isState() && tweaks.swapHands.isState()) {
       return (arm == class_1306.field_6183) ? class_1306.field_6182 : class_1306.field_6183;
     }
     return arm;
   }
   
   @Inject(method = {"method_3228"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/class_4587;method_22903()V", shift = At.Shift.AFTER)})
   private void onRenderFirstPersonItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 stack, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
     ViewModel viewModel = getViewModel();
     if (viewModel == null || !viewModel.isEnable()) {
       return;
     }
     
     if (hand == class_1268.field_5808) {
       matrices.method_46416(viewModel.mainHandX.get(), viewModel.mainHandY.get(), viewModel.mainHandZ.get());
     } else {
       matrices.method_46416(viewModel.offHandX.get(), viewModel.offHandY.get(), viewModel.offHandZ.get());
     } 
   }
 
 
 
 
 
 
   
   @Redirect(method = {"method_3228"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_759;method_65816(FFLnet/minecraft/class_4587;ILnet/minecraft/class_1306;)V", ordinal = 2))
   private void onSwingArm(class_759 instance, float swingProgress, float equipProgress, class_4587 matrices, int armX, class_1306 arm) {
     float f2, g, f1, panderAnim, anim, tilt, panderF, rotate;
     SwingAnimations tweaks = getTweaks();
     if (tweaks == null || !tweaks.isEnable() || tweaks.hmiEnable.isState() || !tweaks.swingEnabled.isState()) {
       callSwingArm(instance, swingProgress, equipProgress, matrices, armX, arm);
       return;
     } 
     Aura aura = (ModuleClass.INSTANCE != null) ? ModuleClass.aura : null;
     if (tweaks.auraTargetOnly.isState() && (
       aura == null || !aura.isEnable() || aura.getTarget() == null || !aura.getTarget().method_5805())) {
       callSwingArm(instance, swingProgress, equipProgress, matrices, armX, arm);
       
       return;
     } 
     if ((class_310.method_1551()).field_1724 != null) {
       class_1306 expectedSwingArm = (class_310.method_1551()).field_1724.method_6068();
       if (tweaks.swapHands.isState()) {
         expectedSwingArm = (expectedSwingArm == class_1306.field_6183) ? class_1306.field_6182 : class_1306.field_6183;
       }
       if (arm != expectedSwingArm) {
         callSwingArm(instance, swingProgress, equipProgress, matrices, armX, arm);
         
         return;
       } 
     } 
     int i = (arm == class_1306.field_6183) ? 1 : -1;
     float strength = tweaks.swingStrength.get();
     float sin1 = class_3532.method_15374(swingProgress * swingProgress * 3.1415927F);
     float sin2 = class_3532.method_15374(class_3532.method_15355(swingProgress) * 3.1415927F);
     
     switch (tweaks.swingType.getCurrent()) {
       case "Down":
         matrices.method_46416(i * 0.56F, -0.32F, -0.72F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees((76 * i)));
         matrices.method_22907(class_7833.field_40716.rotationDegrees(sin2 * -5.0F * strength));
         matrices.method_22907(class_7833.field_40713.rotationDegrees(sin2 * -100.0F * strength));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(sin2 * -155.0F * strength));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-100.0F));
         return;
       case "Poke":
         f2 = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         tilt = strength / 3.0F;
         matrices.method_46416(i * 0.56F, -0.52F, -0.72F);
         matrices.method_46416(0.0F, 0.0F, tilt * -f2);
         matrices.method_22907(class_7833.field_40716.rotationDegrees(75.0F * i));
         matrices.method_22907(class_7833.field_40718.rotationDegrees((-75.0F * strength / 4.0F * f2 - 60.0F) * i));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-75.0F));
         return;
       case "Static":
         matrices.method_46416(i * 0.56F, -0.42F, -0.72F);
         matrices.method_22907(class_7833.field_40714.rotationDegrees(sin2 * -60.0F * strength));
         matrices.method_22904(0.0D, -0.1D, 0.0D);
         return;
       case "Feast":
         matrices.method_46416(i * 0.56F, -0.32F, -0.72F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees((30 * i)));
         matrices.method_22907(class_7833.field_40716.rotationDegrees(sin2 * 75.0F * i * strength));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(sin2 * -65.0F * strength));
         matrices.method_22907(class_7833.field_40716.rotationDegrees((30 * i)));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-80.0F));
         matrices.method_22907(class_7833.field_40716.rotationDegrees((35 * i)));
         return;
       case "Akrien":
         matrices.method_46416(i * 0.65F, -0.32F, -0.72F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees((76 * i)));
         matrices.method_22907(class_7833.field_40716.rotationDegrees(sin2 * -5.0F * strength));
         matrices.method_22907(class_7833.field_40713.rotationDegrees(sin2 * -100.0F * strength));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(sin2 * -155.0F * strength));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-100.0F));
         matrices.method_22907(class_7833.field_40716.rotationDegrees(sin2 * 25.0F * strength));
         matrices.method_22907(class_7833.field_40713.rotationDegrees(sin2 * -25.0F * strength));
         matrices.method_22907(class_7833.field_40713.rotationDegrees(sin1 * 15.0F * strength));
         matrices.method_46416(sin2 * 0.18F * strength, sin2 * 0.59F * strength, 0.0F); return;
       case "Smooth":
         applySwingOffset(matrices, i, swingProgress, strength); return;
       case "Block":
         if (swingProgress > 0.0F) {
           float f = class_3532.method_15374(class_3532.method_15355(swingProgress) * 3.1415927F);
           matrices.method_46416(0.56F * i, equipProgress * -0.2F - 0.5F, -0.7F);
           matrices.method_22907(class_7833.field_40716.rotationDegrees((45 * i)));
           matrices.method_22907(class_7833.field_40714.rotationDegrees(f * -85.0F * strength));
           matrices.method_46416(-0.1F * i, 0.28F, 0.2F);
           matrices.method_22907(class_7833.field_40714.rotationDegrees(-85.0F));
         } else {
           float n = -0.4F * class_3532.method_15374(class_3532.method_15355(swingProgress) * 3.1415927F);
           float m = 0.2F * class_3532.method_15374(class_3532.method_15355(swingProgress) * 6.2831855F);
           float f3 = -0.2F * class_3532.method_15374(swingProgress * 3.1415927F);
           matrices.method_46416(n * i * strength, m * strength, f3 * strength);
           applyEquipOffset(matrices, i, equipProgress);
           applySwingOffset(matrices, i, swingProgress, strength);
         } 
         return;
       case "ToBack":
         g = class_3532.method_15374(class_3532.method_15355(swingProgress) * 3.1415927F);
         matrices.method_46416(0.65F * i, -0.45F, -0.9F);
         matrices.method_22907(class_7833.field_40714.rotationDegrees(50.0F));
         matrices.method_22907(class_7833.field_40716.rotationDegrees((-30.0F * (1.0F - g * strength) - 30.0F) * i));
         matrices.method_22907(class_7833.field_40718.rotationDegrees(110.0F * i));
         return;
       case "SelfBack":
         f1 = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         matrices.method_46416(0.65F * i, -0.3F, -0.8F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees((90 * i)));
         matrices.method_22907(class_7833.field_40718.rotationDegrees((-70 * i)));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-100.0F - 60.0F * strength * f1)); return;
       case "Break":
       case "Брик":
         matrices.method_46416(0.66F * i, -0.3F, -0.38F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees((270 * i)));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(sin2 * 10.0F * strength));
         
         matrices.method_22905(0.5F, 0.5F, 0.5F);
         matrices.method_46416(-0.1F * i, 0.2F, 0.0F);
         
         matrices.method_22907(class_7833.field_40716.rotationDegrees(-10.0F * i));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
         matrices.method_22907(class_7833.field_40716.rotationDegrees(-105.0F * i));
         return;
       case "DropDown":
         f1 = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         applyEquipOffset(matrices, i, 0.0F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees(80.0F));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(tweaks.corner.get()));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-tweaks.slant.get() * f1 * strength));
         return;
       case "Pander":
         panderAnim = class_3532.method_15374(swingProgress * 3.1415927F);
         panderF = 1.0F - equipProgress;
         matrices.method_46416(i * 0.56F, -0.52F, -0.72F);
         matrices.method_46416((0.3F - panderAnim * 0.15F) * i, 0.2F - panderF * 0.12F, -0.15F - panderAnim * 0.13F);
         matrices.method_22907(class_7833.field_40716.rotationDegrees((76.0F - 10.0F * panderAnim) * i));
         matrices.method_22907(class_7833.field_40718.rotationDegrees((-16.0F - 8.0F * panderAnim) * i));
         matrices.method_22907(class_7833.field_40714.rotationDegrees(-83.0F - 26.0F * panderAnim));
         return;
       case "Slant":
         anim = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         rotate = 35.0F * strength;
         matrices.method_46416(i * 0.56F, -0.52F, -0.72F);
         matrices.method_46416(0.0F, 0.0F, -0.3F * anim * strength);
         matrices.method_22907(class_7833.field_40714.rotationDegrees(anim * -rotate));
         matrices.method_22907(class_7833.field_40718.rotationDegrees(anim * rotate)); return;
     } 
     callSwingArm(instance, swingProgress, equipProgress, matrices, armX, arm);
   }
 
   
   @Overwrite
   public void method_22976(float tickDelta, class_4587 matrices, class_4597.class_4598 vertexConsumers, class_746 player, int light) {
     float f = player.method_6055(tickDelta);
     class_1268 hand = (class_1268)MoreObjects.firstNonNull(player.field_6266, class_1268.field_5808);
     float g = player.method_61414(tickDelta);
     (class_759)this; class_759.class_5773 handRenderType = class_759.method_33303(player);
     float h = class_3532.method_16439(tickDelta, player.field_3914, player.field_3916);
     float i = class_3532.method_16439(tickDelta, player.field_3931, player.field_3932);
 
     
     if (handRenderType.field_28387) {
       float j = (hand == class_1268.field_5808) ? f : 0.0F;
       float k = 1.0F - class_3532.method_16439(tickDelta, this.field_4053, this.field_4043);
       method_3228((class_742)player, tickDelta, g, class_1268.field_5808, j, this.field_4047, k, matrices, (class_4597)vertexConsumers, light);
     } 
     
     if (handRenderType.field_28388) {
       float j = (hand == class_1268.field_5810) ? f : 0.0F;
       float k = 1.0F - class_3532.method_16439(tickDelta, this.field_4051, this.field_4052);
       method_3228((class_742)player, tickDelta, g, class_1268.field_5810, j, this.field_4048, k, matrices, (class_4597)vertexConsumers, light);
     } 
     
     vertexConsumers.method_22993();
   }
   
   @Inject(method = {"method_3218"}, at = {@At("HEAD")}, cancellable = true)
   private void onApplyEatOrDrinkTransformation(class_4587 matrices, float tickDelta, class_1306 arm, class_1799 stack, class_1657 player, CallbackInfo ci) {
     SwingAnimations tweaks = getTweaks();
     if (tweaks == null || !tweaks.isEnable() || tweaks.hmiEnable.isState() || !tweaks.eatAnim.isState() || !player.method_6115()) {
       return;
     }
     
     applyEatOrDrinkTransformationCustom(matrices, tickDelta, arm, stack);
     ci.cancel();
   }
   
   private void applyEatOrDrinkTransformationCustom(class_4587 matrices, float tickDelta, class_1306 arm, class_1799 stack) {
     if ((class_310.method_1551()).field_1724 == null) {
       return;
     }
     
     float f = (class_310.method_1551()).field_1724.method_6014() - tickDelta + 1.0F;
     float g = f / stack.method_7935((class_1309)(class_310.method_1551()).field_1724);
     
     if (g < 0.8F) {
       float f1 = class_3532.method_15379(class_3532.method_15362(f / 4.0F * 3.1415927F) * 0.005F);
       matrices.method_46416(0.0F, f1, 0.0F);
     } 
     
     float h = 1.0F - (float)Math.pow(g, 27.0D);
     int i = (arm == class_1306.field_6183) ? 1 : -1;
     
     float offsetX = 0.0F;
     float offsetY = 0.0F;
     float offsetZ = 0.0F;
     
     ViewModel viewModel = getViewModel();
     if (viewModel != null && viewModel.isEnable()) {
       if (arm == class_1306.field_6183) {
         offsetX = viewModel.mainHandX.get();
         offsetY = viewModel.mainHandY.get();
         offsetZ = viewModel.mainHandZ.get();
       } else {
         offsetX = viewModel.offHandX.get();
         offsetY = viewModel.offHandY.get();
         offsetZ = viewModel.offHandZ.get();
       } 
     }
     
     matrices.method_46416(h * 0.6F * i + offsetX, h * -0.5F + offsetY, offsetZ);
     matrices.method_22907(class_7833.field_40716.rotationDegrees(i * h * 90.0F));
     matrices.method_22907(class_7833.field_40714.rotationDegrees(h * 10.0F));
     matrices.method_22907(class_7833.field_40718.rotationDegrees(i * h * 30.0F));
   }
   
   private void applyEquipOffset(class_4587 matrices, int i, float equipProgress) {
     matrices.method_46416(i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
   }
   
   private void applySwingOffset(class_4587 matrices, int i, float swingProgress, float strength) {
     float f = class_3532.method_15374(swingProgress * swingProgress * 3.1415927F);
     matrices.method_46416(0.56F * i, -0.52F, -0.72F);
     matrices.method_22907(class_7833.field_40716.rotationDegrees(i * (45.0F + f * -20.0F * strength)));
     float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * 3.1415927F);
     matrices.method_22907(class_7833.field_40718.rotationDegrees(i * g * -20.0F * strength));
     matrices.method_22907(class_7833.field_40714.rotationDegrees(g * -80.0F * strength));
     matrices.method_22907(class_7833.field_40716.rotationDegrees(i * -45.0F));
   }
   
   private void callSwingArm(class_759 instance, float swingProgress, float equipProgress, class_4587 matrices, int armX, class_1306 arm) {
     ((HeldItemRendererInvoker)instance).whylol$callSwingArm(swingProgress, equipProgress, matrices, armX, arm);
   }
   
   private SwingAnimations getTweaks() {
     if (ModuleClass.INSTANCE == null) {
       return null;
     }
     return ModuleClass.swingAnimations;
   }
   
   private ViewModel getViewModel() {
     if (ModuleClass.INSTANCE == null) {
       return null;
     }
     return ModuleClass.viewModel;
   }
   
   private ShaderHands getShaderHands() {
     if (ModuleClass.INSTANCE == null) {
       return null;
     }
     return ModuleClass.shaderHands;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\HeldItemRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */