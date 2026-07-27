package shame.nazuna.api.storages.implement;
 
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Packet;
 import net.minecraft.EntityStatusS2CPacket;
 import net.minecraft.PlayerMoveC2SPacket;
 import net.minecraft.ClientCommandC2SPacket;
 import net.minecraft.UpdateSelectedSlotC2SPacket;
 import shame.nazuna.api.events.implement.EventPacket;
 
 public class ServerStorage implements QClient {
   private int serverSlot;
   private float serverYaw;
   private float serverPitch;
   private float fallDistance;
   private double serverX;
   
   
   public void ServerManager() {
     EventInvoker.register(this);
   }
   
   @EventLink
   public void onTick(EventTickPre e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     double y = mc.field_1724.field_6036 - mc.field_1724.method_23318();
     if (mc.field_1724.method_24828()) { this.fallDistance = 0.0F; }
     else if (y > 0.0D) { this.fallDistance += (float)y; }
   
   }
   @EventLink
   public void onPacketSend(EventPacket e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     Packet Packet = e.getPacket(); if (Packet instanceof PlayerMoveC2SPacket) { PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)Packet;
       if (packet.method_36171()) {
         this.serverX = packet.method_12269(mc.field_1724.method_23317());
         this.serverY = packet.method_12268(mc.field_1724.method_23318());
         this.serverZ = packet.method_12274(mc.field_1724.method_23321());
       } 
       
       if (packet.method_36172()) {
         this.serverYaw = packet.method_12271(mc.field_1724.method_36454());
         this.serverPitch = packet.method_12270(mc.field_1724.method_36455());
       } 
       
       this.serverOnGround = packet.method_12273();
       this.serverHorizontalCollision = packet.method_61225(); }
 
     
     Packet = e.getPacket(); if (Packet instanceof UpdateSelectedSlotC2SPacket) { UpdateSelectedSlotC2SPacket packet = (UpdateSelectedSlotC2SPacket)Packet; this.serverSlot = packet.method_12442(); }
     
     Packet = e.getPacket(); if (Packet instanceof ClientCommandC2SPacket) { ClientCommandC2SPacket packet = (ClientCommandC2SPacket)Packet;
       switch (packet.method_12365()) {
         case field_12981:
           e.setCancelled(this.serverSprinting);
           if (!e.isCancelled()) {
             this.serverSprinting = true;
           }
           break;
         case field_12985:
           e.setCancelled(!this.serverSprinting);
           if (!e.isCancelled())
             this.serverSprinting = false; 
           break;
         case field_12979:
           this.serverSneaking = true; break;
         case field_12984: this.serverSneaking = false;
           break;
       }  }
   
   }
   @EventLink
   public void onPacketReceive(EventPacket e) throws InvocationTargetException, IllegalAccessException, InstantiationException {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     Packet Packet = e.getPacket(); if (Packet instanceof EntityStatusS2CPacket) { EntityStatusS2CPacket packet = (EntityStatusS2CPacket)Packet; if (packet.method_11470() == 35) {
         PlayerEntity player; Entity Entity = packet.method_11469((World)mc.field_1687); if (Entity instanceof PlayerEntity) { player = (PlayerEntity)Entity; } else { return; }
          EventInvoker.invoke((Event)new EventPopTotem(player));
       }  }
   
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\ServerStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */