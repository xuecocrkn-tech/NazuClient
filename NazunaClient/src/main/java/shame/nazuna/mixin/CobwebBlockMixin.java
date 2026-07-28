package shame.nazuna.mixin;
 
 import net.minecraft.Entity;
 import net.minecraft.World;
 import net.minecraft.BlockPos;
 import net.minecraft.CobwebBlock;
 import net.minecraft.BlockState;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 
 @Mixin({CobwebBlock.class})
 public class CobwebBlockMixin {
   @Inject(method = {"method_9548"}, at = {@At("HEAD")}, cancellable = true)
   public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
     if (ModuleClass.noWeb.isEnable()) if (ModuleClass.noWeb.web.is("Коллизия")) ci.cancel();  
   }
 }

