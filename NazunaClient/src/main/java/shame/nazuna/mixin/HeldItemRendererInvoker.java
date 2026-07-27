package shame.nazuna.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_759.class})
public interface HeldItemRendererInvoker {
  @Accessor("field_4047")
  class_1799 whylol$getMainHand();
  
  @Accessor("field_4048")
  class_1799 whylol$getOffHand();
  
  @Invoker("method_3228")
  void whylol$callRenderFirstPersonItem(class_742 paramclass_742, float paramFloat1, float paramFloat2, class_1268 paramclass_1268, float paramFloat3, class_1799 paramclass_1799, float paramFloat4, class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt);
  
  @Invoker("method_3224")
  void whylol$applyEquipOffset(class_4587 paramclass_4587, class_1306 paramclass_1306, float paramFloat);
  
  @Invoker("method_65816")
  void whylol$callSwingArm(float paramFloat1, float paramFloat2, class_4587 paramclass_4587, int paramInt, class_1306 paramclass_1306);
  
  @Invoker("method_3219")
  void whylol$renderArmHoldingItem(class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, class_1306 paramclass_1306);
  
  @Invoker("method_3231")
  void whylol$renderMapInBothHands(class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3);
  
  @Invoker("method_3222")
  void whylol$renderMapInOneHand(class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt, float paramFloat1, class_1306 paramclass_1306, float paramFloat2, class_1799 paramclass_1799);
  
  @Invoker("method_3233")
  void whylol$renderItem(class_1309 paramclass_1309, class_1799 paramclass_1799, class_811 paramclass_811, boolean paramBoolean, class_4587 paramclass_4587, class_4597 paramclass_4597, int paramInt);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\HeldItemRendererInvoker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */