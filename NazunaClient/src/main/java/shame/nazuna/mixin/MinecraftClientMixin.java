package shame.nazuna.mixin;
 
 import java.lang.reflect.InvocationTargetException;
 import net.minecraft.class_1297;
 import net.minecraft.class_156;
 import net.minecraft.class_310;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Unique;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 import shame.nazuna.api.events.Event;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventGameUpdate;
 import shame.nazuna.api.events.implement.EventTickPost;
 import shame.nazuna.api.events.implement.EventTickPre;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.baritone.BaritoneAntiStuck;
 import shame.nazuna.api.utils.player.Counter;
 import shame.nazuna.client.modules.impl.render.ShaderEsp;
 
 @Mixin({class_310.class})
 public abstract class MinecraftClientMixin
 {
   @Unique
   private long lastHookTime = class_156.method_648(); @Unique
   private int accumulatedCalls = 0;
 
   
   @Inject(method = {"method_1574"}, at = {@At("HEAD")})
   public void tick(CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     if (EventInvoker.hasListeners(EventTickPre.class)) {
       EventTickPre event = new EventTickPre();
       EventInvoker.invoke((Event)event);
     } 
     Counter.updateFPS();
   }
   
   @Inject(method = {"method_1574"}, at = {@At("RETURN")})
   public void tickEnd(CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     if (EventInvoker.hasListeners(EventTickPost.class)) {
       EventTickPost event = new EventTickPost();
       EventInvoker.invoke((Event)event);
     } 
     BaritoneAntiStuck.tick();
   }
   
   @Inject(method = {"method_1523"}, at = {@At("HEAD")})
   private void render(boolean tick, CallbackInfo ci) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     if (!EventInvoker.hasListeners(EventGameUpdate.class)) {
       this.lastHookTime = class_156.method_648();
       this.accumulatedCalls = 0;
       
       return;
     } 
     long now = class_156.method_648();
     long delta = now - this.lastHookTime;
     this.accumulatedCalls += (int)(delta / 4166666L);
     this.lastHookTime += this.accumulatedCalls * 4166666L;
     
     for (this.accumulatedCalls = Math.min(this.accumulatedCalls, 240); this.accumulatedCalls > 0; this.accumulatedCalls--) {
       EventInvoker.invoke((Event)new EventGameUpdate());
     }
   }
   
   @Inject(method = {"method_27022"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$hasOutline(class_1297 entity, CallbackInfoReturnable<Boolean> cir) {
     if (ModuleClass.INSTANCE == null)
       return; 
     ShaderEsp shaderEsp = ModuleClass.shaderEsp;
     if (shaderEsp != null && shaderEsp.shouldOutline(entity)) {
       cir.setReturnValue(Boolean.valueOf(true));
       return;
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\MinecraftClientMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */