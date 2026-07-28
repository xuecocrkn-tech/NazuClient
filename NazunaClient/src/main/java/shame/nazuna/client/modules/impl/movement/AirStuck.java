package shame.nazuna.client.modules.impl.movement;
 import net.minecraft.EquipmentSlot;
 import net.minecraft.PlayerEntity;
 import net.minecraft.SlotActionType;
 import net.minecraft.ItemStack;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.CloseHandledScreenC2SPacket;
 import net.minecraft.PlayerMoveC2SPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventMove;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.network.NetworkUtils;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AirStuck extends Module {
   public static AirStuck INSTANCE = new AirStuck();
   
   private final ModeSetting mode = new ModeSetting("Мод", "Обычный", new String[] { "Обычный", "LonyGrief" });
   private final BooleanSetting cancelPackets = new BooleanSetting("Отменять пакеты", true);
   private final BooleanSetting swapElytra = new BooleanSetting("Свапать элитру", true);
   
   private Vec3d freezePosition = Vec3d.field_1353;
   private boolean frozen = false;
   
   public AirStuck() {
     super("AirStuck", "Зависает в воздухе", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.cancelPackets, (Setting)this.swapElytra });
   }
 
   
   public void onEnable() {
     this.frozen = false;
     
     if (mc.field_1724 != null && this.swapElytra.isState()) {
       swapChestEquipment();
     }
     
     if (mc.field_1724 != null && this.mode.is("Обычный")) {
       this.freezePosition = mc.field_1724.method_19538();
       this.frozen = true;
     } 
     
     super.onEnable();
   }
 
   
   public void onDisable() {
     this.frozen = false;
     super.onDisable();
   }
   
   private void swapChestEquipment() {
     ItemStack chestStack = mc.field_1724.method_6118(EquipmentSlot.field_6174);
     
     if (!chestStack.method_31574(Items.field_8833)) {
       return;
     }
     
     int chestplateSlot = InventoryUtils.findBestChestplateSlot();
     if (chestplateSlot != -1) {
       doSwap(chestplateSlot);
     }
   }
   
   private void doSwap(int slot) {
     if (slot >= 0 && slot < 9) {
       mc.field_1761.method_2906(0, 6, slot, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
     } else {
       mc.field_1761.method_2906(0, slot, 0, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
       mc.field_1761.method_2906(0, 6, 0, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
       mc.field_1761.method_2906(0, slot, 0, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
     } 
     
     mc.field_1724.field_3944.method_52787((Packet)new CloseHandledScreenC2SPacket(0));
   }
   
   @EventLink
   public void onMove(EventMove e) {
     if (mc.field_1724 == null)
       return; 
     if (this.mode.is("LonyGrief") && !this.frozen && 
       mc.field_1724.field_6017 > 0.0F && (mc.field_1724.method_18798()).field_1351 < 0.0D) {
       this.freezePosition = mc.field_1724.method_19538();
       this.frozen = true;
     } 
 
     
     if (this.frozen) {
       e.setMovePos(Vec3d.field_1353);
       mc.field_1724.method_5814(this.freezePosition.field_1352, this.freezePosition.field_1351, this.freezePosition.field_1350);
       mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
     } 
   }
   
   @EventLink
   public void onPacket(EventPacket e) {
     if (!this.frozen || e.getType() != EventPacket.Type.SEND)
       return; 
     Packet Packet = e.getPacket(); if (Packet instanceof PlayerMoveC2SPacket) { PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)Packet;
       if (this.cancelPackets.isState()) {
         e.cancel();
       } else {
         e.cancel();
         NetworkUtils.sendSilentPacket((Packet)createFrozenPacket(packet));
       }  }
   
   }
   
   private PlayerMoveC2SPacket createFrozenPacket(PlayerMoveC2SPacket packet) {
     boolean onGround = packet.method_12273();
     boolean horizontalCollision = packet.method_61225();
     
     if (packet.method_36171() && packet.method_36172()) {
       return (PlayerMoveC2SPacket)new PlayerMoveC2SPacket.class_2830(this.freezePosition.field_1352, this.freezePosition.field_1351, this.freezePosition.field_1350, packet
 
 
           
           .method_12271(mc.field_1724.method_36454()), packet
           .method_12270(mc.field_1724.method_36455()), onGround, horizontalCollision);
     }
 
 
 
     
     if (packet.method_36171()) {
       return (PlayerMoveC2SPacket)new PlayerMoveC2SPacket.class_2829(this.freezePosition.field_1352, this.freezePosition.field_1351, this.freezePosition.field_1350, onGround, horizontalCollision);
     }
 
 
 
 
 
 
     
     if (packet.method_36172()) {
       return (PlayerMoveC2SPacket)new PlayerMoveC2SPacket.class_2831(packet
           .method_12271(mc.field_1724.method_36454()), packet
           .method_12270(mc.field_1724.method_36455()), onGround, horizontalCollision);
     }
 
 
 
     
     return (PlayerMoveC2SPacket)new PlayerMoveC2SPacket.class_5911(onGround, horizontalCollision);
   }
 }

