package shame.nazuna.client.modules.impl.movement;
 
 import net.minecraft.MinecraftClient;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 
 public class Sprint
   extends Module
 {
   public static Sprint INSTANCE = new Sprint();
   private static final MinecraftClient CLIENT = MinecraftClient.method_1551();
   private final BooleanSetting keepInWater = new BooleanSetting("Сохранять в воде", false); private static boolean sprinting;
   
   public Sprint() {
     super("Sprint", "Автоматический бег", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.keepInWater });
   }
   public static void setSprinting(boolean sprinting) { Sprint.sprinting = sprinting; }
   public static void setTime(long time) { Sprint.time = time; }
    private static long time = 0L;
   private static int pauseDepth = 0;
   
   private static boolean restoreAfterPause = false;
   private ClientPlayerEntity lastPlayer;
   
   public void onEnable() {
     resetPauseState();
     sprinting = true;
     super.onEnable();
   }
 
   
   public void onDisable() {
     resetPauseState();
     sprinting = false;
     this.lastPlayer = null;
     if (mc.field_1690 != null) {
       mc.field_1690.field_1867.method_23481(false);
     }
     if (mc.field_1724 != null) {
       mc.field_1724.method_5728(false);
     }
     super.onDisable();
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   @EventLink
   public void onEvent(EventUpdate ignored) {
     // Byte code:
     //   0: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   3: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   6: ifnonnull -> 40
     //   9: aload_0
     //   10: aconst_null
     //   11: putfield lastPlayer : Lnet/minecraft/ClientPlayerEntity;
     //   14: invokestatic resetPauseState : ()V
     //   17: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   20: getfield field_1690 : Lnet/minecraft/GameOptions;
     //   23: ifnull -> 39
     //   26: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   29: getfield field_1690 : Lnet/minecraft/GameOptions;
     //   32: getfield field_1867 : Lnet/minecraft/KeyBinding;
     //   35: iconst_0
     //   36: invokevirtual method_23481 : (Z)V
     //   39: return
     //   40: aload_0
     //   41: getfield lastPlayer : Lnet/minecraft/ClientPlayerEntity;
     //   44: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   47: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   50: if_acmpeq -> 70
     //   53: aload_0
     //   54: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   57: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   60: putfield lastPlayer : Lnet/minecraft/ClientPlayerEntity;
     //   63: invokestatic resetPauseState : ()V
     //   66: iconst_1
     //   67: putstatic shame/astra/client/modules/impl/movement/Sprint.sprinting : Z
     //   70: invokestatic isTargetProtocolBelowOneNineteen : ()Z
     //   73: istore_2
     //   74: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   77: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   80: invokevirtual method_5799 : ()Z
     //   83: ifne -> 98
     //   86: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   89: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   92: invokevirtual method_5869 : ()Z
     //   95: ifeq -> 102
     //   98: iconst_1
     //   99: goto -> 103
     //   102: iconst_0
     //   103: istore_3
     //   104: getstatic shame/astra/client/modules/impl/movement/Sprint.pauseDepth : I
     //   107: ifne -> 193
     //   110: invokestatic currentTimeMillis : ()J
     //   113: getstatic shame/astra/client/modules/impl/movement/Sprint.time : J
     //   116: lcmp
     //   117: iflt -> 193
     //   120: getstatic shame/astra/client/modules/impl/movement/Sprint.sprinting : Z
     //   123: ifeq -> 193
     //   126: invokestatic isMoving : ()Z
     //   129: ifeq -> 193
     //   132: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   135: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   138: getfield field_3913 : Lnet/minecraft/Input;
     //   141: getfield field_3905 : F
     //   144: fconst_0
     //   145: fcmpl
     //   146: ifle -> 193
     //   149: iload_2
     //   150: ifeq -> 177
     //   153: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   156: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   159: getfield field_5976 : Z
     //   162: ifne -> 193
     //   165: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   168: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   171: getfield field_34927 : Z
     //   174: ifne -> 193
     //   177: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   180: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   183: invokevirtual method_6128 : ()Z
     //   186: ifne -> 193
     //   189: iconst_1
     //   190: goto -> 194
     //   193: iconst_0
     //   194: istore #4
     //   196: aload_0
     //   197: getfield keepInWater : Lshame/astra/client/modules/settings/implement/BooleanSetting;
     //   200: invokevirtual isState : ()Z
     //   203: ifeq -> 225
     //   206: iload_3
     //   207: ifeq -> 225
     //   210: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   213: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   216: invokevirtual method_5624 : ()Z
     //   219: ifeq -> 225
     //   222: iconst_1
     //   223: istore #4
     //   225: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   228: getfield field_1690 : Lnet/minecraft/GameOptions;
     //   231: getfield field_1867 : Lnet/minecraft/KeyBinding;
     //   234: iload #4
     //   236: invokevirtual method_23481 : (Z)V
     //   239: getstatic shame/astra/client/modules/impl/movement/Sprint.mc : Lnet/minecraft/MinecraftClient;
     //   242: getfield field_1724 : Lnet/minecraft/ClientPlayerEntity;
     //   245: iload #4
     //   247: invokevirtual method_5728 : (Z)V
     //   250: return
     // Line number table:
     //   Java source line number -> byte code offset
     //   #59	-> 0
     //   #60	-> 9
     //   #61	-> 14
     //   #62	-> 17
     //   #63	-> 26
     //   #65	-> 39
     //   #68	-> 40
     //   #69	-> 53
     //   #70	-> 63
     //   #71	-> 66
     //   #74	-> 70
     //   #75	-> 74
     //   #76	-> 104
     //   #77	-> 110
     //   #79	-> 126
     //   #82	-> 183
     //   #84	-> 196
     //   #85	-> 222
     //   #88	-> 225
     //   #89	-> 239
     //   #90	-> 250
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	251	0	this	Lshame/astra/client/modules/impl/movement/Sprint;
     //   0	251	1	ignored	Lshame/astra/api/events/implement/EventUpdate;
     //   74	177	2	legacyProtocol	Z
     //   104	147	3	inWater	Z
     //   196	55	4	shouldSprint	Z
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public boolean shouldKeepSprintInWater() {
     return (isEnable() && this.keepInWater.isState());
   }
   
   public static void pushPause(long delayMs) {
     restoreAfterPause |= shouldRestoreAfterPause();
     pauseDepth++;
     time = Math.max(time, System.currentTimeMillis() + Math.max(0L, delayMs));
     sprinting = false;
     
     if (CLIENT.field_1690 != null) {
       CLIENT.field_1690.field_1867.method_23481(false);
     }
     
     if (CLIENT.field_1724 != null) {
       CLIENT.field_1724.method_5728(false);
     }
   }
   
   public static void popPause() {
     if (pauseDepth > 0) {
       pauseDepth--;
     }
     
     if (pauseDepth > 0) {
       return;
     }
     
     time = 0L;
     sprinting = restoreAfterPause;
     restoreAfterPause = false;
   }
   
   private static boolean shouldRestoreAfterPause() {
     if (CLIENT.field_1724 != null && CLIENT.field_1724.method_5624()) {
       return true;
     }
     
     return (ModuleClass.sprint != null && ModuleClass.sprint
       .isEnable() && sprinting);
   }
 
   
   private static void resetPauseState() {
     pauseDepth = 0;
     restoreAfterPause = false;
     time = 0L;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\Sprint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */