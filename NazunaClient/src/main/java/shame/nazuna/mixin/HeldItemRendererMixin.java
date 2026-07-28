package shame.nazuna.mixin;
 
 import com.google.common.base.MoreObjects;
 import net.minecraft.Hand;
 import net.minecraft.Arm;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ItemStack;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.AbstractClientPlayerEntity;
 import net.minecraft.ClientPlayerEntity;
 import net.minecraft.HeldItemRenderer;
 import net.minecraft.RotationAxis;
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
 
 @Mixin({HeldItemRenderer.class})
 public abstract class HeldItemRendererMixin {
   @Shadow
   private ItemStack field_4047;
   @Shadow
   private float field_4043;
   @Shadow
   private float field_4053;
   
   @Inject(method = {"method_22976"}, at = {@At("HEAD")})
   private void onRenderItemHead(float tickProgress, MatrixStack matrices, VertexConsumerProvider.class_4598 immediate, ClientPlayerEntity player, int light, CallbackInfo ci) {
     ShaderHands shaderHands = getShaderHands();
     if (shaderHands == null || !shaderHands.isEnable())
       return;  ShaderHandsRenderer.getInstance().captureBeforeHands(); } @Shadow
   private float field_4051; @Shadow
   private float field_4052; @Shadow
   private ItemStack field_4048; @Shadow
   protected abstract void method_3228(AbstractClientPlayerEntity paramclass_742, float paramFloat1, float paramFloat2, Hand paramclass_1268, float paramFloat3, ItemStack paramclass_1799, float paramFloat4, MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt); @Inject(method = {"method_22976"}, at = {@At("TAIL")})
   private void onRenderItemTail(float tickProgress, MatrixStack matrices, VertexConsumerProvider.class_4598 immediate, ClientPlayerEntity player, int light, CallbackInfo ci) { ShaderHands shaderHands = getShaderHands();
     if (shaderHands == null || !shaderHands.isEnable())
       return;  ShaderHandsRenderer.getInstance().captureAfterHands(); }
 
 
   
   @Redirect(method = {"method_22976"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/HeldItemRenderer;method_3228(Lnet/minecraft/AbstractClientPlayerEntity;FFLnet/minecraft/Hand;FLnet/minecraft/ItemStack;FLnet/minecraft/MatrixStack;Lnet/minecraft/VertexConsumerProvider;I)V"))
   private void onRenderFirstPersonItemCall(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
     Hand renderHand = hand;
     SwingAnimations tweaks = getTweaks();
     if (tweaks != null && tweaks.isEnable() && !tweaks.hmiEnable.isState() && tweaks.swapHands.isState()) {
       renderHand = (hand == Hand.field_5808) ? Hand.field_5810 : Hand.field_5808;
     }
     ((HeldItemRendererInvoker)instance).whylol$callRenderFirstPersonItem(player, tickDelta, pitch, renderHand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light);
   }
 
 
 
 
 
 
 
   
   @ModifyArg(method = {"method_3228"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/HeldItemRenderer;method_3219(Lnet/minecraft/MatrixStack;Lnet/minecraft/VertexConsumerProvider;IFFLnet/minecraft/Arm;)V"), index = 5)
   private Arm swapEmptyHandArm(Arm arm) {
     SwingAnimations tweaks = getTweaks();
     if (tweaks != null && tweaks.isEnable() && !tweaks.hmiEnable.isState() && tweaks.swapHands.isState()) {
       return (arm == Arm.field_6183) ? Arm.field_6182 : Arm.field_6183;
     }
     return arm;
   }
   
   @Inject(method = {"method_3228"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/MatrixStack;method_22903()V", shift = At.Shift.AFTER)})
   private void onRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
     ViewModel viewModel = getViewModel();
     if (viewModel == null || !viewModel.isEnable()) {
       return;
     }
     
     if (hand == Hand.field_5808) {
       matrices.method_46416(viewModel.mainHandX.get(), viewModel.mainHandY.get(), viewModel.mainHandZ.get());
     } else {
       matrices.method_46416(viewModel.offHandX.get(), viewModel.offHandY.get(), viewModel.offHandZ.get());
     } 
   }
 
 
 
 
 
 
   
   @Redirect(method = {"method_3228"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/HeldItemRenderer;method_65816(FFLnet/minecraft/MatrixStack;ILnet/minecraft/Arm;)V", ordinal = 2))
   private void onSwingArm(HeldItemRenderer instance, float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm) {
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
     if ((MinecraftClient.method_1551()).field_1724 != null) {
       Arm expectedSwingArm = (MinecraftClient.method_1551()).field_1724.method_6068();
       if (tweaks.swapHands.isState()) {
         expectedSwingArm = (expectedSwingArm == Arm.field_6183) ? Arm.field_6182 : Arm.field_6183;
       }
       if (arm != expectedSwingArm) {
         callSwingArm(instance, swingProgress, equipProgress, matrices, armX, arm);
         
         return;
       } 
     } 
     int i = (arm == Arm.field_6183) ? 1 : -1;
     float strength = tweaks.swingStrength.get();
     float sin1 = MathHelper.method_15374(swingProgress * swingProgress * 3.1415927F);
     float sin2 = MathHelper.method_15374(MathHelper.method_15355(swingProgress) * 3.1415927F);
     
     switch (tweaks.swingType.getCurrent()) {
       case "Down":
         matrices.method_46416(i * 0.56F, -0.32F, -0.72F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((76 * i)));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(sin2 * -5.0F * strength));
         matrices.method_22907(RotationAxis.field_40713.rotationDegrees(sin2 * -100.0F * strength));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(sin2 * -155.0F * strength));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-100.0F));
         return;
       case "Poke":
         f2 = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         tilt = strength / 3.0F;
         matrices.method_46416(i * 0.56F, -0.52F, -0.72F);
         matrices.method_46416(0.0F, 0.0F, tilt * -f2);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(75.0F * i));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-75.0F * strength / 4.0F * f2 - 60.0F) * i));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-75.0F));
         return;
       case "Static":
         matrices.method_46416(i * 0.56F, -0.42F, -0.72F);
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(sin2 * -60.0F * strength));
         matrices.method_22904(0.0D, -0.1D, 0.0D);
         return;
       case "Feast":
         matrices.method_46416(i * 0.56F, -0.32F, -0.72F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((30 * i)));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(sin2 * 75.0F * i * strength));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(sin2 * -65.0F * strength));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((30 * i)));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-80.0F));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((35 * i)));
         return;
       case "Akrien":
         matrices.method_46416(i * 0.65F, -0.32F, -0.72F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((76 * i)));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(sin2 * -5.0F * strength));
         matrices.method_22907(RotationAxis.field_40713.rotationDegrees(sin2 * -100.0F * strength));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(sin2 * -155.0F * strength));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-100.0F));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(sin2 * 25.0F * strength));
         matrices.method_22907(RotationAxis.field_40713.rotationDegrees(sin2 * -25.0F * strength));
         matrices.method_22907(RotationAxis.field_40713.rotationDegrees(sin1 * 15.0F * strength));
         matrices.method_46416(sin2 * 0.18F * strength, sin2 * 0.59F * strength, 0.0F); return;
       case "Smooth":
         applySwingOffset(matrices, i, swingProgress, strength); return;
       case "Block":
         if (swingProgress > 0.0F) {
           float f = MathHelper.method_15374(MathHelper.method_15355(swingProgress) * 3.1415927F);
           matrices.method_46416(0.56F * i, equipProgress * -0.2F - 0.5F, -0.7F);
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees((45 * i)));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(f * -85.0F * strength));
           matrices.method_46416(-0.1F * i, 0.28F, 0.2F);
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-85.0F));
         } else {
           float n = -0.4F * MathHelper.method_15374(MathHelper.method_15355(swingProgress) * 3.1415927F);
           float m = 0.2F * MathHelper.method_15374(MathHelper.method_15355(swingProgress) * 6.2831855F);
           float f3 = -0.2F * MathHelper.method_15374(swingProgress * 3.1415927F);
           matrices.method_46416(n * i * strength, m * strength, f3 * strength);
           applyEquipOffset(matrices, i, equipProgress);
           applySwingOffset(matrices, i, swingProgress, strength);
         } 
         return;
       case "ToBack":
         g = MathHelper.method_15374(MathHelper.method_15355(swingProgress) * 3.1415927F);
         matrices.method_46416(0.65F * i, -0.45F, -0.9F);
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(50.0F));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-30.0F * (1.0F - g * strength) - 30.0F) * i));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees(110.0F * i));
         return;
       case "SelfBack":
         f1 = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         matrices.method_46416(0.65F * i, -0.3F, -0.8F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((90 * i)));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-70 * i)));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-100.0F - 60.0F * strength * f1)); return;
       case "Break":
       case "Брик":
         matrices.method_46416(0.66F * i, -0.3F, -0.38F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((270 * i)));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(sin2 * 10.0F * strength));
         
         matrices.method_22905(0.5F, 0.5F, 0.5F);
         matrices.method_46416(-0.1F * i, 0.2F, 0.0F);
         
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-10.0F * i));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(90.0F));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-105.0F * i));
         return;
       case "DropDown":
         f1 = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         applyEquipOffset(matrices, i, 0.0F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(80.0F));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(tweaks.corner.get()));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-tweaks.slant.get() * f1 * strength));
         return;
       case "Pander":
         panderAnim = MathHelper.method_15374(swingProgress * 3.1415927F);
         panderF = 1.0F - equipProgress;
         matrices.method_46416(i * 0.56F, -0.52F, -0.72F);
         matrices.method_46416((0.3F - panderAnim * 0.15F) * i, 0.2F - panderF * 0.12F, -0.15F - panderAnim * 0.13F);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((76.0F - 10.0F * panderAnim) * i));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-16.0F - 8.0F * panderAnim) * i));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-83.0F - 26.0F * panderAnim));
         return;
       case "Slant":
         anim = (float)Math.sin(swingProgress * 1.5707963267948966D * 2.0D);
         rotate = 35.0F * strength;
         matrices.method_46416(i * 0.56F, -0.52F, -0.72F);
         matrices.method_46416(0.0F, 0.0F, -0.3F * anim * strength);
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(anim * -rotate));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees(anim * rotate)); return;
     } 
     callSwingArm(instance, swingProgress, equipProgress, matrices, armX, arm);
   }
 
   
   @Overwrite
   public void method_22976(float tickDelta, MatrixStack matrices, VertexConsumerProvider.class_4598 vertexConsumers, ClientPlayerEntity player, int light) {
     float f = player.method_6055(tickDelta);
     Hand hand = (Hand)MoreObjects.firstNonNull(player.field_6266, Hand.field_5808);
     float g = player.method_61414(tickDelta);
     HeldItemRenderer.handRenderType handRenderType = HeldItemRenderer.method_33303(player);
     float h = MathHelper.method_16439(tickDelta, player.field_3914, player.field_3916);
     float i = MathHelper.method_16439(tickDelta, player.field_3931, player.field_3932);
 
     
     if (handRenderType.field_28387) {
       float j = (hand == Hand.field_5808) ? f : 0.0F;
       float k = 1.0F - MathHelper.method_16439(tickDelta, this.field_4053, this.field_4043);
       method_3228((AbstractClientPlayerEntity)player, tickDelta, g, Hand.field_5808, j, this.field_4047, k, matrices, (VertexConsumerProvider)vertexConsumers, light);
     } 
     
     if (handRenderType.field_28388) {
       float j = (hand == Hand.field_5810) ? f : 0.0F;
       float k = 1.0F - MathHelper.method_16439(tickDelta, this.field_4051, this.field_4052);
       method_3228((AbstractClientPlayerEntity)player, tickDelta, g, Hand.field_5810, j, this.field_4048, k, matrices, (VertexConsumerProvider)vertexConsumers, light);
     } 
     
     vertexConsumers.method_22993();
   }
   
   @Inject(method = {"method_3218"}, at = {@At("HEAD")}, cancellable = true)
   private void onApplyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, CallbackInfo ci) {
     SwingAnimations tweaks = getTweaks();
     if (tweaks == null || !tweaks.isEnable() || tweaks.hmiEnable.isState() || !tweaks.eatAnim.isState() || !player.method_6115()) {
       return;
     }
     
     applyEatOrDrinkTransformationCustom(matrices, tickDelta, arm, stack);
     ci.cancel();
   }
   
   private void applyEatOrDrinkTransformationCustom(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
     if ((MinecraftClient.method_1551()).field_1724 == null) {
       return;
     }
     
     float f = (MinecraftClient.method_1551()).field_1724.method_6014() - tickDelta + 1.0F;
     float g = f / stack.method_7935((LivingEntity)(MinecraftClient.method_1551()).field_1724);
     
     if (g < 0.8F) {
       float f1 = MathHelper.method_15379(MathHelper.method_15362(f / 4.0F * 3.1415927F) * 0.005F);
       matrices.method_46416(0.0F, f1, 0.0F);
     } 
     
     float h = 1.0F - (float)Math.pow(g, 27.0D);
     int i = (arm == Arm.field_6183) ? 1 : -1;
     
     float offsetX = 0.0F;
     float offsetY = 0.0F;
     float offsetZ = 0.0F;
     
     ViewModel viewModel = getViewModel();
     if (viewModel != null && viewModel.isEnable()) {
       if (arm == Arm.field_6183) {
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
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * h * 90.0F));
     matrices.method_22907(RotationAxis.field_40714.rotationDegrees(h * 10.0F));
     matrices.method_22907(RotationAxis.field_40718.rotationDegrees(i * h * 30.0F));
   }
   
   private void applyEquipOffset(MatrixStack matrices, int i, float equipProgress) {
     matrices.method_46416(i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
   }
   
   private void applySwingOffset(MatrixStack matrices, int i, float swingProgress, float strength) {
     float f = MathHelper.method_15374(swingProgress * swingProgress * 3.1415927F);
     matrices.method_46416(0.56F * i, -0.52F, -0.72F);
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * (45.0F + f * -20.0F * strength)));
     float g = MathHelper.method_15374(MathHelper.method_15355(swingProgress) * 3.1415927F);
     matrices.method_22907(RotationAxis.field_40718.rotationDegrees(i * g * -20.0F * strength));
     matrices.method_22907(RotationAxis.field_40714.rotationDegrees(g * -80.0F * strength));
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * -45.0F));
   }
   
   private void callSwingArm(HeldItemRenderer instance, float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm) {
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

