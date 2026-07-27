package shame.nazuna.mixin;
 
 import net.minecraft.BlockView;
 import net.minecraft.BlockPos;
 import net.minecraft.VoxelShapes;
 import net.minecraft.VoxelShape;
 import net.minecraft.BlockState;
 import net.minecraft.ShapeContext;
 import net.minecraft.AbstractBlock;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.events.implement.EventBlockCollide;
 
 @Mixin({AbstractBlock.class})
 public class AbstractBlockMixin
 {
   @Inject(method = {"method_9530"}, at = {@At("HEAD")}, cancellable = true)
   public void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
     EventBlockCollide eventBlockCollide = new EventBlockCollide(pos);
     eventBlockCollide.call();
     if (eventBlockCollide.isCancelled()) cir.setReturnValue(VoxelShapes.method_1073()); 
   }
   
   @Inject(method = {"method_9549"}, at = {@At("HEAD")}, cancellable = true)
   public void getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
     EventBlockCollide eventBlockCollide = new EventBlockCollide(pos);
     eventBlockCollide.call();
     if (eventBlockCollide.isCancelled()) cir.setReturnValue(VoxelShapes.method_1073()); 
   }
   
   @Inject(method = {"method_9584"}, at = {@At("HEAD")}, cancellable = true)
   public void getRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
     EventBlockCollide eventBlockCollide = new EventBlockCollide(pos);
     eventBlockCollide.call();
     if (eventBlockCollide.isCancelled()) cir.setReturnValue(VoxelShapes.method_1073()); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\AbstractBlockMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */