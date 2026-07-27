package shame.nazuna.api.storages.implement.helpertstorages;
 
 
 public class Theme implements QClient {
   private String name;
   public int[] color;
   
   public int[] getColor() { return this.color; }
   
   public Theme(String name, int... color) {
     this.name = name;
     this.color = color;
   }
   
   public int getColor(int index) {
     if (this.name.equals("Rainbow")) {
       return ColorUtils.rainbow(10, index, 0.6F, 1.0F, 1.0F);
     }
     return ColorUtils.gradient(5, index, this.color);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\helpertstorages\Theme.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */