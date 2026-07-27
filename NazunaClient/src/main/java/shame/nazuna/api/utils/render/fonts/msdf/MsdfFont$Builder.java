package shame.nazuna.api.utils.render.fonts.msdf;
 
 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 import java.util.HashMap;
 import net.minecraft.AbstractTexture;
 import net.minecraft.Identifier;
 import shame.nazuna.api.QClient;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Builder
 {
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


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\fonts\msdf\MsdfFont$Builder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */