package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.platform.GlStateManager;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;
 import java.util.UUID;
 import net.minecraft.class_10142;
 import net.minecraft.class_10366;
 import net.minecraft.class_1297;
 import net.minecraft.class_1309;
 import net.minecraft.class_1542;
 import net.minecraft.class_1657;
 import net.minecraft.class_1792;
 import net.minecraft.class_1799;
 import net.minecraft.class_1814;
 import net.minecraft.class_238;
 import net.minecraft.class_243;
 import net.minecraft.class_2561;
 import net.minecraft.class_2583;
 import net.minecraft.class_268;
 import net.minecraft.class_276;
 import net.minecraft.class_284;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_3532;
 import net.minecraft.class_4587;
 import net.minecraft.class_5944;
 import net.minecraft.class_6367;
 import net.minecraft.class_640;
 import net.minecraft.class_7923;
 import org.joml.Matrix4f;
 import org.joml.Matrix4fc;
 import org.joml.Quaternionf;
 import org.joml.Quaternionfc;
 import org.joml.Vector3f;
 import org.joml.Vector4f;
 import org.lwjgl.opengl.GL11;
 import org.lwjgl.opengl.GL30;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.Theme;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.api.utils.render.ShaderUtils;
 import shame.nazuna.api.utils.render.font.ReplaceSymbols;
 import shame.nazuna.api.utils.render.fonts.msdf.Font;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.misc.NameProtect;
 import shame.nazuna.client.modules.impl.misc.ScoreboardHP;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class EntityESP extends Module {
   public static EntityESP INSTANCE = new EntityESP();
   
   private static final float TAG_FROM_ENTITY_GAP = 0.0F;
   private static final int TAG_FONT_SIZE = 13;
   private static final int TAG_TEXT_COLOR = -1;
   private static final int TAG_HEALTH_COLOR = -43691;
   private static final int TAG_FRIEND_COLOR = -11141291;
   private static final float TAG_HUD_RADIUS = 1.1F;
   private static final int TAG_HUD_ALPHA = 204;
   private static final float ARMOR_CELL_SIZE = 8.4F;
   private static final float ARMOR_ITEM_SCALE = 0.46F;
   private static final float ARMOR_CELL_GAP = 1.0F;
   private static final float PLAYER_HEAD_SIZE = 7.5F;
   private static final float PLAYER_HEAD_GAP = 3.0F;
   private static final float BOX_LINE_WIDTH = 1.5F;
   private static final float FILL_ALPHA = 0.23F;
   private static final float EPSILON = 0.001F;
   private static final long DONATE_CACHE_TTL_MS = 1000L;
   private static final long DONATE_CACHE_CLEANUP_MS = 2000L;
   private static final int MAX_ITEM_TAGS_PER_FRAME = 48;
   private final ListSetting elements = new ListSetting("Элементы", new BooleanSetting[] { new BooleanSetting("Теги", true), new BooleanSetting("Броня", true) });
 
 
   
   private final BooleanSetting show3DBox = new BooleanSetting("Боксы", true);
   private final BooleanSetting boxFilled = new BooleanSetting("Заполнить бокс", true);
   private final ModeSetting boxFillMode = new ModeSetting("Мод заливки", "Обычный", new String[] { "Обычный", "Волны", "Нитки" });
   private final FloatSetting waveSpeed = (new FloatSetting("Скорость волн", 1.2F, 0.1F, 5.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.boxFillMode.is("Волны")));
   private final FloatSetting waveScale = (new FloatSetting("Размер волн", 1.0F, 1.0F, 3.0F, 0.1F))
     .visible(() -> Boolean.valueOf(this.boxFillMode.is("Волны")));
   private final FloatSetting lineSpeed = (new FloatSetting("Скорость линий", 1.4F, 0.1F, 5.0F, 0.1F))
     .visible(() -> Boolean.valueOf((this.boxFillMode.getIndex() == 2)));
   private final FloatSetting lineJitter = (new FloatSetting("Прыжки линий", 0.55F, 0.0F, 1.5F, 0.01F))
     .visible(() -> Boolean.valueOf((this.boxFillMode.getIndex() == 2)));
   private final FloatSetting outline = (new FloatSetting("Обводка", 1.1F, 0.1F, 5.0F, 0.1F))
     .visible(this::isPostBoxMode);
   private final FloatSetting glow = (new FloatSetting("Свечение", 1.0F, 0.0F, 5.0F, 0.1F))
     .visible(this::isPostBoxMode);
   private final FloatSetting fill = (new FloatSetting("Сила заливки", 0.6F, 0.0F, 1.0F, 0.01F))
     .visible(this::isPostBoxMode);
   private final FloatSetting alpha = (new FloatSetting("Прозрачность", 1.0F, 0.0F, 4.0F, 0.01F))
     .visible(this::isPostBoxMode);
   
   private final BooleanSetting hurtTint = new BooleanSetting("Краснеть при ударе", true);
   private final Matrix4f lastProjectionMatrix = new Matrix4f();
   private final Quaternionf lastCameraRotation = new Quaternionf();
   private final Quaternionf lastInverseCameraRotation = new Quaternionf();
   private class_243 lastCameraPos = class_243.field_1353;
   private float lastTickDelta;
   private int lastScaledWidth;
   private int lastScaledHeight;
   private boolean hasProjection;
   private class_276 maskBuffer;
   private final List<class_276> bloomBuffers = new ArrayList<>();
   private final Map<UUID, DonateCache> donateCache = new HashMap<>();
   private final Map<Integer, Float> entityHurtTintProgress = new HashMap<>();
   private long nextDonateCacheCleanupAt;
   private int maskWidth = -1;
   private int maskHeight = -1;
   private boolean hasShaderMask;
   private final Vector3f projectionScratch = new Vector3f();
   private final Vector4f clipScratch = new Vector4f();
   private final ProjectedPoint projectedPoint = new ProjectedPoint();
   private final class_1799[] armorStacksScratch = new class_1799[6];
   private final boolean[] armorHandScratch = new boolean[6];
   private int frameThemeColor = -1;
   private final BooleanSetting targetPlayers = new BooleanSetting("Игроки", true);
   private final BooleanSetting targetMobs = new BooleanSetting("Мобы", true);
   private final BooleanSetting targetAnimals = new BooleanSetting("Животные", true);
   private final BooleanSetting targetItems = new BooleanSetting("Предметы", true);
   private final ListSetting targets = new ListSetting("Отображать", new BooleanSetting[] { this.targetPlayers, this.targetMobs, this.targetAnimals, this.targetItems });
 
 
 
 
 
   
   public EntityESP() {
     super("EntityESP", "Показывает игроков через стену", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.targets, (Setting)this.elements });
     addSettings(new Setting[] { (Setting)this.show3DBox, (Setting)this.boxFilled, (Setting)this.hurtTint });
   }
 
   
   public void onDisable() {
     this.hasProjection = false;
     this.hasShaderMask = false;
     this.donateCache.clear();
     this.entityHurtTintProgress.clear();
     this.nextDonateCacheCleanupAt = 0L;
     if (this.maskBuffer != null) {
       this.maskBuffer.method_1238();
       this.maskBuffer = null;
     } 
     for (class_276 fb : this.bloomBuffers) {
       fb.method_1238();
     }
     this.bloomBuffers.clear();
     super.onDisable();
   }
   
   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
     this.hasProjection = true;
     this.lastProjectionMatrix.set((Matrix4fc)event.getProjectionMatrix());
     this.lastCameraPos = event.getCamera().method_19326();
     this.lastCameraRotation.set((Quaternionfc)event.getCamera().method_23767());
     this.lastInverseCameraRotation.set((Quaternionfc)this.lastCameraRotation).conjugate();
     this.lastTickDelta = event.getTickDelta();
     this.lastScaledWidth = mc.method_22683().method_4486();
     this.lastScaledHeight = mc.method_22683().method_4502();
     this.frameThemeColor = getStableThemeColor();
     
     this.hasShaderMask = false;
     if (!this.show3DBox.isState() || mc.field_1687 == null || mc.field_1724 == null)
       return;  class_4587 matrices = event.getMatrices();
     float tickDelta = event.getTickDelta();
     boolean postMode = isPostBoxMode();
     boolean threadMode = isThreadMode();
     
     if (postMode) {
       ensureMaskBuffer();
       if (this.maskBuffer != null) {
         this.maskBuffer.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
         this.maskBuffer.method_1230();
         copyMainDepthToMask();
         this.maskBuffer.method_1235(false);
         RenderSystem.disableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(class_10142.field_53876);
       } 
     } 
     
     for (class_1297 entity : mc.field_1687.method_18112()) {
       if (!shouldProcess3DEntity(entity))
         continue;  if (postMode && this.maskBuffer != null) {
         drawPlayerMaskBox(matrices, entity, tickDelta);
         this.hasShaderMask = true; continue;
       } 
       render3DBox(matrices, entity, tickDelta);
     } 
 
     
     if (postMode && this.maskBuffer != null) {
       RenderSystem.disableBlend();
       RenderSystem.depthMask(true);
       RenderSystem.enableDepthTest();
       RenderSystem.enableCull();
       mc.method_1522().method_1235(true);
       if (this.show3DBox.isState()) {
         renderShaderBoxesWorldPass();
       }
     } 
     
     if (threadMode) {
       for (class_1297 entity : mc.field_1687.method_18112()) {
         if (!shouldProcess3DEntity(entity))
           continue;  renderThreadWeb(matrices, entity, tickDelta);
       } 
     }
   }
   
   @EventLink(priority = 100)
   public void onRender2D(EventRender.Default event) {
     if (!this.hasProjection || mc.field_1687 == null || mc.field_1724 == null)
       return;  this.frameThemeColor = getStableThemeColor();
     boolean tagsEnabled = (!this.elements.getSettings().isEmpty() && ((BooleanSetting)this.elements.getSettings().get(0)).isState());
     boolean armorEnabled = (this.elements.getSettings().size() > 1 && ((BooleanSetting)this.elements.getSettings().get(1)).isState());
     if (!tagsEnabled && !armorEnabled)
       return; 
     Font font = tagsEnabled ? Fonts.getFont("sf_regular", 13) : null;
     int renderedItemTags = 0;
     
     for (class_1297 entity : mc.field_1687.method_18112()) {
       if (entity instanceof class_1657) { class_1657 player = (class_1657)entity;
         if (!shouldProcess2DPlayer(player))
           continue; 
         class_238 interpolatedBox = getInterpolatedBox((class_1297)player, this.lastTickDelta);
         ScreenRect rect = projectBox(interpolatedBox);
         if (rect == null)
           continue; 
         if (tagsEnabled && font != null) {
           drawTag(event, player, rect, font);
         }
         if (armorEnabled) {
           drawArmor(event, player, rect, tagsEnabled);
         }
         
         continue; }
       
       if (!tagsEnabled || font == null) {
         continue;
       }
       
       if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
         if (!shouldProcessItem2D(itemEntity) || renderedItemTags >= 48) {
           continue;
         }
         
         if (projectEntityAnchor((class_1297)itemEntity, itemEntity.method_17682() + 0.25D, this.projectedPoint)) {
           drawDroppedItemTag(event, itemEntity, this.projectedPoint.x, this.projectedPoint.y, font);
           renderedItemTags++;
         } 
         
         continue; }
       
       if (entity instanceof class_1309) { class_1309 livingEntity = (class_1309)entity; if (!shouldProcessLiving2D(livingEntity)) {
           continue;
         }
         
         class_238 interpolatedBox = getInterpolatedBox((class_1297)livingEntity, this.lastTickDelta);
         ScreenRect rect = projectBox(interpolatedBox);
         if (rect == null)
           continue; 
         drawLivingTag(event, livingEntity, rect, font); }
     
     } 
   }
   private void drawTag(EventRender.Default event, class_1657 player, ScreenRect rect, Font font) {
     class_4587 matrices = event.getContext().method_51448();
     List<DonateSegment> donateSegments = getDonateSegmentsFromTab(player);
     String nameText = getProtectedName(player.method_5820());
     float hp = ScoreboardHP.getHealthWithAbsorption((class_1309)player);
     String leftBracket = "";
     String hpText = "" + Math.round(hp) + " hp";
     String rightBracket = "";
     
     boolean isFriend = (astra.INSTANCE.friendStorage != null && astra.INSTANCE.friendStorage.isFriend(player.method_5477().getString()));
     String friendSuffix = isFriend ? " [F]" : "";
     
     float donateWidth = 0.0F;
     for (DonateSegment segment : donateSegments) {
       donateWidth += font.getStringWidth(segment.text());
     }
 
 
 
 
     
     float totalWidth = donateWidth + font.getStringWidth(nameText) + font.getStringWidth(leftBracket) + font.getStringWidth(hpText) + font.getStringWidth(rightBracket) + font.getStringWidth(friendSuffix) + 7.5F + 3.0F + 2.0F;
 
     
     float boxHeight = 16.0F;
     float x = rect.centerX() - totalWidth * 0.5F;
     float y = getTagTopY(rect, boxHeight);
     
     drawDefaultTagPanel(matrices, x - 1.0F, y - 0.5F, totalWidth + 2.0F, boxHeight - 4.0F);
     
     float headY = y + 1.7F;
     RenderUtils.drawPlayerHead(matrices, player.method_5667(), x + 1.0F, headY, 7.5F, 1.0F, 1.0F, 0.0F);
     
     float drawX = x + 1.5F + 7.5F + 3.0F;
     for (DonateSegment segment : donateSegments) {
       font.drawString(matrices, segment.text(), drawX, y + 4.0F, segment.color());
       drawX += font.getStringWidth(segment.text());
     } 
     font.drawString(matrices, nameText, drawX, y + 4.0F, -1);
     drawX += font.getStringWidth(nameText);
     font.drawString(matrices, leftBracket, drawX, y + 4.0F, -1);
     drawX += font.getStringWidth(leftBracket);
     font.drawString(matrices, hpText, drawX, y + 4.0F, -43691);
     drawX += font.getStringWidth(hpText);
     font.drawString(matrices, rightBracket, drawX, y + 4.0F, -1);
     drawX += font.getStringWidth(rightBracket);
     if (isFriend) font.drawString(matrices, friendSuffix, drawX, y + 4.0F, -11141291); 
   }
   
   private void drawArmor(EventRender.Default event, class_1657 player, ScreenRect rect, boolean tagsEnabled) {
     class_4587 matrices = event.getContext().method_51448();
     int count = 0;
     class_1799 offHand = player.method_6079();
     if (!offHand.method_7960()) {
       this.armorStacksScratch[count] = offHand;
       this.armorHandScratch[count++] = true;
     } 
     for (class_1799 stack : player.method_5661()) {
       if (!stack.method_7960()) {
         this.armorStacksScratch[count] = stack;
         this.armorHandScratch[count++] = false;
       } 
     } 
     class_1799 mainHand = player.method_6047();
     if (!mainHand.method_7960()) {
       this.armorStacksScratch[count] = mainHand;
       this.armorHandScratch[count++] = true;
     } 
     if (count == 0)
       return; 
     float step = 9.4F;
     float rowWidth = count * 8.4F + Math.max(0, count - 1) * 1.0F;
     float x = rect.centerX() - rowWidth * 0.5F;
 
     
     float y = tagsEnabled ? (getTagTopY(rect, 14.0F) - 13.0F) : (rect.minY() - 13.0F);
     int i;
     for (i = 0; i < count; i++) {
       float drawX = x + i * step;
       float drawY = y;
       drawDefaultTagPanel(matrices, drawX, drawY, 8.4F, 8.4F);
     } 
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     for (i = 0; i < count; i++) {
       float drawX = x + i * step;
       float drawY = y;
       int stackIndex = count - 1 - i;
       class_1799 stack = this.armorStacksScratch[stackIndex];
       boolean handStack = this.armorHandScratch[stackIndex];
       
       matrices.method_22903();
       float itemSize = 7.36F;
       float itemX = drawX + (8.4F - itemSize) * 0.5F;
       float itemY = drawY + (8.4F - itemSize) * 0.5F;
       matrices.method_46416(itemX, itemY, 0.0F);
       matrices.method_22905(0.46F, 0.46F, 1.0F);
       event.getContext().method_51427(stack, 0, 0);
       if (!handStack) {
         event.getContext().method_51432(mc.field_1772, stack, 0, 0, null);
       }
       matrices.method_22909();
     } 
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.disableBlend();
     
     for (i = 0; i < count; i++) {
       this.armorStacksScratch[i] = class_1799.field_8037;
       this.armorHandScratch[i] = false;
     } 
   }
   
   private void drawLivingTag(EventRender.Default event, class_1309 entity, ScreenRect rect, Font font) {
     class_4587 matrices = event.getContext().method_51448();
     class_1657 player = (class_1657)entity;
     
     String nameText = (entity instanceof class_1657) ? getProtectedName(player.method_5476().getString()) : entity.method_5476().getString();
     String hpText = "" + Math.round(ScoreboardHP.getHealthWithAbsorption(entity)) + " hp";
     float totalWidth = font.getStringWidth(nameText) + font.getStringWidth(" ") + font.getStringWidth(hpText);
     float boxHeight = 14.0F;
     float x = rect.centerX() - totalWidth * 0.5F;
     float y = getTagTopY(rect, boxHeight);
     
     drawDefaultTagPanel(matrices, x - 1.0F, y - 0.5F, totalWidth + 2.0F, boxHeight - 4.0F);
     font.drawString(matrices, nameText, x, y + 3.0F, -1);
     font.drawString(matrices, hpText, x + font.getStringWidth(nameText) + font.getStringWidth(" "), y + 3.0F, -43691);
   }
   
   private void drawDroppedItemTag(EventRender.Default event, class_1542 itemEntity, float anchorX, float anchorY, Font font) {
     class_4587 matrices = event.getContext().method_51448();
     class_1799 stack = itemEntity.method_6983();
     String countText = "" + stack.method_7947() + "x";
     List<DonateSegment> itemSegments = getStyledTextSegments(stack.method_7964(), getDroppedItemTextColor(stack));
     int countColor = ColorUtils.rgba(155, 155, 155, 255);
     float itemNameWidth = 0.0F;
     for (DonateSegment segment : itemSegments) {
       itemNameWidth += font.getStringWidth(segment.text());
     }
     float spaceWidth = font.getStringWidth(" ");
     float totalWidth = itemNameWidth + spaceWidth + font.getStringWidth(countText);
     float boxHeight = 14.0F;
     float x = anchorX - totalWidth * 0.5F;
     float y = anchorY - boxHeight - 2.0F;
     
     drawDefaultTagPanel(matrices, x - 2.0F, y - 0.5F, totalWidth + 4.0F, boxHeight - 3.0F);
     float drawX = x;
     for (DonateSegment segment : itemSegments) {
       font.drawString(matrices, segment.text(), drawX, y + 3.5F, segment.color());
       drawX += font.getStringWidth(segment.text());
     } 
     font.drawString(matrices, countText, drawX + spaceWidth, y + 3.5F, countColor);
   }
   
   private int getMinecraftItemNameColor(class_1799 stack) {
     class_2561 name = stack.method_7964();
     if (name != null) {
       int[] discoveredColor = { 0 };
       boolean[] found = { false };
       name.method_27658((style, string) -> { if (!found[0] && style != null && style.method_10973() != null) { discoveredColor[0] = 0xFF000000 | style.method_10973().method_27716(); found[0] = true; }  return found[0] ? Optional.<String>of(string) : Optional.empty(); }class_2583.field_24360);
 
 
 
 
 
       
       if (found[0]) return discoveredColor[0];
     
     } 
     switch (stack.method_7932()) { default: throw new MatchException(null, null);case field_8907: case field_8903: case field_8904: case field_8906: break; }  return 
 
 
       
       -1;
   }
 
   
   private int getDroppedItemTextColor(class_1799 stack) {
     return getMinecraftItemNameColor(stack);
   }
   
   private boolean isNetheriteItem(class_1792 item) {
     return class_7923.field_41178.method_10221(item).method_12832().contains("netherite");
   }
   
   private void drawDefaultTagPanel(class_4587 matrices, float x, float y, float width, float height) {
     int themeColor = this.frameThemeColor;
     RenderUtils.drawDefaultHudPanel(matrices, x, y, width, height, 1.1F, 1.1F, 
 
         
         ColorUtils.rgba(50, 50, 50, 204), 
         ColorUtils.setAlphaColor(ColorUtils.darken(themeColor, 0.15F), 204), 
         ColorUtils.setAlphaColor(ColorUtils.darken(themeColor, 0.05F), 204));
   }
 
   
   public boolean shouldHideVanillaTags() {
     return (isEnable() && !this.elements.getSettings().isEmpty() && ((BooleanSetting)this.elements.getSettings().get(0)).isState());
   }
   
   private float getTagTopY(ScreenRect rect, float tagHeight) {
     return rect.minY() - tagHeight - 0.0F;
   }
   
   private String[] getNameVariants(class_1657 player) {
     String profileName = (player.method_7334() != null) ? player.method_7334().getName() : "";
     String scoreboardName = player.method_5820();
     String protectedScoreboardName = getProtectedName(scoreboardName);
     String protectedProfileName = getProtectedName(profileName);
     String protectedPlainName = getProtectedName(player.method_5477().getString());
     return new String[] { player
         .method_5477().getString(), protectedPlainName, scoreboardName, protectedScoreboardName, profileName, protectedProfileName };
   }
 
 
 
 
 
 
   
   private String getProtectedName(String input) {
     NameProtect nameProtect = (ModuleClass.INSTANCE != null) ? ModuleClass.nameProtect : null;
     if (nameProtect == null || !nameProtect.isEnable()) {
       return input;
     }
     return nameProtect.patch(input);
   }
   
   private int findAnyNameIndex(String text, String[] names) {
     if (text == null || text.isEmpty() || names == null) return -1; 
     int best = -1;
     for (String name : names) {
       if (name != null && !name.isEmpty()) {
         int idx = indexOfIgnoreCase(text, name);
         if (idx >= 0 && (best == -1 || idx < best))
           best = idx; 
       } 
     } 
     return best;
   }
   
   private int indexOfIgnoreCase(String text, String search) {
     if (text == null || search == null || search.isEmpty()) return -1; 
     int limit = text.length() - search.length();
     for (int i = 0; i <= limit; i++) {
       if (text.regionMatches(true, i, search, 0, search.length())) {
         return i;
       }
     } 
     return -1;
   }
   
   private void trimSegmentsToLength(List<DonateSegment> segments, int maxLength) {
     int remaining = Math.max(0, maxLength);
     List<DonateSegment> trimmed = new ArrayList<>();
     for (DonateSegment seg : segments) {
       if (remaining <= 0)
         break;  String text = seg.text();
       if (text.length() <= remaining) {
         trimmed.add(seg);
         remaining -= text.length(); continue;
       } 
       trimmed.add(new DonateSegment(text.substring(0, remaining), seg.color()));
       remaining = 0;
     } 
     
     segments.clear();
     segments.addAll(trimmed);
   }
   
   private List<DonateSegment> getDonateSegmentsFromTab(class_1657 player) {
     long now = System.currentTimeMillis();
     DonateCache cache = this.donateCache.computeIfAbsent(player.method_5667(), uuid -> new DonateCache());
     if (now < cache.nextUpdateAt) {
       return cache.segments;
     }
     
     List<DonateSegment> segments = new ArrayList<>();
     if (mc.method_1562() == null) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       return cache.segments;
     } 
     
     class_640 entry = mc.method_1562().method_2871(player.method_5667());
     if (entry == null) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       return cache.segments;
     } 
     
     class_2561 displayName = entry.method_2971();
     if (displayName == null) displayName = player.method_5476(); 
     if (displayName == null) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       return cache.segments;
     } 
     
     String[] nameVariants = getNameVariants(player);
     boolean[] foundName = { false };
     
     displayName.method_27658((style, string) -> { if (foundName[0] || string == null || string.isEmpty()) return Optional.empty();  String part = string.replace('\n', ' ').replace('\r', ' '); int nameIndex = findAnyNameIndex(part, nameVariants); String donatePart = (nameIndex >= 0) ? part.substring(0, nameIndex) : part; if (!donatePart.isEmpty()) { int baseColor = (style.method_10973() != null) ? style.method_10973().method_27716() : 16777215; appendColoredSegments(segments, donatePart, baseColor); }  if (nameIndex >= 0) foundName[0] = true;  return Optional.empty(); }class_2583.field_24360);
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     if (!foundName[0]) {
       segments.clear();
       class_268 team = player.method_5781();
       if (team != null && team.method_1144() != null) {
         appendTextSegments(segments, team.method_1144());
       }
     } 
     
     if (segments.isEmpty()) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       cleanupDonateCache(now);
       return cache.segments;
     } 
     
     StringBuilder combined = new StringBuilder();
     for (DonateSegment seg : segments) {
       combined.append(seg.text());
     }
     int donateNameIndex = findAnyNameIndex(combined.toString(), nameVariants);
     if (donateNameIndex >= 0) {
       if (donateNameIndex == 0) {
         cache.segments = Collections.emptyList();
         cache.nextUpdateAt = now + 1000L;
         cleanupDonateCache(now);
         return cache.segments;
       } 
       trimSegmentsToLength(segments, donateNameIndex);
     } 
     
     if (segments.isEmpty()) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       cleanupDonateCache(now);
       return cache.segments;
     } 
     
     StringBuilder textCheck = new StringBuilder();
     for (DonateSegment seg : segments) {
       textCheck.append(seg.text());
     }
     if (textCheck.toString().trim().isEmpty()) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       cleanupDonateCache(now);
       return cache.segments;
     } 
     
     DonateSegment last = segments.get(segments.size() - 1);
     if (!last.text().endsWith(" ")) {
       segments.set(segments.size() - 1, new DonateSegment(last.text() + " ", last.color()));
     }
     cache.segments = List.copyOf(segments);
     cache.nextUpdateAt = now + 1000L;
     cleanupDonateCache(now);
     return cache.segments;
   }
   
   private void appendTextSegments(List<DonateSegment> out, class_2561 text) {
     text.method_27658((style, string) -> { if (string == null || string.isEmpty()) return Optional.empty();  int baseColor = (style.method_10973() != null) ? style.method_10973().method_27716() : 16777215; appendColoredSegments(out, string.replace('\n', ' ').replace('\r', ' '), baseColor); return Optional.empty(); }class_2583.field_24360);
   }
 
 
 
 
 
 
 
 
   
   private List<DonateSegment> getStyledTextSegments(class_2561 text, int fallbackColor) {
     List<DonateSegment> segments = new ArrayList<>();
     if (text != null) {
       appendTextSegments(segments, text);
     }
     if (segments.isEmpty() && text != null && !text.getString().isEmpty()) {
       segments.add(new DonateSegment(text.getString(), fallbackColor));
     }
     return segments;
   }
   
   private void appendColoredSegments(List<DonateSegment> out, String text, int baseColor) {
     if (text == null || text.isEmpty())
       return;  int currentColor = baseColor;
     StringBuilder chunk = new StringBuilder();
     
     int chunkColor = currentColor;
     int offset;
     for (offset = 0; offset < text.length(); ) {
       int codePoint = text.codePointAt(offset);
       int charCount = Character.charCount(codePoint);
       
       if (codePoint == 167 && offset + charCount < text.length()) {
         flushSegment(out, chunk, chunkColor);
         char code = Character.toLowerCase(text.charAt(offset + charCount));
         Integer mappedColor = sectionColorToRgb(code);
         if (mappedColor != null) {
           currentColor = mappedColor.intValue();
         } else if (code == 'r') {
           currentColor = baseColor;
         } 
         chunkColor = currentColor;
         offset += charCount + 1;
         
         continue;
       } 
       String replacement = ReplaceSymbols.replaceCodePoint(codePoint);
       if (replacement != null) {
         flushSegment(out, chunk, chunkColor);
         int totalChars = Math.max(1, replacement.length());
         for (int i = 0; i < replacement.length(); i++) {
           int gradientColor = ReplaceSymbols.getGradientColorForReplacement(codePoint, i, totalChars, 1.0F, currentColor);
           if (chunk.length() > 0 && chunkColor != gradientColor) {
             flushSegment(out, chunk, chunkColor);
           }
           chunkColor = gradientColor;
           chunk.append(replacement.charAt(i));
         } 
         offset += charCount;
         
         continue;
       } 
       if (chunk.length() > 0 && chunkColor != currentColor) {
         flushSegment(out, chunk, chunkColor);
       }
       chunkColor = currentColor;
       chunk.appendCodePoint(codePoint);
       offset += charCount;
     } 
     
     flushSegment(out, chunk, chunkColor);
   }
   
   private void flushSegment(List<DonateSegment> out, StringBuilder chunk, int color) {
     if (chunk.isEmpty())
       return;  out.add(new DonateSegment(chunk.toString(), color));
     chunk.setLength(0);
   }
   
   private Integer sectionColorToRgb(char code) {
     switch (code) { case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':  }  return 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       
       null;
   }
 
   
   private void cleanupDonateCache(long now) {
     if (now < this.nextDonateCacheCleanupAt || mc.field_1687 == null) {
       return;
     }
     this.nextDonateCacheCleanupAt = now + 2000L;
     this.donateCache.entrySet().removeIf(entry -> (mc.field_1687.method_18470((UUID)entry.getKey()) == null));
   }
   
   private class_238 getInterpolatedBox(class_1297 entity, float tickDelta) {
     double x = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317());
     double y = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318());
     double z = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321());
     
     double ox = x - entity.method_23317();
     double oy = y - entity.method_23318();
     double oz = z - entity.method_23321();
     
     return entity.method_5829().method_989(ox, oy, oz).method_1014(0.05D);
   }
   
   private ScreenRect projectBox(class_238 box) {
     double minX = Double.POSITIVE_INFINITY;
     double minY = Double.POSITIVE_INFINITY;
     double maxX = Double.NEGATIVE_INFINITY;
     double maxY = Double.NEGATIVE_INFINITY;
     boolean projectedAny = false;
     
     for (int xi = 0; xi < 2; xi++) {
       for (int yi = 0; yi < 2; yi++) {
         for (int zi = 0; zi < 2; zi++) {
           if (projectToScreen(
               (xi == 0) ? box.field_1323 : box.field_1320, 
               (yi == 0) ? box.field_1322 : box.field_1325, 
               (zi == 0) ? box.field_1321 : box.field_1324, this.projectedPoint)) {
 
             
             projectedAny = true;
             minX = Math.min(minX, this.projectedPoint.x);
             minY = Math.min(minY, this.projectedPoint.y);
             maxX = Math.max(maxX, this.projectedPoint.x);
             maxY = Math.max(maxY, this.projectedPoint.y);
           } 
         } 
       } 
     } 
     if (!projectedAny) return null; 
     if (minX > (mc.method_22683().method_4486() + 300) || maxX < -300.0D) return null; 
     if (minY > (mc.method_22683().method_4502() + 300) || maxY < -300.0D) return null; 
     if (maxX - minX < 2.0D || maxY - minY < 2.0D) return null;
     
     return new ScreenRect((float)minX, (float)minY, (float)maxX, (float)maxY);
   }
   
   private boolean projectToScreen(double worldX, double worldY, double worldZ, ProjectedPoint out) {
     this.projectionScratch.set((float)(worldX - this.lastCameraPos.field_1352), (float)(worldY - this.lastCameraPos.field_1351), (float)(worldZ - this.lastCameraPos.field_1350));
 
 
 
     
     this.projectionScratch.rotate((Quaternionfc)this.lastInverseCameraRotation);
     
     this.clipScratch.set(this.projectionScratch.x, this.projectionScratch.y, this.projectionScratch.z, 1.0F);
     this.lastProjectionMatrix.transform(this.clipScratch);
     
     float w = this.clipScratch.w;
     if (w <= 1.0E-5F) return false;
     
     float ndcX = this.clipScratch.x / w;
     float ndcY = this.clipScratch.y / w;
     float ndcZ = this.clipScratch.z / w;
     
     float screenX = (ndcX * 0.5F + 0.5F) * this.lastScaledWidth;
     float screenY = (1.0F - ndcY * 0.5F + 0.5F) * this.lastScaledHeight;
     
     if (Float.isNaN(screenX) || Float.isNaN(screenY)) return false; 
     if (Float.isInfinite(screenX) || Float.isInfinite(screenY)) return false;
     
     out.x = screenX;
     out.y = screenY;
     out.z = ndcZ;
     return true;
   }
   
   private boolean projectEntityAnchor(class_1297 entity, double yOffset, ProjectedPoint out) {
     double x = class_3532.method_16436(this.lastTickDelta, entity.field_6038, entity.method_23317());
     double y = class_3532.method_16436(this.lastTickDelta, entity.field_5971, entity.method_23318()) + yOffset;
     double z = class_3532.method_16436(this.lastTickDelta, entity.field_5989, entity.method_23321());
     return projectToScreen(x, y, z, out);
   }
   
   private boolean isInFirstPerson() {
     return (mc != null && mc.field_1773 != null && !mc.field_1773.method_19418().method_19333());
   }
   
   private boolean shouldProcess3DEntity(class_1297 entity) {
     if (entity == null || entity.method_31481() || entity instanceof net.minecraft.class_1531) {
       return false;
     }
     if (entity instanceof class_1657) { class_1657 player = (class_1657)entity;
       return shouldProcessPlayer(player, false); }
     
     if (entity instanceof class_1542) { class_1542 itemEntity = (class_1542)entity;
       return (this.targetItems.isState() && itemEntity.method_5805()); }
     
     if (entity instanceof class_1309) { class_1309 livingEntity = (class_1309)entity; if (livingEntity.method_5805()) {
 
         
         if (isAnimalEntity(entity)) {
           return this.targetAnimals.isState();
         }
         if (isMobEntity(entity)) {
           return this.targetMobs.isState();
         }
         return false;
       }  }
     
     return false; } private boolean shouldProcess2DPlayer(class_1657 player) { return shouldProcessPlayer(player, true); }
 
   
   private boolean shouldProcessLiving2D(class_1309 entity) {
     return shouldProcess3DEntity((class_1297)entity);
   }
   
   private boolean shouldProcessItem2D(class_1542 itemEntity) {
     return (this.targetItems.isState() && itemEntity.method_5805());
   }
   
   private boolean shouldProcessPlayer(class_1657 player, boolean skipInvisible) {
     if (!this.targetPlayers.isState()) {
       return false;
     }
     if (player == null || !player.method_5805()) {
       return false;
     }
     if (player == mc.field_1724 && isInFirstPerson()) {
       return false;
     }
     if (skipInvisible && player.method_5767() && !canRenderInvisiblePlayer(player)) {
       return false;
     }
     return true;
   }
   
   private boolean isTargetEnabled(int index) {
     return (this.targets.getSettings().size() > index && ((BooleanSetting)this.targets.getSettings().get(index)).isState());
   }
   
   private boolean isAnimalEntity(class_1297 entity) {
     return (entity instanceof net.minecraft.class_1429 || entity instanceof net.minecraft.class_9866 || entity instanceof net.minecraft.class_1421);
   }
 
 
   
   private boolean isMobEntity(class_1297 entity) {
     return (entity instanceof net.minecraft.class_1308 && !isAnimalEntity(entity) && !(entity instanceof class_1657));
   }
   
   private boolean canRenderInvisiblePlayer(class_1657 player) {
     SeeInvisibles seeInvisibles = ModuleClass.seeInvisibles;
     return (seeInvisibles != null && seeInvisibles.shouldRenderInvisible(player));
   }
   
   private boolean isOutsideRenderDistance(class_1297 entity) {
     int viewDistanceChunks = ((Integer)mc.field_1690.method_42503().method_41753()).intValue();
     double maxDistance = Math.max(48.0D, viewDistanceChunks * 16.0D + 16.0D);
     return (entity.method_5707(this.lastCameraPos) > maxDistance * maxDistance);
   }
   private static final class ScreenRect extends Record {
     private final float minX; private final float minY; private final float maxX; private final float maxY;
     private ScreenRect(float minX, float minY, float maxX, float maxY) { this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/EntityESP$ScreenRect;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #943	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/EntityESP$ScreenRect; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/EntityESP$ScreenRect;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #943	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/EntityESP$ScreenRect; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/EntityESP$ScreenRect;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #943	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/render/EntityESP$ScreenRect;
       //   0	8	1	o	Ljava/lang/Object; } public float minX() { return this.minX; } public float minY() { return this.minY; } public float maxX() { return this.maxX; } public float maxY() { return this.maxY; }
      float centerX() {
       return (this.minX + this.maxX) * 0.5F;
     }
     
     float centerY() {
       return (this.minY + this.maxY) * 0.5F;
     }
   }
   private static final class DonateSegment extends Record { private final String text; private final int color;
     private DonateSegment(String text, int color) { this.text = text; this.color = color; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/render/EntityESP$DonateSegment;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #953	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/EntityESP$DonateSegment; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/render/EntityESP$DonateSegment;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #953	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/render/EntityESP$DonateSegment; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/render/EntityESP$DonateSegment;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #953	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/render/EntityESP$DonateSegment;
       //   0	8	1	o	Ljava/lang/Object; } public String text() { return this.text; } public int color() { return this.color; }
      }
   private static class DonateCache { private List<EntityESP.DonateSegment> segments; private long nextUpdateAt;
     private DonateCache() {
       this.segments = Collections.emptyList();
     } }
 
 
 
 
 
 
 
 
 
 
   
   private static class ProjectedPoint
   {
     private float x;
 
 
 
 
 
 
 
 
     
     private float y;
 
 
 
 
 
 
 
 
     
     private float z;
   }
 
 
 
 
 
 
 
 
 
   
   private void render3DBox(class_4587 matrices, class_1297 entity, float tickDelta) {
     // Byte code:
     //   0: getstatic shame/astra/client/modules/impl/render/EntityESP.mc : Lnet/minecraft/class_310;
     //   3: getfield field_1773 : Lnet/minecraft/class_757;
     //   6: invokevirtual method_19418 : ()Lnet/minecraft/class_4184;
     //   9: invokevirtual method_19326 : ()Lnet/minecraft/class_243;
     //   12: astore #4
     //   14: fload_3
     //   15: f2d
     //   16: aload_2
     //   17: getfield field_6038 : D
     //   20: aload_2
     //   21: invokevirtual method_23317 : ()D
     //   24: invokestatic method_16436 : (DDD)D
     //   27: aload #4
     //   29: getfield field_1352 : D
     //   32: dsub
     //   33: dstore #5
     //   35: fload_3
     //   36: f2d
     //   37: aload_2
     //   38: getfield field_5971 : D
     //   41: aload_2
     //   42: invokevirtual method_23318 : ()D
     //   45: invokestatic method_16436 : (DDD)D
     //   48: aload #4
     //   50: getfield field_1351 : D
     //   53: dsub
     //   54: dstore #7
     //   56: fload_3
     //   57: f2d
     //   58: aload_2
     //   59: getfield field_5989 : D
     //   62: aload_2
     //   63: invokevirtual method_23321 : ()D
     //   66: invokestatic method_16436 : (DDD)D
     //   69: aload #4
     //   71: getfield field_1350 : D
     //   74: dsub
     //   75: dstore #9
     //   77: aload_2
     //   78: invokevirtual method_5829 : ()Lnet/minecraft/class_238;
     //   81: aload_2
     //   82: invokevirtual method_23317 : ()D
     //   85: dneg
     //   86: aload_2
     //   87: invokevirtual method_23318 : ()D
     //   90: dneg
     //   91: aload_2
     //   92: invokevirtual method_23321 : ()D
     //   95: dneg
     //   96: invokevirtual method_989 : (DDD)Lnet/minecraft/class_238;
     //   99: astore #11
     //   101: aload_1
     //   102: invokevirtual method_22903 : ()V
     //   105: aload_1
     //   106: dload #5
     //   108: dload #7
     //   110: dload #9
     //   112: invokevirtual method_22904 : (DDD)V
     //   115: aload_2
     //   116: instanceof net/minecraft/class_1657
     //   119: ifeq -> 163
     //   122: aload_2
     //   123: checkcast net/minecraft/class_1657
     //   126: astore #13
     //   128: getstatic shame/astra/astra.INSTANCE : Lshame/astra/astra;
     //   131: getfield friendStorage : Lshame/astra/api/storages/implement/FriendStorage;
     //   134: ifnull -> 163
     //   137: getstatic shame/astra/astra.INSTANCE : Lshame/astra/astra;
     //   140: getfield friendStorage : Lshame/astra/api/storages/implement/FriendStorage;
     //   143: aload #13
     //   145: invokevirtual method_5477 : ()Lnet/minecraft/class_2561;
     //   148: invokeinterface getString : ()Ljava/lang/String;
     //   153: invokevirtual isFriend : (Ljava/lang/String;)Z
     //   156: ifeq -> 163
     //   159: iconst_1
     //   160: goto -> 164
     //   163: iconst_0
     //   164: istore #12
     //   166: iload #12
     //   168: ifeq -> 189
     //   171: bipush #84
     //   173: sipush #255
     //   176: bipush #84
     //   178: sipush #255
     //   181: invokestatic rgba : (IIII)I
     //   184: istore #13
     //   186: goto -> 195
     //   189: aload_0
     //   190: invokevirtual getStableThemeColor : ()I
     //   193: istore #13
     //   195: aload_0
     //   196: aload_2
     //   197: iload #13
     //   199: invokevirtual applyEntityHurtTint : (Lnet/minecraft/class_1297;I)I
     //   202: istore #13
     //   204: iload #13
     //   206: invokestatic redf : (I)F
     //   209: fstore #14
     //   211: iload #13
     //   213: invokestatic greenf : (I)F
     //   216: fstore #15
     //   218: iload #13
     //   220: invokestatic bluef : (I)F
     //   223: fstore #16
     //   225: invokestatic enableBlend : ()V
     //   228: invokestatic defaultBlendFunc : ()V
     //   231: invokestatic disableCull : ()V
     //   234: invokestatic enableDepthTest : ()V
     //   237: iconst_0
     //   238: invokestatic depthMask : (Z)V
     //   241: getstatic net/minecraft/class_10142.field_53876 : Lnet/minecraft/class_10156;
     //   244: invokestatic setShader : (Lnet/minecraft/class_10156;)Lnet/minecraft/class_5944;
     //   247: pop
     //   248: ldc 1.5
     //   250: invokestatic lineWidth : (F)V
     //   253: aload_1
     //   254: invokevirtual method_23760 : ()Lnet/minecraft/class_4587$class_4665;
     //   257: invokevirtual method_23761 : ()Lorg/joml/Matrix4f;
     //   260: astore #17
     //   262: invokestatic method_1348 : ()Lnet/minecraft/class_289;
     //   265: astore #18
     //   267: aload_0
     //   268: getfield boxFilled : Lshame/astra/client/modules/settings/implement/BooleanSetting;
     //   271: invokevirtual isState : ()Z
     //   274: ifeq -> 295
     //   277: aload_0
     //   278: aload #18
     //   280: aload #17
     //   282: aload #11
     //   284: fload #14
     //   286: fload #15
     //   288: fload #16
     //   290: ldc 0.23
     //   292: invokevirtual drawFilledBox : (Lnet/minecraft/class_289;Lorg/joml/Matrix4f;Lnet/minecraft/class_238;FFFF)V
     //   295: aload_0
     //   296: aload #18
     //   298: aload #17
     //   300: aload #11
     //   302: fload #14
     //   304: fload #15
     //   306: fload #16
     //   308: fconst_1
     //   309: invokevirtual drawBoxOutline : (Lnet/minecraft/class_289;Lorg/joml/Matrix4f;Lnet/minecraft/class_238;FFFF)V
     //   312: invokestatic enableCull : ()V
     //   315: invokestatic enableDepthTest : ()V
     //   318: iconst_1
     //   319: invokestatic depthMask : (Z)V
     //   322: invokestatic disableBlend : ()V
     //   325: aload_1
     //   326: invokevirtual method_22909 : ()V
     //   329: return
     // Line number table:
     //   Java source line number -> byte code offset
     //   #968	-> 0
     //   #969	-> 14
     //   #970	-> 35
     //   #971	-> 56
     //   #973	-> 77
     //   #975	-> 101
     //   #976	-> 105
     //   #980	-> 115
     //   #978	-> 122
     //   #980	-> 145
     //   #982	-> 166
     //   #983	-> 171
     //   #985	-> 189
     //   #987	-> 195
     //   #988	-> 204
     //   #989	-> 211
     //   #990	-> 218
     //   #992	-> 225
     //   #993	-> 228
     //   #994	-> 231
     //   #995	-> 234
     //   #996	-> 237
     //   #997	-> 241
     //   #998	-> 248
     //   #1000	-> 253
     //   #1001	-> 262
     //   #1003	-> 267
     //   #1004	-> 277
     //   #1007	-> 295
     //   #1009	-> 312
     //   #1010	-> 315
     //   #1011	-> 318
     //   #1012	-> 322
     //   #1013	-> 325
     //   #1014	-> 329
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   128	35	13	player	Lnet/minecraft/class_1657;
     //   186	3	13	boxColor	I
     //   0	330	0	this	Lshame/astra/client/modules/impl/render/EntityESP;
     //   0	330	1	matrices	Lnet/minecraft/class_4587;
     //   0	330	2	entity	Lnet/minecraft/class_1297;
     //   0	330	3	tickDelta	F
     //   14	316	4	camera	Lnet/minecraft/class_243;
     //   35	295	5	x	D
     //   56	274	7	y	D
     //   77	253	9	z	D
     //   101	229	11	box	Lnet/minecraft/class_238;
     //   166	164	12	isFriend	Z
     //   195	135	13	boxColor	I
     //   211	119	14	r	F
     //   218	112	15	g	F
     //   225	105	16	b	F
     //   262	68	17	matrix	Lorg/joml/Matrix4f;
     //   267	63	18	tessellator	Lnet/minecraft/class_289;
   }
 
 
 
 
 
 
 
 
   
   private int applyEntityHurtTint(class_1297 entity, int baseColor) {
     if (entity instanceof class_1309) { class_1309 livingEntity = (class_1309)entity; if (this.hurtTint.isState()) {
 
 
 
         
         float target = class_3532.method_15363(livingEntity.field_6235 / 10.0F, 0.0F, 1.0F);
         float current = ((Float)this.entityHurtTintProgress.getOrDefault(Integer.valueOf(entity.method_5628()), Float.valueOf(0.0F))).floatValue();
         float speed = (target > current) ? 0.38F : 0.16F;
         current += (target - current) * speed;
         
         if (current <= 0.003F && target <= 0.0F) {
           this.entityHurtTintProgress.remove(Integer.valueOf(entity.method_5628()));
           return baseColor;
         } 
         
         this.entityHurtTintProgress.put(Integer.valueOf(entity.method_5628()), Float.valueOf(current));
         int hitColor = ColorUtils.rgba(255, 70, 70, 255);
         return ColorUtils.interpolateColor(baseColor, hitColor, current);
       }  }
     
     this.entityHurtTintProgress.remove(Integer.valueOf(entity.method_5628()));
     return baseColor; } private void drawPlayerMaskBox(class_4587 matrices, class_1297 entity, float tickDelta) { class_243 camera = mc.field_1773.method_19418().method_19326();
     double x = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317()) - camera.field_1352;
     double y = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318()) - camera.field_1351;
     double z = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321()) - camera.field_1350;
     class_238 box = entity.method_5829().method_989(-entity.method_23317(), -entity.method_23318(), -entity.method_23321());
     
     matrices.method_22903();
     matrices.method_22904(x, y, z);
     drawMaskBox(class_289.method_1348(), matrices.method_23760().method_23761(), box);
     matrices.method_22909(); }
 
   
   private void drawMaskBox(class_289 tessellator, Matrix4f matrix, class_238 box) {
     class_287 b = tessellator.method_60827(class_293.class_5596.field_27382, class_290.field_1576);
     float minX = (float)box.field_1323;
     float minY = (float)box.field_1322;
     float minZ = (float)box.field_1321;
     float maxX = (float)box.field_1320;
     float maxY = (float)box.field_1325;
     float maxZ = (float)box.field_1324;
     int white = -1;
     
     b.method_22918(matrix, minX, minY, minZ).method_39415(white);
     b.method_22918(matrix, maxX, minY, minZ).method_39415(white);
     b.method_22918(matrix, maxX, minY, maxZ).method_39415(white);
     b.method_22918(matrix, minX, minY, maxZ).method_39415(white);
     
     b.method_22918(matrix, minX, maxY, minZ).method_39415(white);
     b.method_22918(matrix, minX, maxY, maxZ).method_39415(white);
     b.method_22918(matrix, maxX, maxY, maxZ).method_39415(white);
     b.method_22918(matrix, maxX, maxY, minZ).method_39415(white);
     
     b.method_22918(matrix, minX, minY, minZ).method_39415(white);
     b.method_22918(matrix, minX, maxY, minZ).method_39415(white);
     b.method_22918(matrix, maxX, maxY, minZ).method_39415(white);
     b.method_22918(matrix, maxX, minY, minZ).method_39415(white);
     
     b.method_22918(matrix, minX, minY, maxZ).method_39415(white);
     b.method_22918(matrix, maxX, minY, maxZ).method_39415(white);
     b.method_22918(matrix, maxX, maxY, maxZ).method_39415(white);
     b.method_22918(matrix, minX, maxY, maxZ).method_39415(white);
     
     b.method_22918(matrix, minX, minY, minZ).method_39415(white);
     b.method_22918(matrix, minX, minY, maxZ).method_39415(white);
     b.method_22918(matrix, minX, maxY, maxZ).method_39415(white);
     b.method_22918(matrix, minX, maxY, minZ).method_39415(white);
     
     b.method_22918(matrix, maxX, minY, minZ).method_39415(white);
     b.method_22918(matrix, maxX, maxY, minZ).method_39415(white);
     b.method_22918(matrix, maxX, maxY, maxZ).method_39415(white);
     b.method_22918(matrix, maxX, minY, maxZ).method_39415(white);
     
     class_286.method_43433(b.method_60800());
   }
   
   private void renderShaderBoxes() {
     if (!this.hasShaderMask || this.maskBuffer == null)
       return;  boolean lineMode = isThreadMode();
     
     class_5944 shader = mc.method_62887().method_62947(ShaderUtils.blockOverlay);
     if (shader == null)
       return; 
     int color1 = getStableThemeColor();
     int color2 = isRainbowTheme() ? ColorUtils.getThemeColor(180) : color1;
     
     mc.method_1522().method_1235(false);
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableDepthTest();
     RenderSystem.setShader(ShaderUtils.blockOverlay);
     RenderSystem.setShaderTexture(0, this.maskBuffer.method_30277());
     setUniform(shader, "texelSize", 1.0F / Math.max(1, mc.method_22683().method_4489()), 1.0F / Math.max(1, mc.method_22683().method_4506()));
     setUniform(shader, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
     setUniform(shader, "color2", ColorUtils.redf(color2), ColorUtils.greenf(color2), ColorUtils.bluef(color2));
     setUniform(shader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
     setUniform(shader, "speed", this.waveSpeed.get());
     setUniform(shader, "scale", this.waveScale.get());
     setUniform(shader, "outline", this.outline.get());
     setUniform(shader, "glow", lineMode ? 0.0F : this.glow.get());
     setUniform(shader, "fill", lineMode ? 0.0F : this.fill.get());
     setUniform(shader, "alpha", lineMode ? 1.0F : this.alpha.get());
     setUniform(shader, "outlineOnly", lineMode ? 1.0F : 0.0F);
     drawFullscreenQuad();
     
     if (this.glow.get() > 0.001F) {
       int blurredMask = runKawaseBloom(Math.max(3, Math.min(8, 4 + Math.round(this.outline.get() * 0.7F))));
       class_5944 glowShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsGlow);
       if (glowShader != null) {
         RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE);
 
 
 
 
         
         RenderSystem.setShader(ShaderUtils.shaderHandsGlow);
         RenderSystem.setShaderTexture(0, blurredMask);
         RenderSystem.setShaderTexture(1, this.maskBuffer.method_30277());
         setUniform(glowShader, "color", ColorUtils.redf(color1), ColorUtils.greenf(color1), ColorUtils.bluef(color1));
         setUniform(glowShader, "color2", ColorUtils.redf(color2), ColorUtils.greenf(color2), ColorUtils.bluef(color2));
         setUniform(glowShader, "exposure", 1.0F + this.glow.get() * 1.8F);
         drawFullscreenQuad();
       } 
     } 
     
     RenderSystem.enableDepthTest();
     RenderSystem.disableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.setShaderTexture(1, 0);
     mc.method_1522().method_1235(true);
   }
   
   private void renderShaderBoxesWorldPass() {
     if (!isPostBoxMode())
       return; 
     Matrix4f savedProjection = new Matrix4f((Matrix4fc)RenderSystem.getProjectionMatrix());
     float width = Math.max(mc.method_22683().method_4486(), 1);
     float height = Math.max(mc.method_22683().method_4502(), 1);
     Matrix4f ortho = (new Matrix4f()).setOrtho(0.0F, width, height, 0.0F, -1000.0F, 1000.0F);
     RenderSystem.setProjectionMatrix(ortho, class_10366.field_54954);
     
     try {
       renderShaderBoxes();
     } finally {
       RenderSystem.setProjectionMatrix(savedProjection, class_10366.field_54954);
     } 
   }
   
   private int runKawaseBloom(int iterations) {
     ensureBloomBuffers(iterations);
     if (this.bloomBuffers.isEmpty()) return this.maskBuffer.method_30277();
     
     int currentTexture = this.maskBuffer.method_30277();
     class_5944 downShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsKawaseDown);
     class_5944 upShader = mc.method_62887().method_62947(ShaderUtils.shaderHandsKawaseUp);
     if (downShader == null || upShader == null) return currentTexture; 
     int i;
     for (i = 0; i < iterations; i++) {
       class_276 dst = this.bloomBuffers.get(i);
       dst.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
       dst.method_1230();
       dst.method_1235(true);
       
       RenderSystem.setShader(ShaderUtils.shaderHandsKawaseDown);
       RenderSystem.setShaderTexture(0, currentTexture);
       setHandsKawaseUniforms(downShader, dst.field_1482, dst.field_1481, 1.0F + i);
       drawFullscreenQuad();
       
       currentTexture = dst.method_30277();
     } 
     
     for (i = iterations - 1; i >= 1; i--) {
       class_276 dst = this.bloomBuffers.get(i - 1);
       dst.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
       dst.method_1230();
       dst.method_1235(true);
       
       RenderSystem.setShader(ShaderUtils.shaderHandsKawaseUp);
       RenderSystem.setShaderTexture(0, currentTexture);
       setHandsKawaseUniforms(upShader, dst.field_1482, dst.field_1481, 1.0F + i);
       setUniform(upShader, "color", 1.0F, 1.0F, 1.0F);
       drawFullscreenQuad();
       
       currentTexture = dst.method_30277();
     } 
     
     mc.method_1522().method_1235(true);
     return currentTexture;
   }
   
   private void ensureMaskBuffer() {
     int w = mc.method_22683().method_4489();
     int h = mc.method_22683().method_4506();
     if (this.maskBuffer == null || this.maskWidth != w || this.maskHeight != h) {
       if (this.maskBuffer != null) {
         this.maskBuffer.method_1238();
       }
       this.maskBuffer = (class_276)new class_6367(w, h, true);
       this.maskWidth = w;
       this.maskHeight = h;
       for (class_276 fb : this.bloomBuffers) {
         fb.method_1238();
       }
       this.bloomBuffers.clear();
     } 
   }
   
   private void ensureBloomBuffers(int iterations) {
     while (this.bloomBuffers.size() > iterations) {
       int last = this.bloomBuffers.size() - 1;
       ((class_276)this.bloomBuffers.get(last)).method_1238();
       this.bloomBuffers.remove(last);
     } 
     
     for (int i = 0; i < iterations; i++) {
       int w = Math.max(2, this.maskWidth >> i + 1);
       int h = Math.max(2, this.maskHeight >> i + 1);
       if (i >= this.bloomBuffers.size()) {
         this.bloomBuffers.add(new class_6367(w, h, false));
       } else {
         
         class_276 fb = this.bloomBuffers.get(i);
         if (fb.field_1482 != w || fb.field_1481 != h) {
           fb.method_1238();
           this.bloomBuffers.set(i, new class_6367(w, h, false));
         } 
       } 
     } 
   }
   private void copyMainDepthToMask() {
     if (this.maskBuffer == null)
       return;  int readFbo = GL11.glGetInteger(36010);
     int drawFbo = GL11.glGetInteger(36006);
     int w = mc.method_22683().method_4489();
     int h = mc.method_22683().method_4506();
     
     GL30.glBindFramebuffer(36008, (mc.method_1522()).field_1476);
     GL30.glBindFramebuffer(36009, this.maskBuffer.field_1476);
     GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, 256, 9728);
     GL30.glBindFramebuffer(36008, readFbo);
     GL30.glBindFramebuffer(36009, drawFbo);
   }
   
   private void setUniform(class_5944 shader, String name, float value) {
     class_284 uniform = shader.method_34582(name);
     if (uniform != null) uniform.method_1251(value); 
   }
   
   private void setUniform(class_5944 shader, String name, float x, float y) {
     class_284 uniform = shader.method_34582(name);
     if (uniform != null) uniform.method_1255(x, y); 
   }
   
   private void setUniform(class_5944 shader, String name, float x, float y, float z) {
     class_284 uniform = shader.method_34582(name);
     if (uniform != null) uniform.method_1249(x, y, z); 
   }
   
   private void setHandsKawaseUniforms(class_5944 shader, int texWidth, int texHeight, float offset) {
     setUniform(shader, "uSize", Math.max(1, texWidth), Math.max(1, texHeight));
     setUniform(shader, "uOffset", offset, offset);
     setUniform(shader, "uHalfPixel", 0.5F / Math.max(1, texWidth), 0.5F / Math.max(1, texHeight));
   }
   
   private void drawFullscreenQuad() {
     float width = Math.max(mc.method_22683().method_4486(), 1);
     float height = Math.max(mc.method_22683().method_4502(), 1);
     class_287 b = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1575);
     b.method_22912(0.0F, 0.0F, 0.0F).method_22913(0.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(0.0F, height, 0.0F).method_22913(0.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(width, height, 0.0F).method_22913(1.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     b.method_22912(width, 0.0F, 0.0F).method_22913(1.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     class_286.method_43433(b.method_60800());
   }
   
   private boolean isPostBoxMode() {
     return false;
   }
   
   private boolean isThreadMode() {
     return false;
   }
   
   private boolean isRainbowTheme() {
     if (astra.INSTANCE == null || astra.INSTANCE.themeStorage == null || astra.INSTANCE.themeStorage.getThemes() == null) {
       return false;
     }
     Theme theme = astra.INSTANCE.themeStorage.getThemes().getTheme();
     return (theme != null && "Rainbow".equals(theme.getName()));
   }
   
   private int getStableThemeColor() {
     if (astra.INSTANCE == null || astra.INSTANCE.themeStorage == null || astra.INSTANCE.themeStorage.getThemes() == null) {
       return ColorUtils.getThemeColor(0);
     }
     Theme theme = astra.INSTANCE.themeStorage.getThemes().getTheme();
     if (theme == null || theme.color == null || theme.color.length == 0) {
       return ColorUtils.getThemeColor(0);
     }
     return theme.color[0];
   }
   
   private void renderThreadWeb(class_4587 matrices, class_1297 entity, float tickDelta) {
     class_243 camera = mc.field_1773.method_19418().method_19326();
     double x = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317()) - camera.field_1352;
     double y = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318()) - camera.field_1351;
     double z = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321()) - camera.field_1350;
     class_238 box = entity.method_5829().method_989(-entity.method_23317(), -entity.method_23318(), -entity.method_23321());
     
     matrices.method_22903();
     matrices.method_22904(x, y, z);
     drawAnimatedWeb(matrices.method_23760().method_23761(), box, entity.method_5628());
     matrices.method_22909();
   }
   
   private void drawAnimatedWeb(Matrix4f matrix, class_238 box, long seedBase) {
     int strandsPerFace = 5;
     int samples = 18;
     float t = (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.lineSpeed.get();
     float lineWidth = 0.0025F;
     float bendBase = 0.06F + this.lineJitter.get() * 0.2F;
     int baseAlpha = Math.max(20, Math.min(255, (int)(this.alpha.get() * 210.0F)));
     int themeColor = getStableThemeColor();
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(class_10142.field_53876);
     drawFilledBoxInt(matrix, box, ColorUtils.setAlphaColor(themeColor, (int)(this.alpha.get() * this.fill.get() * 170.0F)));
     
     for (int face = 0; face < 6; face++) {
       int[] neighbors = faceNeighbors(face);
       for (int strand = 0; strand < strandsPerFace; strand++) {
         int key = face * 1000 + strand * 53;
         int adj = neighbors[strand % neighbors.length];
         double phase = t * (0.95D + rand01(seedBase, key + 1) * 0.55D) + strand * 0.83D + face * 1.11D;
         double edgeT = clamp01(0.5D + Math.sin(phase * 1.37D + rand01(seedBase, key + 2) * 6.2831853D) * 0.38D);
         
         class_243 pivot = edgePoint(box, face, adj, edgeT, 0.0015D);
         class_243 start = facePoint(box, face, 
             clamp01(0.5D + (rand01(seedBase, key + 3) - 0.5D) * 0.46D), 
             clamp01(0.5D + (rand01(seedBase, key + 4) - 0.5D) * 0.46D), 0.0015D);
         
         class_243 end = facePoint(box, adj, 
             clamp01(0.5D + (rand01(seedBase, key + 5) - 0.5D) * 0.46D), 
             clamp01(0.5D + (rand01(seedBase, key + 6) - 0.5D) * 0.46D), 0.0015D);
 
         
         class_243[] basisA = faceBasis(face);
         class_243[] basisB = faceBasis(adj);
         class_243 normalA = faceNormal(face);
         class_243 normalB = faceNormal(adj);
         
         double bendA = bendBase * (0.7D + rand01(seedBase, key + 7)) * Math.sin(phase * 1.9D + rand01(seedBase, key + 8) * 6.2831853D);
         
         double bendB = bendBase * (0.7D + rand01(seedBase, key + 9)) * Math.cos(phase * 1.7D + rand01(seedBase, key + 10) * 6.2831853D);
         
         class_243 dirA = pivot.method_1020(start);
         class_243 c1a = start.method_1019(dirA.method_1021(0.38D)).method_1019(basisA[0].method_1021(bendA)).method_1019(basisA[1].method_1021(-bendA * 0.55D));
         class_243 c2a = start.method_1019(dirA.method_1021(0.76D)).method_1019(basisA[0].method_1021(-bendA * 0.65D)).method_1019(basisA[1].method_1021(bendA * 0.4D));
         
         class_243 dirB = end.method_1020(pivot);
         class_243 c1b = pivot.method_1019(dirB.method_1021(0.24D)).method_1019(basisB[0].method_1021(bendB)).method_1019(basisB[1].method_1021(bendB * 0.45D));
         class_243 c2b = pivot.method_1019(dirB.method_1021(0.62D)).method_1019(basisB[0].method_1021(-bendB * 0.7D)).method_1019(basisB[1].method_1021(-bendB * 0.35D));
         
         int alphaLine = Math.max(18, Math.min(255, (int)(baseAlpha * (0.74D + 0.26D * Math.sin(phase * 2.6D)))));
         int color = ColorUtils.setAlphaColor(themeColor, alphaLine);
         drawBezierRibbon(matrix, start, c1a, c2a, pivot, normalA, samples, color, lineWidth);
         drawBezierRibbon(matrix, pivot, c1b, c2b, end, normalB, samples, color, lineWidth);
       } 
     } 
     
     RenderSystem.depthMask(true);
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
   }
   
   private class_243 cubicBezier(class_243 p0, class_243 p1, class_243 p2, class_243 p3, float t) {
     double it = 1.0D - t;
     double it2 = it * it;
     double t2 = (t * t);
     return p0.method_1021(it2 * it)
       .method_1019(p1.method_1021(3.0D * it2 * t))
       .method_1019(p2.method_1021(3.0D * it * t2))
       .method_1019(p3.method_1021(t2 * t));
   }
   
   private void drawBezierRibbon(Matrix4f matrix, class_243 p0, class_243 p1, class_243 p2, class_243 p3, class_243 faceNormal, int samples, int color, float halfWidth) {
     class_243[] points = new class_243[samples + 1];
     for (int s = 0; s <= samples; s++) {
       float u = s / samples;
       points[s] = cubicBezier(p0, p1, p2, p3, u);
     } 
     
     class_287 quads = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1576);
     for (int i = 0; i < samples; i++) {
       class_243 a = points[i];
       class_243 b = points[i + 1];
       class_243 dir = b.method_1020(a);
       if (dir.method_1027() >= 1.0E-6D) {
         
         class_243 perp = faceNormal.method_1036(dir).method_1029().method_1021(halfWidth);
         class_243 aL = a.method_1019(perp);
         class_243 aR = a.method_1020(perp);
         class_243 bL = b.method_1019(perp);
         class_243 bR = b.method_1020(perp);
         
         quads.method_22918(matrix, (float)aL.field_1352, (float)aL.field_1351, (float)aL.field_1350).method_39415(color);
         quads.method_22918(matrix, (float)aR.field_1352, (float)aR.field_1351, (float)aR.field_1350).method_39415(color);
         quads.method_22918(matrix, (float)bR.field_1352, (float)bR.field_1351, (float)bR.field_1350).method_39415(color);
         quads.method_22918(matrix, (float)bL.field_1352, (float)bL.field_1351, (float)bL.field_1350).method_39415(color);
       } 
     }  class_286.method_43433(quads.method_60800());
   }
   
   private int[] faceNeighbors(int face) {
     switch (face) { case 0: case 1:
         (new int[4])[0] = 2; (new int[4])[1] = 3; (new int[4])[2] = 4; (new int[4])[3] = 5;
       case 2: case 3: (new int[4])[0] = 0; (new int[4])[1] = 1; (new int[4])[2] = 4; (new int[4])[3] = 5; }
      return new int[] { 0, 1, 2, 3 };
   }
 
   
   private class_243[] faceBasis(int face) {
     switch (face) { case 0: case 1:
         (new class_243[2])[0] = new class_243(1.0D, 0.0D, 0.0D); (new class_243[2])[1] = new class_243(0.0D, 0.0D, 1.0D);
       case 2: case 3: (new class_243[2])[0] = new class_243(1.0D, 0.0D, 0.0D); (new class_243[2])[1] = new class_243(0.0D, 1.0D, 0.0D); }
      return new class_243[] { new class_243(0.0D, 0.0D, 1.0D), new class_243(0.0D, 1.0D, 0.0D) };
   }
 
   
   private class_243 faceNormal(int face) {
     switch (face) { case 0: case 1: case 2: case 3: case 4:  }  return 
 
 
 
 
       
       new class_243(1.0D, 0.0D, 0.0D);
   }
 
   
   private class_243 edgePoint(class_238 box, int faceA, int faceB, double t, double inset) {
     double x = Double.NaN;
     double y = Double.NaN;
     double z = Double.NaN;
     
     double[] fixedA = faceFixedCoords(box, faceA, inset);
     if (!Double.isNaN(fixedA[0])) x = fixedA[0]; 
     if (!Double.isNaN(fixedA[1])) y = fixedA[1]; 
     if (!Double.isNaN(fixedA[2])) z = fixedA[2];
     
     double[] fixedB = faceFixedCoords(box, faceB, inset);
     if (!Double.isNaN(fixedB[0])) x = fixedB[0]; 
     if (!Double.isNaN(fixedB[1])) y = fixedB[1]; 
     if (!Double.isNaN(fixedB[2])) z = fixedB[2];
     
     double tt = clamp01(t);
     if (Double.isNaN(x)) x = lerp(box.field_1323, box.field_1320, tt); 
     if (Double.isNaN(y)) y = lerp(box.field_1322, box.field_1325, tt); 
     if (Double.isNaN(z)) z = lerp(box.field_1321, box.field_1324, tt); 
     return new class_243(x, y, z);
   }
   
   private double[] faceFixedCoords(class_238 box, int face, double inset) {
     switch (face) { case 0:
         (new double[3])[0] = Double.NaN; (new double[3])[1] = box.field_1325 - inset; (new double[3])[2] = Double.NaN;
       case 1: (new double[3])[0] = Double.NaN; (new double[3])[1] = box.field_1322 + inset; (new double[3])[2] = Double.NaN;
       case 2: (new double[3])[0] = Double.NaN; (new double[3])[1] = Double.NaN; (new double[3])[2] = box.field_1321 + inset;
       case 3: (new double[3])[0] = Double.NaN; (new double[3])[1] = Double.NaN; (new double[3])[2] = box.field_1324 - inset;
       case 4: (new double[3])[0] = box.field_1323 + inset; (new double[3])[1] = Double.NaN; (new double[3])[2] = Double.NaN; }
      return new double[] { box.field_1320 - inset, Double.NaN, Double.NaN };
   }
 
   
   private class_243 facePoint(class_238 box, int face, double u, double v, double inset) {
     u = clamp01(u);
     v = clamp01(v);
     switch (face) { case 0: case 1: case 2: case 3: case 4:  }  return 
 
 
 
 
       
       new class_243(box.field_1320 - inset, lerp(box.field_1322, box.field_1325, v), lerp(box.field_1321, box.field_1324, u));
   }
 
   
   private double rand01(long seed, int salt) {
     long x = seed + -7046029254386353131L * (salt + 1L);
     x ^= x >>> 30L;
     x *= -4658895280553007687L;
     x ^= x >>> 27L;
     x *= -7723592293110705685L;
     x ^= x >>> 31L;
     return (x & 0xFFFFFFL) / 1.6777216E7D;
   }
   
   private double lerp(double a, double b, double t) {
     return a + (b - a) * t;
   }
   
   private double clamp01(double v) {
     return Math.max(0.0D, Math.min(1.0D, v));
   }
   
   private void drawFilledBoxInt(Matrix4f matrix, class_238 box, int color) {
     class_287 b = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1576);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1323, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1321).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1322, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324).method_39415(color);
     b.method_22918(matrix, (float)box.field_1320, (float)box.field_1325, (float)box.field_1321).method_39415(color);
     
     class_286.method_43433(b.method_60800());
   }
   
   private void drawFilledBox(class_289 tessellator, Matrix4f matrix, class_238 box, float r, float g, float b, float a) {
     RenderSystem.setShader(class_10142.field_53876);
     class_287 buffer = tessellator.method_60827(class_293.class_5596.field_27382, class_290.field_1576);
     
     float minX = (float)box.field_1323;
     float minY = (float)box.field_1322;
     float minZ = (float)box.field_1321;
     float maxX = (float)box.field_1320;
     float maxY = (float)box.field_1325;
     float maxZ = (float)box.field_1324;
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     
     class_286.method_43433(buffer.method_60800());
   }
   
   private void drawBoxOutline(class_289 tessellator, Matrix4f matrix, class_238 box, float r, float g, float b, float a) {
     RenderSystem.setShader(class_10142.field_53876);
     RenderSystem.lineWidth(1.5F);
     class_287 buffer = tessellator.method_60827(class_293.class_5596.field_29344, class_290.field_1576);
     
     float minX = (float)box.field_1323;
     float minY = (float)box.field_1322;
     float minZ = (float)box.field_1321;
     float maxX = (float)box.field_1320;
     float maxY = (float)box.field_1325;
     float maxZ = (float)box.field_1324;
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     
     class_286.method_43433(buffer.method_60800());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\EntityESP.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */