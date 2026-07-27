package shame.nazuna.client.modules.impl.player;
 
 import java.util.Comparator;
 import net.minecraft.class_1297;
 import net.minecraft.class_1309;
 import net.minecraft.class_1657;
 import net.minecraft.class_1684;
 import net.minecraft.class_1799;
 import net.minecraft.class_1802;
 import net.minecraft.class_1935;
 import net.minecraft.class_2246;
 import net.minecraft.class_2338;
 import net.minecraft.class_2374;
 import net.minecraft.class_238;
 import net.minecraft.class_239;
 import net.minecraft.class_241;
 import net.minecraft.class_243;
 import net.minecraft.class_2596;
 import net.minecraft.class_2828;
 import net.minecraft.class_3532;
 import net.minecraft.class_3959;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventMoveInput;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class TargetPearl extends Module {
   private static final double MAX_TRACK_DISTANCE = 256.0D;
   private static final double MIN_LANDING_DISTANCE = 11.0D;
   private static final long LOCAL_THROW_COOLDOWN_MS = 2500L;
   public static final TargetPearl INSTANCE = new TargetPearl();
   private static final float DIRECT_MIN_PITCH = -25.0F;
   private final ModeSetting mode = new ModeSetting("Тип", "Автоматический", new String[] { "По бинду", "Автоматический" }); private static final float DIRECT_MAX_PITCH = 35.0F; private static final float PITCH_STEP = 0.25F;
   private final BindSetting bind = (new BindSetting("Бинд", -1))
     .visible(() -> Boolean.valueOf(this.mode.is("По бинду")));
   private final BooleanSetting onlyTarget = new BooleanSetting("Только за противником", false);
   private final BooleanSetting ignoreFriends = new BooleanSetting("Игнорировать друзей", true);
   
   private final TimerUtils timer = new TimerUtils();
   
   private class_1684 targetPearl;
   private int lastHandledPearlId = -1;
   private long nextThrowAt;
   private boolean isThrowing;
   private class_241 serverRotation;
   
   public TargetPearl() {
     super("TargetPearl", "Автоматически бросает жемчуг в цель", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.bind, (Setting)this.onlyTarget, (Setting)this.ignoreFriends });
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1755 != null) {
       return;
     }
     
     if (!this.mode.is("По бинду") || event.getKey() != this.bind.getKey()) {
       return;
     }
     
     if (canThrowNow()) {
       aimAndThrowPearl();
     }
   }
 
 
 
 
 
 
 
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     // Byte code:
     //   0: getstatic shame/astra/client/modules/impl/player/TargetPearl.mc : Lnet/minecraft/class_310;
     //   3: getfield field_1724 : Lnet/minecraft/class_746;
     //   6: ifnull -> 18
     //   9: getstatic shame/astra/client/modules/impl/player/TargetPearl.mc : Lnet/minecraft/class_310;
     //   12: getfield field_1687 : Lnet/minecraft/class_638;
     //   15: ifnonnull -> 23
     //   18: aload_0
     //   19: invokevirtual resetThrowState : ()V
     //   22: return
     //   23: aload_0
     //   24: getfield lastHandledPearlId : I
     //   27: iconst_m1
     //   28: if_icmpeq -> 69
     //   31: getstatic shame/astra/client/modules/impl/player/TargetPearl.mc : Lnet/minecraft/class_310;
     //   34: getfield field_1687 : Lnet/minecraft/class_638;
     //   37: aload_0
     //   38: getfield lastHandledPearlId : I
     //   41: invokevirtual method_8469 : (I)Lnet/minecraft/class_1297;
     //   44: astore_2
     //   45: aload_2
     //   46: instanceof net/minecraft/class_1684
     //   49: ifeq -> 64
     //   52: aload_2
     //   53: checkcast net/minecraft/class_1684
     //   56: astore_3
     //   57: aload_3
     //   58: invokevirtual method_5805 : ()Z
     //   61: ifne -> 69
     //   64: aload_0
     //   65: iconst_m1
     //   66: putfield lastHandledPearlId : I
     //   69: aload_0
     //   70: getfield mode : Lshame/astra/client/modules/settings/implement/ModeSetting;
     //   73: ldc 'Автоматический'
     //   75: invokevirtual is : (Ljava/lang/String;)Z
     //   78: ifeq -> 92
     //   81: aload_0
     //   82: invokevirtual canThrowNow : ()Z
     //   85: ifeq -> 92
     //   88: aload_0
     //   89: invokevirtual aimAndThrowPearl : ()V
     //   92: return
     // Line number table:
     //   Java source line number -> byte code offset
     //   #78	-> 0
     //   #79	-> 18
     //   #80	-> 22
     //   #83	-> 23
     //   #84	-> 31
     //   #85	-> 45
     //   #86	-> 64
     //   #90	-> 69
     //   #91	-> 88
     //   #93	-> 92
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   57	7	3	pearl	Lnet/minecraft/class_1684;
     //   45	24	2	handled	Lnet/minecraft/class_1297;
     //   0	93	0	this	Lshame/astra/client/modules/impl/player/TargetPearl;
     //   0	93	1	event	Lshame/astra/api/events/implement/EventUpdate;
   }
 
 
 
 
 
 
 
   
   @EventLink
   public void onMoveInput(EventMoveInput event) {
     if (!isEnable() || !this.isThrowing || this.serverRotation == null) {
       return;
     }
     
     float forward = event.getForward();
     float strafe = event.getStrafe();
     if (forward == 0.0F && strafe == 0.0F) {
       return;
     }
     
     double targetAngle = class_3532.method_15338(Math.toDegrees(direction(this.serverRotation.field_1343, forward, strafe)));
     float bestForward = 0.0F;
     float bestStrafe = 0.0F;
     float smallestDifference = Float.MAX_VALUE;
     float testForward;
     for (testForward = -1.0F; testForward <= 1.0F; testForward++) {
       float testStrafe; for (testStrafe = -1.0F; testStrafe <= 1.0F; testStrafe++) {
         if (testForward != 0.0F || testStrafe != 0.0F) {
 
 
           
           double testAngle = class_3532.method_15338(Math.toDegrees(direction(this.serverRotation.field_1343, testForward, testStrafe)));
           float difference = Math.abs(class_3532.method_15393((float)(targetAngle - testAngle)));
           if (difference < smallestDifference) {
             smallestDifference = difference;
             bestForward = testForward;
             bestStrafe = testStrafe;
           } 
         } 
       } 
     } 
     event.setForward(bestForward);
     event.setStrafe(bestStrafe);
   }
 
   
   public void onDisable() {
     resetThrowState();
     this.lastHandledPearlId = -1;
     this.nextThrowAt = 0L;
     this.timer.reset();
     super.onDisable();
   }
   
   private boolean canThrowNow() {
     if (System.currentTimeMillis() < this.nextThrowAt) {
       return false;
     }
     return (!mc.field_1724.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8634)) && this.timer
       .finished(1000L));
   }
   
   private void aimAndThrowPearl() {
     class_243 landingPosition = getTargetPearlLandingPosition();
     if (landingPosition == null) {
       resetThrowState();
       
       return;
     } 
     float[] rotations = calculateYawPitch(landingPosition);
     if (rotations == null || Float.isNaN(rotations[0]) || Float.isNaN(rotations[1])) {
       resetThrowState();
       
       return;
     } 
     class_243 trajectoryLanding = checkTrajectory(rotations[0], rotations[1]);
     double allowedError = Math.max(3.0D, mc.field_1724.method_19538().method_1022(landingPosition) * 0.12D);
     if (trajectoryLanding == null || landingPosition.method_1022(trajectoryLanding) > allowedError) {
       resetThrowState();
       
       return;
     } 
     if (!hasPearl()) {
       resetThrowState();
       
       return;
     } 
     float previousYaw = mc.field_1724.method_36454();
     float previousPitch = mc.field_1724.method_36455();
     this.isThrowing = true;
     this.serverRotation = new class_241(rotations[0], rotations[1]);
     
     try {
       mc.field_1724.method_36456(rotations[0]);
       mc.field_1724.method_36457(rotations[1]);
       mc.field_1724.field_3944.method_52787((class_2596)new class_2828.class_2831(rotations[0], rotations[1], mc.field_1724
             .method_24828(), mc.field_1724.field_5976));
       
       InventoryUtils.swapAndUseHvH(class_1802.field_8634);
       this.timer.reset();
       this.nextThrowAt = System.currentTimeMillis() + 2500L;
       if (this.targetPearl != null) {
         this.lastHandledPearlId = this.targetPearl.method_5628();
       }
     } finally {
       mc.field_1724.method_36456(previousYaw);
       mc.field_1724.method_36457(previousPitch);
       resetThrowState();
     } 
   }
   
   private class_243 getTargetPearlLandingPosition() {
     this.targetPearl = getTargetPearl();
     if (this.targetPearl == null || !this.targetPearl.method_5805()) {
       return null;
     }
     
     class_243 landingPos = predictPearlLanding(this.targetPearl);
     if (landingPos == null || !isWithinRange(landingPos)) {
       return null;
     }
     
     return landingPos;
   }
   
   private class_1684 getTargetPearl() {
     class_238 searchBox = mc.field_1724.method_5829().method_1014(256.0D);
     class_1309 auraTarget = (ModuleClass.INSTANCE != null) ? ModuleClass.aura.getTarget() : null;
     
     return mc.field_1687.method_8333((class_1297)mc.field_1724, searchBox, entity -> {
 
 
 
           
           // Byte code:
           //   0: aload_2
           //   1: instanceof net/minecraft/class_1684
           //   4: ifeq -> 80
           //   7: aload_2
           //   8: checkcast net/minecraft/class_1684
           //   11: astore_3
           //   12: aload_3
           //   13: invokevirtual method_5805 : ()Z
           //   16: ifeq -> 80
           //   19: aload_3
           //   20: invokevirtual method_24921 : ()Lnet/minecraft/class_1297;
           //   23: getstatic shame/astra/client/modules/impl/player/TargetPearl.mc : Lnet/minecraft/class_310;
           //   26: getfield field_1724 : Lnet/minecraft/class_746;
           //   29: if_acmpeq -> 80
           //   32: aload_3
           //   33: invokevirtual method_5628 : ()I
           //   36: aload_0
           //   37: getfield lastHandledPearlId : I
           //   40: if_icmpeq -> 80
           //   43: aload_0
           //   44: aload_3
           //   45: invokevirtual method_24921 : ()Lnet/minecraft/class_1297;
           //   48: invokevirtual isIgnoredFriend : (Lnet/minecraft/class_1297;)Z
           //   51: ifne -> 80
           //   54: aload_0
           //   55: getfield onlyTarget : Lshame/astra/client/modules/settings/implement/BooleanSetting;
           //   58: invokevirtual isState : ()Z
           //   61: ifeq -> 76
           //   64: aload_1
           //   65: ifnull -> 80
           //   68: aload_3
           //   69: invokevirtual method_24921 : ()Lnet/minecraft/class_1297;
           //   72: aload_1
           //   73: if_acmpne -> 80
           //   76: iconst_1
           //   77: goto -> 81
           //   80: iconst_0
           //   81: ireturn
           // Line number table:
           //   Java source line number -> byte code offset
           //   #221	-> 0
           //   #216	-> 7
           //   #217	-> 13
           //   #218	-> 20
           //   #219	-> 33
           //   #220	-> 45
           //   #221	-> 58
           // Local variable table:
           //   start	length	slot	name	descriptor
           //   12	68	3	pearl	Lnet/minecraft/class_1684;
           //   0	82	0	this	Lshame/astra/client/modules/impl/player/TargetPearl;
           //   0	82	1	auraTarget	Lnet/minecraft/class_1309;
           //   0	82	2	entity	Lnet/minecraft/class_1297;
         }).stream()
       .map(entity -> (class_1684)entity)
       .filter(pearl -> (getHorizontalDistanceTo(pearl) <= 256.0D))
       .min(Comparator.comparingDouble(this::getHorizontalDistanceTo))
       .orElse(null);
   }
   private boolean isIgnoredFriend(class_1297 owner) {
     class_1657 player;
     if (this.ignoreFriends.isState() && owner instanceof class_1657) { player = (class_1657)owner; }
     else { return false; }
     
     return (astra.INSTANCE != null && astra.INSTANCE.friendStorage != null && astra.INSTANCE.friendStorage
       
       .isFriend(player.method_5477().getString()));
   }
   
   private double getHorizontalDistanceTo(class_1684 pearl) {
     class_243 playerPos = mc.field_1724.method_19538();
     class_243 pearlPos = pearl.method_19538();
     double dx = pearlPos.field_1352 - playerPos.field_1352;
     double dz = pearlPos.field_1350 - playerPos.field_1350;
     return Math.sqrt(dx * dx + dz * dz);
   }
   
   private class_243 predictPearlLanding(class_1684 pearl) {
     class_243 position = pearl.method_19538();
     class_243 velocity = pearl.method_18798();
     class_243 lastPosition = position;
     
     for (int i = 0; i < 200; i++) {
       lastPosition = position;
       position = position.method_1019(velocity);
       
       if (hitsBlock(lastPosition, position) || position.field_1351 <= mc.field_1687.method_31607()) {
         return new class_243(class_3532.method_15357(lastPosition.field_1352) + 0.5D, class_3532.method_15357(lastPosition.field_1351), class_3532.method_15357(lastPosition.field_1350) + 0.5D);
       }
       
       velocity = updatePearlMotion(velocity, position);
     } 
     
     return new class_243(class_3532.method_15357(lastPosition.field_1352) + 0.5D, class_3532.method_15357(lastPosition.field_1351), class_3532.method_15357(lastPosition.field_1350) + 0.5D);
   }
   
   private class_243 updatePearlMotion(class_243 motion, class_243 position) {
     class_2338 blockPos = class_2338.method_49638((class_2374)position);
     if (mc.field_1687.method_8320(blockPos).method_27852(class_2246.field_10382)) {
       return motion.method_1021(0.8D).method_1031(0.0D, -0.03D, 0.0D);
     }
     return motion.method_1021(0.99D).method_1031(0.0D, -0.03D, 0.0D);
   }
   
   private boolean isWithinRange(class_243 landingPos) {
     double distanceToLanding = mc.field_1724.method_19538().method_1022(landingPos);
     return (distanceToLanding >= 11.0D && distanceToLanding <= 256.0D);
   }
   
   private float[] calculateYawPitch(class_243 targetPosition) {
     class_243 playerPosition = mc.field_1724.method_19538();
     double dx = targetPosition.field_1352 - playerPosition.field_1352;
     double dy = targetPosition.field_1351 - mc.field_1724.method_23320();
     double dz = targetPosition.field_1350 - playerPosition.field_1350;
     float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
     double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
     double allowedError = Math.max(1.5D, mc.field_1724.method_19538().method_1022(targetPosition) * 0.08D);
     TrajectoryCandidate directCandidate = findBestCandidate(targetPosition, yaw, -25.0F, 35.0F, allowedError, true);
     if (directCandidate != null) {
       return new float[] { yaw, class_3532.method_15363(directCandidate.pitch, -90.0F, 90.0F) };
     }
     
     TrajectoryCandidate fallbackCandidate = findBestCandidate(targetPosition, yaw, -85.0F, 85.0F, allowedError, false);
     if (fallbackCandidate == null) {
       double fallbackPitch = -Math.toDegrees(Math.atan2(dy, horizontalDistance)) + 5.0D;
       return new float[] { yaw, class_3532.method_15363((float)fallbackPitch, -90.0F, 90.0F) };
     } 
     
     return new float[] { yaw, class_3532.method_15363(fallbackCandidate.pitch, -90.0F, 90.0F) };
   }
   
   private TrajectoryCandidate findBestCandidate(class_243 targetPosition, float yaw, float minPitch, float maxPitch, double allowedError, boolean preferDirect) {
     class_243 playerPosition = mc.field_1724.method_19538();
     double velocity = 1.5D;
     TrajectoryCandidate bestCandidate = null;
     float pitch;
     for (pitch = minPitch; pitch <= maxPitch; pitch += 0.25F) {
       float pitchRad = (float)Math.toRadians(pitch);
       double vx = (-class_3532.method_15374((float)Math.toRadians(yaw)) * class_3532.method_15362(pitchRad)) * velocity;
       double vy = -class_3532.method_15374(pitchRad) * velocity;
       double vz = (class_3532.method_15362((float)Math.toRadians(yaw)) * class_3532.method_15362(pitchRad)) * velocity;
       class_243 pos = new class_243(playerPosition.field_1352, mc.field_1724.method_23320(), playerPosition.field_1350);
       class_243 motion = new class_243(vx, vy, vz);
       
       int ticks = 0;
       for (int i = 0; i < 200; ) {
         class_243 previous = pos;
         pos = pos.method_1019(motion);
         motion = updatePearlMotion(motion, pos);
         ticks++;
         
         if (hitsEntity(previous, pos)) {
           break;
         }
         
         if (!hitsBlock(previous, pos) && pos.field_1351 > mc.field_1687.method_31607()) {
           i++;
           continue;
         } 
         double distanceToTarget = pos.method_1022(targetPosition);
         TrajectoryCandidate candidate = new TrajectoryCandidate(pitch, distanceToTarget, ticks, pos);
         if (isBetterCandidate(candidate, bestCandidate, allowedError, preferDirect)) {
           bestCandidate = candidate;
         }
       } 
     } 
 
     
     if (bestCandidate == null || bestCandidate.distanceToTarget > allowedError) {
       return null;
     }
     return bestCandidate;
   }
   
   private boolean isBetterCandidate(TrajectoryCandidate candidate, TrajectoryCandidate currentBest, double allowedError, boolean preferDirect) {
     if (currentBest == null) {
       return true;
     }
     
     boolean candidateAccurate = (candidate.distanceToTarget <= allowedError);
     boolean bestAccurate = (currentBest.distanceToTarget <= allowedError);
     if (candidateAccurate != bestAccurate) {
       return candidateAccurate;
     }
     
     if (preferDirect && candidateAccurate && bestAccurate) {
       float candidatePitchAbs = Math.abs(candidate.pitch);
       float bestPitchAbs = Math.abs(currentBest.pitch);
       if (Math.abs(candidatePitchAbs - bestPitchAbs) > 0.01F) {
         return (candidatePitchAbs < bestPitchAbs);
       }
       if (candidate.ticks != currentBest.ticks) {
         return (candidate.ticks < currentBest.ticks);
       }
     } 
     
     if (Math.abs(candidate.distanceToTarget - currentBest.distanceToTarget) > 0.01D) {
       return (candidate.distanceToTarget < currentBest.distanceToTarget);
     }
     
     if (candidate.ticks != currentBest.ticks) {
       return (candidate.ticks < currentBest.ticks);
     }
     
     if (!preferDirect) {
       return (Math.abs(candidate.pitch) < Math.abs(currentBest.pitch));
     }
     
     return false;
   }
   
   private class_243 checkTrajectory(float yaw, float pitch) {
     float yawRad = (float)Math.toRadians(yaw);
     float pitchRad = (float)Math.toRadians(pitch);
     double velocity = 1.5D;
     
     double x = mc.field_1724.method_23317() - (class_3532.method_15362(yawRad) * 0.16F);
     double y = mc.field_1724.method_23318() + mc.field_1724.method_18381(mc.field_1724.method_18376()) - 0.1D;
     double z = mc.field_1724.method_23321() - (class_3532.method_15374(yawRad) * 0.16F);
     
     double motionX = (-class_3532.method_15374(yawRad) * class_3532.method_15362(pitchRad)) * velocity;
     double motionY = -class_3532.method_15374(pitchRad) * velocity;
     double motionZ = (class_3532.method_15362(yawRad) * class_3532.method_15362(pitchRad)) * velocity;
     
     class_243 position = new class_243(x, y, z);
     class_243 motion = new class_243(motionX, motionY, motionZ);
     
     for (int i = 0; i <= 200; i++) {
       class_243 previous = position;
       position = position.method_1019(motion);
       motion = updatePearlMotion(motion, position);
       
       if (hitsEntity(previous, position)) {
         return null;
       }
       
       if (hitsBlock(previous, position) || position.field_1351 <= mc.field_1687.method_31607()) {
         return new class_243(class_3532.method_15357(position.field_1352) + 0.5D, class_3532.method_15357(position.field_1351), class_3532.method_15357(position.field_1350) + 0.5D);
       }
     } 
     
     return null;
   }
   
   private boolean hitsBlock(class_243 from, class_243 to) {
     return 
 
 
 
 
       
       (mc.field_1687.method_17742(new class_3959(from, to, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)mc.field_1724)).method_17783() == class_239.class_240.field_1332);
   }
   
   private boolean hitsEntity(class_243 from, class_243 to) {
     class_238 searchBox = (new class_238(from, to)).method_1014(0.3D);
     for (class_1297 entity : mc.field_1687.method_8333((class_1297)mc.field_1724, searchBox, entity -> 
         (!entity.method_5805() || entity.method_7325() || entity.field_5960) ? false : ((entity == this.targetPearl) ? false : (!(entity instanceof class_1684))))) {
 
 
 
 
 
 
       
       if (entity.method_5829().method_1014(0.25D).method_992(from, to).isPresent()) {
         return true;
       }
     } 
     return false;
   }
   
   private boolean hasPearl() {
     return (mc.field_1724.method_6047().method_31574(class_1802.field_8634) || mc.field_1724
       .method_6079().method_31574(class_1802.field_8634) || 
       InventoryUtils.find(class_1802.field_8634, 0, 8) != -1 || 
       InventoryUtils.find(class_1802.field_8634, 9, 45) != -1);
   }
   
   private void resetThrowState() {
     this.isThrowing = false;
     this.targetPearl = null;
     this.serverRotation = null;
   }
   
   private static double direction(float rotationYaw, float moveForward, float moveStrafing) {
     if (moveForward < 0.0F) rotationYaw += 180.0F; 
     float forward = 1.0F;
     if (moveForward < 0.0F) { forward = -0.5F; }
     else if (moveForward > 0.0F) { forward = 0.5F; }
      if (moveStrafing > 0.0F) rotationYaw -= 90.0F * forward; 
     if (moveStrafing < 0.0F) rotationYaw += 90.0F * forward; 
     return Math.toRadians(rotationYaw);
   }
   
   private static final class TrajectoryCandidate {
     private final float pitch;
     private final double distanceToTarget;
     private final int ticks;
     private final class_243 landingPos;
     
     private TrajectoryCandidate(float pitch, double distanceToTarget, int ticks, class_243 landingPos) {
       this.pitch = pitch;
       this.distanceToTarget = distanceToTarget;
       this.ticks = ticks;
       this.landingPos = landingPos;
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\TargetPearl.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */