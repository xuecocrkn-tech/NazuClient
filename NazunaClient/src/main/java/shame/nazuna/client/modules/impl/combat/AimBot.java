package shame.nazuna.client.modules.impl.combat;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ItemStack;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.RotationAxis;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventGameUpdate;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 
 public class AimBot
   extends Module
 {
   public static AimBot INSTANCE = new AimBot();
   
   private final ListSetting targetTypes = new ListSetting("Типы целей", new BooleanSetting[] { new BooleanSetting("Игроки", true), new BooleanSetting("В броне", true), new BooleanSetting("Без брони", false), new BooleanSetting("Мобы", false), new BooleanSetting("Зомби", false) });
 
 
 
 
 
   
   private final FloatSetting range = new FloatSetting("Дистанция", 40.0F, 10.0F, 100.0F, 1.0F);
   private final FloatSetting aimTime = new FloatSetting("Время наводки (тики)", 10.0F, 0.0F, 40.0F, 1.0F);
   private final BooleanSetting silentRotations = new BooleanSetting("Тихие повороты", true);
   private final BooleanSetting showCrosshair = new BooleanSetting("Показать прицел", true);
   private final FloatSetting crosshairSize = new FloatSetting("Размер прицела", 1.0F, 0.3F, 3.0F, 0.1F);
   
   private LivingEntity target = null;
   private boolean isAiming = false;
   private float aimProgress = 0.0F;
   private Rotation targetRotation = null;
   
   public AimBot() {
     super("AimBot", "Авто-наведение для лука и арбалета", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.targetTypes, (Setting)this.range, (Setting)this.aimTime, (Setting)this.silentRotations, (Setting)this.showCrosshair, (Setting)this.crosshairSize });
   }
   
   private Identifier getCrosshairTexture() {
     return Identifier.method_60655("astra", "textures/cross/hit.png");
   }
   
   private boolean isHoldingBowOrCrossbow() {
     ItemStack mainHand = mc.field_1724.method_6047();
     ItemStack offHand = mc.field_1724.method_6079();
     return (mainHand.method_7909() instanceof net.minecraft.BowItem || mainHand
       .method_7909() instanceof net.minecraft.CrossbowItem || offHand
       .method_7909() instanceof net.minecraft.BowItem || offHand
       .method_7909() instanceof net.minecraft.CrossbowItem);
   }
   
   private boolean isUsingBowOrCrossbow() {
     return (mc.field_1724.method_6115() && isHoldingBowOrCrossbow());
   }
   
   private boolean isValidTarget(LivingEntity entity) {
     if (entity == mc.field_1724) return false; 
     if (!entity.method_5805() || entity.method_6032() <= 0.0F) return false;
     
     if (entity instanceof PlayerEntity) {
       if (!this.targetTypes.is("Игроки")) return false; 
       if (astra.INSTANCE.friendStorage.isFriend(entity.method_5477().getString())) return false;
       
       boolean hasArmor = false;
       PlayerEntity player = (PlayerEntity)entity;
       for (ItemStack armor : player.method_5661()) {
         if (!armor.method_7960()) {
           hasArmor = true;
           
           break;
         } 
       } 
       if (this.targetTypes.is("В броне") && hasArmor) return true; 
       if (this.targetTypes.is("Без брони") && !hasArmor) return true; 
       if (!this.targetTypes.is("В броне") && !this.targetTypes.is("Без брони")) return true;
       
       return false;
     } 
     
     if (entity instanceof net.minecraft.ZombieEntity) {
       return this.targetTypes.is("Зомби");
     }
     
     if (entity instanceof net.minecraft.HostileEntity) {
       return this.targetTypes.is("Мобы");
     }
     
     return false;
   }
   
   private LivingEntity findBestTarget() {
     List<LivingEntity> targets = new ArrayList<>();
     
     Box searchBox = mc.field_1724.method_5829().method_1014(this.range.getValue().floatValue());
     
     for (LivingEntity entity : mc.field_1687.method_8390(LivingEntity.class, searchBox, e -> true)) {
       if (!isValidTarget(entity))
         continue; 
       double dist = mc.field_1724.method_5739((Entity)entity);
       if (dist > this.range.getValue().floatValue())
         continue; 
       targets.add(entity);
     } 
     
     if (targets.isEmpty()) return null;
     
     targets.sort(Comparator.comparingDouble(entity -> mc.field_1724.method_5739((Entity)entity)));
     return targets.get(0);
   }
 
   
   private Rotation calculateBowRotation(LivingEntity target) {
     Vec3d eyes = mc.field_1724.method_33571();
     Vec3d targetPos = target.method_5829().method_1005();
     
     double dx = targetPos.field_1352 - eyes.field_1352;
     double dy = targetPos.field_1351 - eyes.field_1351;
     double dz = targetPos.field_1350 - eyes.field_1350;
     
     double distance = Math.sqrt(dx * dx + dz * dz);
     
     float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
     float pitch = (float)-Math.toDegrees(Math.atan2(dy, distance));
     
     return new Rotation(yaw, pitch);
   }
   
   @EventLink
   public void onRender3D(Event3DRender event) {
     if (!this.showCrosshair.isState() || this.target == null || !this.isAiming)
       return; 
     float partialTicks = event.getTickDelta();
 
 
     
     Vec3d targetPos = new Vec3d(MathHelper.method_16436(partialTicks, this.target.field_6038, this.target.method_23317()), MathHelper.method_16436(partialTicks, this.target.field_5971, this.target.method_23318()) + this.target.method_17682() / 2.0D, MathHelper.method_16436(partialTicks, this.target.field_5989, this.target.method_23321()));
 
     
     Vec3d cameraPos = mc.field_1773.method_19418().method_19326();
     MatrixStack matrices = event.getMatrices();
     
     double renderX = targetPos.field_1352 - cameraPos.field_1352;
     double renderY = targetPos.field_1351 - cameraPos.field_1351;
     double renderZ = targetPos.field_1350 - cameraPos.field_1350;
     
     RenderSystem.enableBlend();
     RenderSystem.blendFunc(770, 1);
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     RenderSystem.setShaderTexture(0, getCrosshairTexture());
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     
     matrices.method_22903();
     matrices.method_22904(renderX, renderY, renderZ);
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-mc.field_1773.method_19418().method_19330()));
     matrices.method_22907(RotationAxis.field_40714.rotationDegrees(mc.field_1773.method_19418().method_19329()));
     
     float size = this.crosshairSize.get() * 0.5F;
     int alpha = (int)(255.0F * this.aimProgress);
     int color = ColorUtils.getThemeColor();
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     
     buffer.method_22918(matrix, -size, -size, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, alpha);
     buffer.method_22918(matrix, -size, size, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, alpha);
     buffer.method_22918(matrix, size, size, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, alpha);
     buffer.method_22918(matrix, size, -size, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, alpha);
     
     BufferRenderer.method_43433(buffer.method_60800());
     
     matrices.method_22909();
     
     RenderSystem.enableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(true);
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   @EventLink
   public void onGameUpdate(EventGameUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     this.isAiming = isUsingBowOrCrossbow();
     
     if (this.isAiming) {
       LivingEntity newTarget = findBestTarget();
       
       if (newTarget != null) {
         if (this.target != newTarget) {
           this.target = newTarget;
           this.aimProgress = 0.0F;
         } 
         
         Rotation newRotation = calculateBowRotation(this.target);
         
         float maxStep = 1.0F / Math.max(1.0F, this.aimTime.getValue().floatValue());
         this.aimProgress = Math.min(this.aimProgress + maxStep, 1.0F);
         
         float currentYaw = mc.field_1724.method_36454();
         float currentPitch = mc.field_1724.method_36455();
         float targetYaw = newRotation.getYaw();
         float targetPitch = newRotation.getPitch();
         
         float yawDiff = MathHelper.method_15393(targetYaw - currentYaw);
         float pitchDiff = targetPitch - currentPitch;
         
         float stepYaw = yawDiff * this.aimProgress;
         float stepPitch = pitchDiff * this.aimProgress;
         
         this.targetRotation = new Rotation(currentYaw + stepYaw, currentPitch + stepPitch);
       }
     
     }
     else {
       
       this.target = null;
       this.targetRotation = null;
       this.aimProgress = 0.0F;
     } 
   }
 
   
   @EventLink
   public void onUpdate(EventGameUpdate ignoredghj) {
     if (this.target != null && this.isAiming && this.targetRotation != null) {
       if (this.silentRotations.isState()) {
         float gcd = GCDUtil.getGCD();
         float yaw = this.targetRotation.getYaw();
         float pitch = this.targetRotation.getPitch();
         
         yaw -= (yaw - mc.field_1724.method_36454()) % gcd;
         pitch -= (pitch - mc.field_1724.method_36455()) % gcd;
         
         RotationStorage.update(new Rotation(yaw, pitch), 180.0F, 180.0F, 45.0F, 45.0F, 0, 2, false);
       } else {
         mc.field_1724.method_36456(this.targetRotation.getYaw());
         mc.field_1724.method_36457(this.targetRotation.getPitch());
       } 
     }
   }
   
   public LivingEntity getTarget() {
     return this.target;
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.target = null;
     this.isAiming = false;
     this.aimProgress = 0.0F;
     this.targetRotation = null;
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.target = null;
     this.isAiming = false;
     this.aimProgress = 0.0F;
     this.targetRotation = null;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\AimBot.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */