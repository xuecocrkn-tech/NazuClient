package shame.nazuna.api.utils.render.font;
 
 import java.util.HashMap;
 import java.util.Map;
 import shame.nazuna.api.utils.color.ColorUtils;
 
 public class ReplaceSymbols
 {
   private static final Map<Integer, String> REPLACEMENTS = new HashMap<>();
   private static final Map<Integer, Integer> RANK_COLORS = new HashMap<>();
   
   private static final int[] RANKS = new int[] { 42240, 42244, 42248, 42258, 42262, 42272, 42276, 42280, 42336, 42290, 42294, 42308, 42326, 42312, 42304, 42322, 42249, 42259, 42263, 42273, 42277, 42281, 42291, 42295, 42241, 42245, 42313, 4144, 4138, 4132, 4134, 4140, 4139, 4151, 4130, 4148, 4141, 4152, 4133, 4131, 4149, 4150, 4137, 4129, 4145, 4143, 4146, 4135, 4153, 4126 };
 
 
 
 
 
 
 
   
   static {
     REPLACEMENTS.put(Integer.valueOf(9889), "");
     REPLACEMENTS.put(Integer.valueOf(9733), "");
     
     REPLACEMENTS.put(Integer.valueOf(42240), "PLAYER");
     REPLACEMENTS.put(Integer.valueOf(42244), "HERO");
     REPLACEMENTS.put(Integer.valueOf(42248), "TITAN");
     REPLACEMENTS.put(Integer.valueOf(42258), "AVENGER");
     REPLACEMENTS.put(Integer.valueOf(42262), "OVERLORD");
     REPLACEMENTS.put(Integer.valueOf(42272), "MAGISTER");
     REPLACEMENTS.put(Integer.valueOf(42276), "IMPERATOR");
     REPLACEMENTS.put(Integer.valueOf(42280), "DRAGON");
     REPLACEMENTS.put(Integer.valueOf(42336), "D.HELPER");
     REPLACEMENTS.put(Integer.valueOf(42290), "BULL");
     REPLACEMENTS.put(Integer.valueOf(42294), "TIGER");
     REPLACEMENTS.put(Integer.valueOf(42308), "VAMPIRE");
     REPLACEMENTS.put(Integer.valueOf(42326), "BUNNY");
     REPLACEMENTS.put(Integer.valueOf(42312), "COBRA");
     REPLACEMENTS.put(Integer.valueOf(42304), "HYDRA");
     REPLACEMENTS.put(Integer.valueOf(42322), "RABBIT");
     REPLACEMENTS.put(Integer.valueOf(42249), "HELPER");
     REPLACEMENTS.put(Integer.valueOf(42259), "ML.MODER");
     REPLACEMENTS.put(Integer.valueOf(42263), "MODER");
     REPLACEMENTS.put(Integer.valueOf(42273), "MODER+");
     REPLACEMENTS.put(Integer.valueOf(42277), "ST.MODER");
     REPLACEMENTS.put(Integer.valueOf(42281), "GL.MODER");
     REPLACEMENTS.put(Integer.valueOf(42291), "ML.ADMIN");
     REPLACEMENTS.put(Integer.valueOf(42295), "ADMIN");
     REPLACEMENTS.put(Integer.valueOf(42241), "MEDIA");
     REPLACEMENTS.put(Integer.valueOf(42245), "YT");
     REPLACEMENTS.put(Integer.valueOf(42305), "GOD");
     REPLACEMENTS.put(Integer.valueOf(4144), "HERO");
     REPLACEMENTS.put(Integer.valueOf(4138), "TITAN");
     REPLACEMENTS.put(Integer.valueOf(4132), "PRINCE");
     REPLACEMENTS.put(Integer.valueOf(4134), "PHOENIX");
     REPLACEMENTS.put(Integer.valueOf(4140), "OVERLORD");
     REPLACEMENTS.put(Integer.valueOf(4139), "GUARDIAN");
     REPLACEMENTS.put(Integer.valueOf(4151), "KRATOS");
     REPLACEMENTS.put(Integer.valueOf(4130), "PHANTOM");
     REPLACEMENTS.put(Integer.valueOf(4148), "CUSTOM");
     REPLACEMENTS.put(Integer.valueOf(4141), "WINTER");
     REPLACEMENTS.put(Integer.valueOf(4152), "SAKURA");
     REPLACEMENTS.put(Integer.valueOf(4133), "SUMMER");
     REPLACEMENTS.put(Integer.valueOf(4131), "HALLOWEEN");
     REPLACEMENTS.put(Integer.valueOf(4149), "TIKTOK");
     REPLACEMENTS.put(Integer.valueOf(4150), "TIKTOK+");
     REPLACEMENTS.put(Integer.valueOf(4137), "MEDIA");
     REPLACEMENTS.put(Integer.valueOf(4129), "YOUTUBE");
     REPLACEMENTS.put(Integer.valueOf(4145), "HELPER");
     REPLACEMENTS.put(Integer.valueOf(4143), "ML.ADMIN");
     REPLACEMENTS.put(Integer.valueOf(4146), "MODER");
     REPLACEMENTS.put(Integer.valueOf(4135), "CURATOR");
     REPLACEMENTS.put(Integer.valueOf(4153), "SPECTATOR");
     REPLACEMENTS.put(Integer.valueOf(4126), "DEVELOPER");
     
     REPLACEMENTS.put(Integer.valueOf(7424), "A");
     REPLACEMENTS.put(Integer.valueOf(665), "B");
     REPLACEMENTS.put(Integer.valueOf(7428), "C");
     REPLACEMENTS.put(Integer.valueOf(7429), "D");
     REPLACEMENTS.put(Integer.valueOf(7431), "E");
     REPLACEMENTS.put(Integer.valueOf(42800), "F");
     REPLACEMENTS.put(Integer.valueOf(610), "G");
     REPLACEMENTS.put(Integer.valueOf(668), "H");
     REPLACEMENTS.put(Integer.valueOf(618), "I");
     REPLACEMENTS.put(Integer.valueOf(7434), "J");
     REPLACEMENTS.put(Integer.valueOf(7435), "K");
     REPLACEMENTS.put(Integer.valueOf(671), "L");
     REPLACEMENTS.put(Integer.valueOf(7437), "M");
     REPLACEMENTS.put(Integer.valueOf(628), "N");
     REPLACEMENTS.put(Integer.valueOf(7439), "O");
     REPLACEMENTS.put(Integer.valueOf(7448), "P");
     REPLACEMENTS.put(Integer.valueOf(491), "Q");
     REPLACEMENTS.put(Integer.valueOf(640), "R");
     REPLACEMENTS.put(Integer.valueOf(7451), "T");
     REPLACEMENTS.put(Integer.valueOf(7452), "U");
     REPLACEMENTS.put(Integer.valueOf(42801), "S");
     REPLACEMENTS.put(Integer.valueOf(7456), "V");
     REPLACEMENTS.put(Integer.valueOf(7457), "W");
     REPLACEMENTS.put(Integer.valueOf(7521), "X");
     REPLACEMENTS.put(Integer.valueOf(655), "Y");
     REPLACEMENTS.put(Integer.valueOf(7458), "Z");
     
     RANK_COLORS.put(Integer.valueOf(42240), Integer.valueOf(ColorUtils.rgb(141, 143, 141)));
     RANK_COLORS.put(Integer.valueOf(42244), Integer.valueOf(ColorUtils.rgb(100, 113, 251)));
     RANK_COLORS.put(Integer.valueOf(42248), Integer.valueOf(ColorUtils.rgb(245, 220, 29)));
     RANK_COLORS.put(Integer.valueOf(42258), Integer.valueOf(ColorUtils.rgb(79, 201, 83)));
     RANK_COLORS.put(Integer.valueOf(42262), Integer.valueOf(ColorUtils.rgb(85, 255, 255)));
     RANK_COLORS.put(Integer.valueOf(42272), Integer.valueOf(ColorUtils.rgb(224, 138, 52)));
     RANK_COLORS.put(Integer.valueOf(42276), Integer.valueOf(ColorUtils.rgb(202, 60, 60)));
     RANK_COLORS.put(Integer.valueOf(42280), Integer.valueOf(ColorUtils.rgb(245, 51, 238)));
     RANK_COLORS.put(Integer.valueOf(42336), Integer.valueOf(ColorUtils.rgb(214, 200, 42)));
     RANK_COLORS.put(Integer.valueOf(42290), Integer.valueOf(ColorUtils.rgb(121, 81, 202)));
     RANK_COLORS.put(Integer.valueOf(42294), Integer.valueOf(ColorUtils.rgb(202, 130, 60)));
     RANK_COLORS.put(Integer.valueOf(42308), Integer.valueOf(ColorUtils.rgb(202, 60, 60)));
     RANK_COLORS.put(Integer.valueOf(42326), Integer.valueOf(ColorUtils.rgb(68, 65, 66)));
     RANK_COLORS.put(Integer.valueOf(42312), Integer.valueOf(ColorUtils.rgb(127, 214, 86)));
     RANK_COLORS.put(Integer.valueOf(42304), Integer.valueOf(ColorUtils.rgb(92, 120, 7)));
     RANK_COLORS.put(Integer.valueOf(42322), Integer.valueOf(ColorUtils.rgb(230, 232, 230)));
     RANK_COLORS.put(Integer.valueOf(42249), Integer.valueOf(ColorUtils.rgb(214, 200, 42)));
     RANK_COLORS.put(Integer.valueOf(42259), Integer.valueOf(ColorUtils.rgb(100, 113, 251)));
     RANK_COLORS.put(Integer.valueOf(42263), Integer.valueOf(ColorUtils.rgb(100, 113, 251)));
     RANK_COLORS.put(Integer.valueOf(42273), Integer.valueOf(ColorUtils.rgb(121, 81, 202)));
     RANK_COLORS.put(Integer.valueOf(42277), Integer.valueOf(ColorUtils.rgb(100, 113, 251)));
     RANK_COLORS.put(Integer.valueOf(42281), Integer.valueOf(ColorUtils.rgb(121, 81, 202)));
     RANK_COLORS.put(Integer.valueOf(42291), Integer.valueOf(ColorUtils.rgb(64, 151, 214)));
     RANK_COLORS.put(Integer.valueOf(42295), Integer.valueOf(ColorUtils.rgb(202, 60, 60)));
     RANK_COLORS.put(Integer.valueOf(42241), Integer.valueOf(ColorUtils.rgb(121, 81, 202)));
     RANK_COLORS.put(Integer.valueOf(42245), Integer.valueOf(ColorUtils.rgb(255, 255, 255)));
     RANK_COLORS.put(Integer.valueOf(42305), Integer.valueOf(ColorUtils.rgb(245, 198, 29)));
     RANK_COLORS.put(Integer.valueOf(42313), Integer.valueOf(ColorUtils.rgb(202, 130, 60)));
     RANK_COLORS.put(Integer.valueOf(4144), Integer.valueOf(ColorUtils.rgb(13, 176, 209)));
     RANK_COLORS.put(Integer.valueOf(4138), Integer.valueOf(ColorUtils.rgb(21, 232, 24)));
     RANK_COLORS.put(Integer.valueOf(4132), Integer.valueOf(ColorUtils.rgb(232, 169, 21)));
     RANK_COLORS.put(Integer.valueOf(4134), Integer.valueOf(ColorUtils.rgb(237, 215, 19)));
     RANK_COLORS.put(Integer.valueOf(4140), Integer.valueOf(ColorUtils.rgb(64, 163, 152)));
     RANK_COLORS.put(Integer.valueOf(4139), Integer.valueOf(ColorUtils.rgb(86, 196, 99)));
     RANK_COLORS.put(Integer.valueOf(4151), Integer.valueOf(ColorUtils.rgb(147, 46, 230)));
     RANK_COLORS.put(Integer.valueOf(4130), Integer.valueOf(ColorUtils.rgb(230, 46, 46)));
     RANK_COLORS.put(Integer.valueOf(4148), Integer.valueOf(ColorUtils.rgb(16, 35, 179)));
     RANK_COLORS.put(Integer.valueOf(4141), Integer.valueOf(ColorUtils.rgb(55, 154, 184)));
     RANK_COLORS.put(Integer.valueOf(4152), Integer.valueOf(ColorUtils.rgb(184, 39, 159)));
     RANK_COLORS.put(Integer.valueOf(4133), Integer.valueOf(ColorUtils.rgb(255, 182, 56)));
     RANK_COLORS.put(Integer.valueOf(4131), Integer.valueOf(ColorUtils.rgb(232, 60, 30)));
     RANK_COLORS.put(Integer.valueOf(4149), Integer.valueOf(ColorUtils.rgb(0, 0, 0)));
     RANK_COLORS.put(Integer.valueOf(4150), Integer.valueOf(ColorUtils.rgb(0, 0, 0)));
     RANK_COLORS.put(Integer.valueOf(4137), Integer.valueOf(ColorUtils.rgb(37, 232, 30)));
     RANK_COLORS.put(Integer.valueOf(4129), Integer.valueOf(ColorUtils.rgb(232, 30, 30)));
     RANK_COLORS.put(Integer.valueOf(4145), Integer.valueOf(ColorUtils.rgb(30, 134, 232)));
     RANK_COLORS.put(Integer.valueOf(4143), Integer.valueOf(ColorUtils.rgb(89, 167, 227)));
     RANK_COLORS.put(Integer.valueOf(4146), Integer.valueOf(ColorUtils.rgb(62, 137, 194)));
     RANK_COLORS.put(Integer.valueOf(4135), Integer.valueOf(ColorUtils.rgb(56, 235, 74)));
     RANK_COLORS.put(Integer.valueOf(4153), Integer.valueOf(ColorUtils.rgb(173, 184, 174)));
     RANK_COLORS.put(Integer.valueOf(4126), Integer.valueOf(ColorUtils.rgb(255, 0, 25)));
   }
 
   
   public static String replaceCodePoint(int codePoint) {
     return REPLACEMENTS.get(Integer.valueOf(codePoint));
   }
   
   public static int getGradientColorForReplacement(int codePoint, int charIndex, int totalChars, float alpha, int currentColor) {
     if (isRank(codePoint)) {
       Integer baseColor = RANK_COLORS.get(Integer.valueOf(codePoint));
       if (baseColor == null) {
         return withOpacity(currentColor, alpha);
       }
       int endColor = ColorUtils.darken(baseColor.intValue(), 0.8F);
       float ratio = (totalChars <= 1) ? 1.0F : (charIndex / (totalChars - 1));
       int interpolatedColor = ColorUtils.interpolateColor(endColor, baseColor.intValue(), ratio);
       return withOpacity(interpolatedColor, alpha);
     } 
     return withOpacity(currentColor, alpha);
   }
   
   private static boolean isRank(int codePoint) {
     for (int rank : RANKS) {
       if (rank == codePoint) return true; 
     } 
     return false;
   }
   
   private static int withOpacity(int color, float alpha) {
     int a = Math.max(0, Math.min(255, (int)(alpha * 255.0F)));
     return ColorUtils.setAlphaColor(color, a);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\render\font\ReplaceSymbols.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */