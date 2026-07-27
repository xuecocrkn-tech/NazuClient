package shame.nazuna.api.storages.implement;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.nio.file.attribute.FileAttribute;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.CopyOnWriteArrayList;
 import java.util.concurrent.ThreadLocalRandom;
 import net.minecraft.LivingEntity;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.NeuroPattern;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.client.modules.impl.combat.components.gcd.GCDUtil;
 
 
 
 public class NeuroAuraStorage
   implements QClient
 {
   private static final long MIN_RECORD_INTERVAL = 50L;
   private static final int MAX_FRAMES = 20000;
   private static final String PATTERNS_DIRECTORY = "data_patterns";
   private static final String LEGACY_PATTERNS_DIRECTORY = "neuro_patterns";
   private static final String PRIMARY_EXTENSION = ".data";
   private static final String LEGACY_EXTENSION = ".neuro";
   private static final float SYNC_SCORE_THRESHOLD = 45.0F;
   private static final float MAX_YAW_CORRECTION = 8.0F;
   private static final float MAX_PITCH_CORRECTION = 6.0F;
   public void setRecording(boolean isRecording) { this.isRecording = isRecording; }
   public void setUsingNeuro(boolean isUsingNeuro) { this.isUsingNeuro = isUsingNeuro; }
   public void setShowStats(boolean showStats) { this.showStats = showStats; }
   
   public void setCurrentPatternName(String currentPatternName) { this.currentPatternName = currentPatternName; }
   
   
 
   
   private long lastRecordTime = 0L;
   private float prevRecordYaw = 0.0F;
   private float prevRecordPitch = 0.0F;
   
   private boolean hasRecordedBefore = false;
   private final List<Frame> frames = new CopyOnWriteArrayList<>();
   private int playbackIndex = -1;
   private int ticksSinceSync = 0;
   private float smoothedYawDelta = 0.0F;
   private float smoothedPitchDelta = 0.0F;
   private float smoothedOutputYaw = Float.NaN;
   private float smoothedOutputPitch = Float.NaN;
   private float yawSpeedFactor = 1.0F;
   private float pitchSpeedFactor = 1.0F;
   private int speedProfileTicks = 0;
   private Vec3d currentAimPoint = null;
   private Vec3d targetRandomPoint = null;
   private int aimPointTicks = 0;
   private LivingEntity lastAimTarget = null;
   private boolean lastWasIdle = true;
   private int attackCount = 0;
   private float randomXOffset = 0.0F;
   private float randomYRatio = 0.66F;
   private float randomZOffset = 0.0F;
   
   public NeuroAuraStorage() {
     createPatternsDirectory();
   }
   
   private void createPatternsDirectory() {
     try {
       Path path = Paths.get("data_patterns", new String[0]);
       if (!Files.exists(path, new java.nio.file.LinkOption[0])) {
         Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]);
       }
     } catch (IOException e) {
       this.lastDebugMessage = "§cОшибка папки";
     } 
   }
   
   public void recordTick(LivingEntity target, float currentYaw, float currentPitch) {
     if (!this.isRecording || mc.field_1724 == null) {
       return;
     }
     
     long now = System.currentTimeMillis();
     if (now - this.lastRecordTime < 50L) {
       return;
     }
     
     float deltaYaw = 0.0F;
     float deltaPitch = 0.0F;
     
     if (this.hasRecordedBefore) {
       deltaYaw = MathHelper.method_15393(currentYaw - this.prevRecordYaw);
       deltaPitch = currentPitch - this.prevRecordPitch;
     } 
     
     float angleYaw = 0.0F;
     float anglePitch = 0.0F;
     double distance = 0.0D;
     boolean hasTarget = (target != null);
     
     if (hasTarget) {
       AimData aimData = getAimData(target, currentYaw, currentPitch, null, true);
       angleYaw = aimData.angleYaw;
       anglePitch = aimData.anglePitch;
       distance = aimData.distance;
     } 
     
     Frame frame = new Frame();
     frame.deltaYaw = deltaYaw;
     frame.deltaPitch = deltaPitch;
     frame.angleYaw = angleYaw;
     frame.anglePitch = anglePitch;
     frame.distance = distance;
     frame.hasTarget = hasTarget;
     frame.smoothness = calculateSmoothness(deltaYaw, deltaPitch);
     
     this.frames.add(frame);
     while (this.frames.size() > 20000) {
       this.frames.remove(0);
     }
     
     if (hasTarget) {
       boolean crit = (mc.field_1724.field_6017 > 0.0F && !mc.field_1724.method_24828());
       String type = (target instanceof net.minecraft.PlayerEntity) ? "player" : "mob";
       this.recordedPatterns.add(new NeuroPattern(angleYaw, anglePitch, deltaYaw, deltaPitch, distance, crit, 0.0D, type, frame.smoothness));
 
 
       
       while (this.recordedPatterns.size() > 20000) {
         this.recordedPatterns.remove(0);
       }
     } 
     
     this.prevRecordYaw = currentYaw;
     this.prevRecordPitch = currentPitch;
     this.hasRecordedBefore = true;
     this.lastRecordTime = now;
     this.recordedThisSession++;
     
     if (this.recordedThisSession % 20 == 0) {
       this.lastDebugMessage = "§aЗапись: §f" + this.frames.size();
     }
   }
   
   public Rotation getNeuroRotation(LivingEntity target, float currentYaw, float currentPitch, boolean idle) {
     if (!this.isUsingNeuro || target == null || mc.field_1724 == null || this.frames.isEmpty()) {
       resetState();
       return null;
     } 
     
     if (!idle && this.lastWasIdle) {
       rollNewRandomPoint();
       this.attackCount++;
     } 
     this.lastWasIdle = idle;
     
     boolean needSync = (this.playbackIndex < 0 || this.playbackIndex >= this.frames.size());
     AimData aimData = getAimData(target, currentYaw, currentPitch, null, idle);
     boolean airborne = (!mc.field_1724.method_24828() || (mc.field_1724.method_18798()).field_1351 != 0.0D);
     
     if (Math.abs(aimData.angleYaw) > 110.0F) {
       needSync = true;
       this.smoothedYawDelta = 0.0F;
       this.smoothedPitchDelta = 0.0F;
       this.smoothedOutputYaw = currentYaw;
       this.smoothedOutputPitch = currentPitch;
     } 
     
     if (!needSync && this.ticksSinceSync >= 5) {
       Frame currentFrame = this.frames.get(this.playbackIndex);
       float yawDiff = Math.abs(MathHelper.method_15393(currentFrame.angleYaw - aimData.angleYaw));
       float pitchDiff = Math.abs(currentFrame.anglePitch - aimData.anglePitch);
       float distDiff = (float)Math.abs(currentFrame.distance - aimData.distance);
       if (yawDiff + pitchDiff + distDiff * 0.3F > 45.0F) {
         needSync = true;
       }
     } 
     
     if (needSync) {
       this.playbackIndex = findBest(aimData.angleYaw, aimData.anglePitch, aimData.distance);
       this.ticksSinceSync = 0;
     } 
     
     Frame frame = this.frames.get(this.playbackIndex);
     aimData = getAimData(target, currentYaw, currentPitch, frame, idle);
     
     float applyYaw = frame.deltaYaw;
     float applyPitch = frame.deltaPitch;
     updateSpeedProfile(idle, airborne, aimData);
     
     if (Math.abs(frame.angleYaw) > 3.0F && Math.abs(aimData.angleYaw) > 3.0F && Math.signum(frame.angleYaw) != Math.signum(aimData.angleYaw)) {
       applyYaw = -applyYaw;
     }
     
     if (Math.abs(frame.anglePitch) > 3.0F && Math.abs(aimData.anglePitch) > 3.0F && Math.signum(frame.anglePitch) != Math.signum(aimData.anglePitch)) {
       applyPitch = -applyPitch;
     }
     
     applyYaw = adaptRecordedDelta(applyYaw, aimData.angleYaw, frame.smoothness, idle, 8.0F);
     applyPitch = adaptRecordedDelta(applyPitch, aimData.anglePitch, frame.smoothness, idle, 6.0F);
     
     if (Math.abs(aimData.angleYaw) < 32.0F) {
       applyYaw = MathHelper.method_16439(0.58F, applyYaw, aimData.angleYaw);
     }
     
     if (Math.abs(aimData.anglePitch) < 24.0F) {
       applyPitch = MathHelper.method_16439(0.52F, applyPitch, aimData.anglePitch);
     }
     
     this.smoothedYawDelta = smoothDelta(this.smoothedYawDelta, applyYaw, frame.smoothness);
     this.smoothedPitchDelta = smoothDelta(this.smoothedPitchDelta, applyPitch, frame.smoothness);
     
     float quantizedYaw = quantizeToMouseStep(this.smoothedYawDelta, aimData.angleYaw);
     float quantizedPitch = quantizeToMouseStep(this.smoothedPitchDelta, aimData.anglePitch);
     quantizedYaw += getMicroJitter(true, idle, airborne, aimData);
     quantizedPitch += getMicroJitter(false, idle, airborne, aimData);
     
     float rawYaw = MathHelper.method_15393(currentYaw + quantizedYaw);
     float rawPitch = MathHelper.method_15363(currentPitch + quantizedPitch, -90.0F, 90.0F);
     float finalYaw = smoothOutputRotation(rawYaw, currentYaw, frame.smoothness, idle, true);
     float finalPitch = smoothOutputRotation(rawPitch, currentPitch, frame.smoothness, idle, false);
     
     this.playbackIndex++;
     this.ticksSinceSync++;
     
     int skipped = 0;
     while (this.playbackIndex < this.frames.size() && !((Frame)this.frames.get(this.playbackIndex)).hasTarget && skipped < 5) {
       this.playbackIndex++;
       skipped++;
     } 
     
     if (this.playbackIndex >= this.frames.size()) {
       float newAngleYaw = MathHelper.method_15393(aimData.perfectYaw - finalYaw);
       float newAnglePitch = aimData.perfectPitch - finalPitch;
       this.playbackIndex = findBest(newAngleYaw, newAnglePitch, aimData.distance);
       this.ticksSinceSync = 0;
     } 
     
     this.lastDebugMessage = String.format("§a[%d/%d] dY%.2f dP%.2f", new Object[] { Integer.valueOf(this.playbackIndex), Integer.valueOf(this.frames.size()), Float.valueOf(quantizedYaw), Float.valueOf(quantizedPitch) });
     return new Rotation(finalYaw, finalPitch);
   }
   
   private void rollNewRandomPoint() {
     ThreadLocalRandom r = ThreadLocalRandom.current();
     this.randomXOffset = r.nextFloat(-0.38F, 0.38F);
     this.randomYRatio = r.nextFloat(0.4F, 0.85F);
     this.randomZOffset = r.nextFloat(-0.38F, 0.38F);
   }
   
   private AimData getAimData(LivingEntity target, float currentYaw, float currentPitch, Frame frame, boolean relaxed) {
     Vec3d eyePos = mc.field_1724.method_33571();
     Vec3d point = selectAimPoint(target, relaxed);
     double distance = eyePos.method_1022(point);
     
     double dx = point.field_1352 - eyePos.field_1352;
     double dy = point.field_1351 - eyePos.field_1351;
     double dz = point.field_1350 - eyePos.field_1350;
     double distXZ = Math.sqrt(dx * dx + dz * dz);
     
     float perfectYaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
     float perfectPitch = (float)Math.toDegrees(Math.atan2(-dy, distXZ));
     
     AimData aimData = new AimData();
     aimData.targetPoint = point;
     aimData.distance = distance;
     aimData.perfectYaw = perfectYaw;
     aimData.perfectPitch = perfectPitch;
     aimData.angleYaw = MathHelper.method_15393(perfectYaw - currentYaw);
     aimData.anglePitch = perfectPitch - currentPitch;
     return aimData;
   }
   
   private float adaptRecordedDelta(float recordedDelta, float currentAngle, float smoothness, boolean idle, float maxCorrection) {
     float correctionWeight = idle ? 0.14F : 0.045F;
     float correctionLimit = idle ? (maxCorrection * 0.65F) : (maxCorrection * 0.3F);
     float correction = MathHelper.method_15363(currentAngle - recordedDelta, -correctionLimit, correctionLimit);
     float result = recordedDelta + correction * correctionWeight;
     
     if (Math.abs(currentAngle) < Math.abs(result) && Math.signum(currentAngle) == Math.signum(result)) {
       result = currentAngle;
     }
 
 
     
     float preserveFactor = idle ? MathHelper.method_15363(1.0F - smoothness * 0.22F, 0.8F, 0.97F) : MathHelper.method_15363(1.0F - smoothness * 0.1F, 0.91F, 0.99F);
     result *= preserveFactor;
     
     if (Math.abs(currentAngle) <= GCDUtil.getGCDValue()) {
       return currentAngle;
     }
     
     return result;
   }
   
   private Vec3d selectAimPoint(LivingEntity target, boolean relaxed) {
     if (target != this.lastAimTarget) {
       this.lastAimTarget = target;
       this.currentAimPoint = null;
       this.targetRandomPoint = null;
       this.aimPointTicks = 0;
       rollNewRandomPoint();
     } 
     
     Box box = target.method_5829();
     Vec3d eyePos = mc.field_1724.method_33571();
 
 
     
     Vec3d stablePoint = new Vec3d((box.method_1005()).field_1352, box.field_1322 + box.method_17940() * 0.72D, (box.method_1005()).field_1350);
     
     if (box.method_1014(0.12D).method_1006(eyePos) || eyePos.method_1025(stablePoint) <= 2.25D) {
       this.currentAimPoint = stablePoint;
       this.targetRandomPoint = stablePoint;
       this.aimPointTicks = 0;
       return stablePoint;
     } 
     
     double xCenter = (box.field_1323 + box.field_1320) * 0.5D;
     double zCenter = (box.field_1321 + box.field_1324) * 0.5D;
     double halfW = box.method_17939() * 0.5D;
     double halfD = box.method_17941() * 0.5D;
     double height = box.method_17940();
     
     Vec3d desired = new Vec3d(xCenter + halfW * this.randomXOffset, box.field_1322 + height * this.randomYRatio, zCenter + halfD * this.randomZOffset);
 
 
 
 
     
     if (this.targetRandomPoint == null) {
       this.targetRandomPoint = desired;
     } else {
       float driftLerp = relaxed ? 0.13F : 0.07F;
       this
 
         
         .targetRandomPoint = new Vec3d(MathHelper.method_16436(driftLerp, this.targetRandomPoint.field_1352, desired.field_1352), MathHelper.method_16436(driftLerp, this.targetRandomPoint.field_1351, desired.field_1351), MathHelper.method_16436(driftLerp, this.targetRandomPoint.field_1350, desired.field_1350));
     } 
 
     
     if (this.currentAimPoint == null) {
       this.currentAimPoint = this.targetRandomPoint;
       this.aimPointTicks = 0;
       return this.currentAimPoint;
     } 
     
     float pointLerp = relaxed ? 0.11F : 0.055F;
     this
 
       
       .currentAimPoint = new Vec3d(MathHelper.method_16436(pointLerp, this.currentAimPoint.field_1352, this.targetRandomPoint.field_1352), MathHelper.method_16436(pointLerp, this.currentAimPoint.field_1351, this.targetRandomPoint.field_1351), MathHelper.method_16436(pointLerp, this.currentAimPoint.field_1350, this.targetRandomPoint.field_1350));
     
     this.aimPointTicks++;
     return this.currentAimPoint;
   }
   
   private float smoothDelta(float current, float target, float smoothness) {
     float lerpFactor = MathHelper.method_15363(0.035F + (1.0F - smoothness) * 0.12F, 0.035F, 0.15F);
     return current + (target - current) * lerpFactor;
   }
   
   private float smoothOutputRotation(float targetRotation, float currentRotation, float smoothness, boolean idle, boolean yawAxis) {
     float previous = yawAxis ? this.smoothedOutputYaw : this.smoothedOutputPitch;
     if (Float.isNaN(previous)) {
       previous = currentRotation;
     }
 
 
     
     float delta = yawAxis ? MathHelper.method_15393(targetRotation - previous) : (targetRotation - previous);
 
 
     
     float maxStep = yawAxis ? (idle ? 1.68F : 0.86F) : (idle ? 1.26F : 0.62F);
 
     
     float lerpFactor = idle ? MathHelper.method_15363(0.08F + (1.0F - smoothness) * 0.09F, 0.08F, 0.17F) : MathHelper.method_15363(0.04F + (1.0F - smoothness) * 0.055F, 0.04F, 0.095F);
     
     maxStep *= yawAxis ? this.yawSpeedFactor : this.pitchSpeedFactor;
     lerpFactor *= yawAxis ? this.yawSpeedFactor : this.pitchSpeedFactor;
     
     float smoothed = previous + MathHelper.method_15363(delta * lerpFactor, -maxStep, maxStep);
     if (yawAxis) {
       smoothed = MathHelper.method_15393(smoothed);
       this.smoothedOutputYaw = smoothed;
     } else {
       smoothed = MathHelper.method_15363(smoothed, -90.0F, 90.0F);
       this.smoothedOutputPitch = smoothed;
     } 
     return smoothed;
   }
   
   private void updateSpeedProfile(boolean idle, boolean airborne, AimData aimData) {
     if (this.speedProfileTicks > 0) {
       this.speedProfileTicks--;
       
       return;
     } 
     ThreadLocalRandom random = ThreadLocalRandom.current();
     float anglePressure = MathHelper.method_15363((Math.abs(aimData.angleYaw) + Math.abs(aimData.anglePitch)) / 35.0F, 0.0F, 1.0F);
     float baseYawMin = idle ? 1.06F : 0.96F;
     float baseYawMax = idle ? 1.34F : 1.12F;
     float basePitchMin = idle ? 1.0F : 0.9F;
     float basePitchMax = idle ? 1.24F : 1.05F;
     
     this.yawSpeedFactor = random.nextFloat(baseYawMin, baseYawMax + anglePressure * (idle ? 0.1F : 0.16F));
     this.pitchSpeedFactor = random.nextFloat(basePitchMin, basePitchMax + anglePressure * (idle ? 0.08F : 0.12F));
     
     if (!idle && anglePressure > 0.58F) {
       this.yawSpeedFactor = Math.max(this.yawSpeedFactor, 1.08F + anglePressure * 0.24F);
       this.pitchSpeedFactor = Math.max(this.pitchSpeedFactor, 1.0F + anglePressure * 0.18F);
     } 
     
     if (airborne) {
       this.yawSpeedFactor *= 0.97F;
       this.pitchSpeedFactor *= 0.95F;
     } 
     
     this.speedProfileTicks = random.nextInt(idle ? 3 : 2, idle ? 7 : 5);
   }
   
   private float getMicroJitter(boolean yawAxis, boolean idle, boolean airborne, AimData aimData) {
     float gcd = GCDUtil.getGCDValue();
     if (gcd <= 0.0F) {
       return 0.0F;
     }
     
     float pressure = Math.abs(yawAxis ? aimData.angleYaw : aimData.anglePitch);
     if (!idle && pressure > (yawAxis ? 10.0F : 7.0F)) {
       return 0.0F;
     }
     
     float amplitude = yawAxis ? (gcd * 0.018F) : (gcd * 0.012F);
     if (airborne) {
       amplitude *= 0.35F;
     }
     float wave = (float)Math.sin(((mc.field_1724.field_6012 + (yawAxis ? 0.0F : 7.0F)) * (idle ? 0.42F : 0.28F)));
     return wave * amplitude;
   }
   
   private float quantizeToMouseStep(float delta, float remainingAngle) {
     float gcd = GCDUtil.getGCDValue();
     if (gcd <= 0.0F) {
       return delta;
     }
     
     float limited = delta;
     if (Math.abs(remainingAngle) < Math.abs(limited) && Math.signum(remainingAngle) == Math.signum(limited)) {
       limited = remainingAngle;
     }
     
     float quantized = Math.round(limited / gcd) * gcd;
     if (quantized == 0.0F && Math.abs(remainingAngle) >= gcd * 0.35F && Math.abs(limited) > 0.001F) {
       quantized = Math.signum(limited) * gcd;
     }
     
     if (Math.abs(remainingAngle) < Math.abs(quantized) && Math.signum(remainingAngle) == Math.signum(quantized)) {
       quantized = remainingAngle;
     }
     
     return quantized;
   }
   
   private float calculateSmoothness(float deltaYaw, float deltaPitch) {
     float magnitude = Math.abs(deltaYaw) + Math.abs(deltaPitch);
     float base = 1.0F - magnitude / 18.0F;
     float periodic = (float)Math.sin(((this.recordedThisSession + mc.field_1724.field_6012 * 0.31F) * 0.34F)) * 0.012F;
     float noise = ThreadLocalRandom.current().nextFloat(-0.008F, 0.008F);
     return MathHelper.method_15363(base + periodic + noise, 0.22F, 0.88F);
   }
   
   private int findBest(float angleYaw, float anglePitch, double distance) {
     int best = 0;
     float bestScore = Float.MAX_VALUE;
     
     for (int i = 0; i < this.frames.size(); i++) {
       Frame frame = this.frames.get(i);
       if (frame.hasTarget) {
 
 
         
         float yawDiff = Math.abs(MathHelper.method_15393(frame.angleYaw - angleYaw));
         float pitchDiff = Math.abs(frame.anglePitch - anglePitch);
         float distanceDiff = (float)Math.abs(frame.distance - distance);
         float score = yawDiff + pitchDiff + distanceDiff * 0.3F;
         
         if (score < bestScore) {
           bestScore = score;
           best = i;
         } 
       } 
     } 
     return best;
   }
   
   public boolean savePatterns(String profileName) {
     if (this.frames.isEmpty()) {
       this.lastDebugMessage = "§cНет записей";
       return false;
     } 
     
     try { ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data_patterns/" + profileName + ".data"));
       
       try { SaveData data = new SaveData();
         data.patterns = new ArrayList<>(this.recordedPatterns);
         data.frames = new ArrayList<>(this.frames);
         out.writeObject(data);
         this.currentPatternName = profileName;
         this.lastDebugMessage = "§aСохранено " + this.frames.size();
         boolean bool = true;
         out.close(); return bool; } catch (Throwable throwable) { try { out.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
     { this.lastDebugMessage = "§cОшибка сохранения";
       return false; }
   
   }
 
   
   public boolean loadPatterns(String profileName) {
     File file = resolveProfileFile(profileName);
     if (!file.exists()) {
       this.lastDebugMessage = "§eНе найдено: " + profileName;
       return false;
     } 
     
     try { ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)); 
       try { Object obj = in.readObject();
         this.recordedPatterns.clear();
         this.frames.clear();
         
         if (obj instanceof SaveData) { SaveData data = (SaveData)obj;
           if (data.patterns != null) {
             this.recordedPatterns.addAll(data.patterns);
           }
           if (data.frames != null) {
             this.frames.addAll(data.frames);
           } }
         else if (obj instanceof List) { List<?> list = (List)obj;
           this.recordedPatterns.addAll(list); }
 
         
         if (this.frames.isEmpty() && !this.recordedPatterns.isEmpty()) {
           rebuildFramesFromPatterns();
         }
         
         this.currentPatternName = profileName;
         resetState();
         this.lastDebugMessage = "§aЗагружено " + this.frames.size();
         boolean bool = !this.frames.isEmpty() ? true : false;
         in.close(); return bool; } catch (Throwable throwable) { try { in.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException|ClassNotFoundException e)
     { this.lastDebugMessage = "§cОшибка загрузки";
       return false; }
   
   }
   
   private void rebuildFramesFromPatterns() {
     for (NeuroPattern pattern : this.recordedPatterns) {
       Frame frame = new Frame();
       frame.deltaYaw = pattern.getDeltaYaw();
       frame.deltaPitch = pattern.getDeltaPitch();
       frame.angleYaw = pattern.getYaw();
       frame.anglePitch = pattern.getPitch();
       frame.distance = pattern.getDistance();
       frame.hasTarget = true;
       frame.smoothness = MathHelper.method_15363(pattern.getSmoothness(), 0.18F, 0.9F);
       this.frames.add(frame);
     } 
   }
   
   public boolean deletePatterns(String profileName) {
     File primaryFile = new File("data_patterns/" + profileName + ".data");
     File legacyFile = new File("neuro_patterns/" + profileName + ".neuro");
     boolean deleted = false;
     
     if (primaryFile.exists()) {
       deleted = primaryFile.delete();
     }
     if (legacyFile.exists()) {
       deleted = (legacyFile.delete() || deleted);
     }
     
     if (deleted) {
       if (profileName.equals(this.currentPatternName)) {
         this.currentPatternName = null;
       }
       this.lastDebugMessage = "§aУдалено";
       return true;
     } 
     
     return false;
   }
   
   public int getPatternCount() {
     return this.recordedPatterns.size();
   }
   
   public int getFrameCount() {
     return this.frames.size();
   }
   
   public void startRecording() {
     this.recordedPatterns.clear();
     this.frames.clear();
     this.isRecording = true;
     this.isUsingNeuro = false;
     this.recordedThisSession = 0;
     this.lastRecordTime = 0L;
     this.currentPatternName = null;
     this.hasRecordedBefore = false;
     this.prevRecordYaw = 0.0F;
     this.prevRecordPitch = 0.0F;
     resetState();
     this.lastDebugMessage = "§aЗапись";
   }
   
   public void stopRecording() {
     this.isRecording = false;
     this.lastDebugMessage = "§eСтоп: " + this.frames.size();
   }
   
   public void clearPatterns() {
     this.recordedPatterns.clear();
     this.frames.clear();
     this.isRecording = false;
     this.isUsingNeuro = false;
     this.recordedThisSession = 0;
     this.currentPatternName = null;
     this.hasRecordedBefore = false;
     this.prevRecordYaw = 0.0F;
     this.prevRecordPitch = 0.0F;
     resetState();
     this.lastDebugMessage = "§eОчищено";
   }
   
   public void resetState() {
     this.playbackIndex = -1;
     this.ticksSinceSync = 0;
     this.smoothedYawDelta = 0.0F;
     this.smoothedPitchDelta = 0.0F;
     this.smoothedOutputYaw = Float.NaN;
     this.smoothedOutputPitch = Float.NaN;
     this.yawSpeedFactor = 1.0F;
     this.pitchSpeedFactor = 1.0F;
     this.speedProfileTicks = 0;
     this.currentAimPoint = null;
     this.targetRandomPoint = null;
     this.aimPointTicks = 0;
     this.lastAimTarget = null;
     this.lastWasIdle = true;
     this.attackCount = 0;
     rollNewRandomPoint();
   }
   
   public String getStatusString() {
     String status = "§8[§bData§8] §f" + this.frames.size();
     if (this.isRecording) {
       status = status + " §a[REC]";
     }
     if (this.isUsingNeuro) {
       status = status + " §b[ON " + status + "]";
     }
     return status;
   }
   
   public List<String> getPatternNames() {
     List<String> names = new ArrayList<>();
     collectPatternNames(names, new File("data_patterns"), ".data");
     collectPatternNames(names, new File("neuro_patterns"), ".neuro");
     return names;
   }
   
   private void collectPatternNames(List<String> names, File directory, String extension) {
     if (!directory.exists() || !directory.isDirectory()) {
       return;
     }
     
     File[] files = directory.listFiles((dir, name) -> name.endsWith(extension));
     if (files == null) {
       return;
     }
     
     for (File file : files) {
       String name = file.getName().replace(extension, "");
       if (!names.contains(name)) {
         names.add(name);
       }
     } 
   }
   
   private File resolveProfileFile(String profileName) {
     File primaryFile = new File("data_patterns/" + profileName + ".data");
     if (primaryFile.exists()) {
       return primaryFile;
     }
     return new File("neuro_patterns/" + profileName + ".neuro");
   }
   
   private static class AimData {
     Vec3d targetPoint;
     float perfectYaw;
     float perfectPitch;
     float angleYaw;
     float anglePitch;
     double distance;
   }
   
   private static class Frame implements Serializable {
     private static final long serialVersionUID = 7L;
     float deltaYaw;
     float deltaPitch;
     float angleYaw;
     float anglePitch;
     double distance;
     boolean hasTarget;
     float smoothness;
   }
   
   private static class SaveData implements Serializable {
     private static final long serialVersionUID = 7L;
     List<NeuroPattern> patterns;
     List<NeuroAuraStorage.Frame> frames;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\NeuroAuraStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */