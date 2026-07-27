package shame.nazuna.client.modules.impl.movement;
 
 import it.unimi.dsi.fastutil.objects.ObjectArrayList;
 import it.unimi.dsi.fastutil.objects.ObjectListIterator;
 import net.minecraft.Packet;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class PlayerFakeLags
   extends Module {
   public void setReleasing(boolean releasing) {
     this.releasing = releasing;
   }
 
   
   public static PlayerFakeLags INSTANCE = new PlayerFakeLags();
   
   
   public boolean isReleasing() { return this.releasing; }
    public boolean releasing = false;
   public PlayerFakeLags() {
     super("PlayerFakeLags", "Фейковые лаги", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.delay, (Setting)this.onlyMovement });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.packets.clear();
     this.timer.reset();
     this.releasing = false;
   }
   
   @EventLink
   void onEvent(EventUpdate ignored) {
     if (mc.field_1724 == null)
       return; 
     if (this.mode.is("Pulse") && this.timer.finished(this.delay.getValue().longValue())) {
       releasePackets();
       this.timer.reset();
     } 
   }
 
   
   @EventLink
   void onEvent(EventPacket event) {
     if (mc.field_1724 == null || this.releasing)
       return; 
     if (event.getType() == EventPacket.Type.SEND) {
       if (this.onlyMovement.isState()) {
         if (event.getPacket() instanceof net.minecraft.PlayerMoveC2SPacket) {
           event.cancel();
           this.packets.add(event.getPacket());
         } 
       } else {
         event.cancel();
         this.packets.add(event.getPacket());
       } 
     }
   }
   
   private void releasePackets() {
     if (this.packets.isEmpty())
       return; 
     this.releasing = true;
     for (ObjectListIterator<Packet> objectListIterator = this.packets.iterator(); objectListIterator.hasNext(); ) { Packet<?> packet = objectListIterator.next();
       mc.field_1724.field_3944.method_52787(packet); }
     
     this.packets.clear();
     this.releasing = false;
   }
 
   
   public void onDisable() {
     super.onDisable();
     releasePackets();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\PlayerFakeLags.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */