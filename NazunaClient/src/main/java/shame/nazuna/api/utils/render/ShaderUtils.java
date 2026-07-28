package shame.nazuna.api.utils.render;
 import net.minecraft.ShaderProgramKey;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.Identifier;
 import shame.nazuna.api.QClient;
 
 public final class ShaderUtils implements QClient {
   private ShaderUtils() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
   public static final ShaderProgramKey roundedRect = register("rect", "rounded_rect", VertexFormats.field_1576);
   public static final ShaderProgramKey roundedRectOutline = register("rect", "rounded_rect_outline", VertexFormats.field_1576);
   public static final ShaderProgramKey ringArc = register("ring_arc", "ring_arc", VertexFormats.field_1576);
   public static final ShaderProgramKey roundedTexture = register("texture", "texture_rect", VertexFormats.field_1575);
   public static final ShaderProgramKey liquidGlass = register("liquidglass", "liquid", VertexFormats.field_1575);
   public static final ShaderProgramKey kawaseDown = register("kawase_down", "kawase_down", VertexFormats.field_1575);
   public static final ShaderProgramKey kawaseUp = register("kawase_up", "kawase_up", VertexFormats.field_1575);
   public static final ShaderProgramKey gradientRect = register("gradient_rect", "gradient", VertexFormats.field_1575);
   public static final ShaderProgramKey shadowRect = register("shadow_rect", "shadow", VertexFormats.field_1576);
   public static final ShaderProgramKey shadow6Rect = register("shadow6", "shadow", VertexFormats.field_1576);
   public static final ShaderProgramKey fontsMsdf = register("fonts", "fonts", VertexFormats.field_1575);
   public static final ShaderProgramKey face = register("face", "face", VertexFormats.field_1575);
   public static final ShaderProgramKey gradient6Rect = register("gradient6", "gradient", VertexFormats.field_1576);
   public static final ShaderProgramKey sonar = register("sonar", "sonar", VertexFormats.field_1576);
   public static final ShaderProgramKey scanEffect = register("sonar", "scan_effect", VertexFormats.field_1585);
   public static final ShaderProgramKey blockOverlay = register("blockoverlay", "block_overlay", VertexFormats.field_1575);
   public static final ShaderProgramKey chamsFill = register("chams", "chams_fill", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderHandsMaskDiff = register("hands", "hands_mask_diff", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderHandsOverlay = register("hands", "hands_overlay", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderHandsGlow = register("hands", "hands_glow", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderHandsKawaseDown = register("hands", "hands_kawase_down", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderHandsKawaseUp = register("hands", "hands_kawase_up", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderEspGlow = register("shaderesp", "glow", VertexFormats.field_1575);
   public static final ShaderProgramKey shaderEspFill = register("shaderesp", "fill", VertexFormats.field_1575);
   
   private static ShaderProgramKey register(String shaderNamePackage, String shaderName, VertexFormat vertexFormat) {
     return new ShaderProgramKey(Identifier.method_60655("astra", "core/" + shaderNamePackage + "/" + shaderName), vertexFormat, Defines.field_53930);
   }
 }

