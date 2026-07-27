package shame.nazuna.mixin;

import net.minecraft.class_342;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_408.class})
public interface ChatScreenAccessor {
  @Accessor("field_2382")
  class_342 astra$getChatField();
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ChatScreenAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */