package shame.nazuna.api.utils.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.Color;
 import java.util.UUID;
 import java.util.concurrent.ConcurrentHashMap;
 import net.minecraft.Sprite;
 import net.minecraft.DefaultSkinHelper;
 import net.minecraft.PlayerEntity;
 import net.minecraft.ItemStack;
 import net.minecraft.GlUniform;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import net.minecraft.DrawContext;
 import net.minecraft.MatrixStack;
 import net.minecraft.ShaderProgram;
 import net.minecraft.PlayerListEntry;
 import org.joml.Matrix4f;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.blur.BlurProgram;
 import shame.nazuna.api.utils.render.glow.GlowCallback;
 import shame.nazuna.api.utils.render.glow.GlowProgram;
 import shame.nazuna.api.utils.scissor.ScissorUtils;
 
 public final class RenderUtils implements QClient {
   private RenderUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   private static final ConcurrentHashMap<String, Identifier> skinCache = new ConcurrentHashMap<>();
   private static final UUID DEFAULT_SKIN_UUID = new UUID(0L, 0L);
   
   public static void drawHudItem(DrawContext context, ItemStack stack, float x, float y, float scale, float z) {
     if (context == null || stack == null || stack.method_7960()) {
       return;
     }
     
     MatrixStack matrices = context.method_51448();
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     
     matrices.method_22903();
     matrices.method_46416(x, y, z);
     matrices.method_22905(scale, scale, 1.0F);
     context.method_51427(stack, 0, 0);
     matrices.method_22909();
     
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(true);
   }
 
 
 
 
   
   public static void drawGradient6Rect(MatrixStack matrices, float x, float y, float width, float height, float radius, int leftTopColor, int leftBottomColor, int centerTopColor, int centerBottomColor, int rightTopColor, int rightBottomColor) {
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.gradient6Rect);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform leftTopColorUniform = shader.method_34582("LeftTopColor");
     GlUniform leftBottomColorUniform = shader.method_34582("LeftBottomColor");
     GlUniform centerTopColorUniform = shader.method_34582("CenterTopColor");
     GlUniform centerBottomColorUniform = shader.method_34582("CenterBottomColor");
     GlUniform rightTopColorUniform = shader.method_34582("RightTopColor");
     GlUniform rightBottomColorUniform = shader.method_34582("RightBottomColor");
     
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(radius, radius, radius, radius); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(1.0F);
     
     if (leftTopColorUniform != null) {
       int a = leftTopColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       leftTopColorUniform.method_35657((leftTopColor >> 16 & 0xFF) / 255.0F, (leftTopColor >> 8 & 0xFF) / 255.0F, (leftTopColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (leftBottomColorUniform != null) {
       int a = leftBottomColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       leftBottomColorUniform.method_35657((leftBottomColor >> 16 & 0xFF) / 255.0F, (leftBottomColor >> 8 & 0xFF) / 255.0F, (leftBottomColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (centerTopColorUniform != null) {
       int a = centerTopColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       centerTopColorUniform.method_35657((centerTopColor >> 16 & 0xFF) / 255.0F, (centerTopColor >> 8 & 0xFF) / 255.0F, (centerTopColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (centerBottomColorUniform != null) {
       int a = centerBottomColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       centerBottomColorUniform.method_35657((centerBottomColor >> 16 & 0xFF) / 255.0F, (centerBottomColor >> 8 & 0xFF) / 255.0F, (centerBottomColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (rightTopColorUniform != null) {
       int a = rightTopColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       rightTopColorUniform.method_35657((rightTopColor >> 16 & 0xFF) / 255.0F, (rightTopColor >> 8 & 0xFF) / 255.0F, (rightTopColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (rightBottomColorUniform != null) {
       int a = rightBottomColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       rightBottomColorUniform.method_35657((rightBottomColor >> 16 & 0xFF) / 255.0F, (rightBottomColor >> 8 & 0xFF) / 255.0F, (rightBottomColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x + width, y + height, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     
     RenderSystem.setShader(ShaderUtils.gradient6Rect);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
 
 
 
 
 
 
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.shadowRect);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     float extendedWidth = width + softness * 2.0F;
     float extendedHeight = height + softness * 2.0F;
     float drawX = x - softness;
     float drawY = y - softness;
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform softnessUniform = shader.method_34582("Softness");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform topLeftColorUniform = shader.method_34582("TopLeftColor");
     GlUniform topRightColorUniform = shader.method_34582("TopRightColor");
     GlUniform bottomLeftColorUniform = shader.method_34582("BottomLeftColor");
     GlUniform bottomRightColorUniform = shader.method_34582("BottomRightColor");
     
     if (sizeUniform != null) sizeUniform.method_1255(extendedWidth, extendedHeight); 
     if (softnessUniform != null) softnessUniform.method_1251(softness); 
     if (radiusUniform != null) radiusUniform.method_1251(radius);
     
     if (topLeftColorUniform != null) {
       int a = topLeftColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       topLeftColorUniform.method_35657((topLeftColor >> 16 & 0xFF) / 255.0F, (topLeftColor >> 8 & 0xFF) / 255.0F, (topLeftColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (topRightColorUniform != null) {
       int a = topRightColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       topRightColorUniform.method_35657((topRightColor >> 16 & 0xFF) / 255.0F, (topRightColor >> 8 & 0xFF) / 255.0F, (topRightColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (bottomLeftColorUniform != null) {
       int a = bottomLeftColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       bottomLeftColorUniform.method_35657((bottomLeftColor >> 16 & 0xFF) / 255.0F, (bottomLeftColor >> 8 & 0xFF) / 255.0F, (bottomLeftColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (bottomRightColorUniform != null) {
       int a = bottomRightColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       bottomRightColorUniform.method_35657((bottomRightColor >> 16 & 0xFF) / 255.0F, (bottomRightColor >> 8 & 0xFF) / 255.0F, (bottomRightColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1585);
     
     buffer.method_22918(matrix, drawX, drawY, 0.0F).method_22913(0.0F, 0.0F);
     buffer.method_22918(matrix, drawX, drawY + extendedHeight, 0.0F).method_22913(0.0F, 1.0F);
     buffer.method_22918(matrix, drawX + extendedWidth, drawY + extendedHeight, 0.0F).method_22913(1.0F, 1.0F);
     buffer.method_22918(matrix, drawX + extendedWidth, drawY, 0.0F).method_22913(1.0F, 0.0F);
     
     RenderSystem.setShader(ShaderUtils.shadowRect);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
 
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     drawShadow(matrices, x, y, width, height, radius, 10.0F, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }
 
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int color) {
     drawShadow(matrices, x, y, width, height, radius, softness, color, color, color, color);
   }
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
     drawShadow(matrices, x, y, width, height, radius, 10.0F, color, color, color, color);
   }
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, int color) {
     drawShadow(matrices, x, y, width, height, 0.0F, 10.0F, color, color, color, color);
   }
 
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int topColor, int bottomColor) {
     drawShadow(matrices, x, y, width, height, radius, softness, topColor, topColor, bottomColor, bottomColor);
   }
 
   
   public static void drawShadowHorizontal(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int leftColor, int rightColor) {
     drawShadow(matrices, x, y, width, height, radius, softness, leftColor, rightColor, leftColor, rightColor);
   }
 
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, float offsetX, float offsetY, int color) {
     drawShadow(matrices, x + offsetX, y + offsetY, width, height, radius, softness, color, color, color, color);
   }
 
 
   
   public static void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, float offsetX, float offsetY, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     drawShadow(matrices, x + offsetX, y + offsetY, width, height, radius, softness, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }
 
 
 
 
 
   
   public static void drawShadow6(MatrixStack matrices, float x, float y, float width, float height, float radius, float softness, int leftTopColor, int leftBottomColor, int centerTopColor, int centerBottomColor, int rightTopColor, int rightBottomColor) {
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.shadow6Rect);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     float extendedWidth = width + softness * 2.0F;
     float extendedHeight = height + softness * 2.0F;
     float drawX = x - softness;
     float drawY = y - softness;
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform softnessUniform = shader.method_34582("Softness");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform leftTopColorUniform = shader.method_34582("LeftTopColor");
     GlUniform leftBottomColorUniform = shader.method_34582("LeftBottomColor");
     GlUniform centerTopColorUniform = shader.method_34582("CenterTopColor");
     GlUniform centerBottomColorUniform = shader.method_34582("CenterBottomColor");
     GlUniform rightTopColorUniform = shader.method_34582("RightTopColor");
     GlUniform rightBottomColorUniform = shader.method_34582("RightBottomColor");
     
     if (sizeUniform != null) sizeUniform.method_1255(extendedWidth, extendedHeight); 
     if (softnessUniform != null) softnessUniform.method_1251(softness); 
     if (radiusUniform != null) radiusUniform.method_1251(radius);
     
     if (leftTopColorUniform != null) {
       int a = leftTopColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       leftTopColorUniform.method_35657((leftTopColor >> 16 & 0xFF) / 255.0F, (leftTopColor >> 8 & 0xFF) / 255.0F, (leftTopColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (leftBottomColorUniform != null) {
       int a = leftBottomColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       leftBottomColorUniform.method_35657((leftBottomColor >> 16 & 0xFF) / 255.0F, (leftBottomColor >> 8 & 0xFF) / 255.0F, (leftBottomColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (centerTopColorUniform != null) {
       int a = centerTopColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       centerTopColorUniform.method_35657((centerTopColor >> 16 & 0xFF) / 255.0F, (centerTopColor >> 8 & 0xFF) / 255.0F, (centerTopColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (centerBottomColorUniform != null) {
       int a = centerBottomColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       centerBottomColorUniform.method_35657((centerBottomColor >> 16 & 0xFF) / 255.0F, (centerBottomColor >> 8 & 0xFF) / 255.0F, (centerBottomColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (rightTopColorUniform != null) {
       int a = rightTopColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       rightTopColorUniform.method_35657((rightTopColor >> 16 & 0xFF) / 255.0F, (rightTopColor >> 8 & 0xFF) / 255.0F, (rightTopColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     if (rightBottomColorUniform != null) {
       int a = rightBottomColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       rightBottomColorUniform.method_35657((rightBottomColor >> 16 & 0xFF) / 255.0F, (rightBottomColor >> 8 & 0xFF) / 255.0F, (rightBottomColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1585);
     
     buffer.method_22918(matrix, drawX, drawY, 0.0F).method_22913(0.0F, 0.0F);
     buffer.method_22918(matrix, drawX, drawY + extendedHeight, 0.0F).method_22913(0.0F, 1.0F);
     buffer.method_22918(matrix, drawX + extendedWidth, drawY + extendedHeight, 0.0F).method_22913(1.0F, 1.0F);
     buffer.method_22918(matrix, drawX + extendedWidth, drawY, 0.0F).method_22913(1.0F, 0.0F);
     
     RenderSystem.setShader(ShaderUtils.shadow6Rect);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
   
   public static void drawTexture(MatrixStack matrices, Identifier texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2, int color) {
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShaderTexture(0, texture);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     int alpha = color >> 24 & 0xFF;
     if (alpha == 0) alpha = 255; 
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float a = alpha / 255.0F;
     
     RenderSystem.setShader(ShaderProgramKeys.field_53880);
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     buffer.method_22918(matrix, x, y, 0.0F).method_22913(u1, v1).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22913(u1, v2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x + width, y + height, 0.0F).method_22913(u2, v2).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22913(u2, v1).method_22915(r, g, b, a);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.disableBlend();
   }
   
   public static void drawImage(MatrixStack matrices, Identifier texture, float x, float y, float width, float height, int color) {
     drawTexture(matrices, texture, x, y, width, height, 0.0F, 0.0F, 1.0F, 1.0F, color);
   }
   
   public static void drawImage(MatrixStack matrices, String namespace, String path, float x, float y, float width, float height, int color) {
     drawImage(matrices, Identifier.method_60655(namespace, path), x, y, width, height, color);
   }
   
   public static void drawSprite(MatrixStack matrices, Sprite sprite, float x, float y, float size, int color) {
     drawTexture(matrices, sprite.method_45852(), x, y, size, size, sprite
         .method_4594(), sprite.method_4593(), sprite.method_4577(), sprite.method_4575(), color);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, PlayerEntity player, float x, float y, float size, float radius, float hurtPercent) {
     if (player == null)
       return;  Identifier skinTexture = getSkinTexture(player);
     drawHeadInternal(matrices, skinTexture, x, y, size, radius, 1.0F, hurtPercent);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, String username, float x, float y, float size, float radius) {
     drawPlayerHead(matrices, username, x, y, size, radius, 1.0F, 0.0F);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, String username, float x, float y, float size, float radius, float alpha, float hurtPercent) {
     if (username == null || username.isEmpty())
       return;  Identifier skinTexture = getSkinTextureByName(username);
     drawHeadInternal(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, UUID uuid, float x, float y, float size, float radius) {
     drawPlayerHead(matrices, uuid, x, y, size, radius, 1.0F, 0.0F);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, UUID uuid, float x, float y, float size, float radius, float alpha, float hurtPercent) {
     if (uuid == null)
       return;  Identifier skinTexture = getSkinTextureByUUID(uuid);
     drawHeadInternal(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, PlayerListEntry entry, float x, float y, float size, float radius) {
     drawPlayerHead(matrices, entry, x, y, size, radius, 1.0F, 0.0F);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, PlayerListEntry entry, float x, float y, float size, float radius, float alpha, float hurtPercent) {
     if (entry == null)
       return;  Identifier skinTexture = entry.method_52810().comp_1626();
     if (skinTexture == null) {
       skinTexture = DefaultSkinHelper.method_4648(entry.method_2966().getId()).comp_1626();
     }
     drawHeadInternal(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
   }
   
   public static void drawPlayerHead(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius) {
     drawHeadInternal(matrices, skinTexture, x, y, size, radius, 1.0F, 0.0F);
   }
   
   private static void drawHeadInternal(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius, float alpha, float hurtPercent) {
     if (skinTexture == null) {
       skinTexture = DefaultSkinHelper.method_4648(DEFAULT_SKIN_UUID).comp_1626();
     }
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.setShaderTexture(0, skinTexture);
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.face);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     GlUniform locationUniform = shader.method_34582("location");
     GlUniform sizeUniform = shader.method_34582("size");
     GlUniform radiusUniform = shader.method_34582("radius");
     GlUniform alphaUniform = shader.method_34582("alpha");
     GlUniform uUniform = shader.method_34582("u");
     GlUniform vUniform = shader.method_34582("v");
     GlUniform wUniform = shader.method_34582("w");
     GlUniform hUniform = shader.method_34582("h");
     GlUniform hurtPercentUniform = shader.method_34582("hurtPercent");
     
     if (locationUniform != null) locationUniform.method_1255(x, y); 
     if (sizeUniform != null) sizeUniform.method_1255(size, size); 
     if (radiusUniform != null) radiusUniform.method_1251(radius); 
     if (alphaUniform != null) alphaUniform.method_1251(alpha); 
     if (uUniform != null) uUniform.method_1251(0.125F); 
     if (vUniform != null) vUniform.method_1251(0.125F); 
     if (wUniform != null) wUniform.method_1251(0.125F); 
     if (hUniform != null) hUniform.method_1251(0.125F); 
     if (hurtPercentUniform != null) hurtPercentUniform.method_1251(hurtPercent);
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1585);
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22913(0.0F, 0.0F);
     buffer.method_22918(matrix, x, y + size, 0.0F).method_22913(0.0F, 1.0F);
     buffer.method_22918(matrix, x + size, y + size, 0.0F).method_22913(1.0F, 1.0F);
     buffer.method_22918(matrix, x + size, y, 0.0F).method_22913(1.0F, 0.0F);
     
     RenderSystem.setShader(ShaderUtils.face);
     BufferRenderer.method_43433(buffer.method_60800());
     
     drawHeadOverlay(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.disableBlend();
   }
   
   private static void drawHeadOverlay(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius, float alpha, float hurtPercent) {
     RenderSystem.setShaderTexture(0, skinTexture);
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.face);
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     GlUniform locationUniform = shader.method_34582("location");
     GlUniform sizeUniform = shader.method_34582("size");
     GlUniform radiusUniform = shader.method_34582("radius");
     GlUniform alphaUniform = shader.method_34582("alpha");
     GlUniform uUniform = shader.method_34582("u");
     GlUniform vUniform = shader.method_34582("v");
     GlUniform wUniform = shader.method_34582("w");
     GlUniform hUniform = shader.method_34582("h");
     GlUniform hurtPercentUniform = shader.method_34582("hurtPercent");
     
     if (locationUniform != null) locationUniform.method_1255(x, y); 
     if (sizeUniform != null) sizeUniform.method_1255(size, size); 
     if (radiusUniform != null) radiusUniform.method_1251(radius); 
     if (alphaUniform != null) alphaUniform.method_1251(alpha); 
     if (uUniform != null) uUniform.method_1251(0.625F); 
     if (vUniform != null) vUniform.method_1251(0.125F); 
     if (wUniform != null) wUniform.method_1251(0.125F); 
     if (hUniform != null) hUniform.method_1251(0.125F); 
     if (hurtPercentUniform != null) hurtPercentUniform.method_1251(hurtPercent);
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1585);
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22913(0.0F, 0.0F);
     buffer.method_22918(matrix, x, y + size, 0.0F).method_22913(0.0F, 1.0F);
     buffer.method_22918(matrix, x + size, y + size, 0.0F).method_22913(1.0F, 1.0F);
     buffer.method_22918(matrix, x + size, y, 0.0F).method_22913(1.0F, 0.0F);
     
     RenderSystem.setShader(ShaderUtils.face);
     BufferRenderer.method_43433(buffer.method_60800());
   }
   
   private static Identifier getSkinTexture(PlayerEntity player) {
     if (mc.method_1562() == null) {
       return DefaultSkinHelper.method_4648(player.method_5667()).comp_1626();
     }
     
     PlayerListEntry entry = mc.method_1562().method_2871(player.method_5667());
     if (entry != null) {
       return entry.method_52810().comp_1626();
     }
     
     return DefaultSkinHelper.method_4648(player.method_5667()).comp_1626();
   }
   
   private static Identifier getSkinTextureByName(String username) {
     String key = username.toLowerCase(Locale.ROOT);
     Identifier cachedTexture = skinCache.get(key);
     if (cachedTexture != null) {
       return cachedTexture;
     }
     
     if (mc.method_1562() != null) {
       for (PlayerListEntry entry : mc.method_1562().method_2880()) {
         if (entry.method_2966().getName().equalsIgnoreCase(username)) {
           Identifier Identifier = entry.method_52810().comp_1626();
           skinCache.put(key, Identifier);
           return Identifier;
         } 
       } 
     }
     
     if (mc.field_1687 != null) {
       for (PlayerEntity player : mc.field_1687.method_18456()) {
         if (player.method_5477().getString().equalsIgnoreCase(username)) {
           Identifier Identifier = getSkinTexture(player);
           skinCache.put(key, Identifier);
           return Identifier;
         } 
       } 
     }
     
     Identifier texture = DefaultSkinHelper.method_4648(UUID.nameUUIDFromBytes(username.getBytes())).comp_1626();
     skinCache.put(key, texture);
     return texture;
   }
   
   private static Identifier getSkinTextureByUUID(UUID uuid) {
     String key = uuid.toString();
     
     if (skinCache.containsKey(key)) {
       return skinCache.get(key);
     }
     
     if (mc.method_1562() != null) {
       PlayerListEntry entry = mc.method_1562().method_2871(uuid);
       if (entry != null) {
         Identifier texture = entry.method_52810().comp_1626();
         skinCache.put(key, texture);
         return texture;
       } 
     } 
     
     if (mc.field_1687 != null) {
       PlayerEntity player = mc.field_1687.method_18470(uuid);
       if (player != null) {
         Identifier texture = getSkinTexture(player);
         skinCache.put(key, texture);
         return texture;
       } 
     } 
     
     return DefaultSkinHelper.method_4648(uuid).comp_1626();
   }
   
   public static void clearSkinCache() {
     skinCache.clear();
   }
   
   public static void removeSkinFromCache(String username) {
     skinCache.remove(username.toLowerCase(Locale.ROOT));
   }
   
   public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
     drawRoundedRect(matrices, x, y, width, height, radius, radius, radius, radius, color);
   }
   
   public static void drawDefaultHudElementRects(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
     drawDefaultHudElementRects(matrices, x, y, width, height, themeColor, true);
   }
   
   public static void drawDefaultHudElementRects(MatrixStack matrices, float x, float y, float width, float height, int themeColor, boolean drawPattern) {
     drawDefaultHudThemedPanel(matrices, x, y, width, height, 3.0F, 3.5F, themeColor);
     if (drawPattern) {
       drawHudSquarePattern(matrices, x, y, width, height, themeColor);
     }
     drawRoundedRect(matrices, x + width - 14.5F, y + 3.0F, 10.0F, 10.0F, 2.0F, ColorUtils.darken(themeColor, 0.4F));
   }
   
   public static void drawHudSquarePattern(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
     if (width <= 6.0F || height <= 6.0F)
       return; 
     float clipX = x - 1.0F;
     float clipY = y + 1.0F;
     float clipW = Math.max(1.0F, width - 2.0F);
     float clipH = Math.max(1.0F, height - 2.0F);
     float themeAlphaMul = (themeColor >>> 24 & 0xFF) / 255.0F;
     if (themeAlphaMul <= 0.001F) {
       return;
     }
     
     if (clipH <= 20.0F) {
       float[][] compactSlots = { { 0.05F, 0.08F, 8.6F }, { 0.92F, 0.1F, 8.8F }, { 0.16F, 0.78F, 6.3F }, { 0.77F, 0.8F, 6.5F }, { 0.31F, 0.18F, 6.0F }, { 0.58F, 0.74F, 5.8F }, { 0.45F, 0.45F, 5.1F }, { 0.86F, 0.46F, 5.3F }, { 0.23F, 0.52F, 4.9F }, { 0.67F, 0.3F, 5.0F }, { 0.11F, 0.34F, 5.5F }, { 0.38F, 0.7F, 5.2F }, { 0.72F, 0.16F, 5.7F }, { 0.95F, 0.68F, 5.1F } };
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       
       float f = Math.min(compactSlots.length, 3.7F + 
           
           Math.max(0.0F, (clipW - 84.0F) / 32.0F));
       
       int i = ColorUtils.setAlphaColor(
           ColorUtils.darken(themeColor, 0.62F), 
           Math.max(0, Math.min(255, (int)(82.0F * themeAlphaMul))));
 
       
       ScissorUtils.push();
       ScissorUtils.setFromComponentCoordinates(clipX, clipY, clipW, clipH);
       
       try { for (int j = 0; j < compactSlots.length; j++) {
           float reveal = f - j;
           if (reveal > 0.0F) {
 
 
             
             float alphaMul = Math.max(0.0F, Math.min(1.0F, reveal));
             alphaMul = alphaMul * alphaMul * (3.0F - 2.0F * alphaMul);
             if (alphaMul > 0.02F)
             
             { 
               
               float size = compactSlots[j][2];
               float px = clipX + 0.8F + compactSlots[j][0] * Math.max(1.0F, clipW - size + 1.6F);
               float py = clipY - 1.2F + compactSlots[j][1] * Math.max(1.0F, clipH - size + 2.4F);
               
               int outlineAlpha = Math.max(0, Math.min(255, (int)(86.0F * alphaMul * themeAlphaMul)));
               if (outlineAlpha > 0)
               
               { 
                 int outlineColor = ColorUtils.setAlphaColor(i, outlineAlpha);
                 drawRoundedRectOutline(matrices, px, py, size, size, 0.0F, 0.5F, outlineColor, outlineColor, outlineColor, outlineColor); }  } 
           } 
         }  }
       finally { ScissorUtils.unset();
         ScissorUtils.pop(); }
 
       
       return;
     } 
     float[][] slots = { { 0.05F, 4.0F, 9.6F }, { 0.87F, 4.0F, 9.2F }, { 0.5F, 8.0F, 7.4F }, { 0.18F, 13.0F, 6.2F }, { 0.72F, 13.0F, 6.0F }, { 0.07F, 21.0F, 5.6F }, { 0.91F, 21.0F, 5.8F }, { 0.24F, 30.0F, 5.4F }, { 0.66F, 30.0F, 5.5F }, { 0.04F, 38.0F, 6.8F }, { 0.9F, 38.0F, 7.0F }, { 0.15F, 47.0F, 5.4F }, { 0.78F, 47.0F, 5.5F }, { 0.08F, 56.0F, 5.1F }, { 0.92F, 56.0F, 5.2F }, { 0.23F, 65.0F, 5.8F }, { 0.69F, 65.0F, 5.9F }, { 0.52F, 71.0F, 7.2F }, { 0.06F, 74.0F, 7.6F }, { 0.88F, 74.0F, 7.4F }, { 0.14F, 85.0F, 5.7F }, { 0.82F, 85.0F, 5.8F }, { 0.09F, 97.0F, 6.5F }, { 0.9F, 98.0F, 6.6F } };
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     int baseCount = 10;
     float extraHeight = Math.max(0.0F, clipH - 24.0F);
     float desiredCount = Math.min(slots.length, baseCount + extraHeight / 10.0F);
     float panelAlpha = Math.max(0.0F, Math.min(1.0F, (clipH - 10.0F) / 16.0F));
     panelAlpha = panelAlpha * panelAlpha * (3.0F - 2.0F * panelAlpha);
     int outlineColorBase = ColorUtils.setAlphaColor(ColorUtils.darken(themeColor, 0.72F), Math.max(0, Math.min(255, (int)(40.0F * themeAlphaMul))));
     
     ScissorUtils.push();
     ScissorUtils.setFromComponentCoordinates(clipX, clipY, clipW, clipH);
     
     try { for (int i = 0; i < slots.length; i++) {
         float reveal = desiredCount - i;
         if (reveal <= 0.0F) {
           continue;
         }
         
         float alphaMul = Math.max(0.0F, Math.min(1.0F, reveal));
         alphaMul = alphaMul * alphaMul * (3.0F - 2.0F * alphaMul);
         alphaMul *= panelAlpha;
         if (alphaMul <= 0.015F) {
           continue;
         }
         
         float size = slots[i][2];
         float px = clipX + 2.0F + slots[i][0] * Math.max(1.0F, clipW - size - 4.0F);
         float py = clipY + slots[i][1];
         float bottomLimit = clipY + clipH - 1.0F;
         if (py >= bottomLimit) {
           continue;
         }
         if (py + size > bottomLimit) {
           float visible = Math.max(0.0F, Math.min(1.0F, (bottomLimit - py) / Math.max(1.0F, size)));
           visible = visible * visible * (3.0F - 2.0F * visible);
           alphaMul *= visible;
           if (alphaMul <= 0.015F) {
             continue;
           }
         } 
         
         int outlineAlpha = Math.max(0, Math.min(255, (int)(58.0F * alphaMul * themeAlphaMul)));
         if (outlineAlpha > 0) {
 
           
           int outlineColor = ColorUtils.setAlphaColor(outlineColorBase, outlineAlpha);
           drawRoundedRectOutline(matrices, px, py, size, size, 0.0F, 0.55F, outlineColor, outlineColor, outlineColor, outlineColor);
         }  continue;
       }  }
     finally { ScissorUtils.unset();
       ScissorUtils.pop(); }
   
   }
   
   public static void drawDefaultHudInfoBox(MatrixStack matrices, float x, float y, float width, int outerColor, int innerColor) {
     drawRoundedRect(matrices, x - 0.25F, y - 1.25F, width + 0.5F, 9.0F, 1.3F, outerColor);
     drawRoundedRect(matrices, x, y - 1.0F, width, 8.5F, 1.0F, innerColor);
   }
 
 
   
   public static void drawDefaultHudPanel(MatrixStack matrices, float x, float y, float width, float height, float gradientRadius, float borderRadius, int borderColor, int topColor, int bottomColor) {
     drawRoundedRect(matrices, x - 0.5F, y - 0.5F, width + 1.0F, height + 1.0F, borderRadius, borderColor);
     drawGradientRect(matrices, x, y, width, height, gradientRadius, topColor, bottomColor);
   }
 
   
   public static void drawDefaultHudThemedPanel(MatrixStack matrices, float x, float y, float width, float height, float gradientRadius, float borderRadius, int themeColor) {
     drawDefaultHudPanel(matrices, x, y, width, height, gradientRadius, borderRadius, 
         
         ColorUtils.rgba(50, 50, 50, 255), 
         ColorUtils.darken(themeColor, 0.15F), 
         ColorUtils.darken(themeColor, 0.05F));
   }
 
 
 
   
   public static void drawWaveHudHeader(MatrixStack matrices, float x, float y, float width, float height, float radius, float shadowRadius, float shadowSoftness, int leftTop, int leftBottom, int centerTop, int centerBottom, int rightTop, int rightBottom) {
     drawShadow6(matrices, x, y, width, height, shadowRadius, shadowSoftness, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
     
     drawGradient6Rect(matrices, x, y, width, height, radius, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
   }
 
 
 
   
   public static void drawWaveHudPanel(MatrixStack matrices, float x, float y, float width, float height, int bgColor, float headerHeight, float headerRadius, float shadowRadius, float shadowSoftness, int leftTop, int leftBottom, int centerTop, int centerBottom, int rightTop, int rightBottom) {
     drawRoundedRect(matrices, x, y, width, height, 0.0F, bgColor);
     drawWaveHudHeader(matrices, x, y, width, headerHeight, headerRadius, shadowRadius, shadowSoftness, leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
   }
 
 
   
   public static void drawTargetHudWaveFrame(MatrixStack matrices, float x, float y, float width, float height, float padding, float entityBoxSize, float alpha) {
     drawRoundedRect(matrices, x, y, width, height, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 40, 255), alpha));
     drawRoundedRect(matrices, x + padding, y + padding, width - padding * 2.0F, height - padding * 2.0F, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(20, 20, 20, 255), alpha));
     drawRoundedRect(matrices, x + padding + 2.0F, y + padding + 2.0F, entityBoxSize, entityBoxSize, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 40, 255), alpha));
     drawRoundedRect(matrices, x + padding + 3.0F, y + padding + 3.0F, entityBoxSize - 2.0F, entityBoxSize - 2.0F, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(25, 25, 25, 255), alpha));
   }
   
   public static void drawTargetHudDefaultPlaceholder(MatrixStack matrices, float x, float y, float alpha) {
     drawRoundedRect(matrices, x - 1.0F, y - 1.0F, 22.0F, 22.0F, 1.0F, ColorUtils.applyAlpha(ColorUtils.rgba(21, 21, 21, 255), alpha));
   }
   
   public static void drawTargetHudHealthBars(MatrixStack matrices, float x, float y, float width, float trailProgress, float progress, int themeColor, int themecolor2, float alpha) {
     drawRoundedRect(matrices, x, y, width, 5.5F, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.5F), alpha * 0.8F));
     drawRoundedRect(matrices, x, y, width * trailProgress, 5.5F, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.8F), alpha * 0.8F));
     drawGradientRect(matrices, x, y, width * progress, 5.5F, 1.25F, ColorUtils.applyAlpha(themeColor, alpha), ColorUtils.applyAlpha(themecolor2, alpha), true);
   }
   
   public static void drawTargetHudGoldenBars(MatrixStack matrices, float x, float y, float width, float height, float trailProgress, float progress, float alpha, float goldenAlpha) {
     int goldenColor = ColorUtils.rgba(255, 215, 0, 255);
     drawRoundedRect(matrices, x, y, width * trailProgress, height, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(goldenColor, 0.65F), alpha * goldenAlpha * 0.8F));
     drawGradientRect(matrices, x, y, width * progress, height, 1.25F, ColorUtils.applyAlpha(ColorUtils.darken(goldenColor, 0.55F), alpha * goldenAlpha), ColorUtils.applyAlpha(goldenColor, alpha * goldenAlpha), true);
   }
   
   public static void drawTargetHudHeartBase(MatrixStack matrices, float x, float y, float alpha) {
     drawRoundedRect(matrices, x, y, 6.2F, 4.5F, 0.0F, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 255), alpha));
   }
   
   public static void drawTargetHudHeartFill(MatrixStack matrices, float x, float y, float width, int heartColor, int shadowColor) {
     drawShadow(matrices, x + 1.0F, y + 1.0F, width, 2.0F, 0.0F, 8.0F, shadowColor);
     drawRoundedRect(matrices, x, y, width + 1.2F, 4.5F, 0.0F, heartColor);
   }
   
   public static void drawKeyStrokeRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
     drawRoundedRect(matrices, x, y, width, height, radius, color);
   }
   
   public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color) {
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.roundedRect);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(topLeft, topRight, bottomRight, bottomLeft);
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     
     int alpha = color >> 24 & 0xFF;
     if (alpha == 0) alpha = 255; 
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float a = alpha / 255.0F;
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x + width, y + height, 0.0F).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22915(r, g, b, a);
     
     RenderSystem.setShader(ShaderUtils.roundedRect);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
   
   public static void drawRoundCircle(MatrixStack matrices, float x, float y, float radius, int color) {
     Matrix4f matrix = matrices.method_23760().method_23761();
     drawRoundedRect(matrices, x - radius / 2.0F, y - radius / 2.0F, radius, radius, radius / 2.0F - 0.5F, color);
   }
   
   public static void drawRingArc(MatrixStack matrices, float x, float y, float size, float thickness, float startDeg, float endDeg, int color) {
     if (size <= 0.0F || thickness <= 0.0F)
       return; 
     float radius = size / 2.0F;
     float start = (float)Math.toRadians(startDeg);
     float end = (float)Math.toRadians(endDeg);
     float twoPi = 6.2831855F;
     if (start < 0.0F) start += twoPi; 
     if (end < 0.0F) end += twoPi; 
     for (; end < start; end += twoPi);
     if (end - start <= 1.0E-4F) {
       end = start + twoPi;
     }
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.ringArc);
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform thicknessUniform = shader.method_34582("Thickness");
     GlUniform startUniform = shader.method_34582("StartAngle");
     GlUniform endUniform = shader.method_34582("EndAngle");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform colorModulatorUniform = shader.method_34582("ColorModulator");
     
     if (sizeUniform != null) sizeUniform.method_1255(size, size); 
     if (radiusUniform != null) radiusUniform.method_1251(radius); 
     if (thicknessUniform != null) thicknessUniform.method_1251(thickness); 
     if (startUniform != null) startUniform.method_1251(start); 
     if (endUniform != null) endUniform.method_1251(end); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(Math.min(1.0F, thickness * 0.5F)); 
     if (colorModulatorUniform != null) colorModulatorUniform.method_35657(1.0F, 1.0F, 1.0F, 1.0F);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     
     int alpha = color >> 24 & 0xFF;
     if (alpha == 0) alpha = 255; 
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float a = alpha / 255.0F;
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x, y + size, 0.0F).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x + size, y + size, 0.0F).method_22915(r, g, b, a);
     buffer.method_22918(matrix, x + size, y, 0.0F).method_22915(r, g, b, a);
     
     RenderSystem.setShader(ShaderUtils.ringArc);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
   
   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.gradientRect);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform colorModulatorUniform = shader.method_34582("ColorModulator");
     GlUniform topLeftColorUniform = shader.method_34582("TopLeftColor");
     GlUniform bottomLeftColorUniform = shader.method_34582("BottomLeftColor");
     GlUniform topRightColorUniform = shader.method_34582("TopRightColor");
     GlUniform bottomRightColorUniform = shader.method_34582("BottomRightColor");
     
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(topLeft, topRight, bottomRight, bottomLeft); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(1.0F); 
     if (colorModulatorUniform != null) colorModulatorUniform.method_35657(1.0F, 1.0F, 1.0F, 1.0F);
     
     int tlAlpha = topLeftColor >> 24 & 0xFF;
     if (tlAlpha == 0) tlAlpha = 255; 
     if (topLeftColorUniform != null) topLeftColorUniform.method_35657((topLeftColor >> 16 & 0xFF) / 255.0F, (topLeftColor >> 8 & 0xFF) / 255.0F, (topLeftColor & 0xFF) / 255.0F, tlAlpha / 255.0F);
 
 
 
 
 
     
     int blAlpha = bottomLeftColor >> 24 & 0xFF;
     if (blAlpha == 0) blAlpha = 255; 
     if (bottomLeftColorUniform != null) bottomLeftColorUniform.method_35657((bottomLeftColor >> 16 & 0xFF) / 255.0F, (bottomLeftColor >> 8 & 0xFF) / 255.0F, (bottomLeftColor & 0xFF) / 255.0F, blAlpha / 255.0F);
 
 
 
 
 
     
     int trAlpha = topRightColor >> 24 & 0xFF;
     if (trAlpha == 0) trAlpha = 255; 
     if (topRightColorUniform != null) topRightColorUniform.method_35657((topRightColor >> 16 & 0xFF) / 255.0F, (topRightColor >> 8 & 0xFF) / 255.0F, (topRightColor & 0xFF) / 255.0F, trAlpha / 255.0F);
 
 
 
 
 
     
     int brAlpha = bottomRightColor >> 24 & 0xFF;
     if (brAlpha == 0) brAlpha = 255; 
     if (bottomRightColorUniform != null) bottomRightColorUniform.method_35657((bottomRightColor >> 16 & 0xFF) / 255.0F, (bottomRightColor >> 8 & 0xFF) / 255.0F, (bottomRightColor & 0xFF) / 255.0F, brAlpha / 255.0F);
 
 
 
 
 
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22913(0.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22913(0.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x + width, y + height, 0.0F).method_22913(1.0F, 1.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22913(1.0F, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     
     RenderSystem.setShader(ShaderUtils.gradientRect);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
   
   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }
   
   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int topColor, int bottomColor) {
     drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, topColor, topColor, bottomColor, bottomColor);
   }
   
   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, int topColor, int bottomColor) {
     drawGradientRect(matrices, x, y, width, height, 0.0F, 0.0F, 0.0F, 0.0F, topColor, topColor, bottomColor, bottomColor);
   }
   
   public static void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int leftColor, int rightColor, boolean horizontal) {
     if (horizontal) {
       drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, leftColor, rightColor, leftColor, rightColor);
     } else {
       drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, leftColor, leftColor, rightColor, rightColor);
     } 
   }
 
 
   
   public static void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, float outline, int outlineColor) {
     drawRoundedRectOutline(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, outline, outlineColor, outlineColor, outlineColor, outlineColor);
   }
 
 
   
   public static void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height, float radius, float outline, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     drawRoundedRectOutline(matrices, x, y, width, height, radius, radius, radius, radius, outline, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
   }
 
 
 
   
   public static void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, float outline, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
     if (outline <= 0.0F)
       return; 
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.roundedRectOutline);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform colorModulatorUniform = shader.method_34582("ColorModulator");
     GlUniform outlineUniform = shader.method_34582("Outline");
     GlUniform topLeftColorUniform = shader.method_34582("TopLeftColor");
     GlUniform bottomLeftColorUniform = shader.method_34582("BottomLeftColor");
     GlUniform topRightColorUniform = shader.method_34582("TopRightColor");
     GlUniform bottomRightColorUniform = shader.method_34582("BottomRightColor");
     
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(topLeft, topRight, bottomRight, bottomLeft); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(1.0F); 
     if (colorModulatorUniform != null) colorModulatorUniform.method_35657(1.0F, 1.0F, 1.0F, 1.0F); 
     if (outlineUniform != null) outlineUniform.method_1251(outline);
     
     if (topLeftColorUniform != null) {
       int a = topLeftColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       topLeftColorUniform.method_35657((topLeftColor >> 16 & 0xFF) / 255.0F, (topLeftColor >> 8 & 0xFF) / 255.0F, (topLeftColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
     
     if (bottomLeftColorUniform != null) {
       int a = bottomLeftColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       bottomLeftColorUniform.method_35657((bottomLeftColor >> 16 & 0xFF) / 255.0F, (bottomLeftColor >> 8 & 0xFF) / 255.0F, (bottomLeftColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
     
     if (topRightColorUniform != null) {
       int a = topRightColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       topRightColorUniform.method_35657((topRightColor >> 16 & 0xFF) / 255.0F, (topRightColor >> 8 & 0xFF) / 255.0F, (topRightColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
     
     if (bottomRightColorUniform != null) {
       int a = bottomRightColor >> 24 & 0xFF;
       if (a == 0) a = 255; 
       bottomRightColorUniform.method_35657((bottomRightColor >> 16 & 0xFF) / 255.0F, (bottomRightColor >> 8 & 0xFF) / 255.0F, (bottomRightColor & 0xFF) / 255.0F, a / 255.0F);
     } 
 
 
 
 
 
     
     BufferBuilder buffer = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     
     buffer.method_22918(matrix, x, y, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x, y + height, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x + width, y + height, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     buffer.method_22918(matrix, x + width, y, 0.0F).method_22915(1.0F, 1.0F, 1.0F, 1.0F);
     
     RenderSystem.setShader(ShaderUtils.roundedRectOutline);
     BufferRenderer.method_43433(buffer.method_60800());
     
     RenderSystem.disableBlend();
   }
 
   
   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color) {
     if (BlurProgram.getBuffer2() == null)
       return; 
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.roundedTexture);
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform colorModulatorUniform = shader.method_34582("ColorModulator");
     
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(topLeft, topRight, bottomRight, bottomLeft); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(0.5F); 
     if (colorModulatorUniform != null) colorModulatorUniform.method_35657(1.0F, 1.0F, 1.0F, 1.0F);
     
     RenderSystem.setShaderTexture(0, BlurProgram.getTexture());
     RenderSystem.setShader(ShaderUtils.roundedTexture);
     
     int screenWidth = mc.method_22683().method_4486();
     int screenHeight = mc.method_22683().method_4502();
     
     float u1 = x / screenWidth;
     float v1 = (screenHeight - y) / screenHeight;
     float u2 = (x + width) / screenWidth;
     float v2 = (screenHeight - y - height) / screenHeight;
     
     int alpha = color >> 24 & 0xFF;
     if (alpha == 0) alpha = 255; 
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float a = alpha / 255.0F;
     
     BufferBuilder builder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     builder.method_22918(matrix, x, y, 0.0F).method_22913(u1, v1).method_22915(r, g, b, a);
     builder.method_22918(matrix, x, y + height, 0.0F).method_22913(u1, v2).method_22915(r, g, b, a);
     builder.method_22918(matrix, x + width, y + height, 0.0F).method_22913(u2, v2).method_22915(r, g, b, a);
     builder.method_22918(matrix, x + width, y, 0.0F).method_22913(u2, v1).method_22915(r, g, b, a);
     BufferRenderer.method_43433(builder.method_60800());
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.disableBlend();
   }
   
   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
     drawBlur(matrices, x, y, width, height, radius, radius, radius, radius, color);
   }
   
   public static void startGlow(float radius, int color, GlowCallback callback, MatrixStack matrices) {
     int a = color >> 24 & 0xFF;
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     if (a == 0) a = 255; 
     GlowProgram.getInstance().begin(radius, new Color(r, g, b, a));
     callback.render();
     GlowProgram.getInstance().end(matrices, callback);
   }
   
   public static void startGlow(float radius, float intensity, int color, GlowCallback callback, MatrixStack matrices) {
     int a = color >> 24 & 0xFF;
     int r = color >> 16 & 0xFF;
     int g = color >> 8 & 0xFF;
     int b = color & 0xFF;
     if (a == 0) a = 255; 
     GlowProgram.getInstance().begin(radius, intensity, new Color(r, g, b, a));
     callback.render();
     GlowProgram.getInstance().end(matrices, callback);
   }
   
   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, float blurStrength, int color) {
     BlurProgram.getInstance().request();
     if (BlurProgram.getBuffer2() == null)
       return; 
     BlurProgram.getInstance().setBlurOffset(blurStrength);
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.roundedTexture);
     
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform colorModulatorUniform = shader.method_34582("ColorModulator");
     
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(topLeft, topRight, bottomRight, bottomLeft); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(0.5F); 
     if (colorModulatorUniform != null) colorModulatorUniform.method_35657(1.0F, 1.0F, 1.0F, 1.0F);
     
     RenderSystem.setShaderTexture(0, BlurProgram.getTexture());
     RenderSystem.setShader(ShaderUtils.roundedTexture);
     
     int screenWidth = mc.method_22683().method_4486();
     int screenHeight = mc.method_22683().method_4502();
     
     float u1 = x / screenWidth;
     float v1 = (screenHeight - y) / screenHeight;
     float u2 = (x + width) / screenWidth;
     float v2 = (screenHeight - y - height) / screenHeight;
     
     int alpha = color >> 24 & 0xFF;
     if (alpha == 0) alpha = 255; 
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float a = alpha / 255.0F;
     
     BufferBuilder builder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     builder.method_22918(matrix, x, y, 0.0F).method_22913(u1, v1).method_22915(r, g, b, a);
     builder.method_22918(matrix, x, y + height, 0.0F).method_22913(u1, v2).method_22915(r, g, b, a);
     builder.method_22918(matrix, x + width, y + height, 0.0F).method_22913(u2, v2).method_22915(r, g, b, a);
     builder.method_22918(matrix, x + width, y, 0.0F).method_22913(u2, v1).method_22915(r, g, b, a);
     BufferRenderer.method_43433(builder.method_60800());
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.disableBlend();
   }
   
   public static void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, float blurStrength, int color) {
     drawBlur(matrices, x, y, width, height, radius, radius, radius, radius, blurStrength, color);
   }
   
   public static void drawLiquidGlass(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color, float globalAlpha, float fresnelPower, int fresnelColor, float baseAlpha, boolean fresnelInvert, float fresnelMix, float distortStrength, float squirt, boolean clean) {
     int textureId;
     if (clean) {
       textureId = mc.method_1522().method_30277();
     } else {
       BlurProgram.getInstance().request();
       if (BlurProgram.getBuffer1() == null)
         return;  textureId = BlurProgram.getTexture();
       if (textureId == 0)
         return; 
     } 
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     
     RenderSystem.setShaderTexture(0, textureId);
     
     ShaderProgram shader = mc.method_62887().method_62947(ShaderUtils.liquidGlass);
     
     GlUniform globalAlphaUniform = shader.method_34582("GlobalAlpha");
     GlUniform sizeUniform = shader.method_34582("Size");
     GlUniform radiusUniform = shader.method_34582("Radius");
     GlUniform smoothnessUniform = shader.method_34582("Smoothness");
     GlUniform fresnelPowerUniform = shader.method_34582("FresnelPower");
     GlUniform fresnelColorUniform = shader.method_34582("FresnelColor");
     GlUniform fresnelAlphaUniform = shader.method_34582("FresnelAlpha");
     GlUniform baseAlphaUniform = shader.method_34582("BaseAlpha");
     GlUniform fresnelInvertUniform = shader.method_34582("FresnelInvert");
     GlUniform fresnelMixUniform = shader.method_34582("FresnelMix");
     GlUniform distortStrengthUniform = shader.method_34582("DistortStrength");
     GlUniform cornerSmoothnessUniform = shader.method_34582("CornerSmoothness");
     
     if (globalAlphaUniform != null) globalAlphaUniform.method_1251(globalAlpha); 
     if (sizeUniform != null) sizeUniform.method_1255(width, height); 
     if (radiusUniform != null) radiusUniform.method_35657(topLeft, topRight, bottomRight, bottomLeft); 
     if (smoothnessUniform != null) smoothnessUniform.method_1251(0.5F); 
     if (fresnelPowerUniform != null) fresnelPowerUniform.method_1251(fresnelPower);
     
     int fAlpha = fresnelColor >> 24 & 0xFF;
     if (fAlpha == 0) fAlpha = 255; 
     if (fresnelColorUniform != null) fresnelColorUniform.method_1249((fresnelColor >> 16 & 0xFF) / 255.0F, (fresnelColor >> 8 & 0xFF) / 255.0F, (fresnelColor & 0xFF) / 255.0F);
 
 
 
     
     if (fresnelAlphaUniform != null) fresnelAlphaUniform.method_1251(fAlpha / 255.0F); 
     if (baseAlphaUniform != null) baseAlphaUniform.method_1251(baseAlpha); 
     if (fresnelInvertUniform != null) fresnelInvertUniform.method_35649(fresnelInvert ? 1 : 0); 
     if (fresnelMixUniform != null) fresnelMixUniform.method_1251(fresnelMix); 
     if (distortStrengthUniform != null) distortStrengthUniform.method_1251(distortStrength); 
     if (cornerSmoothnessUniform != null) cornerSmoothnessUniform.method_1251(squirt);
     
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     float screenWidth = mc.method_22683().method_4489();
     float screenHeight = mc.method_22683().method_4506();
     float scaleFactor = (float)mc.method_22683().method_4495();
     
     float scaledX = x * scaleFactor;
     float scaledY = y * scaleFactor;
     float scaledW = width * scaleFactor;
     float scaledH = height * scaleFactor;
     
     float u1 = scaledX / screenWidth;
     float v1 = 1.0F - scaledY / screenHeight;
     float u2 = (scaledX + scaledW) / screenWidth;
     float v2 = 1.0F - (scaledY + scaledH) / screenHeight;
     
     int alpha = color >> 24 & 0xFF;
     if (alpha == 0) alpha = 255; 
     float r = (color >> 16 & 0xFF) / 255.0F;
     float g = (color >> 8 & 0xFF) / 255.0F;
     float b = (color & 0xFF) / 255.0F;
     float a = alpha / 255.0F;
     
     RenderSystem.setShader(ShaderUtils.liquidGlass);
     
     BufferBuilder builder = Tessellator.method_1348().method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1575);
     builder.method_22918(matrix, x, y, 0.0F).method_22913(u1, v1).method_22915(r, g, b, a);
     builder.method_22918(matrix, x, y + height, 0.0F).method_22913(u1, v2).method_22915(r, g, b, a);
     builder.method_22918(matrix, x + width, y + height, 0.0F).method_22913(u2, v2).method_22915(r, g, b, a);
     builder.method_22918(matrix, x + width, y, 0.0F).method_22913(u2, v1).method_22915(r, g, b, a);
     BufferRenderer.method_43433(builder.method_60800());
     
     RenderSystem.setShaderTexture(0, 0);
     RenderSystem.enableCull();
     RenderSystem.disableBlend();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\RenderUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */