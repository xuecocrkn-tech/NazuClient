package shame.nazuna.api.utils.notification;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Entry
 {
   public final String moduleName;
   public final String categoryIcon;
   public final boolean enabled;
   public final String customText;
   public final long startTime;
   
   public Entry(String moduleName, String categoryIcon, boolean enabled, String customText, long startTime) {
     this.moduleName = moduleName;
     this.categoryIcon = categoryIcon;
     this.enabled = enabled;
     this.customText = customText;
     this.startTime = startTime;
   }
   
   public boolean isCustom() {
     return (this.customText != null && !this.customText.isEmpty());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\notification\NotificationManager$Entry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */