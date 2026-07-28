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

