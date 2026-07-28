package shame.nazuna.client.modules.impl.combat;
 
 import net.minecraft.Packet;
 import net.minecraft.EntityVelocityUpdateS2CPacket;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class NoVelocity
   extends Module {
   public static NoVelocity INSTANCE = new NoVelocity();
   
   private final ModeSetting mode = new ModeSetting("Мод", "Vanilla", new String[] { "Vanilla", "Grim", "Jump Reset" });
   private final BooleanSetting explosions = new BooleanSetting("Взрывы", true);
   
   private boolean needJump;
   private int hurtTicks;
   
   public NoVelocity() {
     super("NoVelocity", "Отключает отдачу от урона", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.explosions });
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.needJump = false;
     this.hurtTicks = 0;
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.needJump = false;
     this.hurtTicks = 0;
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (event.getType() != EventPacket.Type.RECEIVE)
       return; 
     Packet Packet = event.getPacket(); if (Packet instanceof EntityVelocityUpdateS2CPacket) { EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket)Packet;
       if (packet.method_11818() != mc.field_1724.method_5628())
         return; 
       if (this.mode.is("Vanilla")) {
         event.cancel();
       }
       
       if (this.mode.is("Grim")) {
         event.cancel();
         double velY = packet.method_11816() / 8000.0D;
         
         if (mc.field_1724.method_24828() && velY > 0.0D) {
           mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, 0.0D, (mc.field_1724.method_18798()).field_1350);
         } else if (velY > 0.0D) {
           mc.field_1724.method_18800((mc.field_1724.method_18798()).field_1352, 0.0D, (mc.field_1724.method_18798()).field_1350);
         } 
       } 
       
       if (this.mode.is("Jump Reset")) {
         double velY = packet.method_11816() / 8000.0D;
         if (velY > 0.1D) {
           this.needJump = true;
           this.hurtTicks = 0;
         } 
       }  }
 
     
     if (this.explosions.isState() && event.getPacket() instanceof net.minecraft.ExplosionS2CPacket && (
       this.mode.is("Vanilla") || this.mode.is("Grim"))) {
       event.cancel();
     }
   }
 
 
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null)
       return; 
     if (this.mode.is("Jump Reset") && this.needJump) {
       this.hurtTicks++;
       
       if (mc.field_1724.method_24828()) {
         mc.field_1724.method_6043();
         this.needJump = false;
         this.hurtTicks = 0;
       } 
       
       if (this.hurtTicks > 5) {
         this.needJump = false;
         this.hurtTicks = 0;
       } 
     } 
   }
 }

