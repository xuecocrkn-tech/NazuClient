package shame.nazuna.client.modules.impl.render.base.implement;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;
 import java.util.Set;
 import java.util.regex.Pattern;
 import net.minecraft.GameMode;
 import net.minecraft.Text;
 import net.minecraft.Style;
 import net.minecraft.Team;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MatrixStack;
 import net.minecraft.PlayerListEntry;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.utils.animation.AnimationUtils;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.font.ReplaceSymbols;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.api.utils.scissor.ScissorUtils;
 import shame.nazuna.astra;
 
 public class StaffList extends InterfaceProcessing {
   private static final int STATUS_VANISH_COLOR = -47526;
   private static final int STATUS_GM3_COLOR = -9146;
   private static final int STATUS_ONLINE_COLOR = -10158216;
   private final MinecraftClient mc = MinecraftClient.method_1551();
   
   private final Map<String, StaffData> staffDataCache = new LinkedHashMap<>();
   private final Map<String, Float> staffAnimations = new HashMap<>();
   private final Set<String> activeStaff = new HashSet<>();
   
   private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
   private final Set<String> validStaffPrefixes = new HashSet<>();
   
   private final AnimationUtils widthAnimation = new AnimationUtils(60.0F, 10.5F, Easings.QUAD_OUT);
   private float staffAnimatedHeight = 18.0F;
   
   private long lastStaffUpdate = 0L;
   
   private final List<String> visiblePlayers = new ArrayList<>();
   private final Set<String> animationScratch = new HashSet<>();
   
   private Font font10;
   private Font font12;
   private Font font14;
   private Font font16;
   private Font iconFont;
   
   public StaffList(Draggable draggable) {
     super(draggable);
     this.validStaffPrefixes.addAll(Arrays.asList(new String[] { "supp", "ꜱupp", "mod", "der", "adm", "wne", "мод", "помо", "адм", "владе", "отри", "таф", "taf", "curat", "курато", "dev", "раз", "сапп", "yt", "ютуб", "стажер", "сотрудник" }));
   }
 
   
   private static class PrefixSegment
   {
     final String text;
     
     final int color;
     
     float width12;
     float width14;
     
     PrefixSegment(String text, int color) {
       this.text = text;
       this.color = color;
     }
   }
   
   private static class StaffData {
     String status;
     List<StaffList.PrefixSegment> segments;
     float prefixWidth12;
     float prefixWidth14;
     float nameWidth12;
     float nameWidth14;
     
     StaffData(String status) {
       this.status = status;
       this.segments = new ArrayList<>();
     }
   }
   
   private void initFonts() {
     if (this.font10 == null) {
       this.font10 = Fonts.getFont("suisse", 10);
       this.font12 = Fonts.getFont("suisse", 12);
       this.font14 = Fonts.getFont("suisse", 14);
       this.font16 = Fonts.getFont("suisse", 16);
       this.iconFont = Fonts.getFont("icon", 13);
     } 
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     if (this.mc.field_1724 == null || this.mc.field_1687 == null)
       return; 
     initFonts();
     
     long currentTime = System.currentTimeMillis();
     if (currentTime - this.lastStaffUpdate > 500L) {
       updateStaffCache();
       this.lastStaffUpdate = currentTime;
     } 
     
     updateAnimations();
     
     if (ModuleClass.interfaceModule.style.is("Обычный")) {
       renderDefaultStyle(eventRender);
     } else {
       renderWaveStyle(eventRender);
     } 
     
     super.onRender(eventRender);
   }
   
   private boolean matchesStaffPrefix(String prefix) {
     String lower = prefix.toLowerCase(Locale.ROOT);
     for (String p : this.validStaffPrefixes) {
       if (lower.contains(p)) return true; 
     } 
     return false;
   }
   
   private List<PrefixSegment> parsePrefix(Text prefix) {
     List<PrefixSegment> segments = new ArrayList<>();
     
     prefix.method_27658((style, string) -> { if (string == null || string.isEmpty()) return Optional.empty();  appendPrefixSegments(segments, string, (style.method_10973() != null) ? style.method_10973().method_27716() : 16777215); return Optional.empty(); }, Style.field_24360);
 
 
 
 
 
 
     
     return segments;
   }
   
   private void appendPrefixSegments(List<PrefixSegment> segments, String text, int baseColor) {
     int currentColor = baseColor;
     StringBuilder chunk = new StringBuilder();
     int chunkColor = currentColor;
     int offset;
     for (offset = 0; offset < text.length(); ) {
       int codePoint = text.codePointAt(offset);
       int charCount = Character.charCount(codePoint);
       
       if (codePoint == 167 && offset + charCount < text.length()) {
         flushPrefixSegment(segments, chunk, chunkColor);
         char code = Character.toLowerCase(text.charAt(offset + charCount));
         Integer mappedColor = sectionColorToRgb(code);
         currentColor = (mappedColor != null) ? mappedColor.intValue() : ((code == 'r') ? baseColor : currentColor);
         chunkColor = currentColor;
         offset += charCount + 1;
         
         continue;
       } 
       String replacement = ReplaceSymbols.replaceCodePoint(codePoint);
       if (replacement != null) {
         flushPrefixSegment(segments, chunk, chunkColor);
         int totalChars = Math.max(1, replacement.length());
         for (int i = 0; i < replacement.length(); i++) {
           int replacementColor = ReplaceSymbols.getGradientColorForReplacement(codePoint, i, totalChars, 1.0F, currentColor);
           if (chunk.length() > 0 && chunkColor != replacementColor) {
             flushPrefixSegment(segments, chunk, chunkColor);
           }
           chunkColor = replacementColor;
           chunk.append(replacement.charAt(i));
         } 
         offset += charCount;
         
         continue;
       } 
       if (chunk.length() > 0 && chunkColor != currentColor) {
         flushPrefixSegment(segments, chunk, chunkColor);
       }
       chunkColor = currentColor;
       chunk.appendCodePoint(codePoint);
       offset += charCount;
     } 
     
     flushPrefixSegment(segments, chunk, chunkColor);
   }
   
   private void flushPrefixSegment(List<PrefixSegment> segments, StringBuilder chunk, int color) {
     if (chunk.isEmpty())
       return;  String text = chunk.toString();
     PrefixSegment seg = new PrefixSegment(text, color);
     seg.width12 = this.font12.getWidth(text);
     seg.width14 = this.font14.getWidth(text);
     segments.add(seg);
     chunk.setLength(0);
   }
   
   private Integer sectionColorToRgb(char code) {
     switch (code) { case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':  }  return 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       
       null;
   }
 
   
   private void updateStaffCache() {
     this.activeStaff.clear();
     String selfName = this.mc.field_1724.method_5477().getString();
     
     for (Team team : this.mc.field_1687.method_8428().method_1159()) {
       Collection<String> players = team.method_1204();
       if (players.size() != 1)
         continue; 
       String name = players.iterator().next();
       if (!this.namePattern.matcher(name).matches() || 
         name.equals(selfName))
         continue; 
       PlayerListEntry info = this.mc.method_1562().method_2874(name);
       boolean vanish = (info == null);
       boolean isGM3 = (info != null && info.method_2958() == GameMode.field_9219);
       
       Text prefixText = team.method_1144();
       String prefixStr = prefixText.getString();
       boolean matchesPrefix = matchesStaffPrefix(prefixStr);
       boolean isInStaffList = NazunaClient.INSTANCE.staffStorage.isStaff(name);
       
       if (matchesPrefix || vanish || isGM3 || isInStaffList) {
         String status; this.activeStaff.add(name);
 
         
         if (vanish) {
           status = "VANISH";
         } else if (isGM3) {
           status = "GM3";
         } else {
           status = "ONLINE";
         } 
         
         StaffData existing = this.staffDataCache.get(name);
         if (existing == null) {
           existing = new StaffData(status);
           this.staffDataCache.put(name, existing);
         } 
         existing.status = status;
         existing.segments = parsePrefix(prefixText);
         calculateWidths(existing, name);
       } 
     } 
     
     for (String staffName : NazunaClient.INSTANCE.staffStorage.getStaffs()) {
       String status; if (staffName.equals(selfName) || 
         !this.namePattern.matcher(staffName).matches() || 
         this.activeStaff.contains(staffName))
         continue; 
       this.activeStaff.add(staffName);
       
       PlayerListEntry info = this.mc.method_1562().method_2874(staffName);
       
       if (info == null) {
         status = "VANISH";
       } else if (info.method_2958() == GameMode.field_9219) {
         status = "GM3";
       } else {
         status = "ONLINE";
       } 
       
       StaffData existing = this.staffDataCache.get(staffName);
       if (existing == null) {
         existing = new StaffData(status);
         existing.segments = new ArrayList<>();
         calculateWidths(existing, staffName);
         this.staffDataCache.put(staffName, existing); continue;
       } 
       existing.status = status;
     } 
   }
 
   
   private void calculateWidths(StaffData data, String name) {
     data.prefixWidth12 = 0.0F;
     data.prefixWidth14 = 0.0F;
     for (PrefixSegment seg : data.segments) {
       data.prefixWidth12 += seg.width12;
       data.prefixWidth14 += seg.width14;
     } 
     data.nameWidth12 = this.font12.getWidth(name);
     data.nameWidth14 = this.font14.getWidth(name + " >> ");
   }
   
   private void updateAnimations() {
     float lerpSpeed = 0.1F;
     
     this.animationScratch.clear();
     this.animationScratch.addAll(this.staffAnimations.keySet());
     this.animationScratch.addAll(this.activeStaff);
     
     for (String playerName : this.animationScratch) {
       boolean isActive = this.activeStaff.contains(playerName);
       float targetAnim = isActive ? 1.0F : 0.0F;
       float currentAnim = ((Float)this.staffAnimations.getOrDefault(playerName, Float.valueOf(0.0F))).floatValue();
       currentAnim += (targetAnim - currentAnim) * lerpSpeed;
       this.staffAnimations.put(playerName, Float.valueOf(currentAnim));
     } 
     
     Iterator<Map.Entry<String, Float>> animIt = this.staffAnimations.entrySet().iterator();
     while (animIt.hasNext()) {
       Map.Entry<String, Float> entry = animIt.next();
       if (((Float)entry.getValue()).floatValue() < 0.01F && !this.activeStaff.contains(entry.getKey())) {
         animIt.remove();
         this.staffDataCache.remove(entry.getKey());
       } 
     } 
   }
   
   private List<String> getVisiblePlayers() {
     this.visiblePlayers.clear();
     for (Map.Entry<String, Float> entry : this.staffAnimations.entrySet()) {
       if (((Float)entry.getValue()).floatValue() > 0.01F) {
         this.visiblePlayers.add(entry.getKey());
       }
     } 
     Collections.sort(this.visiblePlayers);
     return this.visiblePlayers;
   }
   
   private int getStatusColor(String status) {
     switch (status) { case "VANISH": case "GM3":  }  return 
 
       
       -10158216;
   }
 
   
   private float getStatusBoxWidth(String status) {
     return 12.0F;
   }
   private void renderDefaultStyle(EventRender.Default eventRender) {
     int colorTheme;
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     MatrixStack matrices = eventRender.getContext().method_51448();
 
     
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       colorTheme = (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     } else {
       colorTheme = ColorUtils.getThemeColor();
     } 
     
     List<String> visiblePlayers = getVisiblePlayers();
     
     float maxWidth = 60.0F;
     float headerHeight = 16.0F;
     float itemHeight = 12.0F;
     float padding = 5.0F;
     float statusPadding = 4.0F;
     
     for (String playerName : visiblePlayers) {
       StaffData data = this.staffDataCache.get(playerName);
       if (data != null) {
         float statusBoxW = getStatusBoxWidth(data.status);
         float totalW = padding + data.prefixWidth12 + data.nameWidth12 + statusPadding + statusBoxW + padding;
         if (totalW > maxWidth) {
           maxWidth = totalW;
         }
       } 
     } 
     
     this.widthAnimation.update(maxWidth);
     float width = this.widthAnimation.getValue();
     
     float contentHeight = 0.0F;
     for (String playerName : visiblePlayers) {
       contentHeight += itemHeight * ((Float)this.staffAnimations.getOrDefault(playerName, Float.valueOf(0.0F))).floatValue();
     }
     
     float targetHeight = visiblePlayers.isEmpty() ? headerHeight : (headerHeight + contentHeight + 2.0F);
     this.staffAnimatedHeight += (targetHeight - this.staffAnimatedHeight) * 0.12F;
     float height = this.staffAnimatedHeight;
     
     RenderUtils.drawDefaultHudElementRects(matrices, x, y, width, height, colorTheme, isUnusualRectType());
     
     this.font14.draw(matrices, "Staff", x + 5.0F, y + 6.0F, -1);
     this.iconFont.draw(matrices, "y", x + width - 13.0F, y + 7.5F, colorTheme);
     
     float offsetY = 18.0F;
     ScissorUtils.push();
     ScissorUtils.setFromComponentCoordinates(x, y, width, height);
     for (String playerName : visiblePlayers) {
       float anim = ((Float)this.staffAnimations.getOrDefault(playerName, Float.valueOf(0.0F))).floatValue();
       if (anim <= 0.01F)
         continue; 
       StaffData data = this.staffDataCache.get(playerName);
       if (data == null)
         continue; 
       int alpha = (int)(255.0F * anim);
       float yOffset = -5.0F * (1.0F - anim);
       
       float currentX = x + padding;
       for (int i = 0; i < data.segments.size(); i++) {
         PrefixSegment seg = data.segments.get(i);
         int color = ColorUtils.setAlphaColor(seg.color, alpha);
         this.font12.draw(matrices, seg.text, currentX, y + offsetY + 2.0F + yOffset, color);
         currentX += seg.width12;
       } 
       
       this.font12.draw(matrices, playerName, currentX, y + offsetY + 2.0F + yOffset, ColorUtils.rgba(255, 255, 255, alpha));
       
       float statusBoxWidth = getStatusBoxWidth(data.status);
       float statusBoxX = x + width - statusBoxWidth - padding;
       float statusBoxY = y + offsetY + 1.0F + yOffset;
       int statusRectColor = ColorUtils.setAlphaColor(getStatusColor(data.status), alpha);
       RenderUtils.drawRoundedRect(matrices, statusBoxX + 4.0F, statusBoxY + 1.5F, statusBoxWidth - 4.5F, 3.45F, 0.55F, statusRectColor);
       
       offsetY += itemHeight * anim;
     } 
     ScissorUtils.pop();
     ScissorUtils.unset();
     
     this.draggable.setWidth(width);
     this.draggable.setHeight(height);
   }
   
   private void renderWaveStyle(EventRender.Default eventRender) {
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     MatrixStack matrices = eventRender.getContext().method_51448();
     
     int time = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
     
     int leftTop = ColorUtils.getThemeColor(time);
     int leftBottom = ColorUtils.getThemeColor(time + 30);
     int centerTop = ColorUtils.getThemeColor(time + 90);
     int centerBottom = ColorUtils.getThemeColor(time + 120);
     int rightTop = ColorUtils.getThemeColor(time + 180);
     int rightBottom = ColorUtils.getThemeColor(time + 210);
     
     List<String> visiblePlayers = getVisiblePlayers();
     
     float maxWidth = 80.0F;
     float headerHeight = 18.0F;
     float itemHeight = 10.0F;
     float padding = 5.0F;
     
     for (String playerName : visiblePlayers) {
       StaffData data = this.staffDataCache.get(playerName);
       if (data != null) {
         float statusW = this.font12.getWidth(data.status);
         float totalW = padding + data.prefixWidth14 + data.nameWidth14 + statusW + padding;
         if (totalW > maxWidth) {
           maxWidth = totalW;
         }
       } 
     } 
     
     float width = maxWidth;
     
     float contentHeight = 0.0F;
     for (String playerName : visiblePlayers) {
       contentHeight += itemHeight * ((Float)this.staffAnimations.getOrDefault(playerName, Float.valueOf(0.0F))).floatValue();
     }
     
     float height = visiblePlayers.isEmpty() ? headerHeight : (headerHeight + contentHeight + 4.0F);
     
     if (visiblePlayers.isEmpty()) {
       RenderUtils.drawWaveHudHeader(matrices, x, y, width, 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
       
       float f = x + (width - this.font16.getWidth("stafflist")) / 2.0F;
       this.font16.drawStringWithShadow(matrices, "stafflist", f, y + 5.0F, -1);
       this.draggable.setWidth(width);
       this.draggable.setHeight(headerHeight);
       
       return;
     } 
     RenderUtils.drawWaveHudPanel(matrices, x, y, width, height, ColorUtils.rgba(25, 25, 25, 150), 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
 
 
     
     float titleX = x + (width - this.font16.getWidth("stafflist")) / 1.9F;
     this.font16.drawStringWithShadow(matrices, "stafflist", titleX, y + 5.0F, -1);
     
     float yOffsetPos = 20.0F;
     ScissorUtils.push();
     ScissorUtils.setFromComponentCoordinates(x, y, width, height);
     for (String playerName : visiblePlayers) {
       float anim = ((Float)this.staffAnimations.getOrDefault(playerName, Float.valueOf(0.0F))).floatValue();
       if (anim <= 0.01F)
         continue; 
       StaffData data = this.staffDataCache.get(playerName);
       if (data == null)
         continue; 
       int alpha = (int)(255.0F * anim);
       float yOffset = -5.0F * (1.0F - anim);
       
       float textX = x + padding;
       for (int i = 0; i < data.segments.size(); i++) {
         PrefixSegment seg = data.segments.get(i);
         int color = ColorUtils.setAlphaColor(seg.color, alpha);
         this.font14.draw(matrices, seg.text, textX, y + yOffsetPos + 1.5F + yOffset, color);
         textX += seg.width14;
       } 
       
       this.font14.draw(matrices, playerName + " >> ", textX, y + yOffsetPos + 1.5F + yOffset, ColorUtils.rgba(255, 255, 255, alpha));
       float nameArrowWidth = this.font14.getWidth(playerName + " >> ");
       this.font12.draw(matrices, data.status, textX + nameArrowWidth, y + yOffsetPos + 2.5F + yOffset, ColorUtils.setAlphaColor(getStatusColor(data.status), alpha));
       
       yOffsetPos += itemHeight * anim;
     } 
     ScissorUtils.pop();
     ScissorUtils.unset();
     
     this.draggable.setWidth(width);
     this.draggable.setHeight(height);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\StaffList.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */