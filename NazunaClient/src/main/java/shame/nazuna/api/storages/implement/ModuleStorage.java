package shame.nazuna.api.storages.implement;
 
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 
 
 
 
 public class ModuleStorage
   implements QClient
 {
   public ModuleStorage() {
     initModules();
   }
   
   private void initModules() {
     ModuleClass.INSTANCE.initialize();
   }
 }

