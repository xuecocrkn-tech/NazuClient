package shame.nazuna.client.modules.impl.combat;
 
 import net.minecraft.Hand;
 import net.minecraft.StatusEffects;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ProjectileUtil;
 import net.minecraft.Vec3d;
 import net.minecraft.EntityHitResult;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventMoveInput;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.combat.IdealHitUtils;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.movement.Sprint;
 import shame.nazuna.client.modules.impl.player.AutoEat;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 
 
 
 
 public class TriggerBot
   extends Module
 {
   public static TriggerBot INSTANCE = new TriggerBot();
   
   private final FloatSetting range = new FloatSetting("Дистанция атаки", 3.0F, 0.0F, 6.0F, 0.05F);
   
   private final ListSetting options = new ListSetting("Опции", new BooleanSetting[] { new BooleanSetting("Умные криты", true), new BooleanSetting("Сброс спринта", true), new BooleanSetting("Бить через стены", false), new BooleanSetting("Проверка на наведение", true), new BooleanSetting("Отжимать щит", false), new BooleanSetting("Ломать щит", true) });
 
 
 
 
 
 
 
   
   private final ListSetting targets = new ListSetting("Таргеты", new BooleanSetting[] { new BooleanSetting("Игроки", true), new BooleanSetting("Невидимки", true), new BooleanSetting("Мирные", false), new BooleanSetting("Мобы", true) });
   
   private LivingEntity target;
 
   
   public LivingEntity getTarget() {
     return this.target;
   }
   
   private final TimerUtils attackTimer = new TimerUtils();
   
   private boolean needSprintReset = false;
   private boolean sprintResetDone = false;
   private int sprintResetTicks = 0;
   
   public TriggerBot() {
     super("TriggerBot", "Автоматически атакует при наведении на цель", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.range, (Setting)this.options, (Setting)this.targets });
   }
   
   @EventLink
   public void onMoveInput(EventMoveInput event) {
     if (this.needSprintReset) {
       event.setForward(0.0F);
       event.setStrafe(0.0F);
       this.needSprintReset = false;
       this.sprintResetDone = true;
       this.sprintResetTicks = 0;
     } 
   }
 
   
   @EventLink
   public void onUpdate(EventUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (AutoEat.shouldSuppressCombat()) {
       this.target = null;
       resetSprintState();
       
       return;
     } 
     if (this.sprintResetDone) {
       this.sprintResetTicks++;
     }
     
     this.target = getTargetUnderCrosshair();
     
     if (this.target != null) {
       processAttack();
     } else {
       resetSprintState();
     } 
   }
   
   private void processAttack() {
     if (!shouldAttack())
       return; 
     if (this.options.is("Сброс спринта") && mc.field_1724.method_5624() && !this.sprintResetDone && !shouldSkipSprintResetInWater()) {
       this.needSprintReset = true;
       
       return;
     } 
     if (this.options.is("Сброс спринта") && this.sprintResetDone && this.sprintResetTicks < 1) {
       return;
     }
     
     attack();
     this.sprintResetDone = false;
     this.sprintResetTicks = 0;
   }
 
   
   private LivingEntity getTargetUnderCrosshair() {
     Vec3d eyePos = mc.field_1724.method_5836(1.0F);
     Vec3d lookVec = mc.field_1724.method_5828(1.0F);
     float rangeValue = this.range.getValue().floatValue();
     Vec3d reachVec = eyePos.method_1019(lookVec.method_1021(rangeValue));
     
     EntityHitResult result = ProjectileUtil.method_18075((Entity)mc.field_1724, eyePos, reachVec, mc.field_1724
 
 
         
         .method_5829().method_1014(rangeValue), entity -> 
         (entity != mc.field_1724 && entity.method_5805() && entity instanceof LivingEntity), (rangeValue * rangeValue));
 
 
     
     if (result != null) { Entity Entity = result.method_17782(); if (Entity instanceof LivingEntity) { LivingEntity living = (LivingEntity)Entity;
         if (isValidTarget(living)) {
           return living;
         } }
        }
     
     return null;
   }
 
 
 
 
 
 
 
   
   private void attack() {
}
}
