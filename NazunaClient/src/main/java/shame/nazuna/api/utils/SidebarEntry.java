package shame.nazuna.api.utils;
 
 import net.minecraft.Text;
 
 public class SidebarEntry
 {
   public final Text name;
   public final Text score;
   public final int scoreWidth;
   
   public SidebarEntry(Text name, Text score, int scoreWidth) {
     this.name = name;
     this.score = score;
     this.scoreWidth = scoreWidth;
   }
   
   public Text name() {
     return this.name;
   }
   
   public Text score() {
     return this.score;
   }
   
   public int scoreWidth() {
     return this.scoreWidth;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\SidebarEntry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */