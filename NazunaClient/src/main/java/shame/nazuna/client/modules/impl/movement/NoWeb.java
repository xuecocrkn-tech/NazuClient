package shame.nazuna.client.modules.impl.movement;
 import java.util.Iterator;
 import net.minecraft.Blocks;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.Box;
 import net.minecraft.Vec3d;
 import net.minecraft.PlayerActionC2SPacket;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.utils.player.MoveUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class NoWeb extends Module {
   public static NoWeb INSTANCE = new NoWeb();
   public ModeSetting web = new ModeSetting("Мод", "Коллизия", new String[] { "Коллизия", "Обычный", "Тест" });
   
   public NoWeb() {
     super("NoWeb", "Убирает замедление от паутины", Module.ModuleCategory.MOVEMENT);
     addSettings(new Setting[] { (Setting)this.web });
   }
   
   @EventLink
   public void onUpdate(EventUpdate eventUpdate) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (this.web.is("Коллизия")) {
       BlockPos playerPos = mc.field_1724.method_24515();
       
       for (int x = -1; x <= 1; x++) {
         for (int y = 0; y <= 2; y++) {
           for (int z = -1; z <= 1; z++) {
             BlockPos pos = playerPos.method_10069(x, y, z);
             
             if (mc.field_1687.method_8320(pos).method_26204() == Blocks.field_10343) {
               mc.field_1724.field_3944.method_52787((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.class_2847.field_12973, pos, Direction.field_11036));
             }
           } 
         } 
       } 
     } 
 
     
     if (this.web.is("Обычный") && (
       !mc.field_1724.method_5715() || !mc.field_1724.method_24828())) {
 
 
       
       boolean headInWeb = false;
       boolean feetInWeb = false;
       double x;
       for (x = -0.295D; x <= 0.295D; x += 0.05D) {
         
         for (double z = -0.295D; z <= 0.295D; z += 0.05D) {
           double y; for (y = mc.field_1724.method_5751(); y >= 0.0D; ) {
             BlockPos headPos = BlockPos.method_49637(mc.field_1724.method_23317() + x, mc.field_1724.method_23318() + y, mc.field_1724.method_23321() + z);
             if (mc.field_1687.method_8320(headPos).method_26204() != Blocks.field_10343) { y -= 0.1D; continue; }
              headInWeb = true;
           } 
         } 
       } 
 
       
       if (!headInWeb)
       {
         for (x = -0.295D; x <= 0.295D; x += 0.05D) {
           for (double z = -0.295D; z <= 0.295D; ) {
             BlockPos pos = BlockPos.method_49637(mc.field_1724.method_23317() + x, mc.field_1724.method_23318(), mc.field_1724.method_23321() + z);
             if (mc.field_1687.method_8320(pos).method_26204() != Blocks.field_10343) { z += 0.05D; continue; }
              feetInWeb = true;
           } 
         } 
       }
 
       
       BlockPos aboveHeadPos = BlockPos.method_49637(mc.field_1724.method_23317(), mc.field_1724.method_23318() + mc.field_1724.method_5751() + 0.20000000298023224D, mc.field_1724.method_23321());
       if (!headInWeb && !feetInWeb && mc.field_1687.method_8320(aboveHeadPos).method_26204() == Blocks.field_10343) {
         headInWeb = true;
       }
       
       if (headInWeb || feetInWeb) {
         if (mc.field_1690.field_1903.method_1434()) {
           mc.field_1724.method_18800(0.0D, 0.8D, 0.0D);
         } else if (mc.field_1690.field_1832.method_1434()) {
           mc.field_1724.method_18800(0.0D, -0.8D, 0.0D);
         } else {
           mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
         } 
         MoveUtils.setMotion(0.21D);
       } 
     } 
 
     
     if (this.web.is("Тест") && 
       mc.field_1724 != null) {
       boolean cobweb = false;
       Box box = mc.field_1724.method_5829();
       Iterator<BlockPos> it = BlockPos.method_10094(MathHelper.method_15357(box.field_1323), MathHelper.method_15357(box.field_1322), MathHelper.method_15357(box.field_1321), MathHelper.method_15357(box.field_1320), MathHelper.method_15357(box.field_1325), MathHelper.method_15357(box.field_1324)).iterator();
       
       while (it.hasNext()) {
         BlockPos pos = it.next();
         if (mc.field_1687.method_8320(pos).method_27852(Blocks.field_10343)) {
           cobweb = true;
         }
       } 
       
       if (cobweb) {
         Vec3d velocity = mc.field_1724.method_18798();
         float yaw = mc.field_1724.method_36454();
         double forward = 0.0D;
         double strafe = 0.0D;
         if (mc.field_1724.field_3913.field_54155.comp_3159()) {
           forward++;
         }
         
         if (mc.field_1724.field_3913.field_54155.comp_3160()) {
           forward--;
         }
         
         if (mc.field_1724.field_3913.field_54155.comp_3161()) {
           strafe++;
         }
         
         if (mc.field_1724.field_3913.field_54155.comp_3162()) {
           strafe--;
         }
         
         if (forward != 0.0D || strafe != 0.0D) {
           if (forward != 0.0D) {
             if (strafe > 0.0D) {
               yaw += ((forward > 0.0D) ? -45 : 45);
             } else if (strafe < 0.0D) {
               yaw += ((forward > 0.0D) ? 45 : -45);
             } 
             
             strafe = 0.0D;
             if (forward > 0.0D) {
               forward = 1.0D;
             } else {
               forward = -1.0D;
             } 
           } 
           
           double movementYaw = Math.toDegrees(Math.atan2(strafe, forward)) + yaw;
           yaw = (float)((movementYaw % 360.0D + 360.0D) % 360.0D);
         } 
         
         float result = 0.63F;
         if ((yaw < 313.0F || yaw > 317.0F) && (yaw < 223.0F || yaw > 227.0F) && (yaw < 133.0F || yaw > 137.0F) && (yaw < 43.0F || yaw > 47.0F)) {
           if ((yaw < 311.0F || yaw > 319.0F) && (yaw < 221.0F || yaw > 229.0F) && (yaw < 131.0F || yaw > 139.0F) && (yaw < 41.0F || yaw > 49.0F)) {
             if ((yaw < 310.8F || yaw > 320.8F) && (yaw < 220.8F || yaw > 230.8F) && (yaw < 130.8F || yaw > 140.8F) && (yaw < 40.8F || yaw > 50.8F)) {
               if ((yaw < 308.7F || yaw > 322.7F) && (yaw < 218.7F || yaw > 232.7F) && (yaw < 128.7F || yaw > 142.7F) && (yaw < 38.7F || yaw > 52.7F)) {
                 if ((yaw < 306.5F || yaw > 324.5F) && (yaw < 216.5F || yaw > 234.5F) && (yaw < 126.5F || yaw > 144.5F) && (yaw < 36.5F || yaw > 54.5F)) {
                   if ((yaw >= 304.0F && yaw <= 327.0F) || (yaw >= 214.0F && yaw <= 237.0F) || (yaw >= 124.0F && yaw <= 147.0F) || (yaw >= 34.0F && yaw <= 57.0F)) {
                     result = 0.75F;
                   }
                 } else {
                   result = 0.79F;
                 } 
               } else {
                 result = 0.81F;
               } 
             } else {
               result = 0.83F;
             } 
           } else {
             result = 0.85F;
           } 
         } else {
           result = 0.88F;
         } 
         
         if (!mc.field_1690.field_1903.method_1434()) {
           if (mc.field_1690.field_1832.method_1434()) {
             mc.field_1724.method_18800(velocity.field_1352, -3.6D, velocity.field_1350);
           } else {
             mc.field_1724.method_18800(velocity.field_1352, 0.0D, velocity.field_1350);
           } 
         } else {
           mc.field_1724.method_18800(velocity.field_1352, (forward == 0.0D && strafe == 0.0D) ? 1.4D : 1.2D, velocity.field_1350);
         } 
         
         MoveUtils.setVelocity(result);
       } 
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\movement\NoWeb.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */