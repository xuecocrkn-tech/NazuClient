package shame.nazuna.mixin;

import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_4184.class})
public interface ICameraMixin {
  @Invoker("method_19325")
  void setCustomRotation(float paramFloat1, float paramFloat2);
  
  @Invoker("method_19318")
  float setClipToSpace(float paramFloat);
  
  @Invoker("method_19324")
  void setCustomMoveBy(float paramFloat1, float paramFloat2, float paramFloat3);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ICameraMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */