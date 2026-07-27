package shame.nazuna.mixin;
 
 import net.minecraft.class_310;
 import net.minecraft.class_312;
 import net.minecraft.class_3540;
 import org.spongepowered.asm.mixin.Final;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.events.Event;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventLook;
 import shame.nazuna.api.utils.input.KeyBoardUtils;
 
 
 
 @Mixin({class_312.class})
 public abstract class MouseMixin
 {
   @Shadow
   @Final
   private class_310 field_1779;
   @Shadow
   private double field_1789;
   
   @Inject(method = {"method_1601"}, at = {@At("HEAD")}, cancellable = false)
   private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
     try {
       if (this.field_1779.field_1724 == null)
         return; 
       int buttonId = button;
       int actionId = (action == 1) ? 1 : 0;
       KeyBoardUtils.callMouse(buttonId, actionId);
 
     
     }
     catch (Exception exception) {} } @Shadow
   private double field_1787; @Shadow
   private class_3540 field_1793; @Shadow
   private class_3540 field_1782; @Inject(method = {"method_1606"}, at = {@At("HEAD")}, cancellable = true)
   private void onUpdateMouse(double timeDelta, CallbackInfo ci) {
     try {
       double i, j;
       if (this.field_1779.field_1724 == null)
         return; 
       double sensitivity = ((Double)this.field_1779.field_1690.method_42495().method_41753()).doubleValue() * 0.6D + 0.2D;
       double scaled = sensitivity * sensitivity * sensitivity * 8.0D;
 
       
       if (this.field_1779.field_1690.field_1914) {
         i = this.field_1793.method_15429(this.field_1789 * scaled, timeDelta * scaled);
         j = this.field_1782.method_15429(this.field_1787 * scaled, timeDelta * scaled);
       } else if (this.field_1779.field_1690.method_31044().method_31034() && this.field_1779.field_1724.method_31550()) {
         this.field_1793.method_15428();
         this.field_1782.method_15428();
         i = this.field_1789 * sensitivity * sensitivity * sensitivity;
         j = this.field_1787 * sensitivity * sensitivity * sensitivity;
       } else {
         this.field_1793.method_15428();
         this.field_1782.method_15428();
         i = this.field_1789 * scaled;
         j = this.field_1787 * scaled;
       } 
       
       int invert = ((Boolean)this.field_1779.field_1690.method_42438().method_41753()).booleanValue() ? -1 : 1;
       
       EventLook event = new EventLook(i, j * invert);
       EventInvoker.invoke((Event)event);
       
       if (!event.isCancelled()) {
         this.field_1779.method_1577().method_4908(event.getYaw(), event.getPitch());
         this.field_1779.field_1724.method_5872(event.getYaw(), event.getPitch());
       } 
       
       this.field_1789 = 0.0D;
       this.field_1787 = 0.0D;
       
       ci.cancel();
     } catch (Exception exception) {}
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\MouseMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */