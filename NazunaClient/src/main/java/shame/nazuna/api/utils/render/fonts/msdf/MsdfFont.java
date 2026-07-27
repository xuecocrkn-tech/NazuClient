package shame.nazuna.api.utils.render.fonts.msdf;
 
 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 import java.io.BufferedReader;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.util.HashMap;
 import java.util.stream.Collectors;
 import net.minecraft.AbstractTexture;
 import net.minecraft.Identifier;
 import net.minecraft.VertexConsumer;
 import org.joml.Matrix4f;
 import shame.nazuna.api.QClient;
 
 
 
 public final class MsdfFont
   implements QClient
 {
   private final String name;
   private final AbstractTexture texture;
   private final float atlasWidth;
   private final float atlasHeight;
   private final float range;
   private final float lineHeight;
   private final float ascender;
   private final float descender;
   private final HashMap<Integer, MsdfGlyph> glyphs;
   private boolean filtered = false;
   
   private MsdfFont(String name, AbstractTexture texture, float atlasWidth, float atlasHeight, float range, float lineHeight, float ascender, float descender, HashMap<Integer, MsdfGlyph> glyphs) {
     this.name = name;
     this.texture = texture;
     this.atlasWidth = atlasWidth;
     this.atlasHeight = atlasHeight;
     this.range = range;
     this.lineHeight = lineHeight;
     this.ascender = ascender;
     this.descender = descender;
     this.glyphs = glyphs;
   }
   
   public void setFiltered() {
     if (!this.filtered) {
       this.texture.method_4527(true, false);
       this.filtered = true;
     } 
   }
   
   public int getTextureId() {
     return this.texture.method_4624();
   }
   
   public float getAtlasWidth() {
     return this.atlasWidth;
   }
   
   public float getAtlasHeight() {
     return this.atlasHeight;
   }
   
   public float getRange() {
     return this.range;
   }
   
   public float getLineHeight() {
     return this.lineHeight;
   }
   
   public float getBaselineHeight() {
     return this.lineHeight + this.descender;
   }
   
   public String getName() {
     return this.name;
   }
 
 
   
   public void applyGlyphs(Matrix4f matrix, VertexConsumer consumer, float size, String text, float thickness, float x, float y, float z, int red, int green, int blue, int alpha) {
     text = replaceSymbols(text);
     
     for (int i = 0; i < text.length(); i++) {
       char c = text.charAt(i);
       
       if (c == '§' && i + 1 < text.length()) {
         i++;
       }
       else {
         
         MsdfGlyph glyph = this.glyphs.get(Integer.valueOf(c));
         if (glyph != null)
           x += glyph.apply(matrix, consumer, size, x, y, z, red, green, blue, alpha) + thickness; 
       } 
     } 
   }
   
   public float getWidth(String text, float size) {
     text = replaceSymbols(text);
     float width = 0.0F;
     
     for (int i = 0; i < text.length(); i++) {
       char c = text.charAt(i);
       
       if (c == '§' && i + 1 < text.length()) {
         i++;
       }
       else {
         
         MsdfGlyph glyph = this.glyphs.get(Integer.valueOf(c));
         if (glyph != null) {
           width += glyph.getWidth(size);
         }
       } 
     } 
     return width;
   }
   
   private static String replaceSymbols(String text) {
     if (text == null) return ""; 
     return text
       .replace("ᴀ", "A").replace("ʙ", "B").replace("ᴄ", "C")
       .replace("ᴅ", "D").replace("ᴇ", "E").replace("ғ", "F")
       .replace("ɢ", "G").replace("ʜ", "H").replace("ɪ", "I")
       .replace("ᴊ", "J").replace("ᴋ", "K").replace("ʟ", "L")
       .replace("ᴍ", "M").replace("ɴ", "N").replace("ᴏ", "O")
       .replace("ᴘ", "P").replace("ǫ", "Q").replace("ʀ", "R")
       .replace("ꜱ", "S").replace("ᴛ", "T").replace("ᴜ", "U")
       .replace("ᴠ", "V").replace("ᴡ", "W").replace("ʏ", "Y")
       .replace("ᴢ", "Z").replace("ꜰ", "F");
   }
   
   private static String readResource(Identifier identifier) {
     try {
       InputStream inputStream = mc.method_1478().open(identifier);
       BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
       String result = reader.lines().collect(Collectors.joining("\n"));
       reader.close();
       inputStream.close();
       return result;
     } catch (Exception e) {
       throw new RuntimeException("Failed to read resource: " + String.valueOf(identifier), e);
     } 
   }
   
   public static Builder builder() {
     return new Builder();
   }
   
   public static class Builder {
     private String name = "?";
     private Identifier dataIdentifier;
     private Identifier atlasIdentifier;
     
     public Builder name(String name) {
       this.name = name;
       return this;
     }
     
     public Builder data(String dataFileName) {
       this.dataIdentifier = Identifier.method_60655("astra", "fonts/msdf/" + dataFileName + "/font.json");
       return this;
     }
     
     public Builder atlas(String atlasFileName) {
       this.atlasIdentifier = Identifier.method_60655("astra", "fonts/msdf/" + atlasFileName + "/font.png");
       return this;
     }
     
     public MsdfFont build() {
       String json = MsdfFont.readResource(this.dataIdentifier);
       JsonObject root = JsonParser.parseString(json).getAsJsonObject();
       
       JsonObject atlasObj = root.getAsJsonObject("atlas");
       float atlasWidth = atlasObj.get("width").getAsFloat();
       float atlasHeight = atlasObj.get("height").getAsFloat();
       float range = atlasObj.get("distanceRange").getAsFloat();
       
       JsonObject metricsObj = root.getAsJsonObject("metrics");
       float lineHeight = metricsObj.get("lineHeight").getAsFloat();
       float ascender = metricsObj.get("ascender").getAsFloat();
       float descender = metricsObj.get("descender").getAsFloat();
       
       HashMap<Integer, MsdfGlyph> glyphs = new HashMap<>();
       JsonArray glyphsArray = root.getAsJsonArray("glyphs");
       
       for (JsonElement element : glyphsArray) {
         JsonObject glyphObj = element.getAsJsonObject();
         
         int unicode = glyphObj.get("unicode").getAsInt();
         float advance = glyphObj.get("advance").getAsFloat();
         
         float planeLeft = 0.0F, planeTop = 0.0F, planeRight = 0.0F, planeBottom = 0.0F;
         if (glyphObj.has("planeBounds") && !glyphObj.get("planeBounds").isJsonNull()) {
           JsonObject plane = glyphObj.getAsJsonObject("planeBounds");
           planeLeft = plane.get("left").getAsFloat();
           planeTop = plane.get("top").getAsFloat();
           planeRight = plane.get("right").getAsFloat();
           planeBottom = plane.get("bottom").getAsFloat();
         } 
         
         float atlasLeft = 0.0F, atlasTop = 0.0F, atlasRight = 0.0F, atlasBottom = 0.0F;
         if (glyphObj.has("atlasBounds") && !glyphObj.get("atlasBounds").isJsonNull()) {
           JsonObject atlas = glyphObj.getAsJsonObject("atlasBounds");
           atlasLeft = atlas.get("left").getAsFloat();
           atlasTop = atlas.get("top").getAsFloat();
           atlasRight = atlas.get("right").getAsFloat();
           atlasBottom = atlas.get("bottom").getAsFloat();
         } 
         
         MsdfGlyph glyph = new MsdfGlyph(unicode, advance, planeLeft, planeTop, planeRight, planeBottom, atlasLeft, atlasTop, atlasRight, atlasBottom, atlasWidth, atlasHeight);
 
 
 
 
 
         
         glyphs.put(Integer.valueOf(unicode), glyph);
       } 
       
       AbstractTexture texture = QClient.mc.method_1531().method_4619(this.atlasIdentifier);
       
       return new MsdfFont(this.name, texture, atlasWidth, atlasHeight, range, lineHeight, ascender, descender, glyphs);
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\fonts\msdf\MsdfFont.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */