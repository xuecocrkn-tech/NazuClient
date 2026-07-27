package shame.nazuna.api.storages;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.storages.implement.CommandStorage;
 import shame.nazuna.api.storages.implement.MacroStorage;
 import shame.nazuna.api.storages.implement.ModuleStorage;
 import shame.nazuna.api.storages.implement.StaffStorage;
 import shame.nazuna.api.utils.tps.TPSCalc;
 import shame.nazuna.astra;
 
 public class InitializeStorage implements QClient {
   public void onInitialize() {
     EventInvoker.register(this);
     initStorages();
   }
 
   
   public void initStorages() {
     astra.INSTANCE.moduleStorage = new ModuleStorage();
     astra.INSTANCE.themeStorage = new ThemeStorage();
     astra.INSTANCE.tpsCalc = new TPSCalc();
     EventInvoker.register(astra.INSTANCE.tpsCalc);
     astra.INSTANCE.localizationStorage = new LocalizationStorage();
     astra.INSTANCE.freeLookStorage = new FreeLookStorage();
     astra.INSTANCE.rotationStorage = new RotationStorage();
     
     astra.INSTANCE.friendStorage = new FriendStorage();
     astra.INSTANCE.macroStorage = new MacroStorage();
     astra.INSTANCE.staffStorage = new StaffStorage();
     astra.INSTANCE.waypointStorage = new WaypointStorage();
     astra.INSTANCE.commandStorage = new CommandStorage();
     astra.INSTANCE.configStorage = new ConfigStorage();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\InitializeStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */