package shame.nazuna.mixin;

import net.minecraft.Framebuffer;
import net.minecraft.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({WorldRenderer.class})
public interface WorldRendererAccessor {
  @Accessor("field_53080")
  Framebuffer astra$getEntityOutlineFramebufferRaw();
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\WorldRendererAccessor.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */