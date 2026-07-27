package shame.nazuna.client.modules.impl.render;
 import java.util.Objects;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class SwingAnimations extends Module {
   public static SwingAnimations INSTANCE = new SwingAnimations();
   
   public boolean swimmingAnimation = true;
   
   public boolean climbAndCrawl = true;
   public boolean mb3DCompat = false;
   public final BooleanSetting hmiEnable = new BooleanSetting("Мод на красивые руки", false);
 
   
   public final ModeSetting hmiAnimationType;
 
   
   public final FloatSetting hmiSmoothness;
 
   
   public final BooleanSetting swingEnabled;
 
   
   public final ModeSetting swingType;
 
   
   public final FloatSetting swingStrength;
 
   
   public final FloatSetting corner;
 
   
   public final FloatSetting slant;
 
   
   public final BooleanSetting smoothEnabled;
 
   
   public final FloatSetting slowAnimationSpeed;
 
   
   public final BooleanSetting auraTargetOnly;
 
   
   public final BooleanSetting swapHands;
   
   public final BooleanSetting eatAnim;
 
   
   public SwingAnimations() {
     super("SwingAnimations", "Кастомная анимация аттаки", Module.ModuleCategory.RENDER); Objects.requireNonNull(this.hmiEnable); this.hmiAnimationType = (new ModeSetting("Вид анимации", "Классик", new String[] { "Классик", "Шарп" })).visible(this.hmiEnable::isState); Objects.requireNonNull(this.hmiEnable); this.hmiSmoothness = (new FloatSetting("Плавность анимации", 1.0F, 0.35F, 2.5F, 0.05F)).visible(this.hmiEnable::isState); this.swingEnabled = (new BooleanSetting("Анимация свинга", true)).visible(() -> Boolean.valueOf(!this.hmiEnable.isState())); this.swingType = (new ModeSetting("Тип свинга", "Smooth", new String[] { "Smooth", "Static", "Down", "DropDown", "Poke", "SelfBack", "Feast", "ToBack", "Block", "Akrien", 
           "Break", "Pander", "Slant" })).visible(() -> Boolean.valueOf((!this.hmiEnable.isState() && this.swingEnabled.isState()))); this.swingStrength = (new FloatSetting("Сила анимации", 1.0F, 0.1F, 3.0F, 0.01F)).visible(() -> Boolean.valueOf((!this.hmiEnable.isState() && this.swingEnabled.isState() && !this.swingType.is("Pander")))); this.corner = (new FloatSetting("Угол DropDown", 12.0F, 1.0F, 360.0F, 1.0F)).visible(() -> Boolean.valueOf((!this.hmiEnable.isState() && this.swingEnabled.isState() && this.swingType.is("DropDown")))); this.slant = (new FloatSetting("Наклон DropDown", 12.0F, 1.0F, 360.0F, 1.0F)).visible(() -> Boolean.valueOf((!this.hmiEnable.isState() && this.swingEnabled.isState() && this.swingType.is("DropDown")))); this.smoothEnabled = (new BooleanSetting("Плавная анимация", false)).visible(() -> Boolean.valueOf(!this.hmiEnable.isState())); this.slowAnimationSpeed = (new FloatSetting("Скорость анимации", 12.0F, 1.0F, 50.0F, 1.0F)).visible(() -> Boolean.valueOf((!this.hmiEnable.isState() && this.smoothEnabled.isState()))); this.auraTargetOnly = (new BooleanSetting("Только при Aura", false)).visible(() -> Boolean.valueOf(!this.hmiEnable.isState())); this.swapHands = (new BooleanSetting("Свап рук", false)).visible(() -> Boolean.valueOf(!this.hmiEnable.isState())); this.eatAnim = (new BooleanSetting("Анимация еды", false)).visible(() -> Boolean.valueOf(!this.hmiEnable.isState())); addSettings(new Setting[] { (Setting)this.hmiEnable, (Setting)this.hmiAnimationType, (Setting)this.hmiSmoothness, (Setting)this.swingEnabled, (Setting)this.swingType, (Setting)this.swingStrength, (Setting)this.corner, (Setting)this.slant, (Setting)this.smoothEnabled, (Setting)this.slowAnimationSpeed, (Setting)this.auraTargetOnly, (Setting)this.swapHands, (Setting)this.eatAnim });
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\SwingAnimations.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */