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
     NazunaClient.INSTANCE.moduleStorage = new ModuleStorage();
     NazunaClient.INSTANCE.themeStorage = new ThemeStorage();
     NazunaClient.INSTANCE.tpsCalc = new TPSCalc();
     EventInvoker.register(NazunaClient.INSTANCE.tpsCalc);
     NazunaClient.INSTANCE.localizationStorage = new LocalizationStorage();
     NazunaClient.INSTANCE.freeLookStorage = new FreeLookStorage();
     NazunaClient.INSTANCE.rotationStorage = new RotationStorage();
     
     NazunaClient.INSTANCE.friendStorage = new FriendStorage();
     NazunaClient.INSTANCE.macroStorage = new MacroStorage();
     NazunaClient.INSTANCE.staffStorage = new StaffStorage();
     NazunaClient.INSTANCE.waypointStorage = new WaypointStorage();
     NazunaClient.INSTANCE.commandStorage = new CommandStorage();
     NazunaClient.INSTANCE.configStorage = new ConfigStorage();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\InitializeStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */