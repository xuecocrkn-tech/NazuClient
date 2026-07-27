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


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\ModuleStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */