package shame.nazuna.client.modules.settings;
 
 import java.awt.Color;
 import java.util.function.Supplier;
 import shame.nazuna.api.QClient;
 import shame.nazuna.astra;
 
 public abstract class Setting
   implements QClient {
   private final String name;
   
   public String name() {
     return this.name;
   }
   
   public Setting(String name) {
     this.name = name;
   }
   
   public Boolean visible() {
     return this.visible.get();
   }
   
   public String displayName() {
     return (NazunaClient.INSTANCE.localizationStorage == null) ? this.name : NazunaClient.INSTANCE.localizationStorage.translate(this.name);
   }
 }

