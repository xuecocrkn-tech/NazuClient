package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.MathHelper;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventRotation;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.FreeLookStorage;
 import shame.nazuna.client.modules.Module;
 
 public class InterpolateF5
   extends Module
 {
   public static InterpolateF5 INSTANCE = new InterpolateF5();
   
   private static final float SWITCH_ANIM_SPEED = 0.26F;
   
   private static final float DISTANCE_SPEED = 0.13F;
   
   private static final float ROTATION_SMOOTH = 0.28F;
   private static final float CAMERA_DISTANCE = 4.1F;
   private static final float SNEAK_OFFSET = 0.5F;
   private static final float JUMP_MULTIPLIER = 2.0F;
   private static final float ANIM_SPEED = 0.13F;
   private float currentDistance;
   private float prevDistance;
   private float currentYaw;
   private float prevYaw;
   private float currentPitch;
   private float prevPitch;
   private float heightOffset;
   private float prevHeightOffset;
   private boolean switchAnimating;
   private boolean wasThirdPerson;
   private boolean needsInit = true;
   
   public InterpolateF5() {
     super("Cinematic Camera", "Плавная камера от ф5", Module.ModuleCategory.RENDER);
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     boolean isThirdPerson = !mc.field_1690.method_31044().method_31034();
     
     if (isThirdPerson && !this.wasThirdPerson) initCamera(true); 
     if (!isThirdPerson && this.wasThirdPerson) {
       this.needsInit = true;
       this.switchAnimating = false;
     } 
     
     this.wasThirdPerson = isThirdPerson;
     if (isThirdPerson) updateCamera(); 
   }
   
   @EventLink(priority = 100)
   public void onRotation(EventRotation event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (mc.field_1690.method_31044().method_31034())
       return; 
     event.setYaw(getInterpolatedYaw(event.getPartialTicks()));
     event.setPitch(getInterpolatedPitch(event.getPartialTicks()));
   }
   
   private void initCamera(boolean animateSwitch) {
     if (mc.field_1724 == null)
       return; 
     this.currentYaw = this.prevYaw = getReferenceYaw();
     this.currentPitch = this.prevPitch = getReferencePitch();
     this.currentDistance = this.prevDistance = animateSwitch ? 0.0F : 4.1F;
     this.heightOffset = this.prevHeightOffset = 0.0F;
     this.switchAnimating = animateSwitch;
     this.needsInit = false;
   }
   
   private void updateCamera() {
     if (mc.field_1724 == null)
       return;  if (this.needsInit) {
       initCamera(true);
       
       return;
     } 
     this.prevYaw = this.currentYaw;
     this.prevPitch = this.currentPitch;
     this.prevDistance = this.currentDistance;
     this.prevHeightOffset = this.heightOffset;
     
     float rotationSpeed = 0.28F;
     
     this.currentYaw += MathHelper.method_15393(getReferenceYaw() - this.currentYaw) * rotationSpeed;
     this.currentPitch = MathHelper.method_15363(this.currentPitch + (getReferencePitch() - this.currentPitch) * rotationSpeed, -90.0F, 90.0F);
     
     float distanceSpeed = this.switchAnimating ? 0.26F : 0.13F;
     this.currentDistance += (4.1F - this.currentDistance) * distanceSpeed;
     if (this.switchAnimating && Math.abs(4.1F - this.currentDistance) <= 0.02F) {
       this.currentDistance = 4.1F;
       this.switchAnimating = false;
     } 
     
     float targetOffset = 0.0F;
     if (mc.field_1724.method_5715()) {
       targetOffset = -0.5F;
     }
     if (!mc.field_1724.method_24828()) {
       targetOffset += (float)(-(mc.field_1724.method_18798()).field_1351 * 2.0D);
     }
     
     this.heightOffset += (targetOffset - this.heightOffset) * 0.13F;
   }
   
   public float getInterpolatedYaw(float partialTicks) {
     if (mc.field_1724 == null) return 0.0F; 
     return this.prevYaw + (this.currentYaw - this.prevYaw) * partialTicks;
   }
   
   public float getInterpolatedPitch(float partialTicks) {
     if (mc.field_1724 == null) return 0.0F; 
     return MathHelper.method_15363(this.prevPitch + (this.currentPitch - this.prevPitch) * partialTicks, -90.0F, 90.0F);
   }
   
   public float getInterpolatedDistance(float partialTicks) {
     return this.prevDistance + (this.currentDistance - this.prevDistance) * partialTicks;
   }
   
   public float getInterpolatedHeightOffset(float partialTicks) {
     return this.prevHeightOffset + (this.heightOffset - this.prevHeightOffset) * partialTicks;
   }
   
   private float getReferenceYaw() {
     if (FreeLookStorage.isActive()) {
       return FreeLookStorage.getFreeYaw();
     }
     return (mc.field_1724 != null) ? mc.field_1724.method_36454() : 0.0F;
   }
   
   private float getReferencePitch() {
     if (FreeLookStorage.isActive()) {
       return FreeLookStorage.getFreePitch();
     }
     return (mc.field_1724 != null) ? mc.field_1724.method_36455() : 0.0F;
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.needsInit = true;
     this.wasThirdPerson = false;
     
     if (mc.field_1724 != null && !mc.field_1690.method_31044().method_31034()) {
       initCamera(true);
       this.wasThirdPerson = true;
     } 
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.needsInit = true;
     this.heightOffset = 0.0F;
     this.prevHeightOffset = 0.0F;
   }
 }

