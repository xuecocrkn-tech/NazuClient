package shame.nazuna.api.utils.math;
 
 public class HoveringUtils {
   public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
     return (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
   }
 
 
 
 
 
   
   public static boolean isInRegion(int mouseX, int mouseY, int x, int y, int width, int height) {
     return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
   }
 
 
 
 
 
   
   public static boolean isInRegion(double mouseX, double mouseY, float x, float y, float width, float height) {
     return (mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height));
   }
   
   public static boolean isHovering(float x, float y, float width, float height, double mouseX, double mouseY) {
     return (mouseX >= x && mouseY >= y && mouseX < (x + width) && mouseY < (y + height));
   }
 
 
 
 
   
   public static boolean isInRegion(double mouseX, double mouseY, int x, int y, int width, int height) {
     return (mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height));
   }
   
   public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
     return (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\math\HoveringUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */