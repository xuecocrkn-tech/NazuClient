package shame.nazuna.client.modules.impl.player;
 
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class ItemScroller extends Module {
   public static ItemScroller INSTANCE = new ItemScroller();
   
   public final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 200.0F, 1.0F);
   
   private long lastQuickMoveAt;
   
   public ItemScroller() {
     super("ItemScroller", "Убирает задержку перемещения предметов", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.delay });
   }
   
   public boolean canQuickMove() {
     long now = System.currentTimeMillis();
     if (now - this.lastQuickMoveAt < (long)this.delay.get()) {
       return false;
     }
     
     this.lastQuickMoveAt = now;
     return true;
   }
   
   public void resetTimer() {
     this.lastQuickMoveAt = 0L;
   }
 
   
   public void onDisable() {
     resetTimer();
     super.onDisable();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\ItemScroller.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */