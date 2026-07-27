package shame.nazuna.client.modules.impl.movement;
 import net.minecraft.Hand;
 import net.minecraft.Packet;
 import net.minecraft.PlayerInteractItemC2SPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventSlowWalking;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.player.ViaProtocolUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class NoSlow extends Module {
   public static NoSlow INSTANCE = new NoSlow();
   
   private final ModeSetting mode = new ModeSetting("Мод", "Grim Old", new String[] { "Grim Old", "Grim Last" });
   private final BooleanSetting sprint = new BooleanSetting("Спринт", true);
   
   public NoSlow() {
     super("NoSlow", "Убирает замедление во время еды", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.sprint });
   }
   
   @EventLink
   public void onSlowDown(EventSlowWalking event) {
     if (mc.field_1724 == null || !mc.field_1724.method_6115())
       return; 
     if (this.mode.is("Grim Last") && 
       mc.field_1724.method_6048() % 2 == 0) {
       event.setCancelled(true);
     }
 
     
     if (this.mode.is("Grim Old")) {
       Hand activeHand = mc.field_1724.method_6058();
       boolean legacyProtocol = ViaProtocolUtils.isTargetProtocolBelowOneNineteen();
       
       if (this.sprint.isState()) {
         mc.field_1724.method_5728((((ModuleClass.sprint
             .isEnable() && Sprint.isSprinting()) || mc.field_1690.field_1867.method_1434()) && mc.field_1724.field_3913.field_3905 > 0.0F && (!legacyProtocol || (!mc.field_1724.field_5976 && !mc.field_1724.field_34927)) && 
 
             
             !mc.field_1724.method_6128()));
       }
 
       
       Hand otherHand = (activeHand == Hand.field_5808) ? Hand.field_5810 : Hand.field_5808;
       mc.method_1562().method_52787((Packet)new PlayerInteractItemC2SPacket(otherHand, 0, mc.field_1724.method_36454(), mc.field_1724.method_36455()));
       
       event.setCancelled(true);
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\NoSlow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */