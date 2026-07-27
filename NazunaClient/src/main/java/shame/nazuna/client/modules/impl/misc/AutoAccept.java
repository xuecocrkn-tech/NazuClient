package shame.nazuna.client.modules.impl.misc;
 
 import java.util.Locale;
 import net.minecraft.class_2596;
 import net.minecraft.class_7439;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 
 public class AutoAccept
   extends Module {
   public static AutoAccept INSTANCE = new AutoAccept();
   
   private final BooleanSetting onlyFriend = new BooleanSetting("Только друзья", false);
   
   public AutoAccept() {
     super("AutoAccept", "Автоматически принимает телепорт", Module.ModuleCategory.MISC);
     addSettings(new Setting[] { (Setting)this.onlyFriend });
   }
   
   @EventLink
   public void onEvent(EventPacket event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (event.getType() != EventPacket.Type.RECEIVE)
       return; 
     class_2596<?> packet = event.getPacket();
     if (packet instanceof class_7439) { class_7439 messagePacket = (class_7439)packet;
       String raw = messagePacket.comp_763().getString().toLowerCase(Locale.ROOT);
       
       if (raw.contains("телепортироваться") || raw.contains("has requested teleport") || raw.contains("просит к вам телепортироваться")) {
         if (this.onlyFriend.isState()) {
           boolean isFriend = false;
           
           if (astra.INSTANCE.friendStorage != null) {
             for (String friend : astra.INSTANCE.friendStorage.getFriends()) {
               if (raw.contains(friend.toLowerCase(Locale.ROOT))) {
                 isFriend = true;
                 
                 break;
               } 
             } 
           }
           if (!isFriend) {
             return;
           }
         } 
         
         mc.field_1724.field_3944.method_45730("tpaccept");
       }  }
   
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\AutoAccept.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */