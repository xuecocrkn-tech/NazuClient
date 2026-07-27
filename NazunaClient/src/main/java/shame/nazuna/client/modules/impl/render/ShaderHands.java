package shame.nazuna.client.modules.impl.render;
 
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.render.hands.ShaderHandsRenderer;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class ShaderHands
   extends Module {
   public static ShaderHands INSTANCE = new ShaderHands();
   private static final ShaderHandsRenderer RENDERER = ShaderHandsRenderer.getInstance();
   public final ModeSetting mode = new ModeSetting("Режим", "Свечение", new String[] { "Свечение", "Красивый" });
   
   public final FloatSetting waveSpeed = (new FloatSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.mode.is("Красивый")));
   public final FloatSetting waveScale = (new FloatSetting("Частота волн", 1.0F, 1.0F, 3.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.mode.is("Красивый")));
   
   public final FloatSetting outline = new FloatSetting("Ширина обводки", 1.2F, 0.1F, 5.0F, 0.1F);
   public final FloatSetting glow = new FloatSetting("Сила свечения", 1.0F, 0.0F, 5.0F, 0.1F);
   public final FloatSetting fill = new FloatSetting("Заливка", 0.6F, 0.0F, 1.0F, 0.01F);
   public final FloatSetting alpha = new FloatSetting("Прозрачность", 1.0F, 0.0F, 1.0F, 0.05F);
   
   public ShaderHands() {
     super("ShaderHands", "Красивый Шейдер на руки и предметы", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.waveSpeed, (Setting)this.waveScale, (Setting)this.outline, (Setting)this.glow, (Setting)this.fill, (Setting)this.alpha });
   }
 
   
   public void onDisable() {
     RENDERER.invalidateState();
     super.onDisable();
   }
   
   @EventLink(priority = 0)
   public void onRender2D(EventRender.Default event) {
     if (!isEnable())
       return;  RENDERER.renderOverlayIfPending();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\ShaderHands.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */