package shame.nazuna.mixin;
 
 import net.minecraft.Profilers;
 import net.minecraft.Vec3d;
 import net.minecraft.CloudRenderMode;
 import net.minecraft.Camera;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.GameRenderer;
 import net.minecraft.WorldRenderer;
 import net.minecraft.RenderTickCounter;
 import net.minecraft.FrameGraphBuilder;
 import net.minecraft.ObjectAllocator;
 import net.minecraft.Fog;
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
 
 @Mixin({WorldRenderer.class})
 public class WorldRendererMixin
   implements QClient {
   @Inject(method = {"method_62201"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderParticles(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, Fog fog, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Частицы")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62203"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderWeather(FrameGraphBuilder frameGraphBuilder, Vec3d pos, float tickDelta, Fog fog, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Погода")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62209"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$addWeatherParticlesAndSound(Camera camera, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Погода")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62204"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderClouds(FrameGraphBuilder frameGraphBuilder, Matrix4f positionMatrix, Matrix4f projectionMatrix, CloudRenderMode renderMode, Vec3d cameraPos, float ticks, int color, float cloudHeight, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Облака")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_62208"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderBlockEntities(MatrixStack matrices, VertexConsumerProvider.class_4598 mainConsumers, VertexConsumerProvider.class_4598 translucentConsumers, Camera camera, float tickDelta, CallbackInfo ci) {
     if (ModuleClass.INSTANCE == null)
       return; 
     Removals removals = ModuleClass.removals;
     if (removals != null && removals.isEnabled("Блок-сущности")) {
       ci.cancel();
     }
   }
   
   @Inject(method = {"method_22710"}, at = {@At("RETURN")})
   private void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
     Sonar sonar = (ModuleClass.INSTANCE != null) ? ModuleClass.sonar : null;
     boolean has3DListeners = EventInvoker.hasListeners(Event3DRender.class);
     boolean renderSonar = (sonar != null && sonar.isEnable());
     if (!has3DListeners && !renderSonar) {
       return;
     }
     
     Profilers.method_64146().method_15405("astra_renderWorld");
     MatrixStack matrices = new MatrixStack();
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

