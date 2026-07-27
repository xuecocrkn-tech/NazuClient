package shame.nazuna.api.utils.baritone;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_238;
 import net.minecraft.class_243;
 import net.minecraft.class_310;
 import net.minecraft.class_3532;
 
 
 
 
 
 public final class BaritoneAntiStuck
 {
   private static final String PROTECTED_BLOCK_MESSAGE = "Извините, но вы не можете сломать блок здесь";
   private static final long STUCK_TIMEOUT_MS = 7000L;
   private static final double PROGRESS_DISTANCE_SQ = 1.0D;
   private static final int RECOVERY_TICKS = 12;
   private static final double PRIVATE_ESCAPE_DISTANCE_SQ = 2500.0D;
   private static final long PRIVATE_ESCAPE_TIMEOUT_MS = 25000L;
   private static final double SIDE_OFFSET = 0.95D;
   private static final double FORWARD_OFFSET = 0.35D;
   private static final String BARITONE_API_CLASS = "baritone.api.BaritoneAPI";
   private static final String INPUT_ENUM_CLASS = "baritone.api.utils.input.Input";
   private static class_243 anchorPos;
   private static long lastProgressAtMs;
   private static int recoveryTicksRemaining;
   private static boolean strafeRightNext;
   private static boolean privateEscapePending;
   private static boolean privateEscapeActive;
   private static boolean privateEscapeRight;
   private static class_243 privateEscapeStartPos;
   private static long privateEscapeStartedAtMs;
   
   public static void onGameMessage(String message) {
     if (message == null || !message.contains("Извините, но вы не можете сломать блок здесь")) {
       return;
     }
     privateEscapePending = true;
   }
   
   public static void tick() {
     class_310 mc = class_310.method_1551();
     if (mc.field_1724 == null || mc.field_1687 == null) {
       resetState();
       
       return;
     } 
     try {
       Object baritone = getPrimaryBaritone();
       if (baritone == null) {
         resetState();
         
         return;
       } 
       Object pathing = invoke(baritone, "getPathingBehavior");
       Object input = invoke(baritone, "getInputOverrideHandler");
       if (pathing == null || input == null || !Boolean.TRUE.equals(invoke(pathing, "isPathing"))) {
         clearRecovery(input);
         resetTracking();
         
         return;
       } 
       long now = System.currentTimeMillis();
       class_243 currentPos = mc.field_1724.method_19538();
       
       if (anchorPos == null) {
         anchorPos = currentPos;
         lastProgressAtMs = now;
       } 
       
       if (privateEscapePending && isMiningNow(mc, input)) {
         startPrivateEscape(mc, currentPos);
         privateEscapePending = false;
       } 
       
       if (privateEscapeActive) {
         if (currentPos.method_1025(privateEscapeStartPos) >= 2500.0D || now - privateEscapeStartedAtMs >= 25000L) {
           
           clearAllKeys(input);
           privateEscapeActive = false;
           anchorPos = currentPos;
           lastProgressAtMs = now;
           
           return;
         } 
         applyPrivateEscapeInput(mc, input);
         anchorPos = currentPos;
         lastProgressAtMs = now;
         
         return;
       } 
       if (recoveryTicksRemaining > 0) {
         applyRecoveryInput(mc, input);
         recoveryTicksRemaining--;
         if (recoveryTicksRemaining <= 0) {
           clearAllKeys(input);
           anchorPos = mc.field_1724.method_19538();
           lastProgressAtMs = now;
         } 
         
         return;
       } 
       if (isMiningNow(mc, input)) {
         anchorPos = currentPos;
         lastProgressAtMs = now;
         
         return;
       } 
       if (!isTryingToMove(input)) {
         anchorPos = currentPos;
         lastProgressAtMs = now;
         
         return;
       } 
       if (currentPos.method_1025(anchorPos) >= 1.0D) {
         anchorPos = currentPos;
         lastProgressAtMs = now;
         
         return;
       } 
       if (now - lastProgressAtMs < 7000L) {
         return;
       }
       
       recoveryTicksRemaining = 12;
       strafeRightNext = chooseRecoverySide(mc, strafeRightNext, true);
       applyRecoveryInput(mc, input);
       anchorPos = currentPos;
       lastProgressAtMs = now;
     } catch (Throwable ignored) {
       resetState();
     } 
   }
   
   private static Object getPrimaryBaritone() throws ReflectiveOperationException {
     Class<?> apiClass = Class.forName("baritone.api.BaritoneAPI");
     Object provider = apiClass.getMethod("getProvider", new Class[0]).invoke(null, new Object[0]);
     return (provider == null) ? null : provider.getClass().getMethod("getPrimaryBaritone", new Class[0]).invoke(provider, new Object[0]);
   }
   
   private static boolean isMiningNow(class_310 mc, Object input) throws ReflectiveOperationException {
     return ((mc.field_1761 != null && mc.field_1761.method_2923()) || 
       isInputForcedDown(input, "CLICK_LEFT"));
   }
   
   private static boolean isTryingToMove(Object input) throws ReflectiveOperationException {
     return (isInputForcedDown(input, "MOVE_FORWARD") || 
       isInputForcedDown(input, "MOVE_BACK") || 
       isInputForcedDown(input, "MOVE_LEFT") || 
       isInputForcedDown(input, "MOVE_RIGHT") || 
       isInputForcedDown(input, "JUMP"));
   }
   
   private static void startPrivateEscape(class_310 mc, class_243 currentPos) {
     privateEscapeActive = true;
     privateEscapeStartPos = currentPos;
     privateEscapeStartedAtMs = System.currentTimeMillis();
     privateEscapeRight = chooseRecoverySide(mc, privateEscapeRight, false);
   }
   
   private static void applyRecoveryInput(class_310 mc, Object input) throws ReflectiveOperationException {
     clearAllKeys(input);
     setInputForceState(input, "MOVE_FORWARD", true);
     setInputForceState(input, strafeRightNext ? "MOVE_RIGHT" : "MOVE_LEFT", true);
     if (mc.field_1724 != null && mc.field_1724.method_24828()) {
       setInputForceState(input, "JUMP", true);
     }
   }
   
   private static void applyPrivateEscapeInput(class_310 mc, Object input) throws ReflectiveOperationException {
     clearAllKeys(input);
     setInputForceState(input, "MOVE_BACK", true);
     setInputForceState(input, privateEscapeRight ? "MOVE_RIGHT" : "MOVE_LEFT", true);
     if (mc.field_1724 != null && mc.field_1724.method_24828()) {
       setInputForceState(input, "JUMP", true);
     }
   }
   
   private static boolean chooseRecoverySide(class_310 mc, boolean fallbackRight, boolean moveForward) {
     if (mc.field_1724 == null) {
       return fallbackRight;
     }
     
     double yawRad = Math.toRadians(mc.field_1724.method_36454());
     class_243 forwardDirection = new class_243(-class_3532.method_15374((float)yawRad), 0.0D, class_3532.method_15362((float)yawRad));
     class_243 left = new class_243(forwardDirection.field_1350, 0.0D, -forwardDirection.field_1352);
     class_243 right = left.method_1021(-1.0D);
     class_243 direction = moveForward ? forwardDirection : forwardDirection.method_1021(-1.0D);
     
     double leftScore = freeSpaceScore(mc, left.method_1021(0.95D).method_1019(direction.method_1021(0.35D)));
     double rightScore = freeSpaceScore(mc, right.method_1021(0.95D).method_1019(direction.method_1021(0.35D)));
     
     if (leftScore == rightScore) {
       return fallbackRight;
     }
     
     return (rightScore > leftScore);
   }
   
   private static double freeSpaceScore(class_310 mc, class_243 offset) {
     class_238 shifted = mc.field_1724.method_5829().method_997(offset);
     double score = 0.0D;
     if (mc.field_1687.method_8587((class_1297)mc.field_1724, shifted)) {
       score++;
     }
     if (mc.field_1687.method_8587((class_1297)mc.field_1724, shifted.method_989(0.0D, 1.0D, 0.0D))) {
       score += 0.35D;
     }
     return score;
   }
   
   private static void clearRecovery(Object input) {
     if (recoveryTicksRemaining > 0 && input != null) {
       try {
         clearAllKeys(input);
       } catch (ReflectiveOperationException reflectiveOperationException) {}
     }
     
     recoveryTicksRemaining = 0;
     if (privateEscapeActive && input != null) {
       try {
         clearAllKeys(input);
       } catch (ReflectiveOperationException reflectiveOperationException) {}
     }
     
     privateEscapeActive = false;
     privateEscapePending = false;
   }
   
   private static void resetTracking() {
     anchorPos = null;
     lastProgressAtMs = 0L;
   }
   
   private static void resetState() {
     recoveryTicksRemaining = 0;
     anchorPos = null;
     lastProgressAtMs = 0L;
     privateEscapePending = false;
     privateEscapeActive = false;
     privateEscapeStartPos = null;
     privateEscapeStartedAtMs = 0L;
   }
   
   private static boolean isInputForcedDown(Object inputOverrideHandler, String inputName) throws ReflectiveOperationException {
     Object input = getInputEnum(inputName);
 
     
     Object result = inputOverrideHandler.getClass().getMethod("isInputForcedDown", new Class[] { input.getClass() }).invoke(inputOverrideHandler, new Object[] { input });
     return Boolean.TRUE.equals(result);
   }
   
   private static void setInputForceState(Object inputOverrideHandler, String inputName, boolean forced) throws ReflectiveOperationException {
     Object input = getInputEnum(inputName);
     inputOverrideHandler.getClass()
       .getMethod("setInputForceState", new Class[] { input.getClass(), boolean.class
         }).invoke(inputOverrideHandler, new Object[] { input, Boolean.valueOf(forced) });
   }
   
   private static void clearAllKeys(Object inputOverrideHandler) throws ReflectiveOperationException {
     inputOverrideHandler.getClass().getMethod("clearAllKeys", new Class[0]).invoke(inputOverrideHandler, new Object[0]);
   }
   
   private static Object getInputEnum(String inputName) throws ReflectiveOperationException {
     Class<?> inputEnum = Class.forName("baritone.api.utils.input.Input");
     
     Object value = Enum.valueOf((Class)inputEnum.asSubclass(Enum.class), inputName);
     return value;
   }
   
   private static Object invoke(Object target, String methodName) throws ReflectiveOperationException {
     return target.getClass().getMethod(methodName, new Class[0]).invoke(target, new Object[0]);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\baritone\BaritoneAntiStuck.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */