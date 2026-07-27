package shame.nazuna.mixin;

import net.minecraft.Hand;
import net.minecraft.Arm;
import net.minecraft.LivingEntity;
import net.minecraft.ItemStack;
import net.minecraft.MatrixStack;
import net.minecraft.VertexConsumerProvider;
import net.minecraft.AbstractClientPlayerEntity;
import net.minecraft.HeldItemRenderer;
import net.minecraft.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({HeldItemRenderer.class})
public interface HeldItemRendererInvoker {
  @Accessor("field_4047")
  ItemStack whylol$getMainHand();
  
  @Accessor("field_4048")
  ItemStack whylol$getOffHand();
  
  @Invoker("method_3228")
  void whylol$callRenderFirstPersonItem(AbstractClientPlayerEntity paramclass_742, float paramFloat1, float paramFloat2, Hand paramclass_1268, float paramFloat3, ItemStack paramclass_1799, float paramFloat4, MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt);
  
  @Invoker("method_3224")
  void whylol$applyEquipOffset(MatrixStack paramclass_4587, Arm paramclass_1306, float paramFloat);
  
  @Invoker("method_65816")
  void whylol$callSwingArm(float paramFloat1, float paramFloat2, MatrixStack paramclass_4587, int paramInt, Arm paramclass_1306);
  
  @Invoker("method_3219")
  void whylol$renderArmHoldingItem(MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, Arm paramclass_1306);
  
  @Invoker("method_3231")
  void whylol$renderMapInBothHands(MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3);
  
  @Invoker("method_3222")
  void whylol$renderMapInOneHand(MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt, float paramFloat1, Arm paramclass_1306, float paramFloat2, ItemStack paramclass_1799);
  
  @Invoker("method_3233")
  void whylol$renderItem(LivingEntity paramclass_1309, ItemStack paramclass_1799, ModelTransformationMode paramclass_811, boolean paramBoolean, MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\HeldItemRendererInvoker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */