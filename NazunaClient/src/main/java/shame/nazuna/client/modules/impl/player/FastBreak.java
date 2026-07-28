package shame.nazuna.client.modules.impl.player;
 import net.minecraft.Hand;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.HitResult;
 import net.minecraft.Packet;
 import net.minecraft.BlockState;
 import net.minecraft.PlayerActionC2SPacket;
 import net.minecraft.BlockHitResult;
 import net.minecraft.ClientPlayNetworkHandler;
 import net.minecraft.ClientPlayerInteractionManager;
 import net.minecraft.ClientWorld;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class FastBreak extends Module {
   public static FastBreak INSTANCE = new FastBreak();
   
   private final FloatSetting speed = new FloatSetting("Ускорение", 0.5F, 0.3F, 1.0F, 0.1F);
   
   public FastBreak() {
     super("FastBreak", "Ускоряет ломание блоков", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.speed });
   }
   @EventLink
   public void onUpdate(EventUpdate event) {
     BlockHitResult hit;
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
       return;
     }
     
     HitResult HitResult = mc.field_1765; if (HitResult instanceof BlockHitResult) { hit = (BlockHitResult)HitResult; }
     else
     { return; }
     
     if (!mc.field_1690.field_1886.method_1434()) {
       return;
     }
     
     accelerateClientBreak(mc.field_1761, mc.field_1724, mc.field_1687, hit.method_17777(), hit.method_17780(), this.speed.get(), true);
   }
   
   public float getSpeed() {
     return this.speed.get();
   }
   
   public static int getExtraTicks(float speed) {
     return Math.max(1, Math.round(Math.max(0.3F, speed) / 0.35F));
   }
 
 
 
 
 
 
   
   public static boolean accelerateClientBreak(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, BlockPos pos, Direction side, float speed, boolean swing) {
     if (interactionManager == null || player == null || world == null || pos == null) {
       return false;
     }
     
     BlockState state = world.method_8320(pos);
     if (state == null || state.method_26215()) {
       return false;
     }
     
     Direction breakSide = (side == null) ? Direction.field_11036 : side;
     int extraTicks = getExtraTicks(speed);
     for (int i = 0; i < extraTicks; i++) {
       interactionManager.method_2902(pos, breakSide);
     }
     
     if (swing) {
       player.method_6104(Hand.field_5808);
     }
     
     return true;
   }
 
 
 
 
   
   public static boolean packetBreak(ClientPlayNetworkHandler handler, ClientPlayerEntity player, BlockPos pos, Direction side, boolean swing) {
     if (handler == null || player == null || pos == null) {
       return false;
     }
     
     Direction breakSide = (side == null) ? Direction.field_11036 : side;
     handler.method_52787((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.class_2847.field_12968, pos, breakSide));
     handler.method_52787((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.class_2847.field_12973, pos, breakSide));
     
     if (swing) {
       handler.method_52787((Packet)new HandSwingC2SPacket(Hand.field_5808));
     }
     
     return true;
   }
 }

