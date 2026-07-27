package shame.nazuna.api.storages.implement;
 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonPrimitive;
 import it.unimi.dsi.fastutil.objects.ObjectListIterator;
 import java.io.File;
 import java.io.InputStream;
 import java.io.Reader;
 import java.io.Writer;
 import java.util.Map;
 import shame.nazuna.api.utils.cmd.macro.Macro;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.render.Interface;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 import shame.nazuna.client.modules.impl.render.base.implement.WaterMark;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.client.modules.settings.implement.TextSetting;
 
 public class ConfigStorage {
   public String currentConfig = "default";
   private final String extension = ".wonder";
   
   public ConfigStorage() {
     loadAll();
     Runtime.getRuntime().addShutdownHook(new Thread(this::saveAll));
   }
 
   
   private void loadAll() {
     try {
       loadGlobals();
       loadConfig(this.currentConfig);
     } catch (Exception e) {
       e.printStackTrace(System.err);
     } 
   }
 
   
   private void saveAll() {
     try {
       saveGlobals();
       saveConfig(this.currentConfig);
     } catch (Exception e) {
       e.printStackTrace(System.err);
     } 
   }
 
   
   public void saveConfig(String config) throws Exception {
     File file = new File(astra.INSTANCE.configsDir, config + ".wonder");
     
     JsonObject object = new JsonObject();
     object.add("config", (JsonElement)new JsonPrimitive(config));
     object.add("theme", (JsonElement)new JsonPrimitive(astra.INSTANCE.themeStorage.getThemes().name()));
     object.add("language", (JsonElement)new JsonPrimitive(astra.INSTANCE.localizationStorage.getLanguage().name()));
     object.add("modules", (JsonElement)serializeModules());
     object.add("draggables", (JsonElement)serializeDraggables());
     object.add("hud", (JsonElement)serializeHudState());
     
     Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8); 
     try { writer.write((new GsonBuilder())
           .setPrettyPrinting()
           .create()
           .toJson((JsonElement)object));
       writer.close(); } catch (Throwable throwable) { try { writer.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
        throw throwable; }
      this.currentConfig = config;
   }
   
   public void loadConfig(String config) throws Exception {
     JsonObject object;
     if (!FileUtils.exists(String.valueOf(astra.INSTANCE.configsDir) + "/" + String.valueOf(astra.INSTANCE.configsDir) + ".wonder"))
       return; 
     InputStream stream = Files.newInputStream(Paths.get(String.valueOf(astra.INSTANCE.configsDir) + "/" + String.valueOf(astra.INSTANCE.configsDir) + ".wonder", new String[0]), new java.nio.file.OpenOption[0]); 
     try { Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8); 
       try { object = JsonParser.parseReader(reader).getAsJsonObject();
         reader.close(); } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
         try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
      if (object.has("theme")) {
       String themeName = object.get("theme").getAsString();
       for (ThemeStorage.Themes theme : ThemeStorage.Themes.values()) {
         if (theme.name().equals(themeName)) {
           astra.INSTANCE.themeStorage.setThemes(theme);
           
           break;
         } 
       } 
     } 
     if (object.has("language")) {
       try {
         astra.INSTANCE.localizationStorage.setLanguage(LocalizationStorage.Language.valueOf(object.get("language").getAsString()));
       } catch (Exception exception) {}
     }
 
     
     if (object.has("draggables")) {
       deserializeDraggables(object.get("draggables").getAsJsonObject());
     }
     
     if (object.has("modules")) {
       deserializeModules(object.get("modules").getAsJsonObject());
     }
     
     if (object.has("hud")) {
       deserializeHudState(object.get("hud").getAsJsonObject());
     }
     
     this.currentConfig = config;
   }
 
   
   public void saveGlobals() throws Exception {
     File file = new File(astra.INSTANCE.globalsDir, "globals.wonder");
     JsonObject object = new JsonObject();
     object.add("config", (JsonElement)new JsonPrimitive(this.currentConfig));
     
     object.add("theme", (JsonElement)new JsonPrimitive(astra.INSTANCE.themeStorage.getThemes().name()));
     object.add("language", (JsonElement)new JsonPrimitive(astra.INSTANCE.localizationStorage.getLanguage().name()));
     
     object.add("draggables", (JsonElement)serializeDraggables());
     object.add("hud", (JsonElement)serializeHudState());
     
     JsonArray friendsArray = new JsonArray();
     Objects.requireNonNull(friendsArray); astra.INSTANCE.friendStorage.getFriends().forEach(friendsArray::add);
     object.add("friends", (JsonElement)friendsArray);
     
     JsonArray staffsArray = new JsonArray();
     Objects.requireNonNull(staffsArray); astra.INSTANCE.staffStorage.getStaffs().forEach(staffsArray::add);
     object.add("staffs", (JsonElement)staffsArray);
     
     JsonArray macrosArray = new JsonArray();
     astra.INSTANCE.macroStorage.getMacros().forEach(macro -> {
           JsonObject macroObject = new JsonObject();
           macroObject.addProperty("name", macro.getName());
           macroObject.addProperty("command", macro.getCommand());
           macroObject.addProperty("key", Integer.valueOf(macro.getBind().getKey()));
           macrosArray.add((JsonElement)macroObject);
         });
     object.add("macros", (JsonElement)macrosArray);
     
     Writer writer = new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8); 
     try { writer.write((new GsonBuilder()).setPrettyPrinting().create().toJson((JsonElement)object));
       writer.close(); }
     catch (Throwable throwable) { try { writer.close(); }
       catch (Throwable throwable1)
       { throwable.addSuppressed(throwable1); }
        throw throwable; }
      } public void loadGlobals() throws Exception { JsonObject object; if (!FileUtils.exists(String.valueOf(astra.INSTANCE.globalsDir) + "/globals.wonder"))
       return; 
     InputStream stream = Files.newInputStream(Paths.get(String.valueOf(astra.INSTANCE.globalsDir) + "/globals.wonder", new String[0]), new java.nio.file.OpenOption[0]); 
     try { Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8); 
       try { object = JsonParser.parseReader(reader).getAsJsonObject();
         reader.close(); } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
         try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
      if (object.has("config")) this.currentConfig = object.get("config").getAsString();
     
     if (object.has("theme")) {
       String themeName = object.get("theme").getAsString();
       for (ThemeStorage.Themes theme : ThemeStorage.Themes.values()) {
         if (theme.name().equals(themeName)) {
           astra.INSTANCE.themeStorage.setThemes(theme);
           
           break;
         } 
       } 
     } 
     if (object.has("language")) {
       try {
         astra.INSTANCE.localizationStorage.setLanguage(LocalizationStorage.Language.valueOf(object.get("language").getAsString()));
       } catch (Exception exception) {}
     }
 
     
     if (object.has("draggables")) {
       deserializeDraggables(object.get("draggables").getAsJsonObject());
     }
     
     if (object.has("hud")) {
       deserializeHudState(object.get("hud").getAsJsonObject());
     }
     
     if (object.has("friends")) {
       for (JsonElement element : object.get("friends").getAsJsonArray()) {
         if (astra.INSTANCE.friendStorage.isFriend(element.getAsString()))
           continue;  astra.INSTANCE.friendStorage.add(element.getAsString());
       } 
     }
     
     if (object.has("staffs")) {
       for (JsonElement element : object.get("staffs").getAsJsonArray()) {
         if (astra.INSTANCE.staffStorage.isStaff(element.getAsString()))
           continue;  astra.INSTANCE.staffStorage.add(element.getAsString());
       } 
     }
     
     if (object.has("macros")) {
       for (JsonElement element : object.get("macros").getAsJsonArray()) {
         try {
           String name, command;
           
           int key;
           
           if (element.isJsonObject()) {
             JsonObject macroObject = element.getAsJsonObject();
             name = macroObject.has("name") ? macroObject.get("name").getAsString() : "";
             command = macroObject.has("command") ? macroObject.get("command").getAsString() : "";
             key = macroObject.has("key") ? macroObject.get("key").getAsInt() : -1;
           } else {
             String[] split = element.getAsString().split(":", 3);
             if (split.length < 3)
               continue;  name = split[0];
             command = split[1];
             key = Integer.parseInt(split[2]);
           } 
           
           if (name.isBlank() || astra.INSTANCE.macroStorage.getMacro(name) != null) {
             continue;
           }
           
           astra.INSTANCE.macroStorage.add(new Macro(name, command, new BindSetting("bind", key)));
         } catch (Exception exception) {}
       } 
     } }
 
 
   
   private JsonObject serializeModules() {
     JsonObject modules = new JsonObject();
     for (ObjectListIterator<Module> objectListIterator = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator.hasNext(); ) { Module module = objectListIterator.next();
       try {
         JsonObject object = new JsonObject();
         object.add("toggled", (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isEnable())));
         object.add("bind", (JsonElement)new JsonPrimitive(Integer.valueOf(module.getKey())));
         
         JsonObject settings = new JsonObject();
         for (Setting s : module.getSettings()) {
           try {
             if (s instanceof BooleanSetting) { BooleanSetting bool = (BooleanSetting)s;
               settings.add(s.name(), (JsonElement)new JsonPrimitive(Boolean.valueOf(bool.isState()))); continue; }
              if (s instanceof FloatSetting) { FloatSetting num = (FloatSetting)s;
               settings.add(s.name(), (JsonElement)new JsonPrimitive(Float.valueOf(num.getValue().floatValue()))); continue; }
              if (s instanceof ModeSetting) { ModeSetting mode = (ModeSetting)s;
               settings.add(s.name(), (JsonElement)new JsonPrimitive(mode.getCurrent())); continue; }
              if (s instanceof TextSetting) { TextSetting text = (TextSetting)s;
               settings.add(s.name(), (JsonElement)new JsonPrimitive(text.get())); continue; }
              if (s instanceof BindSetting) { BindSetting bind = (BindSetting)s;
               settings.add(s.name(), (JsonElement)new JsonPrimitive(Integer.valueOf(bind.getKey()))); continue; }
              if (s instanceof ListSetting) { ListSetting list = (ListSetting)s;
               JsonObject listObj = new JsonObject();
               for (BooleanSetting setting : list.getSettings()) {
                 listObj.add(setting.name(), (JsonElement)new JsonPrimitive(Boolean.valueOf(setting.isState())));
               }
               settings.add(list.name(), (JsonElement)listObj); }
           
           } catch (Exception exception) {}
         } 
 
         
         object.add("settings", (JsonElement)settings);
         modules.add(module.getName(), (JsonElement)object);
       } catch (Exception exception) {} }
 
 
     
     return modules;
   }
   
   private void deserializeModules(JsonObject modules) {
     Map<Module, Boolean> targetStates = new LinkedHashMap<>();
     ObjectListIterator<Module> objectListIterator;
     for (objectListIterator = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator.hasNext(); ) { Module module = objectListIterator.next();
 
       
       try {
         JsonObject object = modules.has(module.getName()) ? modules.get(module.getName()).getAsJsonObject() : null;
 
 
         
         boolean toggled = (object != null && object.has("toggled") && object.get("toggled").getAsBoolean());
         
         targetStates.put(module, Boolean.valueOf(toggled));
         
         if (module.isEnable()) {
           module.setEnabled(false);
         }
       } catch (Exception ignored) {
         targetStates.put(module, Boolean.valueOf(false));
       }  }
 
     
     for (objectListIterator = ModuleClass.INSTANCE.getObject().iterator(); objectListIterator.hasNext(); ) { Module module = objectListIterator.next();
       try {
         if (!modules.has(module.getName())) {
           continue;
         }
         
         JsonObject object = modules.get(module.getName()).getAsJsonObject();
         
         if (object.has("bind")) {
           module.setKey(object.get("bind").getAsInt());
         }
         
         if (object.has("settings")) {
           JsonObject settings = object.get("settings").getAsJsonObject();
           
           for (Setting s : module.getSettings()) {
             try {
               if (!settings.has(s.name()))
                 continue; 
               JsonElement element = settings.get(s.name());
               
               if (s instanceof BooleanSetting) { BooleanSetting bool = (BooleanSetting)s;
                 bool.setState(element.getAsBoolean()); continue; }
                if (s instanceof FloatSetting) { FloatSetting num = (FloatSetting)s;
                 num.setValue(element.getAsFloat()); continue; }
                if (s instanceof ModeSetting) { ModeSetting mode = (ModeSetting)s;
                 mode.set(element.getAsString()); continue; }
                if (s instanceof TextSetting) { TextSetting text = (TextSetting)s;
                 text.setText(element.getAsString()); continue; }
                if (s instanceof BindSetting) { BindSetting bind = (BindSetting)s;
                 bind.setKey(element.getAsInt()); continue; }
                if (s instanceof ListSetting) { ListSetting list = (ListSetting)s;
                 JsonObject listObj = element.getAsJsonObject();
                 for (BooleanSetting setting : list.getSettings()) {
                   if (listObj.has(setting.name())) {
                     setting.setState(listObj.get(setting.name()).getAsBoolean());
                   }
                 }  }
             
             } catch (Exception exception) {}
           }
         
         }
       
       } catch (Exception exception) {} }
 
 
     
     for (Map.Entry<Module, Boolean> entry : targetStates.entrySet()) {
       try {
         ((Module)entry.getKey()).setEnabled(((Boolean)entry.getValue()).booleanValue());
       } catch (Exception exception) {}
     } 
   }
 
   
   private JsonObject serializeHudState() {
     JsonObject hud = new JsonObject();
     Interface interfaceModule = ModuleClass.interfaceModule;
     if (interfaceModule == null) {
       return hud;
     }
     
     for (Map.Entry<String, InterfaceProcessing> entry : (Iterable<Map.Entry<String, InterfaceProcessing>>)interfaceModule.getConfigurableHudElements().entrySet()) {
       InterfaceProcessing element = entry.getValue();
       if (element == null) {
         continue;
       }
       
       JsonObject object = new JsonObject();
       object.add("unusualRectType", (JsonElement)new JsonPrimitive(Boolean.valueOf(element.isUnusualRectType())));
       
       if (element instanceof WaterMark) { WaterMark waterMark = (WaterMark)element;
         object.add("showFps", (JsonElement)new JsonPrimitive(Boolean.valueOf(waterMark.isShowFps())));
         object.add("showMs", (JsonElement)new JsonPrimitive(Boolean.valueOf(waterMark.isShowMs())));
         object.add("showServer", (JsonElement)new JsonPrimitive(Boolean.valueOf(waterMark.isShowServer())));
         object.add("showTps", (JsonElement)new JsonPrimitive(Boolean.valueOf(waterMark.isShowTps()))); }
       else if (element instanceof TargetHud) { TargetHud targetHud = (TargetHud)element;
         object.add("headParticlesEnabled", (JsonElement)new JsonPrimitive(Boolean.valueOf(targetHud.isHeadParticlesEnabled()))); }
 
       
       hud.add(entry.getKey(), (JsonElement)object);
     } 
     
     return hud;
   }
   
   private void deserializeHudState(JsonObject hud) {
     Interface interfaceModule = ModuleClass.interfaceModule;
     if (interfaceModule == null) {
       return;
     }
     
     for (Map.Entry<String, InterfaceProcessing> entry : (Iterable<Map.Entry<String, InterfaceProcessing>>)interfaceModule.getConfigurableHudElements().entrySet()) {
       if (!hud.has(entry.getKey())) {
         continue;
       }
       
       try {
         JsonObject object = hud.get(entry.getKey()).getAsJsonObject();
         InterfaceProcessing element = entry.getValue();
         
         if (object.has("unusualRectType")) {
           element.setUnusualRectType(object.get("unusualRectType").getAsBoolean());
         }
         
         if (element instanceof WaterMark) { WaterMark waterMark = (WaterMark)element;
           if (object.has("showFps")) {
             waterMark.setShowFps(object.get("showFps").getAsBoolean());
           }
           if (object.has("showMs")) {
             waterMark.setShowMs(object.get("showMs").getAsBoolean());
           }
           if (object.has("showServer")) {
             waterMark.setShowServer(object.get("showServer").getAsBoolean());
           }
           if (object.has("showTps"))
             waterMark.setShowTps(object.get("showTps").getAsBoolean());  continue; }
         
         if (element instanceof TargetHud) { TargetHud targetHud = (TargetHud)element;
           if (object.has("headParticlesEnabled")) {
             targetHud.setHeadParticlesEnabled(object.get("headParticlesEnabled").getAsBoolean());
           } }
       
       } catch (Exception exception) {}
     } 
   }
 
   
   private JsonObject serializeDraggables() {
     JsonObject draggables = new JsonObject();
     for (Draggable drag : DragStorage.draggables.values()) {
       JsonObject object = new JsonObject();
       object.add("x", (JsonElement)new JsonPrimitive(Float.valueOf(drag.getX())));
       object.add("y", (JsonElement)new JsonPrimitive(Float.valueOf(drag.getY())));
       draggables.add(drag.getName(), (JsonElement)object);
     } 
     return draggables;
   }
   
   private void deserializeDraggables(JsonObject draggables) {
     for (String name : draggables.keySet()) {
       Draggable drag = DragStorage.draggables.get(name);
       if (drag == null)
         continue; 
       JsonObject object = draggables.get(name).getAsJsonObject();
       if (object.has("x")) {
         drag.setX(object.get("x").getAsFloat());
       }
       if (object.has("y"))
         drag.setY(object.get("y").getAsFloat()); 
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\ConfigStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */