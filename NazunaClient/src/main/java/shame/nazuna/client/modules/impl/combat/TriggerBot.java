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
     // Byte code:
     //   0: aload_0
     //   1: getfield options : Lshame/astra/client/modules/settings/implement/ListSetting;
     //   4: ldc 'Отжимать щит'
     //   6: invokevirtual is : (Ljava/lang/String;)Z
     //   9: ifeq -> 39
     //   12: getstatic shame/astra/client/modules/impl/combat/TriggerBot.mc : Lnet/minecraft/MinecraftClient;
     //   15: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   18: invokevirtual method_6039 : ()Z
     //   21: ifeq -> 39
     //   24: getstatic shame/astra/client/modules/impl/combat/TriggerBot.mc : Lnet/minecraft/MinecraftClient;
     //   27: getfield field_1761 : Lnet/minecraft/ClientPlayerInteractionManager;
     //   30: getstatic shame/astra/client/modules/impl/combat/TriggerBot.mc : Lnet/minecraft/MinecraftClient;
     //   33: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   36: invokevirtual method_2897 : (Lnet/minecraft/PlayerEntity;)V
     //   39: aload_0
     //   40: getfield target : Lnet/minecraft/LivingEntity;
     //   43: astore_2
     //   44: aload_2
     //   45: instanceof net/minecraft/PlayerEntity
     //   48: ifeq -> 83
     //   51: aload_2
     //   52: checkcast net/minecraft/PlayerEntity
     //   55: astore_1
     //   56: aload_1
     //   57: invokevirtual method_6039 : ()Z
     //   60: ifeq -> 83
     //   63: aload_0
     //   64: getfield options : Lshame/astra/client/modules/settings/implement/ListSetting;
     //   67: ldc 'Ломать щит'
     //   69: invokevirtual is : (Ljava/lang/String;)Z
     //   72: ifeq -> 83
     //   75: aload_0
     //   76: aload_1
     //   77: invokevirtual shieldBreak : (Lnet/minecraft/PlayerEntity;)V
     //   80: goto -> 102
     //   83: getstatic shame/astra/client/modules/impl/combat/TriggerBot.mc : Lnet/minecraft/MinecraftClient;
     //   86: getfield field_1761 : Lnet/minecraft/ClientPlayerInteractionManager;
     //   89: getstatic shame/astra/client/modules/impl/combat/TriggerBot.mc : Lnet/minecraft/MinecraftClient;
     //   92: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   95: aload_0
     //   96: getfield target : Lnet/minecraft/LivingEntity;
     //   99: invokevirtual method_2918 : (Lnet/minecraft/PlayerEntity;Lnet/minecraft/Entity;)V
     //   102: getstatic shame/astra/client/modules/impl/combat/TriggerBot.mc : Lnet/minecraft/MinecraftClient;
     //   105: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   108: getstatic net/minecraft/Hand.field_5808 : Lnet/minecraft/Hand;
     //   111: invokevirtual method_6104 : (Lnet/minecraft/Hand;)V
     //   114: aload_0
     //   115: getfield attackTimer : Lshame/astra/api/utils/math/TimerUtils;
     //   118: invokevirtual reset : ()V
     //   121: return
     // Line number table:
     //   Java source line number -> byte code offset
     //   #144	-> 0
     //   #145	-> 24
     //   #148	-> 39
     //   #149	-> 75
     //   #151	-> 83
     //   #154	-> 102
     //   #155	-> 114
     //   #156	-> 121
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   56	27	1	player	Lnet/minecraft/PlayerEntity;
     //   0	122	0	this	Lshame/astra/client/modules/impl/combat/TriggerBot;
   }
 
 
 
 
 
 
   
   private void shieldBreak(PlayerEntity entity) {
     int axeSlot = findAxeSlot();
     
     if (axeSlot != -1) {
       int prevSlot = (mc.field_1724.method_31548()).field_7545;
       (mc.field_1724.method_31548()).field_7545 = axeSlot;
       mc.field_1761.method_2918((PlayerEntity)mc.field_1724, (Entity)entity);
       mc.field_1724.method_6104(Hand.field_5808);
       (mc.field_1724.method_31548()).field_7545 = prevSlot;
     } else {
       mc.field_1761.method_2918((PlayerEntity)mc.field_1724, (Entity)entity);
     } 
   }
   
   private int findAxeSlot() {
     for (int i = 0; i < 9; i++) {
       if (mc.field_1724.method_31548().method_5438(i).method_7909() instanceof net.minecraft.AxeItem) {
         return i;
       }
     } 
     return -1;
   }
   
   private boolean isValidTarget(LivingEntity entity) {
     if (entity == null || entity == mc.field_1724) return false; 
     if (!entity.method_5805() || entity.method_6032() <= 0.0F) return false; 
     if (entity instanceof net.minecraft.ArmorStandEntity) return false;
     
     if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity;
       if (!this.targets.is("Игроки")) return false; 
       if (player.method_6059(StatusEffects.field_5905) && !this.targets.is("Невидимки")) return false; 
       if (NazunaClient.INSTANCE.friendStorage.isFriend(entity.method_5477().getString())) return false;  }
     else if (entity instanceof net.minecraft.PassiveEntity || entity instanceof net.minecraft.CodEntity)
     { if (!this.targets.is("Мирные")) return false;  }
     else if (entity instanceof net.minecraft.HostileEntity && 
       !this.targets.is("Мобы")) { return false; }
 
     
     if (mc.field_1724.method_33571().method_1022(entity.method_5829().method_1005()) > this.range.getValue().floatValue()) {
       return false;
     }
     
     if (!this.options.is("Бить через стены") && !mc.field_1724.method_6057((Entity)entity)) {
       return false;
     }
     
     return true;
   }
   
   private boolean shouldAttack() {
     if (mc.field_1724.method_7261(1.5F) < IdealHitUtils.getAICooldown()) {
       return false;
     }
     
     if (this.options.is("Проверка на наведение")) {
       Vec3d eyePos = mc.field_1724.method_5836(1.0F);
       Vec3d lookVec = mc.field_1724.method_5828(1.0F);
       float rangeValue = this.range.getValue().floatValue();
       Vec3d reachVec = eyePos.method_1019(lookVec.method_1021(rangeValue));
       
       EntityHitResult result = ProjectileUtil.method_18075((Entity)mc.field_1724, eyePos, reachVec, mc.field_1724
 
 
           
           .method_5829().method_1014(rangeValue), ex -> 
           (ex != mc.field_1724 && ex.method_5805()), (rangeValue * rangeValue));
 
 
       
       if (result == null || result.method_17782() != this.target) {
         return false;
       }
     } 
     
     if (this.options.is("Умные криты") && !IdealHitUtils.canCritical(this.target)) {
       return false;
     }
     
     return true;
   }
   
   private void resetSprintState() {
     this.sprintResetDone = false;
     this.sprintResetTicks = 0;
   }
   
   private boolean shouldSkipSprintResetInWater() {
     return (mc.field_1724 != null && (mc.field_1724
       .method_5799() || mc.field_1724.method_5869()) && Sprint.INSTANCE != null && Sprint.INSTANCE
       
       .shouldKeepSprintInWater());
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.target = null;
     this.needSprintReset = false;
     this.sprintResetDone = false;
     this.sprintResetTicks = 0;
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.needSprintReset = false;
     this.sprintResetDone = false;
     this.sprintResetTicks = 0;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\TriggerBot.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */