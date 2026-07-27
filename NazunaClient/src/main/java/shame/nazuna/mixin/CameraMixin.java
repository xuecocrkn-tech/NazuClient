package shame.nazuna.mixin;
 
 import java.lang.reflect.InvocationTargetException;
 import net.minecraft.Entity;
 import net.minecraft.BlockView;
 import net.minecraft.Camera;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Redirect;
 import shame.nazuna.api.events.Event;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventRotation;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.InterpolateF5;
 
 
 
 
 
 
 
 @Mixin({Camera.class})
 public abstract class CameraMixin
 {
   @Redirect(method = {"method_19321"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;method_19325(FF)V"))
   private void redirectSetRotation(Camera instance, float yaw, float pitch, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     EventRotation event = new EventRotation(yaw, pitch, tickDelta);
     EventInvoker.invoke((Event)event);
     
     float newYaw = event.getYaw();
     float newPitch = event.getPitch();
     
     if (thirdPerson && inverseView) {
       newYaw += 180.0F;
       newPitch = -newPitch;
     } 
     
     ((ICameraMixin)instance).setCustomRotation(newYaw, newPitch);
   }
 
 
 
 
 
 
   
   @Redirect(method = {"method_19321"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;method_19318(F)F"))
   private float redirectClipToSpace(Camera instance, float distance, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
     if (!thirdPerson) {
       return ((ICameraMixin)instance).setClipToSpace(distance);
     }
     
     InterpolateF5 module = (ModuleClass.INSTANCE != null) ? ModuleClass.interpolateF5 : null;
     if (module != null && module.isEnable()) {
       return ((ICameraMixin)instance).setClipToSpace(module.getInterpolatedDistance(tickDelta));
     }
     
     return ((ICameraMixin)instance).setClipToSpace(distance);
   }
 
 
 
 
 
 
   
   @Redirect(method = {"method_19321"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;method_19324(FFF)V"))
   private void redirectMoveBy(Camera instance, float x, float y, float z, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
     float newY = y;
     
     if (thirdPerson) {
       InterpolateF5 module = (ModuleClass.INSTANCE != null) ? ModuleClass.interpolateF5 : null;
       if (module != null && module.isEnable()) {
         newY += module.getInterpolatedHeightOffset(tickDelta);
       }
     } 
     
     ((ICameraMixin)instance).setCustomMoveBy(x, newY, z);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\CameraMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */