package shame.nazuna.client.modules.impl.misc;
 
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import net.minecraft.Text;
 import net.minecraft.TextFieldWidget;
 import net.minecraft.ChatScreen;
 import net.minecraft.Screen;
 import shame.nazuna.api.utils.replace.ReplaceUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.TextSetting;
 import shame.nazuna.mixin.ChatScreenAccessor;
 
 public class NameProtect extends Module {
   public static final NameProtect INSTANCE = new NameProtect();
   private final BooleanSetting friends = new BooleanSetting("Скрывать друзей", true);
   private final BooleanSetting grief = new BooleanSetting("Скрывать гриф", false);
   private final TextSetting nickname = new TextSetting("Никнейм", "astra", 32);
   private static final int PATCH_CACHE_LIMIT = 512;
   
   private final Map<String, String> patchCache = new LinkedHashMap<String, String>(512, 0.75F, true)
     {
       protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
         return (size() > 512);
       }
     };
   
   private NameProtect() {
     super("NameProtect", "Скрывает никнеймы", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.friends, (Setting)this.grief, (Setting)this.nickname });
   }
   
   public String patch(String text) {
     if (text == null) {
       return null;
     }
     if (!shouldPatch()) {
       return text;
     }
     
     String cacheKey = getPatchCacheKey(text);
     String cached = this.patchCache.get(cacheKey);
     if (cached != null) {
       return cached;
     }
     
     String out = text;
     String replacement = getReplacementName();
     out = replaceIgnoreCase(out, mc.method_1548().method_1676(), replacement);
     if (this.friends.isState() && NazunaClient.INSTANCE != null && NazunaClient.INSTANCE.friendStorage != null) {
       for (String friend : NazunaClient.INSTANCE.friendStorage.getFriends()) {
         out = replaceIgnoreCase(out, friend, replacement);
       }
     }
     out = patchGrief(out);
     this.patchCache.put(cacheKey, out);
     return out;
   }
   
   public String patchIncomingText(String text) {
     return patch(text);
   }
   
   public Text patchText(Text text) {
     if (text == null) {
       return null;
     }
     
     if (!shouldPatch()) {
       return text;
     }
     
     Text output = text;
     String replacement = getReplacementName();
     output = ReplaceUtils.replace(output, mc.method_1548().method_1676(), replacement);
     if (this.friends.isState() && NazunaClient.INSTANCE != null && NazunaClient.INSTANCE.friendStorage != null) {
       for (String friend : NazunaClient.INSTANCE.friendStorage.getFriends()) {
         output = ReplaceUtils.replace(output, friend, replacement);
       }
     }
     return output;
   }
   
   public String getReplacementName() {
     String value = this.nickname.get();
     return (value == null || value.isBlank()) ? "astra" : value;
   }
   
   public boolean shouldHideGrief() {
     return this.grief.isState();
   }
   
   private String replaceIgnoreCase(String text, String target, String replacement) {
     if (text == null || target == null || target.isEmpty()) {
       return text;
     }
     int firstIndex = indexOfIgnoreCase(text, target, 0);
     if (firstIndex < 0) {
       return text;
     }
     
     StringBuilder out = new StringBuilder(text.length() + replacement.length());
     int from = 0;
     int index = firstIndex;
     while (index >= 0) {
       out.append(text, from, index).append(replacement);
       from = index + target.length();
       index = indexOfIgnoreCase(text, target, from);
     } 
     out.append(text, from, text.length());
     return out.toString();
   }
   
   private int indexOfIgnoreCase(String text, String target, int from) {
     int max = text.length() - target.length();
     for (int i = Math.max(0, from); i <= max; i++) {
       if (text.regionMatches(true, i, target, 0, target.length())) {
         return i;
       }
     } 
     return -1;
   }
   
   private String patchGrief(String text) {
     if (text == null || !this.grief.isState()) {
       return text;
     }
     
     String out = text.replaceAll("Анархия-\\d+", "AstraBETA.fun");
     out = out.replaceAll("ГРИФ #\\d+", "AstraBETA.fun");
     return out;
   }
   
   private String getPatchCacheKey(String text) {
     String username = (mc != null && mc.method_1548() != null) ? mc.method_1548().method_1676() : "";
     int friendsHash = 0;
     if (this.friends.isState() && NazunaClient.INSTANCE != null && NazunaClient.INSTANCE.friendStorage != null) {
       List<String> friendList = NazunaClient.INSTANCE.friendStorage.getFriends();
       friendsHash = friendList.hashCode();
     } 
     return username + "\002" + username + "\002" + 
       getReplacementName() + "\002" + this.friends
       .isState() + "\002" + this.grief
       .isState() + "\002" + friendsHash;
   }
 
 
   
   private boolean shouldPatch() {
     return (isEnable() && mc != null && mc.field_1724 != null && mc.field_1687 != null && !isFriendRemoveInputActive());
   }
   private boolean isFriendRemoveInputActive() {
     ChatScreen chatScreen;
     Screen Screen = mc.field_1755; if (Screen instanceof ChatScreen) { chatScreen = (ChatScreen)Screen; }
     else { return false; }
 
     
     TextFieldWidget chatField = ((ChatScreenAccessor)chatScreen).astra$getChatField();
     if (chatField == null) {
       return false;
     }
     
     String input = chatField.method_1882();
     if (input == null) {
       return false;
     }
     
     String normalized = input.trim().toLowerCase();
 
     
     String prefix = (NazunaClient.INSTANCE != null && NazunaClient.INSTANCE.commandStorage != null) ? NazunaClient.INSTANCE.commandStorage.getPrefix().toLowerCase() : ".";
     return normalized.startsWith(prefix + "friend remove");
   }
 }

