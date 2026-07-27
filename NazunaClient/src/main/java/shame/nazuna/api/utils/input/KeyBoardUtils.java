package shame.nazuna.api.utils.input;
 import it.unimi.dsi.fastutil.objects.ObjectArrayList;
 import org.lwjgl.glfw.GLFW;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.Module;
 
 public final class KeyBoardUtils implements QClient {
   public static final int MOUSE_BUTTON_OFFSET = 1000;
   
   private KeyBoardUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
 
   
   public static void call(int key, int action) {
     if (key <= -1) {
       return;
     }
     if (action == 1) {
       if (key == 344) {
         ClientSoundPlayer.playSound("opengui.wav", 0.6D, 1.0F);
         mc.method_1507((Screen)new MenuPanel());
       } 
       if (key == ModuleClass.autoBuy.openKey.getKey()) {
         mc.method_1507((Screen)new AutoBuy());
       }
       
       (new EventBinding(key, EventBinding.BindType.KEYBOARD)).call();
       
       ObjectArrayList<Module> modules = ModuleClass.INSTANCE.getObject();
       for (int i = 0, size = modules.size(); i < size; i++) {
         Module module = (Module)modules.get(i);
         if (module.getKey() == key) {
           module.toggle();
         }
       } 
     } 
   }
   
   public static String getKeyName(int keyCode) {
     if (keyCode == -1) return "None"; 
     String name = GLFW.glfwGetKeyName(keyCode, 0);
     if (name != null) return name.toUpperCase(); 
     switch (keyCode) { case 256: 
       case 32: 
       case 340: 
       case 344: 
       case 341:
       
       case 345:
        }  return "KEY" + keyCode;
   }
 
 
   
   public static void callMouse(int button, int action) {
     if (mc.field_1755 != null) {
       return;
     }
     
     if (button < 0) {
       return;
     }
     
     if (action == 1) {
       int mouseKey = 1000 + button;
       
       (new EventBinding(mouseKey, EventBinding.BindType.MOUSE)).call();
       
       ObjectArrayList<Module> modules = ModuleClass.INSTANCE.getObject();
       for (int i = 0, size = modules.size(); i < size; i++) {
         Module module = (Module)modules.get(i);
         if (module.getKey() == mouseKey) {
           module.toggle();
         }
       } 
     } 
   }
   
   public static boolean isBindHeld(int key) {
     if (key == -1) return false;
     
     long window = mc.method_22683().method_4490();
     
     if (key >= 1000) {
       int mouseButton = key - 1000;
       return (GLFW.glfwGetMouseButton(window, mouseButton) == 1);
     } 
     return (GLFW.glfwGetKey(window, key) == 1);
   }
 
   
   public static boolean isBindPressed(int key) {
     return isBindHeld(key);
   }
   
   public static String getBindName(int key) {
     if (key == -1)
       return "n/a"; 
     if (key >= 1000) {
       int mouseButton = key - 1000;
       switch (mouseButton) { case 0: 
         case 1: 
         case 2: 
         case 3:
         
         case 4:
          }  return "MOUSE" + mouseButton + 1;
     } 
     
     if (key >= 65 && key <= 90) {
       return String.valueOf((char)(65 + key - 65));
     }
     
     if (key >= 48 && key <= 57) {
       return String.valueOf((char)(48 + key - 48));
     }
     
     switch (key) { case 96: 
       case 45: 
       case 61: 
       case 91: 
       case 93: 
       case 92: 
       case 59: 
       case 39: 
       case 44: 
       case 46: 
       case 47: 
       default:
         break; }  String symbol = null;
     
     if (symbol != null) {
       return symbol;
     }
     switch (key) { case 32: 
       case 340: 
       case 344: 
       case 341: 
       case 345: 
       case 342: 
       case 346: 
       case 258: 
       case 257: 
       case 256: 
       case 259: 
       case 261: 
       case 260: 
       case 268: 
       case 269: 
       case 266: 
       case 267: 
       case 265: 
       case 264: 
       case 263: 
       case 262: 
       case 280: 
       case 290: 
       case 291: 
       case 292: 
       case 293: 
       case 294: 
       case 295: 
       case 296: 
       case 297: 
       case 298: 
       case 299: 
       case 300: 
       case 301: 
       case 320: 
       case 321: 
       case 322: 
       case 323: 
       case 324: 
       case 325: 
       case 326: 
       case 327: 
       case 328: 
       case 329: 
       case 330: 
       case 331: 
       case 332: 
       case 333: 
       case 334:
       
       case 335:
        }  return "KEY" + key;
   }
 
 
   
   public static boolean isMouseButton(int key) {
     return (key >= 1000);
   }
   
   public static int getMouseButtonFromKey(int key) {
     if (isMouseButton(key)) {
       return key - 1000;
     }
     return -1;
   }
   
   public static int createMouseBind(int mouseButton) {
     return 1000 + mouseButton;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\input\KeyBoardUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */