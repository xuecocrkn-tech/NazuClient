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
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.ProjectionType;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.ItemEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Item;
 import net.minecraft.ItemStack;
 import net.minecraft.Rarity;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.Text;
 import net.minecraft.Style;
 import net.minecraft.Team;
 import net.minecraft.Framebuffer;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.ShaderProgram;
 import net.minecraft.SimpleFramebuffer;
 import net.minecraft.PlayerListEntry;
 import net.minecraft.Registries;
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
   private Vec3d lastCameraPos = Vec3d.field_1353;
   private float lastTickDelta;
   private int lastScaledWidth;
   private int lastScaledHeight;
   private boolean hasProjection;
   private Framebuffer maskBuffer;
   private final List<Framebuffer> bloomBuffers = new ArrayList<>();
   private final Map<UUID, DonateCache> donateCache = new HashMap<>();
   private final Map<Integer, Float> entityHurtTintProgress = new HashMap<>();
   private long nextDonateCacheCleanupAt;
   private int maskWidth = -1;
   private int maskHeight = -1;
   private boolean hasShaderMask;
   private final Vector3f projectionScratch = new Vector3f();
   private final Vector4f clipScratch = new Vector4f();
   private final ProjectedPoint projectedPoint = new ProjectedPoint();
   private final ItemStack[] armorStacksScratch = new ItemStack[6];
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
     for (Framebuffer fb : this.bloomBuffers) {
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
       return;  MatrixStack matrices = event.getMatrices();
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
         RenderSystem.setShader(ShaderProgramKeys.field_53876);
       } 
     } 
     
     for (Entity entity : mc.field_1687.method_18112()) {
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
       for (Entity entity : mc.field_1687.method_18112()) {
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
     
     for (Entity entity : mc.field_1687.method_18112()) {
       if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity;
         if (!shouldProcess2DPlayer(player))
           continue; 
         Box interpolatedBox = getInterpolatedBox((Entity)player, this.lastTickDelta);
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
       
       if (entity instanceof ItemEntity) { ItemEntity itemEntity = (ItemEntity)entity;
         if (!shouldProcessItem2D(itemEntity) || renderedItemTags >= 48) {
           continue;
         }
         
         if (projectEntityAnchor((Entity)itemEntity, itemEntity.method_17682() + 0.25D, this.projectedPoint)) {
           drawDroppedItemTag(event, itemEntity, this.projectedPoint.x, this.projectedPoint.y, font);
           renderedItemTags++;
         } 
         
         continue; }
       
       if (entity instanceof LivingEntity) { LivingEntity livingEntity = (LivingEntity)entity; if (!shouldProcessLiving2D(livingEntity)) {
           continue;
         }
         
         Box interpolatedBox = getInterpolatedBox((Entity)livingEntity, this.lastTickDelta);
         ScreenRect rect = projectBox(interpolatedBox);
         if (rect == null)
           continue; 
         drawLivingTag(event, livingEntity, rect, font); }
     
     } 
   }
   private void drawTag(EventRender.Default event, PlayerEntity player, ScreenRect rect, Font font) {
     MatrixStack matrices = event.getContext().method_51448();
     List<DonateSegment> donateSegments = getDonateSegmentsFromTab(player);
     String nameText = getProtectedName(player.method_5820());
     float hp = ScoreboardHP.getHealthWithAbsorption((LivingEntity)player);
     String leftBracket = "";
     String hpText = "" + Math.round(hp) + " hp";
     String rightBracket = "";
     
     boolean isFriend = (NazunaClient.INSTANCE.friendStorage != null && NazunaClient.INSTANCE.friendStorage.isFriend(player.method_5477().getString()));
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
   
   private void drawArmor(EventRender.Default event, PlayerEntity player, ScreenRect rect, boolean tagsEnabled) {
     MatrixStack matrices = event.getContext().method_51448();
     int count = 0;
     ItemStack offHand = player.method_6079();
     if (!offHand.method_7960()) {
       this.armorStacksScratch[count] = offHand;
       this.armorHandScratch[count++] = true;
     } 
     for (ItemStack stack : player.method_5661()) {
       if (!stack.method_7960()) {
         this.armorStacksScratch[count] = stack;
         this.armorHandScratch[count++] = false;
       } 
     } 
     ItemStack mainHand = player.method_6047();
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
       ItemStack stack = this.armorStacksScratch[stackIndex];
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
       this.armorStacksScratch[i] = ItemStack.field_8037;
       this.armorHandScratch[i] = false;
     } 
   }
   
   private void drawLivingTag(EventRender.Default event, LivingEntity entity, ScreenRect rect, Font font) {
     MatrixStack matrices = event.getContext().method_51448();
     PlayerEntity player = (PlayerEntity)entity;
     
     String nameText = (entity instanceof PlayerEntity) ? getProtectedName(player.method_5476().getString()) : entity.method_5476().getString();
     String hpText = "" + Math.round(ScoreboardHP.getHealthWithAbsorption(entity)) + " hp";
     float totalWidth = font.getStringWidth(nameText) + font.getStringWidth(" ") + font.getStringWidth(hpText);
     float boxHeight = 14.0F;
     float x = rect.centerX() - totalWidth * 0.5F;
     float y = getTagTopY(rect, boxHeight);
     
     drawDefaultTagPanel(matrices, x - 1.0F, y - 0.5F, totalWidth + 2.0F, boxHeight - 4.0F);
     font.drawString(matrices, nameText, x, y + 3.0F, -1);
     font.drawString(matrices, hpText, x + font.getStringWidth(nameText) + font.getStringWidth(" "), y + 3.0F, -43691);
   }
   
   private void drawDroppedItemTag(EventRender.Default event, ItemEntity itemEntity, float anchorX, float anchorY, Font font) {
     MatrixStack matrices = event.getContext().method_51448();
     ItemStack stack = itemEntity.method_6983();
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
   
   private int getMinecraftItemNameColor(ItemStack stack) {
     Text name = stack.method_7964();
     if (name != null) {
       int[] discoveredColor = { 0 };
       boolean[] found = { false };
       name.method_27658((style, string) -> { if (!found[0] && style != null && style.method_10973() != null) { discoveredColor[0] = 0xFF000000 | style.method_10973().method_27716(); found[0] = true; }  return found[0] ? Optional.<String>of(string) : Optional.empty(); }, Style.field_24360);
 
 
 
 
 
       
       if (found[0]) return discoveredColor[0];
     
     } 
     switch (stack.method_7932()) { default: throw new MatchException(null, null);case field_8907: case field_8903: case field_8904: case field_8906: break; }  return 
 
 
       
       -1;
   }
 
   
   private int getDroppedItemTextColor(ItemStack stack) {
     return getMinecraftItemNameColor(stack);
   }
   
   private boolean isNetheriteItem(Item item) {
     return Registries.field_41178.method_10221(item).method_12832().contains("netherite");
   }
   
   private void drawDefaultTagPanel(MatrixStack matrices, float x, float y, float width, float height) {
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
   
   private String[] getNameVariants(PlayerEntity player) {
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
   
   private List<DonateSegment> getDonateSegmentsFromTab(PlayerEntity player) {
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
     
     PlayerListEntry entry = mc.method_1562().method_2871(player.method_5667());
     if (entry == null) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       return cache.segments;
     } 
     
     Text displayName = entry.method_2971();
     if (displayName == null) displayName = player.method_5476(); 
     if (displayName == null) {
       cache.segments = Collections.emptyList();
       cache.nextUpdateAt = now + 1000L;
       return cache.segments;
     } 
     
     String[] nameVariants = getNameVariants(player);
     boolean[] foundName = { false };
     
     displayName.method_27658((style, string) -> { if (foundName[0] || string == null || string.isEmpty()) return Optional.empty();  String part = string.replace('\n', ' ').replace('\r', ' '); int nameIndex = findAnyNameIndex(part, nameVariants); String donatePart = (nameIndex >= 0) ? part.substring(0, nameIndex) : part; if (!donatePart.isEmpty()) { int baseColor = (style.method_10973() != null) ? style.method_10973().method_27716() : 16777215; appendColoredSegments(segments, donatePart, baseColor); }  if (nameIndex >= 0) foundName[0] = true;  return Optional.empty(); }, Style.field_24360);
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     if (!foundName[0]) {
       segments.clear();
       Team team = player.method_5781();
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
   
   private void appendTextSegments(List<DonateSegment> out, Text text) {
     text.method_27658((style, string) -> { if (string == null || string.isEmpty()) return Optional.empty();  int baseColor = (style.method_10973() != null) ? style.method_10973().method_27716() : 16777215; appendColoredSegments(out, string.replace('\n', ' ').replace('\r', ' '), baseColor); return Optional.empty(); }, Style.field_24360);
   }
 
 
 
 
 
 
 
 
   
   private List<DonateSegment> getStyledTextSegments(Text text, int fallbackColor) {
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
   
   private Box getInterpolatedBox(Entity entity, float tickDelta) {
     double x = MathHelper.method_16436(tickDelta, entity.field_6038, entity.method_23317());
     double y = MathHelper.method_16436(tickDelta, entity.field_5971, entity.method_23318());
     double z = MathHelper.method_16436(tickDelta, entity.field_5989, entity.method_23321());
     
     double ox = x - entity.method_23317();
     double oy = y - entity.method_23318();
     double oz = z - entity.method_23321();
     
     return entity.method_5829().method_989(ox, oy, oz).method_1014(0.05D);
   }
   
   private ScreenRect projectBox(Box box) {
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
   
   private boolean projectEntityAnchor(Entity entity, double yOffset, ProjectedPoint out) {
     double x = MathHelper.method_16436(this.lastTickDelta, entity.field_6038, entity.method_23317());
     double y = MathHelper.method_16436(this.lastTickDelta, entity.field_5971, entity.method_23318()) + yOffset;
     double z = MathHelper.method_16436(this.lastTickDelta, entity.field_5989, entity.method_23321());
     return projectToScreen(x, y, z, out);
   }
   
   private boolean isInFirstPerson() {
     return (mc != null && mc.field_1773 != null && !mc.field_1773.method_19418().method_19333());
   }
   
   private boolean shouldProcess3DEntity(Entity entity) {
     if (entity == null || entity.method_31481() || entity instanceof net.minecraft.ArmorStandEntity) {
       return false;
     }
     if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity;
       return shouldProcessPlayer(player, false); }
     
     if (entity instanceof ItemEntity) { ItemEntity itemEntity = (ItemEntity)entity;
       return (this.targetItems.isState() && itemEntity.method_5805()); }
     
     if (entity instanceof LivingEntity) { LivingEntity livingEntity = (LivingEntity)entity; if (livingEntity.method_5805()) {
 
         
         if (isAnimalEntity(entity)) {
           return this.targetAnimals.isState();
         }
         if (isMobEntity(entity)) {
           return this.targetMobs.isState();
         }
         return false;
       }  }
     
     return false; } private boolean shouldProcess2DPlayer(PlayerEntity player) { return shouldProcessPlayer(player, true); }
 
   
   private boolean shouldProcessLiving2D(LivingEntity entity) {
     return shouldProcess3DEntity((Entity)entity);
   }
   
   private boolean shouldProcessItem2D(ItemEntity itemEntity) {
     return (this.targetItems.isState() && itemEntity.method_5805());
   }
   
   private boolean shouldProcessPlayer(PlayerEntity player, boolean skipInvisible) {
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
   
   private boolean isAnimalEntity(Entity entity) {
     return (entity instanceof net.minecraft.AnimalEntity || entity instanceof net.minecraft.WaterAnimalEntity || entity instanceof net.minecraft.AmbientEntity);
   }
 
 
   
   private boolean isMobEntity(Entity entity) {
     return (entity instanceof net.minecraft.MobEntity && !isAnimalEntity(entity) && !(entity instanceof PlayerEntity));
   }
   
   private boolean canRenderInvisiblePlayer(PlayerEntity player) {
     SeeInvisibles seeInvisibles = ModuleClass.seeInvisibles;
     return (seeInvisibles != null && seeInvisibles.shouldRenderInvisible(player));
   }
   
   private boolean isOutsideRenderDistance(Entity entity) {
     int viewDistanceChunks = ((Integer)mc.field_1690.method_42503().method_41753()).intValue();
     double maxDistance = Math.max(48.0D, viewDistanceChunks * 16.0D + 16.0D);
     return (entity.method_5707(this.lastCameraPos) > maxDistance * maxDistance);
   }
   private static final class ScreenRect {
     private final float minX; private final float minY; private final float maxX; private final float maxY;
     float centerY() {
       return (this.minY + this.maxY) * 0.5F;
     }
   }
 
 
 
 
 
 
 
 
 
   
   private static class ProjectedPoint
   {
     private float x;
 
 
 
 
 
 
 
 
     
     private float y;
 
 
 
 
 
 
 
 
     
     private float z;
   }
 
 
 
 
 
 
 
 
 
   
   private void render3DBox(MatrixStack matrices, Entity entity, float tickDelta) {
}
}
