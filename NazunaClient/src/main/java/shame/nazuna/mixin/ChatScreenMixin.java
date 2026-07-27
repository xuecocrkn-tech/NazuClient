package shame.nazuna.mixin;
 
 import net.minecraft.class_1041;
 import net.minecraft.class_310;
 import net.minecraft.class_332;
 import net.minecraft.class_408;
 import org.lwjgl.glfw.GLFW;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Unique;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.storages.implement.DragStorage;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.astra;
 
 @Mixin({class_408.class})
 public class ChatScreenMixin
 {
   @Unique
   private boolean astra$leftPressed;
   
   @Inject(method = {"method_25402"}, at = {@At("HEAD")}, cancellable = true)
   private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
     for (Draggable draggable : DragStorage.draggables.values()) {
       if (draggable.getModule().isEnable() && draggable.onClick(mouseX, mouseY, button)) {
         cir.setReturnValue(Boolean.valueOf(true));
         return;
       } 
     } 
   }
   
   @Inject(method = {"method_25394"}, at = {@At("HEAD")})
   private void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
     class_310 mc = class_310.method_1551();
     class_1041 window = mc.method_22683();
     
     boolean leftPressed = (GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) == 1);
     if (this.astra$leftPressed && !leftPressed) {
       for (Draggable draggable : DragStorage.draggables.values()) {
         draggable.onRelease(0);
       }
     }
     this.astra$leftPressed = leftPressed;
     
     for (Draggable draggable : DragStorage.draggables.values()) {
       if (draggable.getModule().isEnable()) {
         draggable.onDraw(mouseX, mouseY, window, context.method_51448());
       }
     } 
   }
 
   
   @Inject(method = {"method_25432"}, at = {@At("HEAD")})
   private void onRemoved(CallbackInfo ci) {
     this.astra$leftPressed = false;
     for (Draggable draggable : DragStorage.draggables.values()) {
       draggable.onRelease(0);
     }
     try {
       astra.INSTANCE.configStorage.saveConfig(astra.INSTANCE.configStorage.currentConfig);
     } catch (Exception e) {
       e.printStackTrace();
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\ChatScreenMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */