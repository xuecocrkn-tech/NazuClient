package shame.nazuna.api.utils.combat;
 import net.minecraft.StatusEffects;
 import net.minecraft.LivingEntity;
 import net.minecraft.Items;
 import net.minecraft.BlockView;
 import net.minecraft.Blocks;
 import net.minecraft.Block;
 import net.minecraft.BlockPos;
 import net.minecraft.Position;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.BlockState;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 
 public final class IdealHitUtils implements QClient {
   private IdealHitUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   private static final int WATER_CRIT_INTENT_TICKS = 8; private static final int WATER_CRIT_CONTACT_TICKS = 10;
   private static final double WATER_CRIT_MIN_UPWARD_VELOCITY = 0.05D;
   private static int lastWaterContactAge = Integer.MIN_VALUE;
   private static int lastWaterCritIntentAge = Integer.MIN_VALUE;
   
   public static float getAICooldown() {
     if (mc.field_1724.method_6047().method_7909() == Items.field_8162) return 0.9F;
     
     if (mc.field_1724.method_6047().method_7909() instanceof net.minecraft.AxeItem || mc.field_1724.method_6047().method_7909() instanceof net.minecraft.ShovelItem)
       return 0.95F; 
     return 0.93F;
   }
   
   public static boolean canAIFall() {
     BlockPos posWater = BlockPos.method_49638((Position)mc.field_1724.method_19538().method_1031(0.0D, -0.4000000059604645D, 0.0D));
     if (mc.field_1687.method_8320(posWater).method_27852(Blocks.field_10382)) return true; 
     return ((getBlock(0.0D, 3.0D, 0.0D) == Blocks.field_10124 && getBlock(0.0D, 2.0D, 0.0D) == Blocks.field_10124 && getBlock(0.0D, 1.0D, 0.0D) == Blocks.field_10124) || mc.field_1724.field_6017 < (
       (getBlock(0.0D, 2.0D, 0.0D) != Blocks.field_10124) ? 0.08F : 0.6F) || mc.field_1724.field_6017 > 1.2F);
   }
 
   
   public static boolean canCritical(LivingEntity target) {
     updateWaterCritState();
     
     boolean packetCrits = ModuleClass.packetCriticals.isEnable();
     boolean hasSlowFalling = mc.field_1724.method_6059(StatusEffects.field_5906);
     boolean inCobweb = isInCobweb();
     boolean smartCrit = ModuleClass.aura.smartCrit.isState();
     
     if (packetCrits && inCobweb) {
       return true;
     }
     
     if (packetCrits && hasSlowFalling) {
       return ((mc.field_1724.method_18798()).field_1351 < 0.0D && mc.field_1724.field_6017 > 0.0F);
     }
     
     if (isTryingWaterCrit()) {
       return isWaterCritWindow();
     }
 
     
     boolean isCritPossible = (!mc.field_1724.method_24828() && (mc.field_1724.method_18798()).field_1351 < 0.0D && mc.field_1724.field_6017 > 0.0F);
 
     
     if (isNoJumpDelayCeilingCritIntent()) {
       return isNoJumpDelayCeilingCritWindow();
     }
     
     if (isNoJumpDelayJumpCritIntent()) {
       return isNoJumpDelayJumpCritWindow();
     }
     
     if (cannotPerformCrit()) {
       return true;
     }
     
     if (smartCrit) {
       return (mc.field_1724.method_24828() || isCritPossible);
     }
     
     return isCritPossible;
   }
   
   private static boolean isNoJumpDelayCeilingCritIntent() {
     return (ModuleClass.noJumpDelay.isEnable() && mc.field_1690 != null && mc.field_1690.field_1903
       
       .method_1434() && 
       hasLowCeilingForJumpCrit());
   }
   
   private static boolean isNoJumpDelayJumpCritIntent() {
     return (ModuleClass.noJumpDelay.isEnable() && mc.field_1690 != null && mc.field_1690.field_1903
       
       .method_1434());
   }
   
   private static boolean isNoJumpDelayCeilingCritWindow() {
     return (mc.field_1724 != null && 
       !mc.field_1724.method_24828() && 
       (mc.field_1724.method_18798()).field_1351 <= 0.01D && 
       !mc.field_1724.method_5799() && 
       !mc.field_1724.method_5869() && 
       !mc.field_1724.method_5771() && 
       !mc.field_1724.method_6101() && 
       !mc.field_1724.method_5765() && 
       !(mc.field_1724.method_31549()).field_7479);
   }
   
   public static boolean isNoJumpDelayJumpCritWindow() {
     if (mc.field_1724 != null && mc.field_1687 != null) if (ModuleClass.noJumpDelay
         
         .isEnable() && mc.field_1690 != null && mc.field_1690.field_1903
         
         .method_1434() && 
         !mc.field_1724.method_24828() && 
         (mc.field_1724.method_18798()).field_1351 < 0.0D && 
         !mc.field_1724.method_5799() && 
         !mc.field_1724.method_5869() && 
         !mc.field_1724.method_5771() && 
         !mc.field_1724.method_6101() && 
         !mc.field_1724.method_5765() && 
         !(mc.field_1724.method_31549()).field_7479 && 
         !mc.field_1724.method_6059(StatusEffects.field_5902) && 
         !mc.field_1724.method_6059(StatusEffects.field_5906) && 
         !mc.field_1724.method_6059(StatusEffects.field_5919) && 
         !mc.field_1724.method_6128() && 
         !isInCobweb()); 
     return false;
   }
   private static boolean hasLowCeilingForJumpCrit() {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return false;
     }
     
     Box box = mc.field_1724.method_5829().method_1011(0.03D);
     Box headBox = new Box(box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325 + 0.32D, box.field_1324);
 
 
 
 
 
 
 
     
     for (BlockPos pos : BlockPos.method_10094(
         MathHelper.method_15357(headBox.field_1323), MathHelper.method_15357(headBox.field_1322), MathHelper.method_15357(headBox.field_1321), 
         MathHelper.method_15357(headBox.field_1320), MathHelper.method_15357(headBox.field_1325), MathHelper.method_15357(headBox.field_1324))) {
       BlockState state = mc.field_1687.method_8320(pos);
       if (!state.method_26215() && !state.method_26220((BlockView)mc.field_1687, pos).method_1110()) {
         return true;
       }
     } 
     
     return false;
   }
   
   public static boolean canPacketCrit() {
     return (isInCobweb() || mc.field_1724.method_6059(StatusEffects.field_5906));
   }
   
   private static void updateWaterCritState() {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       lastWaterContactAge = Integer.MIN_VALUE;
       lastWaterCritIntentAge = Integer.MIN_VALUE;
       
       return;
     } 
     boolean nearWaterSurface = isNearWaterSurface();
     if (!nearWaterSurface) {
       return;
     }
     
     lastWaterContactAge = mc.field_1724.field_6012;
     
     if (isWaterCritIntentState()) {
       lastWaterCritIntentAge = mc.field_1724.field_6012;
     }
   }
   
   private static boolean isWaterCritIntentState() {
     if (mc.field_1724 == null || mc.field_1690 == null) {
       return false;
     }
     
     return (mc.field_1690.field_1903.method_1434() && 
       !mc.field_1724.method_24828() && 
       !mc.field_1724.method_5869() && 
       (mc.field_1724.method_18798()).field_1351 > 0.05D);
   }
   
   private static boolean isTryingWaterCrit() {
     if (mc.field_1724 == null || mc.field_1690 == null || !mc.field_1690.field_1903.method_1434()) {
       return false;
     }
     
     return (mc.field_1724.field_6012 - lastWaterCritIntentAge <= 8 && mc.field_1724.field_6012 - lastWaterContactAge <= 10);
   }
 
   
   private static boolean isWaterCritWindow() {
     return (mc.field_1724 != null && 
       !mc.field_1724.method_24828() && 
       !mc.field_1724.method_5799() && 
       !mc.field_1724.method_5869() && mc.field_1724.field_6017 > 0.0F && 
       
       (mc.field_1724.method_18798()).field_1351 < 0.0D);
   }
   
   private static boolean isNearWaterSurface() {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return false;
     }
     
     BlockPos below = BlockPos.method_49638((Position)mc.field_1724.method_19538().method_1031(0.0D, -0.4000000059604645D, 0.0D));
     return (mc.field_1724.method_5799() || mc.field_1724
       .method_5869() || mc.field_1687
       .method_8320(below).method_27852(Blocks.field_10382));
   }
   
   private static boolean cannotPerformCrit() {
     double effectiveJumpHeight = mc.field_1724.method_49476();
     Vec3d jumpVec = new Vec3d(0.0D, effectiveJumpHeight, 0.0D);
     Vec3d allowedMovement = ((IEntity)mc.field_1724).invokeAdjustMovementForCollisions(jumpVec);
     
     boolean cobweb = isInCobweb();
     
     BlockPos posWater = BlockPos.method_49638((Position)mc.field_1724.method_19538().method_1031(0.0D, (mc.field_1724.method_17682() / 2.0F), 0.0D));
     
     return (mc.field_1724.method_5771() || mc.field_1724
       .method_6101() || mc.field_1687
       .method_8320(posWater).method_27852(Blocks.field_10382) || mc.field_1724
       .method_6059(StatusEffects.field_5902) || mc.field_1724
       .method_6059(StatusEffects.field_5906) || mc.field_1724
       .method_6059(StatusEffects.field_5919) || cobweb || mc.field_1724
       
       .method_6128() || mc.field_1724
       .method_5765() || 
       (mc.field_1724.method_31549()).field_7479 || mc.field_1724
       .method_5799() || (allowedMovement.field_1351 < mc.field_1724
       .method_49476() - 0.5D && mc.field_1724.method_24828()));
   }
   
   public static boolean isInCobweb() {
     Box box = mc.field_1724.method_5829();
     for (BlockPos pos : BlockPos.method_10094(
         MathHelper.method_15357(box.field_1323), MathHelper.method_15357(box.field_1322), MathHelper.method_15357(box.field_1321), 
         MathHelper.method_15357(box.field_1320), MathHelper.method_15357(box.field_1325), MathHelper.method_15357(box.field_1324))) {
       if (mc.field_1687.method_8320(pos).method_27852(Blocks.field_10343)) {
         return true;
       }
     } 
     return false;
   }
   
   public static Block getBlock(double x, double y, double z) {
     return mc.field_1687.method_8320(mc.field_1724.method_24515().method_10069((int)x, (int)y, (int)z)).method_26204();
   }
   
   public static boolean findFall(float fallDistance) {
     Vec3d rotationVec = mc.field_1724.method_5720();
     double tempVelocityX = (mc.field_1724.method_18798()).field_1352;
     double tempVelocityY = (mc.field_1724.method_18798()).field_1351;
     double tempVelocityZ = (mc.field_1724.method_18798()).field_1350;
     
     float n = MathHelper.method_15362(mc.field_1724.method_36455() * 0.017453292F);
     n = (float)((n * n) * Math.min(rotationVec.method_1033() / 0.4D, 1.0D));
     
     Vec3d vec3d = (new Vec3d(tempVelocityX, tempVelocityY, tempVelocityZ)).method_1031(0.0D, 0.08D * (-1.0D + n * 0.75D), 0.0D);
     tempVelocityY = vec3d.field_1351 * 0.9800000190734863D;
     
     return (tempVelocityY < fallDistance);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\combat\IdealHitUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */