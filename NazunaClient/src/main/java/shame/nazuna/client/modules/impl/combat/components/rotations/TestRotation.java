package shame.nazuna.client.modules.impl.combat.components.rotations;
 
 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 import java.io.IOException;
 import java.io.Reader;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.ThreadLocalRandom;
 import net.minecraft.LivingEntity;
 import net.minecraft.Box;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.api.utils.rotate.RotationUtils;
 import shame.nazuna.client.modules.impl.combat.Aura;
 import shame.nazuna.client.modules.impl.combat.components.RotationsSystem;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 
 public class TestRotation
   extends RotationsSystem
   implements QClient
 {
   private static final Path DATASET_PATH = Path.of(System.getProperty("user.home"), new String[] { "Desktop", "data.json" });
   
   private final List<DatasetFrame> frames = new ArrayList<>();
   
   private LivingEntity trackedTarget;
   
   private LivingEntity trackedRotationTarget;
   private Vec3d currentAimPoint;
   private Vec3d targetAimPoint;
   private long lastModified = Long.MIN_VALUE;
   
   private long lastLoadAttempt;
   
   private boolean datasetReady;
   private int playbackIndex;
   private int aimPointTicks;
   private int aimPointRefreshTicks;
   private int smoothProfileTicks;
   private float smoothYawStep;
   private float smoothPitchStep;
   private float smoothYaw;
   private float smoothPitch;
   private float yawSmoothFactor = 1.0F;
   private float pitchSmoothFactor = 1.0F;
   
   private boolean hasRotationState;
   
   public void reset() {
     this.trackedTarget = null;
     this.trackedRotationTarget = null;
     this.currentAimPoint = null;
     this.targetAimPoint = null;
     this.playbackIndex = 0;
     this.aimPointTicks = 0;
     this.aimPointRefreshTicks = 0;
     this.smoothProfileTicks = 0;
     this.smoothYawStep = 0.0F;
     this.smoothPitchStep = 0.0F;
     this.smoothYaw = 0.0F;
     this.smoothPitch = 0.0F;
     this.yawSmoothFactor = 1.0F;
     this.pitchSmoothFactor = 1.0F;
     this.hasRotationState = false;
   }
 
 
   
   public void updateRotations(LivingEntity target) {
     if (mc.field_1724 == null || target == null) {
       return;
     }
     
     boolean focus = shouldFocus();
     ensureDatasetLoaded();
     Vec3d aimPoint = selectAimPoint(target, focus);
     Vec2f rot = RotationUtils.getRotations(aimPoint);
     
     if (!this.datasetReady || this.frames.isEmpty()) {
       RotationStorage.update(new Rotation(rot.field_1343, 
             MathHelper.method_15363(rot.field_1342, -89.0F, 89.0F)), 360.0F, 360.0F, 45.0F, 45.0F, 0, 1, Aura.clientLook
           
           .isState());
       
       return;
     } 
     
     float currentYaw = mc.field_1724.method_36454();
     float currentPitch = mc.field_1724.method_36455();
     syncRotationState(target, currentYaw, currentPitch);
     
     float remainingYaw = MathHelper.method_15393(rot.field_1343 - this.smoothYaw);
     float remainingPitch = rot.field_1342 - this.smoothPitch;
     
     DatasetFrame frame = pickFrame(remainingYaw, remainingPitch, focus);
     updateSmoothProfile(frame, remainingYaw, remainingPitch, focus);
     
     float gcd = Math.max(GCDUtil.getGCDValue(), 1.0E-4F);
     float yawStep = buildAxisStep(remainingYaw, frame, true, focus);
     float pitchStep = buildAxisStep(remainingPitch, frame, false, focus);
     
     yawStep += buildJitter(frame, remainingYaw, true, gcd);
     pitchStep += buildJitter(frame, remainingPitch, false, gcd);
     
     this.smoothYawStep = smoothAxisStep(this.smoothYawStep, yawStep, remainingYaw, true, focus);
     this.smoothPitchStep = smoothAxisStep(this.smoothPitchStep, pitchStep, remainingPitch, false, focus);
     
     float quantizedYawStep = quantizeDelta(this.smoothYawStep, remainingYaw, gcd, true);
     float quantizedPitchStep = quantizeDelta(this.smoothPitchStep, remainingPitch, gcd, false);
     
     this.smoothYaw = MathHelper.method_15393(this.smoothYaw + quantizedYawStep);
     this.smoothPitch = MathHelper.method_15363(this.smoothPitch + quantizedPitchStep, -89.0F, 89.0F);
     
     RotationStorage.update(new Rotation(this.smoothYaw, this.smoothPitch), 360.0F, 360.0F, 45.0F, 45.0F, 0, 1, Aura.clientLook
 
         
         .isState());
   }
 
   
   private boolean shouldFocus() {
     float cooldown = mc.field_1724.method_7261(1.5F);
     
     boolean fallingForCrit = (!mc.field_1724.method_24828() && (mc.field_1724.method_18798()).field_1351 < 0.0D && mc.field_1724.field_6017 > 0.0F);
     
     return (cooldown >= 0.88F || fallingForCrit);
   }
   
   private void ensureDatasetLoaded() {
     long now = System.currentTimeMillis();
     if (!shouldReload(now)) {
       return;
     }
     
     this.lastLoadAttempt = now;
     long modified = readLastModified();
     if (this.datasetReady && modified == this.lastModified) {
       return;
     }
     
     this.frames.clear();
     this.datasetReady = false;
     
     if (!Files.exists(DATASET_PATH, new java.nio.file.LinkOption[0])) {
       this.lastModified = Long.MIN_VALUE;
       return;
     } 
     
     try { Reader reader = Files.newBufferedReader(DATASET_PATH); 
       try { JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
         for (JsonElement element : array) {
           if (!element.isJsonObject()) {
             continue;
           }
           
           DatasetFrame frame = parseFrame(element.getAsJsonObject());
           if (frame != null) {
             this.frames.add(frame);
           }
         } 
         
         this.datasetReady = !this.frames.isEmpty();
         this.lastModified = modified;
         reset();
         if (reader != null) reader.close();  } catch (Throwable throwable) { if (reader != null) try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException|IllegalStateException ignored)
     { this.datasetReady = false;
       this.lastModified = Long.MIN_VALUE;
       reset(); }
   
   }
   
   private boolean shouldReload(long now) {
     if (!this.datasetReady || this.frames.isEmpty()) {
       return (now - this.lastLoadAttempt >= 1500L);
     }
     return (now - this.lastLoadAttempt >= 3000L);
   }
   
   private long readLastModified() {
     try {
       return Files.exists(DATASET_PATH, new java.nio.file.LinkOption[0]) ? Files.getLastModifiedTime(DATASET_PATH, new java.nio.file.LinkOption[0]).toMillis() : Long.MIN_VALUE;
     } catch (IOException ignored) {
       return Long.MIN_VALUE;
     } 
   }
   
   private DatasetFrame parseFrame(JsonObject object) {
     float fromYaw = getFloat(object, "fromYaw");
     float toYaw = getFloat(object, "toYaw");
     float fromPitch = getFloat(object, "fromPitch");
     float toPitch = getFloat(object, "toPitch");
     
     float signedYaw = MathHelper.method_15393(toYaw - fromYaw);
     float signedPitch = toPitch - fromPitch;
     float absYaw = Math.abs(signedYaw);
     float absPitch = Math.abs(signedPitch);
     
     float deltaYaw = Math.max(getFloat(object, "deltaYaw"), absYaw);
     float deltaPitch = Math.max(getFloat(object, "deltaPitch"), absPitch);
     if (deltaYaw <= 0.0F && deltaPitch <= 0.0F) {
       return null;
     }
     
     DatasetFrame frame = new DatasetFrame();
     frame.deltaYaw = deltaYaw;
     frame.deltaPitch = deltaPitch;
     frame
       
       .signedYaw = (signedYaw != 0.0F) ? signedYaw : (Math.signum(getFloat(object, "jitterYawDir")) * deltaYaw);
     frame
       
       .signedPitch = (signedPitch != 0.0F) ? signedPitch : (Math.signum(getFloat(object, "jitterPitchDir")) * deltaPitch);
     frame.rotationSpeed = Math.max(getFloat(object, "rotationSpeed"), 0.0F);
     frame.jitterScore = Math.max(getFloat(object, "jitterScore"), 0.0F);
     frame.jitterYawSpeed = Math.max(getFloat(object, "jitterYawSpeed"), 0.0F);
     frame.jitterPitchSpeed = Math.max(getFloat(object, "jitterPitchSpeed"), 0.0F);
     frame.isJittering = getBoolean(object, "isJittering");
     frame.attacking = getBoolean(object, "attacking");
     frame.combatFrame = getBoolean(object, "isCombatFrame");
     frame.instantSnap = getBoolean(object, "isInstantSnap");
     frame.timeDeltaMs = Math.max(1L, object.has("timeDeltaMs") ? object.get("timeDeltaMs").getAsLong() : 50L);
     return frame;
   }
   
   private DatasetFrame pickFrame(float remainingYaw, float remainingPitch, boolean focus) {
     float pressure = Math.abs(remainingYaw) + Math.abs(remainingPitch) * 0.82F;
     int size = this.frames.size();
     int window = Math.min(size, focus ? 78 : 56);
     int bestIndex = this.playbackIndex % size;
     float bestScore = Float.MAX_VALUE;
     
     for (int i = 0; i < window; i++) {
       int index = (this.playbackIndex + i) % size;
       DatasetFrame frame = this.frames.get(index);
       float framePressure = frame.deltaYaw + frame.deltaPitch * 0.82F;
       float score = Math.abs(framePressure - pressure);
       
       if (focus) {
         if (!frame.isCombatLike()) {
           score += 3.0F;
         }
         if (frame.instantSnap) {
           score -= 0.5F;
         }
       } else if (frame.isCombatLike()) {
         score += 1.6F;
       } 
       
       if (pressure < 10.0F && frame.isJittering) {
         score -= Math.min(frame.jitterScore, 2.6F) * 0.2F;
       }
       
       score += i * 0.032F;
       if (score < bestScore) {
         bestScore = score;
         bestIndex = index;
       } 
     } 
     
     this.playbackIndex = (bestIndex + 1) % size;
     return this.frames.get(bestIndex);
   }
   
   private void updateSmoothProfile(DatasetFrame frame, float remainingYaw, float remainingPitch, boolean focus) {
     if (this.smoothProfileTicks > 0) {
       this.smoothProfileTicks--;
       
       return;
     } 
     ThreadLocalRandom random = ThreadLocalRandom.current();
     float pressure = MathHelper.method_15363((Math.abs(remainingYaw) + Math.abs(remainingPitch)) / 32.0F, 0.0F, 1.0F);
     float timePressure = MathHelper.method_15363((float)frame.timeDeltaMs / 120.0F, 0.0F, 1.0F);
     
     float yawMin = focus ? 0.94F : 0.86F;
     float yawMax = focus ? 1.12F : 1.04F;
     float pitchMin = focus ? 0.92F : 0.84F;
     float pitchMax = focus ? 1.08F : 1.0F;
     
     this.yawSmoothFactor = random.nextFloat(yawMin, yawMax + pressure * 0.08F + timePressure * 0.04F);
     this.pitchSmoothFactor = random.nextFloat(pitchMin, pitchMax + pressure * 0.06F + timePressure * 0.03F);
     
     if (frame.isCombatLike()) {
       this.yawSmoothFactor *= 1.02F;
       this.pitchSmoothFactor *= 1.015F;
     } 
     
     this.smoothProfileTicks = random.nextInt(focus ? 2 : 3, focus ? 6 : 8);
   }
   
   private float buildAxisStep(float remaining, DatasetFrame frame, boolean yawAxis, boolean focus) {
     float desiredAbs = Math.abs(remaining);
     if (desiredAbs <= 1.0E-4F) {
       return 0.0F;
     }
     
     float template = yawAxis ? frame.deltaYaw : frame.deltaPitch;
     float speedBoost = 0.3F + MathHelper.method_15363(frame.rotationSpeed * (yawAxis ? 3.4F : 2.8F), 0.0F, yawAxis ? 0.2F : 0.16F);
     float pressureBoost = MathHelper.method_15363(desiredAbs / (yawAxis ? 105.0F : 82.0F), 0.09F, yawAxis ? 0.52F : 0.46F);
     float step = Math.max(template * Math.max(speedBoost, pressureBoost), yawAxis ? 0.03F : 0.024F);
     
     if (frame.instantSnap) {
       step = Math.max(step, desiredAbs * (yawAxis ? 0.085F : 0.065F));
     }
     
     if (frame.attacking || frame.combatFrame) {
       step *= yawAxis ? 1.02F : 1.015F;
     }
     
     step *= yawAxis ? 0.5F : 0.46F;
     
     float finishThreshold = yawAxis ? 6.0F : 4.0F;
     if (desiredAbs < finishThreshold) {
       float finishBoost = 1.0F + (finishThreshold - desiredAbs) / finishThreshold * 0.18F;
       step *= finishBoost;
     } 
 
 
     
     float maxStep = yawAxis ? Math.max(0.48F, desiredAbs * (frame.instantSnap ? 0.11F : 0.065F)) : Math.max(0.34F, desiredAbs * (frame.instantSnap ? 0.09F : 0.058F));
     step = Math.min(step, maxStep);
     step = Math.min(step, desiredAbs);
     return Math.signum(remaining) * step;
   }
   
   private float buildJitter(DatasetFrame frame, float remaining, boolean yawAxis, float gcd) {
     float desiredAbs = Math.abs(remaining);
     if (desiredAbs > (yawAxis ? 6.5F : 4.8F)) {
       return 0.0F;
     }
     
     float speed = yawAxis ? frame.jitterYawSpeed : frame.jitterPitchSpeed;
     float base = gcd * MathHelper.method_15363(frame.jitterScore * 0.01F, 0.0F, yawAxis ? 0.15F : 0.11F);
     base += gcd * MathHelper.method_15363(speed * (yawAxis ? 1.3F : 1.0F), 0.0F, yawAxis ? 0.1F : 0.07F);
     
     if (frame.isJittering) {
       base *= 1.05F;
     }
     
     if (base <= 0.0F) {
       return 0.0F;
     }
     
     float direction = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
     float jitter = base * ThreadLocalRandom.current().nextFloat(0.3F, 0.95F) * direction;
     if (Math.abs(jitter) > desiredAbs && Math.signum(jitter) == Math.signum(remaining)) {
       jitter = remaining;
     }
     
     return jitter;
   }
   
   private void syncRotationState(LivingEntity target, float currentYaw, float currentPitch) {
     if (!this.hasRotationState || this.trackedRotationTarget != target) {
       this.trackedRotationTarget = target;
       this.smoothYaw = currentYaw;
       this.smoothPitch = currentPitch;
       this.smoothYawStep = 0.0F;
       this.smoothPitchStep = 0.0F;
       this.yawSmoothFactor = 1.0F;
       this.pitchSmoothFactor = 1.0F;
       this.smoothProfileTicks = 0;
       this.hasRotationState = true;
     } 
   }
   
   private float smoothAxisStep(float currentStep, float desiredStep, float remaining, boolean yawAxis, boolean focus) {
     float desiredAbs = Math.abs(remaining);
     if (desiredAbs <= 1.0E-4F) {
       return 0.0F;
     }
 
 
     
     float baseAlpha = yawAxis ? (focus ? 0.092F : 0.06F) : (focus ? 0.082F : 0.055F);
     float alpha = baseAlpha * (yawAxis ? this.yawSmoothFactor : this.pitchSmoothFactor);
     float smoothed = currentStep + (desiredStep - currentStep) * MathHelper.method_15363(alpha, 0.025F, 0.16F);
     
     float minCap = yawAxis ? 0.13F : 0.1F;
 
     
     float capScale = yawAxis ? (focus ? 0.056F : 0.036F) : (focus ? 0.046F : 0.032F);
     float randomFactor = yawAxis ? this.yawSmoothFactor : this.pitchSmoothFactor;
     float maxCap = minCap + desiredAbs * capScale * MathHelper.method_15363(randomFactor, 0.88F, 1.18F);
     
     float finishThreshold = yawAxis ? 5.5F : 3.8F;
     if (desiredAbs < finishThreshold) {
       maxCap *= 1.12F;
     }
     
     smoothed = MathHelper.method_15363(smoothed, -maxCap, maxCap);
     if (Math.abs(remaining) < Math.abs(smoothed) && Math.signum(remaining) == Math.signum(smoothed)) {
       smoothed = remaining;
     }
     
     return smoothed;
   }
   
   private float quantizeDelta(float wantedDelta, float remaining, float gcd, boolean yawAxis) {
     float limited = wantedDelta;
     if (Math.abs(remaining) < Math.abs(limited) && Math.signum(remaining) == Math.signum(limited)) {
       limited = remaining;
     }
     
     float quantized = Math.round(limited / gcd) * gcd;
     if (quantized == 0.0F && Math.abs(limited) >= gcd * 0.2F) {
       quantized = Math.signum(limited) * gcd;
     }
     
     if (Math.abs(remaining) < Math.abs(quantized) && Math.signum(remaining) == Math.signum(quantized)) {
       quantized = remaining;
     }
     
     if (!yawAxis) {
       quantized = MathHelper.method_15363(quantized, -89.0F, 89.0F);
     }
     return quantized;
   }
 
   
   private Vec3d selectAimPoint(LivingEntity target, boolean focus) {
     if (this.trackedTarget != target || this.currentAimPoint == null || this.targetAimPoint == null) {
       this.trackedTarget = target;
       this.targetAimPoint = createAimPoint(target, focus);
       this.currentAimPoint = this.targetAimPoint;
       this.aimPointTicks = 0;
       this.aimPointRefreshTicks = randomRefreshTicks(focus);
       return this.currentAimPoint;
     } 
     
     if (this.aimPointTicks++ >= this.aimPointRefreshTicks) {
       this.targetAimPoint = createAimPoint(target, focus);
       this.aimPointTicks = 0;
       this.aimPointRefreshTicks = randomRefreshTicks(focus);
     } 
     
     float lerp = focus ? 0.06F : 0.04F;
     this
 
       
       .currentAimPoint = new Vec3d(MathHelper.method_16436(lerp, this.currentAimPoint.field_1352, this.targetAimPoint.field_1352), MathHelper.method_16436(lerp, this.currentAimPoint.field_1351, this.targetAimPoint.field_1351), MathHelper.method_16436(lerp, this.currentAimPoint.field_1350, this.targetAimPoint.field_1350));
     
     return this.currentAimPoint;
   }
   
   private int randomRefreshTicks(boolean focus) {
     return ThreadLocalRandom.current().nextInt(focus ? 7 : 10, focus ? 13 : 18);
   }
   
   private Vec3d createAimPoint(LivingEntity target, boolean focus) {
     Box box = getPredictedBox(target);
     ThreadLocalRandom random = ThreadLocalRandom.current();
     double x = MathHelper.method_16436(random.nextDouble(0.45D, 0.55D), box.field_1323, box.field_1320);
     double y = MathHelper.method_16436(random.nextDouble(focus ? 0.53D : 0.49D, focus ? 0.7D : 0.76D), box.field_1322, box.field_1325);
     double z = MathHelper.method_16436(random.nextDouble(0.45D, 0.55D), box.field_1321, box.field_1324);
     return new Vec3d(x, y, z);
   }
   
   private float getFloat(JsonObject object, String key) {
     return object.has(key) ? object.get(key).getAsFloat() : 0.0F;
   }
   
   private boolean getBoolean(JsonObject object, String key) {
     return (object.has(key) && object.get(key).getAsBoolean());
   }
   
   private static class DatasetFrame {
     float deltaYaw;
     float deltaPitch;
     float signedYaw;
     float signedPitch;
     float rotationSpeed;
     float jitterScore;
     float jitterYawSpeed;
     float jitterPitchSpeed;
     long timeDeltaMs;
     boolean isJittering;
     boolean attacking;
     boolean combatFrame;
     boolean instantSnap;
     
     boolean isCombatLike() {
       return (this.attacking || this.combatFrame || this.instantSnap);
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\components\rotations\TestRotation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */