package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.PlayerEntityRenderState;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Vec3d;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.EntityPose;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumer;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.OverlayTexture;
 import net.minecraft.EntityModelLayers;
 import net.minecraft.AllayEntityModel;
 import net.minecraft.RotationAxis;
 import net.minecraft.AllayEntityRenderState;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventAttackEntity;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class Satellite
   extends Module {
   private static final Identifier ALLAY_TEXTURE = Identifier.method_60656("textures/entity/allay/allay.png");
   private static final long ATTACK_FOLLOW_TIMEOUT_MS = 3600L;
   private static final long ATTACK_LAUNCH_DURATION_MS = 560L;
   private static final long ATTACK_RETURN_DURATION_MS = 920L;
   public static Satellite INSTANCE = new Satellite();
   
   public final ModeSetting shoulder = new ModeSetting("Плечо", "Правое", new String[] { "Правое", "Левое" });
   public final FloatSetting scale = new FloatSetting("Размер", 0.38F, 0.15F, 1.25F, 0.01F);
   public final FloatSetting offsetX = new FloatSetting("Смещение X", 0.0F, -1.0F, 1.0F, 0.01F);
   public final FloatSetting offsetY = new FloatSetting("Смещение Y", 0.18F, -1.0F, 1.0F, 0.01F);
   public final FloatSetting offsetZ = new FloatSetting("Смещение Z", 0.0F, -1.0F, 1.0F, 0.01F);
   public final FloatSetting rotateX = new FloatSetting("Поворот X", 0.0F, -180.0F, 180.0F, 1.0F);
   public final FloatSetting rotateY = new FloatSetting("Поворот Y", 0.0F, -180.0F, 180.0F, 1.0F);
   public final FloatSetting rotateZ = new FloatSetting("Поворот Z", 0.0F, -180.0F, 180.0F, 1.0F);
   public final BooleanSetting showSelf = new BooleanSetting("Показывать на себе", true);
   public final BooleanSetting showOthers = new BooleanSetting("Показывать на других", true);
   public final BooleanSetting showFriends = new BooleanSetting("Показывать на друзьях", true);
   public final BooleanSetting attackEnemies = new BooleanSetting("Атаковать врагов", true);
   public final BooleanSetting idleAnimation = new BooleanSetting("Idle-анимация", true);
   public final FloatSetting idleSpeed = (new FloatSetting("Скорость idle", 1.0F, 0.1F, 3.0F, 0.05F))
     .visible(() -> Boolean.valueOf(this.idleAnimation.isState()));
   public final FloatSetting idleStrength = (new FloatSetting("Сила idle", 0.35F, 0.0F, 1.5F, 0.05F))
     .visible(() -> Boolean.valueOf(this.idleAnimation.isState()));
   
   private final AllayEntityRenderState attackState = new AllayEntityRenderState();
   private AllayEntityModel attackModel;
   private int attackTargetId = Integer.MIN_VALUE;
   private long attackStartedAt;
   private long lastAttackAt;
   private long attackReturnStartedAt;
   private Vec3d attackReturnStartPos = new Vec3d(0.0D, 0.0D, 0.0D);
   private float attackOrbitSeed;
   private float attackCurveSide;
   private float attackCurveLift;
   private float attackCurveDepth;
   private float attackRadiusJitter;
   private float attackHeightJitter;
   private float attackBobSeed;
   private float attackOrbitSpeed;
   private float attackOrbitDirection;
   private float attackLookYaw;
   private float attackLookPitch;
   private boolean attackLookInitialized;
   
   public Satellite() {
     super("Satellite", "Питомец-аллей на плече", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.shoulder, (Setting)this.scale, (Setting)this.offsetX, (Setting)this.offsetY, (Setting)this.offsetZ, (Setting)this.rotateX, (Setting)this.rotateY, (Setting)this.rotateZ, (Setting)this.showSelf, (Setting)this.showOthers, (Setting)this.showFriends, (Setting)this.attackEnemies, (Setting)this.idleAnimation, (Setting)this.idleSpeed, (Setting)this.idleStrength });
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void onDisable() {
     clearAttackTarget();
     super.onDisable();
   }
   
   @EventLink
   public void onAttack(EventAttackEntity event) {
     if (!this.attackEnemies.isState() || event == null || event.getPlayer() == null || event.getTarget() == null || mc.field_1724 == null) {
       return;
     }
     
     if (event.getPlayer().method_5628() != mc.field_1724.method_5628() || event.getTarget() == mc.field_1724) {
       return;
     }
     
     long now = System.currentTimeMillis();
     if (this.attackTargetId != event.getTarget().method_5628()) {
       this.attackStartedAt = now;
       randomizeAttackPath(now);
     } 
     
     this.attackTargetId = event.getTarget().method_5628();
     this.lastAttackAt = now;
     this.attackReturnStartedAt = 0L;
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (!this.attackEnemies.isState()) {
       clearAttackTarget();
       
       return;
     } 
     updateAttackLifecycle();
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (mc.field_1724 == null || mc.field_1687 == null || event == null) {
       return;
     }
     
     float tickDelta = event.getTickDelta();
     long now = System.currentTimeMillis();
     
     Entity target = updateAttackLifecycle();
     if (target == null) {
       return;
     }
     
     ensureAttackModel();
     if (this.attackModel == null) {
       return;
     }
     
     renderAttackSatellite(event, target, getAttackRenderPosition(target, tickDelta, now), tickDelta, now);
   }
   
   private void renderAttackSatellite(Event3DRender event, Entity target, Vec3d renderPos, float tickDelta, long now) {
     Vec3d cameraPos = event.getCamera().method_19326();
     Vec3d targetPos = getInterpolatedEntityPos(target, tickDelta);
     float elapsed = (float)(now - this.attackStartedAt) / 1000.0F;
     
     Vec3d focusPos = targetPos.method_1031(0.0D, target.method_17682() * 0.56D, 0.0D);
     float desiredYaw = getLookYaw(renderPos, focusPos);
     float desiredPitch = getLookPitch(renderPos, focusPos);
     
     if (!this.attackLookInitialized) {
       this.attackLookYaw = desiredYaw;
       this.attackLookPitch = desiredPitch;
       this.attackLookInitialized = true;
     } else {
       this.attackLookYaw = MathHelper.method_17821(0.32F, this.attackLookYaw, desiredYaw);
       this.attackLookPitch = MathHelper.method_16439(0.24F, this.attackLookPitch, desiredPitch);
     } 
     
     float headYaw = MathHelper.method_15363(MathHelper.method_15393(desiredYaw - this.attackLookYaw), -85.0F, 85.0F);
     
     MatrixStack matrices = event.getMatrices();
     matrices.method_22903();
     matrices.method_22904(renderPos.field_1352 - cameraPos.field_1352, renderPos.field_1351 - cameraPos.field_1351, renderPos.field_1350 - cameraPos.field_1350);
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(180.0F - this.attackLookYaw));
     matrices.method_22905(this.scale.get(), this.scale.get(), this.scale.get());
     matrices.method_22905(-1.0F, -1.0F, 1.0F);
     matrices.method_46416(0.0F, -1.501F, 0.0F);
     
     this.attackState.field_53328 = mc.field_1724.field_6012 + tickDelta + elapsed * 20.0F;
     this.attackState.field_53450 = elapsed * 6.4F;
     this.attackState.field_53451 = 0.72F + MathHelper.method_15374(elapsed * 7.0F + this.attackBobSeed) * 0.12F;
     this.attackState.field_53447 = headYaw;
     this.attackState.field_53448 = this.attackLookPitch;
     this.attackState.field_53333 = false;
     this.attackState.field_53461 = false;
     this.attackState.field_53462 = false;
     this.attackState.field_53456 = false;
     this.attackState.field_53457 = false;
     this.attackState.field_53458 = target.method_5799();
     this.attackState.field_53446 = this.attackLookYaw;
     this.attackState.field_53453 = 1.0F;
     this.attackState.field_53454 = 1.0F;
     LivingEntity living = (LivingEntity)target; this.attackState.field_53465 = (target instanceof LivingEntity) ? living.method_18376() : EntityPose.field_18076;
     this.attackState.field_53449 = 0.0F;
     this.attackState.field_53460 = false;
     this.attackState.field_53237 = false;
     this.attackState.field_53238 = false;
     this.attackState.field_53239 = 0.0F;
     this.attackState.field_53240 = 0.65F;
     
     this.attackModel.method_42732(this.attackState);
     VertexConsumerProvider.class_4598 immediate = mc.method_22940().method_23000();
     VertexConsumer vertexConsumer = immediate.getBuffer(this.attackModel.method_23500(ALLAY_TEXTURE));
     this.attackModel.method_60879(matrices, vertexConsumer, 15728880, OverlayTexture.field_21444);
     immediate.method_22993();
     matrices.method_22909();
   }
   
   public boolean shouldRender(PlayerEntityRenderState playerState) {
     if (!isEnable() || mc.field_1724 == null || mc.field_1687 == null || playerState == null || playerState.field_53542) {
       return false;
     }
     
     boolean self = (playerState.field_53528 == mc.field_1724.method_5628());
     if (self) {
       if (hasActiveAttackTarget()) {
         return false;
       }
       
       return shouldRenderOwnShoulderPet();
     } 
     
     Entity entity = mc.field_1687.method_8469(playerState.field_53528);
     if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity; if (astra.INSTANCE != null && astra.INSTANCE.friendStorage != null && astra.INSTANCE.friendStorage
 
         
         .isFriend(player.method_5477().getString())) {
         return this.showFriends.isState();
       } }
     
     return this.showOthers.isState();
   }
   
   public boolean isLeftShoulder() {
     return this.shoulder.is("Левое");
   }
   
   public boolean hasActiveAttackTarget() {
     return (updateAttackLifecycle() != null);
   }
   
   private boolean shouldRenderOwnShoulderPet() {
     return (this.showSelf.isState() && !mc.field_1690.method_31044().method_31034());
   }
   
   private Entity updateAttackLifecycle() {
     if (!this.attackEnemies.isState() || mc.field_1687 == null || mc.field_1724 == null || this.attackTargetId == Integer.MIN_VALUE) {
       return null;
     }
     
     Entity target = mc.field_1687.method_8469(this.attackTargetId);
     if (target == null || target.method_31481() || target == mc.field_1724) {
       clearAttackTarget();
       return null;
     } 
     
     if (target instanceof LivingEntity) { LivingEntity living = (LivingEntity)target; if (!living.method_5805()) {
         clearAttackTarget();
         return null;
       }  }
     
     if (mc.field_1724.method_5858(target) > 4096.0D) {
       clearAttackTarget();
       return null;
     } 
     
     long now = System.currentTimeMillis();
     if (this.attackReturnStartedAt == 0L && now - this.lastAttackAt > 3600L) {
       float elapsed = (float)(now - this.attackStartedAt) / 1000.0F;
       this.attackReturnStartPos = getOrbitPosition(target, getInterpolatedEntityPos(target, 1.0F), elapsed);
       this.attackReturnStartedAt = now;
     } 
     
     if (this.attackReturnStartedAt != 0L && now - this.attackReturnStartedAt > 920L) {
       clearAttackTarget();
       return null;
     } 
     
     return target;
   }
   
   private Vec3d getAttackRenderPosition(Entity target, float tickDelta, long now) {
     Vec3d shoulderPos = getShoulderWorldPosition(tickDelta);
     Vec3d targetPos = getInterpolatedEntityPos(target, tickDelta);
     float elapsed = (float)(now - this.attackStartedAt) / 1000.0F;
     
     Vec3d orbitPos = getOrbitPosition(target, targetPos, elapsed);
     if (this.attackReturnStartedAt == 0L) {
       float launchProgress = MathHelper.method_15363((float)(now - this.attackStartedAt) / 560.0F, 0.0F, 1.0F);
       if (launchProgress < 1.0F) {
         return buildLaunchCurve(shoulderPos, orbitPos, launchProgress);
       }
       return orbitPos;
     } 
     
     float returnProgress = MathHelper.method_15363((float)(now - this.attackReturnStartedAt) / 920.0F, 0.0F, 1.0F);
     return buildReturnCurve(this.attackReturnStartPos, shoulderPos, returnProgress);
   }
   
   private Vec3d getOrbitPosition(Entity target, Vec3d targetPos, float elapsed) {
     double baseRadius = Math.max(0.86D, target.method_17681() * 1.05D + 0.46D) * this.attackRadiusJitter;
     double angle = (this.attackOrbitSeed * 0.017453292F + elapsed * this.attackOrbitSpeed * this.attackOrbitDirection);
     double radiusPulse = Math.sin((elapsed * 1.25F + this.attackBobSeed * 0.45F)) * 0.07D;
     double orbitRadius = baseRadius + radiusPulse;
     double orbitX = Math.cos(angle) * orbitRadius;
     double orbitZ = Math.sin(angle) * orbitRadius;
 
 
     
     double orbitY = targetPos.field_1351 + target.method_17682() * (0.78D + this.attackHeightJitter) + Math.sin((elapsed * 2.9F + this.attackBobSeed)) * 0.2D + Math.cos((elapsed * 1.8F + this.attackBobSeed * 0.8F)) * 0.08D;
     return new Vec3d(targetPos.field_1352 + orbitX, orbitY, targetPos.field_1350 + orbitZ);
   }
   
   private Vec3d buildLaunchCurve(Vec3d start, Vec3d end, float progress) {
     float eased = easeInOut(progress);
     Vec3d direction = end.method_1020(start);
     Vec3d horizontal = new Vec3d(direction.field_1352, 0.0D, direction.field_1350);
     if (horizontal.method_1027() < 1.0E-4D) {
       horizontal = new Vec3d(0.0D, 0.0D, 1.0D);
     } else {
       horizontal = horizontal.method_1029();
     } 
     
     Vec3d sideways = (new Vec3d(horizontal.field_1350, 0.0D, -horizontal.field_1352)).method_1029();
     Vec3d lift = new Vec3d(0.0D, this.attackCurveLift, 0.0D);
     Vec3d control1 = start.method_1019(sideways.method_1021(this.attackCurveSide * 0.52D)).method_1019(lift.method_1021(0.82D));
     Vec3d control2 = end.method_1019(sideways.method_1021(-this.attackCurveSide * 0.28D)).method_1019(horizontal.method_1021(this.attackCurveDepth * 0.18D)).method_1019(lift.method_1021(0.58D));
     return cubicBezier(start, control1, control2, end, eased);
   }
   
   private Vec3d buildReturnCurve(Vec3d start, Vec3d end, float progress) {
     float eased = easeInOut(progress);
     Vec3d direction = end.method_1020(start);
     Vec3d horizontal = new Vec3d(direction.field_1352, 0.0D, direction.field_1350);
     if (horizontal.method_1027() < 1.0E-4D) {
       horizontal = new Vec3d(0.0D, 0.0D, 1.0D);
     } else {
       horizontal = horizontal.method_1029();
     } 
     
     Vec3d sideways = (new Vec3d(horizontal.field_1350, 0.0D, -horizontal.field_1352)).method_1029();
     Vec3d lift = new Vec3d(0.0D, this.attackCurveLift * 0.72D, 0.0D);
     Vec3d control1 = start.method_1019(sideways.method_1021(-this.attackCurveSide * 0.24D)).method_1019(lift.method_1021(0.62D));
     Vec3d control2 = end.method_1019(sideways.method_1021(this.attackCurveSide * 0.3D)).method_1019(horizontal.method_1021(-this.attackCurveDepth * 0.1D)).method_1019(lift.method_1021(0.22D));
     Vec3d bezier = cubicBezier(start, control1, control2, end, eased);
     return (eased > 0.985F) ? end : bezier;
   }
   
   private Vec3d getShoulderWorldPosition(float tickDelta) {
     Vec3d playerPos = getInterpolatedEntityPos((Entity)mc.field_1724, tickDelta);
     float bodyYaw = MathHelper.method_17821(tickDelta, mc.field_1724.field_6220, mc.field_1724.field_6283);
     float yawRad = bodyYaw * 0.017453292F;
     
     Vec3d forward = new Vec3d(-MathHelper.method_15374(yawRad), 0.0D, MathHelper.method_15362(yawRad));
     Vec3d right = new Vec3d(forward.field_1350, 0.0D, -forward.field_1352);
     double side = (isLeftShoulder() ? 1.0D : -1.0D) * mc.field_1724.method_17681() * 0.42D;
     double height = mc.field_1724.method_17682() - (mc.field_1724.method_5715() ? 0.38D : 0.24D);
     double back = 0.0D;
 
 
 
 
 
 
     
     Vec3d shoulderPos = playerPos.method_1031(0.0D, height, 0.0D).method_1019(right.method_1021(side)).method_1019(forward.method_1021(back)).method_1019(right.method_1021(this.offsetX.get() * 0.65D)).method_1031(0.0D, this.offsetY.get() * 0.45D, 0.0D).method_1019(forward.method_1021(this.offsetZ.get() * 0.35D));
     
     if (this.idleAnimation.isState()) {
       float time = (mc.field_1724.field_6012 + tickDelta) * (0.7F + this.idleSpeed.get() * 0.65F);
       float bob = MathHelper.method_15374(time * 0.42F) * 0.03F * this.idleStrength.get();
       shoulderPos = shoulderPos.method_1031(0.0D, bob, 0.0D);
     } 
     
     return shoulderPos;
   }
   
   private Vec3d getInterpolatedEntityPos(Entity entity, float tickDelta) {
     return new Vec3d(
         MathHelper.method_16436(tickDelta, entity.field_6014, entity.method_23317()), 
         MathHelper.method_16436(tickDelta, entity.field_6036, entity.method_23318()), 
         MathHelper.method_16436(tickDelta, entity.field_5969, entity.method_23321()));
   }
 
   
   private Vec3d cubicBezier(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, float t) {
     float inv = 1.0F - t;
     double w0 = (inv * inv * inv);
     double w1 = 3.0D * inv * inv * t;
     double w2 = 3.0D * inv * t * t;
     double w3 = (t * t * t);
     return new Vec3d(p0.field_1352 * w0 + p1.field_1352 * w1 + p2.field_1352 * w2 + p3.field_1352 * w3, p0.field_1351 * w0 + p1.field_1351 * w1 + p2.field_1351 * w2 + p3.field_1351 * w3, p0.field_1350 * w0 + p1.field_1350 * w1 + p2.field_1350 * w2 + p3.field_1350 * w3);
   }
 
 
 
 
   
   private float easeInOut(float value) {
     float clamped = MathHelper.method_15363(value, 0.0F, 1.0F);
     return clamped * clamped * clamped * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
   }
   
   private void ensureAttackModel() {
     if (this.attackModel != null || mc == null) {
       return;
     }
     
     this.attackModel = new AllayEntityModel(mc.method_31974().method_32072(EntityModelLayers.field_38455));
   }
   
   private void randomizeAttackPath(long now) {
     this.attackOrbitSeed = randomRange(0.0F, 360.0F);
     this.attackCurveSide = randomRange(-1.1F, 1.1F);
     this.attackCurveLift = randomRange(0.48F, 0.96F);
     this.attackCurveDepth = randomRange(-0.42F, 0.42F);
     this.attackRadiusJitter = randomRange(0.92F, 1.24F);
     this.attackHeightJitter = randomRange(-0.06F, 0.14F);
     this.attackBobSeed = randomRange(0.0F, 6.2831855F);
     this.attackOrbitSpeed = randomRange(1.7F, 2.45F);
     this.attackOrbitDirection = (Math.random() > 0.5D) ? 1.0F : -1.0F;
   }
   
   private float randomRange(float min, float max) {
     return min + (float)Math.random() * (max - min);
   }
   
   private float getLookYaw(Vec3d from, Vec3d to) {
     double dx = to.field_1352 - from.field_1352;
     double dz = to.field_1350 - from.field_1350;
     return (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
   }
   
   private float getLookPitch(Vec3d from, Vec3d to) {
     double dx = to.field_1352 - from.field_1352;
     double dy = to.field_1351 - from.field_1351;
     double dz = to.field_1350 - from.field_1350;
     double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
     return MathHelper.method_15363((float)-Math.toDegrees(Math.atan2(dy, horizontalDistance)), -35.0F, 35.0F);
   }
   
   private void clearAttackTarget() {
     this.attackTargetId = Integer.MIN_VALUE;
     this.attackStartedAt = 0L;
     this.lastAttackAt = 0L;
     this.attackReturnStartedAt = 0L;
     this.attackReturnStartPos = new Vec3d(0.0D, 0.0D, 0.0D);
     this.attackLookYaw = 0.0F;
     this.attackLookPitch = 0.0F;
     this.attackLookInitialized = false;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Satellite.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */