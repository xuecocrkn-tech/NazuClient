package shame.nazuna.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_759.class})
public interface HeldItemRendererAccessor {
  @Accessor("field_4047")
  class_1799 getMainHand();
  
  @Accessor("field_4048")
  class_1799 getOffHand();
  
  @Accessor("field_4043")
  float getEquipProgressMainHand();
  
  @Accessor("field_4053")
  float getPrevEquipProgressMainHand();
  
  @Accessor("field_4052")
  float getEquipProgressOffHand();
  
  @Accessor("field_4051")
  float getPrevEquipProgressOffHand();
  
  @Accessor("field_4050")
  class_310 getClient();
  
  @Invoker("method_3228")
  void invokeRenderFirstPersonItem(class_742 paramclass_742, float paramFloat1, float paramFloat2, class_1268 paramclass_1268, float paramFloat3, class_1799 paramclass_1799, float paramFloat4, class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt);
  
  @Invoker("method_3224")
  void invokeApplyEquipOffset(class_4587 paramclass_4587, class_1306 paramclass_1306, float paramFloat);
  
  @Invoker("method_65816")
  void invokeSwingArm(float paramFloat1, float paramFloat2, class_4587 paramclass_4587, int paramInt, class_1306 paramclass_1306);
  
  @Invoker("method_3219")
  void invokeRenderArmHoldingItem(class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, class_1306 paramclass_1306);
  
  @Invoker("method_3231")
  void invokeRenderMapInBothHands(class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3);
  
  @Invoker("method_3222")
  void invokeRenderMapInOneHand(class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt, float paramFloat1, class_1306 paramclass_1306, float paramFloat2, class_1799 paramclass_1799);
  
  @Invoker("method_3217")
  void invokeApplySwingOffset(class_4587 paramclass_4587, class_1306 paramclass_1306, float paramFloat);
  
  @Invoker("method_3233")
  void invokeRenderItem(class_1309 paramclass_1309, class_1799 paramclass_1799, class_811 paramclass_811, boolean paramBoolean, class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\HeldItemRendererAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */