package shame.nazuna.mixin;
 
 import net.minecraft.LivingEntityRenderState;
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.EntityModel;
 import net.minecraft.LivingEntityRenderer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Unique;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Constant;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.ModifyConstant;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.SeeInvisibles;
 import shame.nazuna.client.modules.impl.render.SeeInvisiblesRenderState;
 
 
 
 @Mixin({LivingEntityRenderer.class})
 public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   implements QClient
 {
   @Inject(method = {"method_62355"}, at = {@At("TAIL")})
   private void astra$updateSeeInvisiblesState(T entity, S state, float tickDelta, CallbackInfo ci) {
     boolean shouldRenderInvisible = astra$shouldRenderInvisible(entity);
     ((SeeInvisiblesRenderState)state).astra$setSeeInvisiblesTarget(shouldRenderInvisible);
     if (shouldRenderInvisible) {
       ((LivingEntityRenderState)state).field_53333 = true;
       ((LivingEntityRenderState)state).field_53461 = false;
     } 
   }
 
 
 
   
   @ModifyConstant(method = {"method_4054"}, constant = {@Constant(intValue = 654311423)})
   private int astra$changeInvisibleAlpha(int original, S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
     return ((SeeInvisiblesRenderState)state).astra$isSeeInvisiblesTarget() ? 
       SeeInvisibles.INVISIBLE_COLOR : 
       original;
   }
   
   @Unique
   private boolean astra$shouldRenderInvisible(T entity) {
     if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity; if (ModuleClass.INSTANCE != null) {
 
 
         
         SeeInvisibles seeInvisibles = ModuleClass.seeInvisibles;
         return (seeInvisibles != null && seeInvisibles.shouldRenderInvisible(player));
       }  }
     
     return false; } @Unique
   private PlayerEntity astra$resolvePlayer(S state) {
     if (state instanceof PlayerEntityRenderState) { PlayerEntityRenderState playerState = (PlayerEntityRenderState)state; if (mc.field_1687 != null) {
 
 
         
         Entity entity = mc.field_1687.method_8469(playerState.field_53528);
         PlayerEntity player = (PlayerEntity)entity; return (entity instanceof PlayerEntity) ? player : null;
       }  }
     
     return null;
   }
 }

