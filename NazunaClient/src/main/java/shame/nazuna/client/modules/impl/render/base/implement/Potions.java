package shame.nazuna.client.modules.impl.render.base.implement;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import net.minecraft.Sprite;
 import net.minecraft.I18n;
 import net.minecraft.StatusEffect;
 import net.minecraft.StatusEffectInstance;
 import net.minecraft.MathHelper;
 import net.minecraft.RegistryEntry;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.animation.AnimationUtils;
 import shame.nazuna.api.utils.animation.Easings;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.draggable.Draggable;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.api.utils.render.fonts.msdf.Fonts;
 import shame.nazuna.api.utils.scissor.ScissorUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 
 public class Potions
   extends InterfaceProcessing
 {
   private static final class PotionSnapshot {
     RegistryEntry<StatusEffect> entry;
     String baseName;
     int amplifier;
     int duration;
     boolean infinite;
   }
   private final Map<StatusEffect, AnimationUtils> animations = new LinkedHashMap<>();
   private final Map<StatusEffect, PotionSnapshot> snapshots = new HashMap<>();
   private final Map<StatusEffect, Integer> maxDurations = new HashMap<>();
   private final Set<StatusEffect> renderOrderSeen = new HashSet<>();
   private final AnimationUtils widthAnimation = new AnimationUtils(70.0F, 10.5F, Easings.QUAD_OUT);
 
   
   private float animationProgress = 0.0F;
   private long lastUpdateTime = System.currentTimeMillis();
   private float pulseAnimation = 0.0F;
   private float breathingAnimation = 0.0F;
   
   public Potions(Draggable draggable) {
     super(draggable);
   }
   
   private Font issue(int size) { return Fonts.getFont("suisse", size); } private Font icon(int size) {
     return Fonts.getFont("icon", size);
   }
   private AnimationUtils getAnimation(StatusEffect effect) {
     return this.animations.computeIfAbsent(effect, e -> new AnimationUtils(0.0F, 10.5F, Easings.QUAD_OUT));
   }
   
   private static String getLevelSuffix(int level) {
     return String.valueOf(Math.max(1, level));
   }
   
   private static String formatDuration(StatusEffectInstance effect) {
     return formatDuration(effect.method_5584(), effect.method_48559());
   }
   
   private static String formatDuration(int duration, boolean infinite) {
     if (infinite) {
       return "inf";
     }
     int seconds = Math.max(0, duration / 20);
     int minutes = seconds / 60;
     int secs = seconds % 60;
     return "" + minutes + ":" + minutes;
   }
   
   private void updateSnapshot(StatusEffectInstance effect) {
     StatusEffect type = (StatusEffect)effect.method_5579().comp_349();
     PotionSnapshot snapshot = this.snapshots.computeIfAbsent(type, e -> new PotionSnapshot());
     snapshot.entry = effect.method_5579();
     snapshot.baseName = I18n.method_4662(effect.method_5586(), new Object[0]);
     snapshot.amplifier = effect.method_5578() + 1;
     snapshot.duration = effect.method_5584();
     snapshot.infinite = effect.method_48559();
   }
   
   private List<StatusEffect> buildRenderOrder(Collection<StatusEffectInstance> effects, Set<StatusEffect> active) {
     List<StatusEffect> order = new ArrayList<>();
     this.renderOrderSeen.clear();
     for (StatusEffectInstance effect : effects) {
       StatusEffect type = (StatusEffect)effect.method_5579().comp_349();
       if (this.renderOrderSeen.add(type)) {
         order.add(type);
       }
     } 
     for (StatusEffect type : this.animations.keySet()) {
       if (!active.contains(type)) {
         order.add(type);
       }
     } 
     return order;
   }
   
   private void drawEffectIcon(EventRender.Default eventRender, RegistryEntry<StatusEffect> effect, float x, float y, int size, int alpha) {
     Sprite sprite = mc.method_18505().method_18663(effect);
     int color = ColorUtils.rgba(255, 255, 255, alpha);
     RenderUtils.drawSprite(eventRender.getContext().method_51448(), sprite, x, y, size, color);
   }
   
   private void drawTextWithShadow(EventRender.Default eventRender, Font font, String text, float x, float y, int color) {
     int shadow = ColorUtils.rgba(20, 20, 20, 145);
     font.draw(eventRender.getContext().method_51448(), text, x + 0.8F, y + 0.8F, shadow);
     font.draw(eventRender.getContext().method_51448(), text, x, y, color);
   }
 
   
   public void onRender(EventRender.Default eventRender) {
     if (ModuleClass.interfaceModule.style.is("Обычный")) {
       DefaultStyle(eventRender);
     } else {
       WaveStyle(eventRender);
     } 
     super.onRender(eventRender);
   }
   public void DefaultStyle(EventRender.Default eventRender) {
     int colorTheme;
     float x = this.draggable.getX();
     float y = this.draggable.getY();
 
     
     long currentTime = System.currentTimeMillis();
     float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0F;
     this.lastUpdateTime = currentTime;
     this.animationProgress += deltaTime * 2.0F;
     if (this.animationProgress > 360.0F) this.animationProgress -= 360.0F;
 
     
     this.pulseAnimation += deltaTime * 3.0F;
     if (this.pulseAnimation > 360.0F) this.pulseAnimation -= 360.0F; 
     float pulseScale = 1.0F + (float)Math.sin(this.pulseAnimation) * 0.1F;
 
     
     this.breathingAnimation += deltaTime * 2.5F;
     if (this.breathingAnimation > 360.0F) this.breathingAnimation -= 360.0F; 
     float breathingAlpha = 0.8F + (float)Math.sin(this.breathingAnimation) * 0.2F;
 
     
     if (!NazunaClient.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
       colorTheme = (NazunaClient.INSTANCE.themeStorage.getThemes().getTheme()).color[0];
     } else {
       colorTheme = ColorUtils.getThemeColor((int)this.animationProgress);
     } 
     int colorTheme2 = ColorUtils.getThemeColor((int)(this.animationProgress + 90.0F) % 360);
     float targetWidth = 70.0F;
     float targetHeight = 16.0F;
     int visibleCount = 0;
 
 
     
     Collection<StatusEffectInstance> effects = (mc != null && mc.field_1724 != null) ? mc.field_1724.method_6026() : List.<StatusEffectInstance>of();
     
     Set<StatusEffect> active = new HashSet<>();
     for (StatusEffectInstance effect : effects) {
       StatusEffect type = (StatusEffect)effect.method_5579().comp_349();
       active.add(type);
       getAnimation(type).update(1.0F);
       updateSnapshot(effect);
       
       int duration = effect.method_5584();
       Integer prevMax = this.maxDurations.get(type);
       
       if (prevMax == null || duration > prevMax.intValue()) {
         this.maxDurations.put(type, Integer.valueOf(duration));
       }
     } 
     
     for (Map.Entry<StatusEffect, AnimationUtils> entry : this.animations.entrySet()) {
       if (!active.contains(entry.getKey())) {
         ((AnimationUtils)entry.getValue()).update(0.0F);
       }
     } 
     
     List<StatusEffect> renderOrder = buildRenderOrder(effects, active);
     
     for (StatusEffect type : renderOrder) {
       AnimationUtils anim = getAnimation(type);
       float animValue = anim.getValue();
       PotionSnapshot snapshot = this.snapshots.get(type);
       if (animValue <= 0.01F || 
         snapshot == null) {
         continue;
       }
       visibleCount++;
       String baseName = (snapshot.baseName != null) ? snapshot.baseName : I18n.method_4662(type.method_5567(), new Object[0]);
       String levelSuffix = getLevelSuffix(snapshot.amplifier);
       String time = formatDuration(snapshot.duration, snapshot.infinite);
       float nameWidth = issue(12).getWidth(baseName);
       if (!levelSuffix.isEmpty()) {
         nameWidth += issue(11).getWidth(" LVL") + issue(12).getWidth(levelSuffix);
       }
       float timeWidth = issue(10).getWidth(time) + 6.0F;
       float rowWidth = nameWidth + timeWidth + 25.0F + 9.0F + 9.0F;
       if (rowWidth > targetWidth) targetWidth = rowWidth; 
       targetHeight += 12.0F * animValue;
     } 
 
     
     if (visibleCount > 0) targetHeight += 2.0F;
     
     this.widthAnimation.update(targetWidth);
     float width = this.widthAnimation.getValue();
     float height = targetHeight;
 
     
     int glowColor = ColorUtils.applyAlpha(colorTheme, breathingAlpha * 0.25F);
     RenderUtils.drawShadow(eventRender.getContext().method_51448(), x - 2.0F, y - 2.0F, width + 4.0F, height + 4.0F, 3.0F, 7.0F * pulseScale, glowColor);
     
     RenderUtils.drawDefaultHudElementRects(eventRender.getContext().method_51448(), x, y, width, height, colorTheme, isUnusualRectType());
 
     
     issue(14).drawGradientStringHorizontal(eventRender.getContext().method_51448(), "Effects", x + 5.0F, y + 6.0F, colorTheme, colorTheme2);
     icon(13).drawGradientStringHorizontal(eventRender.getContext().method_51448(), "d", x + width - 12.5F, y + 7.5F, colorTheme, colorTheme2);
     
     float offsetY = 18.0F;
     for (StatusEffect type : renderOrder) {
       AnimationUtils anim = getAnimation(type);
       float animValue = anim.getValue();
       PotionSnapshot snapshot = this.snapshots.get(type);
       
       if (animValue <= 0.01F || 
         snapshot == null) {
         continue;
       }
       ScissorUtils.push();
       ScissorUtils.setFromComponentCoordinates(x, y, width, height);
       
       int alpha = (int)(255.0F * animValue);
       int textColor = ColorUtils.rgba(255, 255, 255, alpha);
       int grayColor = ColorUtils.rgba(55, 55, 55, alpha);
       int darkColor = ColorUtils.rgba(35, 35, 35, alpha);
       
       float iconSize = 7.0F;
       float iconX = x + 5.0F;
       float iconY = y + offsetY;
       if (snapshot.entry != null) {
         drawEffectIcon(eventRender, snapshot.entry, iconX, iconY, (int)iconSize, alpha);
       }
       
       String baseName = (snapshot.baseName != null) ? snapshot.baseName : I18n.method_4662(type.method_5567(), new Object[0]);
       String levelSuffix = getLevelSuffix(snapshot.amplifier);
       float textX = iconX + iconSize + 3.0F;
       float textY = y + 2.0F + offsetY;
       issue(12).draw(eventRender.getContext().method_51448(), baseName, textX, textY, textColor);
       if (!levelSuffix.isEmpty()) {
         float baseWidth = issue(12).getWidth(baseName);
         int levelThemeColor = ColorUtils.setAlphaColor(colorTheme, alpha);
         float lvlX = textX + baseWidth;
         issue(10).draw(eventRender.getContext().method_51448(), " LVL", lvlX, textY + 1.0F, levelThemeColor);
         issue(11).draw(eventRender.getContext().method_51448(), levelSuffix, (lvlX + issue(11).getWidth(" LVL")), textY + 0.5D, levelThemeColor);
       } 
       
       String time = formatDuration(snapshot.duration, snapshot.infinite);
       float timeBoxWidth = Math.max(issue(10).getWidth(time) + 4.0F, 12.0F);
       float ringSize = 6.0F;
       float ringGap = 3.0F;
       float timeBoxX = x + width - timeBoxWidth - 5.0F;
       float ringX = timeBoxX - ringGap - ringSize;
       float ringY = y + offsetY + 0.3F;
       RenderUtils.drawDefaultHudInfoBox(eventRender.getContext().method_51448(), timeBoxX, y + offsetY, timeBoxWidth, grayColor, darkColor);
       issue(10).drawCenteredString(eventRender.getContext().method_51448(), time, timeBoxX + timeBoxWidth / 2.0F, y + offsetY + 3.0F, textColor);
       
       float progress = 1.0F;
       if (!snapshot.infinite) {
         int currentDuration = snapshot.duration;
         int maxDuration = ((Integer)this.maxDurations.getOrDefault(type, Integer.valueOf(currentDuration))).intValue();
         
         if (maxDuration > 0) {
           progress = MathHelper.method_15363(currentDuration / maxDuration, 0.0F, 1.0F);
         } else {
           progress = 0.0F;
         } 
       } 
       
       int ringColor = ColorUtils.setAlphaColor(colorTheme, alpha);
       float thickness = 1.75F;
       RenderUtils.drawRingArc(eventRender.getContext().method_51448(), ringX, ringY, ringSize, thickness, -90.0F, 270.0F, grayColor);
       if (progress > 0.0F) {
         float endAngle = -90.0F + 360.0F * progress;
         RenderUtils.drawRingArc(eventRender.getContext().method_51448(), ringX, ringY, ringSize, thickness, -90.0F, endAngle, ringColor);
       } 
       
       offsetY += 12.0F * animValue;
       ScissorUtils.pop();
       ScissorUtils.unset();
     } 
 
     
     this.animations.entrySet().removeIf(entry -> (!active.contains(entry.getKey()) && ((AnimationUtils)entry.getValue()).getValue() <= 0.01F));
     this.snapshots.keySet().removeIf(type -> !this.animations.containsKey(type));
     this.maxDurations.keySet().removeIf(type -> !this.animations.containsKey(type));
     
     this.draggable.setWidth(width);
     this.draggable.setHeight(height);
   }
   
   public void WaveStyle(EventRender.Default eventRender) {
     float x = this.draggable.getX();
     float y = this.draggable.getY();
     
     int time = (int)((float)(System.currentTimeMillis() % 2000L) / 2000.0F * 360.0F);
     
     int leftTop = ColorUtils.getThemeColor(time);
     int leftBottom = ColorUtils.getThemeColor(time + 30);
     int centerTop = ColorUtils.getThemeColor(time + 90);
     int centerBottom = ColorUtils.getThemeColor(time + 120);
     int rightTop = ColorUtils.getThemeColor(time + 180);
     int rightBottom = ColorUtils.getThemeColor(time + 210);
 
 
     
     Collection<StatusEffectInstance> effects = (mc != null && mc.field_1724 != null) ? mc.field_1724.method_6026() : List.<StatusEffectInstance>of();
     
     Set<StatusEffect> active = new HashSet<>();
     for (StatusEffectInstance effect : effects) {
       StatusEffect type = (StatusEffect)effect.method_5579().comp_349();
       active.add(type);
       getAnimation(type).update(1.0F);
     } 
     for (Map.Entry<StatusEffect, AnimationUtils> entry : this.animations.entrySet()) {
       if (!active.contains(entry.getKey())) {
         ((AnimationUtils)entry.getValue()).update(0.0F);
       }
     } 
     
     float width = 84.0F;
     float height = 18.0F;
     int visibleEffects = 0;
     
     for (StatusEffectInstance effect : effects) {
       AnimationUtils anim = getAnimation((StatusEffect)effect.method_5579().comp_349());
       float animValue = anim.getValue();
       if (animValue <= 0.01F)
         continue;  visibleEffects++;
       
       String baseName = I18n.method_4662(effect.method_5586(), new Object[0]);
       String levelSuffix = getLevelSuffix(effect.method_5578() + 1);
       String line = baseName + baseName;
       width = Math.max(width, issue(16).getWidth(line) + 38.0F);
       width = Math.max(width, issue(15).getWidth(formatDuration(effect)) + 38.0F);
       height += 18.0F * animValue;
     } 
     
     if (visibleEffects == 0) {
       float headerHeight = 18.0F;
       RenderUtils.drawWaveHudHeader(eventRender.getContext().method_51448(), x, y, width, 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
       
       String str = "potions";
       float f1 = x + (width - issue(16).getWidth(str)) / 2.0F;
       drawTextWithShadow(eventRender, issue(16), str, f1, y + 5.0F, -1);
       this.draggable.setWidth(width);
       this.draggable.setHeight(headerHeight);
       
       return;
     } 
     RenderUtils.drawWaveHudPanel(eventRender.getContext().method_51448(), x, y, width, height, ColorUtils.rgba(25, 25, 25, 150), 15.0F, 0.0F, 10.0F, 10.0F, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
 
 
     
     String title = "potions";
     float titleX = x + (width - issue(16).getWidth(title)) / 1.9F;
     drawTextWithShadow(eventRender, issue(16), title, titleX, y + 5.0F, -1);
     
     float yOffset = 20.0F;
     for (StatusEffectInstance effect : effects) {
       AnimationUtils anim = getAnimation((StatusEffect)effect.method_5579().comp_349());
       float animValue = anim.getValue();
       if (animValue <= 0.01F)
         continue; 
       ScissorUtils.push();
       ScissorUtils.setFromComponentCoordinates(x, y, width, height);
       
       int alpha = (int)(255.0F * animValue);
       int textColor = ColorUtils.rgba(255, 255, 255, alpha);
       int levelColor = ColorUtils.rgba(20, 185, 45, alpha);
       
       float iconX = x + 5.0F;
       float iconY = y + yOffset;
       drawEffectIcon(eventRender, effect.method_5579(), iconX, iconY, 11, alpha);
       
       String baseName = I18n.method_4662(effect.method_5586(), new Object[0]).toLowerCase();
       String levelSuffix = getLevelSuffix(effect.method_5578() + 1);
       float textX = iconX + 14.0F;
       
       issue(15).draw(eventRender.getContext().method_51448(), baseName + " >", textX, y + yOffset - 1.0F, textColor);
       if (!levelSuffix.isEmpty()) {
         float nameW = issue(14).getWidth(baseName + " >");
         issue(14).draw(eventRender.getContext().method_51448(), " " + levelSuffix, (textX + nameW + 2.0F), (y + yOffset) - 0.5D, levelColor);
       } 
       
       issue(14).draw(eventRender.getContext().method_51448(), formatDuration(effect), textX, (y + yOffset) + 7.5D, textColor);
       
       yOffset += 18.0F * animValue;
       ScissorUtils.pop();
       ScissorUtils.unset();
     } 
     
     this.draggable.setWidth(width);
     this.draggable.setHeight(height);
   }
 }

