package shame.nazuna.mixin;

import net.minecraft.Hand;
import net.minecraft.Arm;
import net.minecraft.LivingEntity;
import net.minecraft.ItemStack;
import net.minecraft.MinecraftClient;
import net.minecraft.MatrixStack;
import net.minecraft.VertexConsumerProvider;
import net.minecraft.AbstractClientPlayerEntity;
import net.minecraft.HeldItemRenderer;
import net.minecraft.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({HeldItemRenderer.class})
public interface HeldItemRendererAccessor {
  @Accessor("field_4047")
  ItemStack getMainHand();
  
  @Accessor("field_4048")
  ItemStack getOffHand();
  
  @Accessor("field_4043")
  float getEquipProgressMainHand();
  
  @Accessor("field_4053")
  float getPrevEquipProgressMainHand();
  
  @Accessor("field_4052")
  float getEquipProgressOffHand();
  
  @Accessor("field_4051")
  float getPrevEquipProgressOffHand();
  
  @Accessor("field_4050")
  MinecraftClient getClient();
  
  @Invoker("method_3228")
  void invokeRenderFirstPersonItem(AbstractClientPlayerEntity paramclass_742, float paramFloat1, float paramFloat2, Hand paramclass_1268, float paramFloat3, ItemStack paramclass_1799, float paramFloat4, MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt);
  
  @Invoker("method_3224")
  void invokeApplyEquipOffset(MatrixStack paramclass_4587, Arm paramclass_1306, float paramFloat);
  
  @Invoker("method_65816")
  void invokeSwingArm(float paramFloat1, float paramFloat2, MatrixStack paramclass_4587, int paramInt, Arm paramclass_1306);
  
  @Invoker("method_3219")
  void invokeRenderArmHoldingItem(MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, Arm paramclass_1306);
  
  @Invoker("method_3231")
  void invokeRenderMapInBothHands(MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3);
  
  @Invoker("method_3222")
  void invokeRenderMapInOneHand(MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt, float paramFloat1, Arm paramclass_1306, float paramFloat2, ItemStack paramclass_1799);
  
  @Invoker("method_3217")
  void invokeApplySwingOffset(MatrixStack paramclass_4587, Arm paramclass_1306, float paramFloat);
  
  @Invoker("method_3233")
  void invokeRenderItem(LivingEntity paramclass_1309, ItemStack paramclass_1799, ModelTransformationMode paramclass_811, boolean paramBoolean, MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt);
}

