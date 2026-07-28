package shame.nazuna.client.modules.impl.movement;
 import net.minecraft.Vec2f;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class ElytraBoost extends Module {
   public void setLastDebugMessageAt(long lastDebugMessageAt) {
     this.lastDebugMessageAt = lastDebugMessageAt;
   }
   
   private static final String[] RANGE_LABELS = new String[] { "0 - 5", "5 - 10", "10 - 15", "15 - 20", "20 - 25", "25 - 30", "30 - 35", "35 - 40", "40 - 45" };
   private static final long DEBUG_MESSAGE_INTERVAL_MS = 800L;
   public static ElytraBoost INSTANCE = new ElytraBoost();
   
   private int[] field_26775;
   private final FloatSetting[] yawSpeeds = new FloatSetting[9];
   private final FloatSetting[] pitchSpeeds = new FloatSetting[9];
   private final ModeSetting mode = new ModeSetting("Mode", "Custom", new String[]{"Custom", "Default"});
   public BooleanSetting getDebug() { return this.debug; }
   private long lastDebugMessageAt = 0L;
   private final BooleanSetting debug = (new BooleanSetting("Дебаг", false));
   public long getLastDebugMessageAt() { return this.lastDebugMessageAt; }
   
   public ElytraBoost() {
     super("ElytraBoost", "Ускоряет на элитрах", Module.ModuleCategory.MOVEMENT);
     int i;
     for (i = 0; i < this.yawSpeeds.length; i++) {
       this.yawSpeeds[i] = (new FloatSetting("yaw " + RANGE_LABELS[i], 1.5F, 1.5F, 2.5F, 0.01F))
         .visible(this::isCustomMode);
     }
     
     for (i = 0; i < this.pitchSpeeds.length; i++) {
       this.pitchSpeeds[i] = (new FloatSetting("pitch " + RANGE_LABELS[i], 1.5F, 1.5F, 2.5F, 0.01F))
         .visible(this::isCustomMode);
     }
     
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.debug });
     addSettings((Setting[])this.yawSpeeds);
     addSettings((Setting[])this.pitchSpeeds);
   }
   
   public boolean isCustomMode() {
     return this.mode.is("Custom");
   }
   
   public Vec2f getBoostV2() {
     float yaw = (mc.field_1724 != null) ? mc.field_1724.method_36454() : 0.0F;
     float pitch = (mc.field_1724 != null) ? mc.field_1724.method_36455() : 0.0F;
     
     Aura aura = Aura.INSTANCE;
     if (aura != null && aura.isEnable() && aura.getTarget() != null) {
       Vec2f rotations = aura.getTargetRotations();
       if (rotations != null) {
         yaw = rotations.field_1343;
         pitch = rotations.field_1342;
       } 
     } 
     
     float normalizedYaw = convertValToRange(MathHelper.method_15393(yaw));
     float normalizedPitch = convertValToRange(Math.abs(pitch));
     int yawIndex = getRangeIndex(normalizedYaw, this.yawSpeeds.length);
     int pitchIndex = getRangeIndex(normalizedPitch, this.pitchSpeeds.length);
     float yawSpeed = this.yawSpeeds[yawIndex].getValue().floatValue();
     float pitchSpeed = this.pitchSpeeds[pitchIndex].getValue().floatValue();
     
     if (pitchSpeed > yawSpeed) {
       yawSpeed = pitchSpeed;
     }
     
     logDebug(yawIndex, yawSpeed, pitchIndex, pitchSpeed);
     return new Vec2f(yawSpeed, pitchSpeed);
   }
   
   private void logDebug(int yawIndex, float yawSpeed, int pitchIndex, float pitchSpeed) {
     if (!this.debug.isState()) {
       return;
     }
     
     long now = System.currentTimeMillis();
     if (now - this.lastDebugMessageAt < 800L) {
       return;
     }
     
     this.lastDebugMessageAt = now;
     ChatUtils.sendMessage("yaw " + RANGE_LABELS[yawIndex] + ": " + yawSpeed + " | pitch " + RANGE_LABELS[pitchIndex] + ": " + pitchSpeed);
   }
 
 
 
   
   private int getRangeIndex(float value, int length) {
     return Math.min((int)(value / 5.0F), length - 1);
   }
   
   private float convertValToRange(float value) {
     float result = Math.abs(value);
     if (result > 90.0F) {
       result = 180.0F - result;
     }
     if (result > 45.0F) {
       result = 90.0F - result;
     }
     return result;
   }
 }

