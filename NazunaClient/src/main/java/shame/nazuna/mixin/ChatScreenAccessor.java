package shame.nazuna.mixin;

import net.minecraft.TextFieldWidget;
import net.minecraft.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ChatScreen.class})
public interface ChatScreenAccessor {
  @Accessor("field_2382")
  TextFieldWidget astra$getChatField();
}

