package shame.nazuna.mixin;
 
 import net.minecraft.class_10209;
 import net.minecraft.class_243;
 import net.minecraft.class_4063;
 import net.minecraft.class_4184;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_757;
 import net.minecraft.class_761;
 import net.minecraft.class_9779;
 import net.minecraft.class_9909;
 import net.minecraft.class_9922;
 import net.minecraft.class_9958;
 import org.joml.Matrix4f;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.render.Removals;
 import shame.nazuna.client.modules.impl.render.ShaderEsp;
 import shame.nazuna.client.modules.impl.render.Sonar;
 
 @Mixin({class_761.class})
 public class WorldRendererMixin
   implements QClient {
   @Inject(method = {"method_62201"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderParticles(class_9909 frameGraphBuilder, class_4184 camera, float tickDelta, class_9958 fog, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Частицы")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62203"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderWeather(class_9909 frameGraphBuilder, class_243 pos, float tickDelta, class_9958 fog, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Погода")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62209"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$addWeatherParticlesAndSound(class_4184 camera, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Погода")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62204"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderClouds(class_9909 frameGraphBuilder, Matrix4f positionMatrix, Matrix4f projectionMatrix, class_4063 renderMode, class_243 cameraPos, float ticks, int color, float cloudHeight, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Облака")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62208"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderBlockEntities(class_4587 matrices, class_4597.class_4598 mainConsumers, class_4597.class_4598 translucentConsumers, class_4184 camera, float tickDelta, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Блок-сущности")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_22710"}, at = {@At("RETURN")})
   private void render(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, class_757 gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
     Sonar sonar = (ModuleClass.INSTANCE != null) ? ModuleClass.sonar : null;
     boolean has3DListeners = EventInvoker.hasListeners(Event3DRender.class);
     boolean renderSonar = (sonar != null && sonar.isEnable());
     if (!has3DListeners && !renderSonar) {
       return;
     }
     
     class_10209.method_64146().method_15405("astra_renderWorld");
     class_4587 matrices = new class_4587();
     matrices.method_34425(positionMatrix);
     if (has3DListeners) {
       (new Event3DRender(matrices, positionMatrix, projectionMatrix, camera, tickCounter.method_60637(false))).call();
     }
     if (renderSonar) {
       sonar.renderFromMixin(positionMatrix, projectionMatrix, camera.method_19326());
     }
   }
   
   @Inject(method = {"method_3254"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$drawEntityOutlinesFramebuffer(CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return;  ShaderEsp shaderEsp = ModuleClass.shaderEsp;
     if (shaderEsp != null && shaderEsp.isEnable()) {
       ci.cancel();
       return;
     } 
   }
 
   
   @Inject(method = {"method_22712"}, at = {@At("HEAD")}, cancellable = true)
   public void onDrawBlockOutline(CallbackInfo ci) {
     if (ModuleClass.blockOverlay.isEnable()) ci.cancel(); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\WorldRendererMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */