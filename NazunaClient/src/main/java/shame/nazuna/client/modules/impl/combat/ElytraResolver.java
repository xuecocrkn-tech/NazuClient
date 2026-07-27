package shame.nazuna.client.modules.impl.combat;
 
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.class_1297;
 import net.minecraft.class_1802;
 import net.minecraft.class_239;
 import net.minecraft.class_243;
 import net.minecraft.class_3959;
 import net.minecraft.class_3965;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class ElytraResolver extends Module {
   public static ElytraResolver INSTANCE = new ElytraResolver();
   
   private final FloatSetting distance = new FloatSetting("Дистанция отлета", 6.0F, 4.0F, 8.0F, 0.1F);
   private final BooleanSetting autoFirework = new BooleanSetting("Авто-Фейерверк", true);
   
   private static final float MIN_HEIGHT = 4.0F;
   
   private boolean escaping;
   private class_243 escapePos;
   private long escapeStartTime;
   private int returnFireworkTicks = -1;
   private class_243 lastEscapeDirection;
   
   public ElytraResolver() {
     super("ElytraResolver", "Отлет на элитрах", Module.ModuleCategory.COMBAT);
     addSettings(new Setting[] { (Setting)this.distance, (Setting)this.autoFirework });
     INSTANCE = this;
   }
 
   
   public void onDisable() {
     super.onDisable();
     this.escaping = false;
     this.escapePos = null;
     this.returnFireworkTicks = -1;
     this.lastEscapeDirection = null;
   }
   
   public void onAuraAttack() {
     if (!isEnable() || mc.field_1724 == null || !mc.field_1724.method_6128())
       return; 
     class_243 bestPos = calculateSmartEscape(mc.field_1724.method_19538(), this.distance.get());
     if (bestPos != null) {
       this.escapePos = bestPos;
       this.escaping = true;
       this.escapeStartTime = System.currentTimeMillis();
       
       if (this.autoFirework.isState()) {
         useFirework();
       }
     } 
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null || !mc.field_1724.method_6128()) {
       this.escaping = false;
       this.returnFireworkTicks = -1;
       
       return;
     } 
     if (this.returnFireworkTicks > 0) {
       this.returnFireworkTicks--;
     } else if (this.returnFireworkTicks == 0) {
       if (this.autoFirework.isState()) {
         useFirework();
       }
       this.returnFireworkTicks = -1;
     } 
     
     if (this.escaping && this.escapePos != null) {
       double dist = mc.field_1724.method_19538().method_1022(this.escapePos);
       if (dist < 2.0D || System.currentTimeMillis() - this.escapeStartTime > 1000L) {
         this.escaping = false;
         if (this.autoFirework.isState()) {
           this.returnFireworkTicks = 2;
         }
       } 
     } 
   }
   
   public boolean isEscaping() {
     return (isEnable() && this.escaping && this.escapePos != null && mc.field_1724 != null && mc.field_1724.method_6128());
   }
   
   public class_243 getEscapePos() {
     return this.escapePos;
   }
   
   private class_243 calculateSmartEscape(class_243 pPos, float d) {
     class_243 playerLook = mc.field_1724.method_5720();
     class_243 playerVelocity = mc.field_1724.method_18798();
     
     class_243[] directions = generateSmartDirections(playerLook, playerVelocity);
     List<EscapePoint> validPoints = new ArrayList<>();
     
     for (class_243 dir : directions) {
       class_243 target = pPos.method_1019(dir.method_1021(d));
       
       if (target.field_1351 < pPos.field_1351 + 4.0D) {
         continue;
       }
       
       class_3959 context = new class_3959(pPos, target, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)mc.field_1724);
 
 
 
 
 
       
       class_3965 hit = mc.field_1687.method_17742(context);
       double actualDistance = d;
       class_243 finalPos = target;
       
       if (hit.method_17783() != class_239.class_240.field_1333) {
         double hitDist = hit.method_17784().method_1022(pPos);
         if (hitDist > 2.0D) {
           actualDistance = hitDist;
           finalPos = hit.method_17784().method_1019(dir.method_1021(-1.0D));
         } else {
           continue;
         } 
       } 
       
       double score = calculateEscapeScore(dir, playerLook, playerVelocity, actualDistance, finalPos);
       validPoints.add(new EscapePoint(finalPos, actualDistance, score));
       continue;
     } 
     if (validPoints.isEmpty()) return null;
     
     validPoints.sort(Comparator.comparingDouble(p -> -p.score));
     this.lastEscapeDirection = ((EscapePoint)validPoints.get(0)).pos.method_1020(pPos).method_1029();
     return ((EscapePoint)validPoints.get(0)).pos;
   }
   
   private class_243[] generateSmartDirections(class_243 playerLook, class_243 velocity) {
     class_243 back = (new class_243(-playerLook.field_1352, 0.0D, -playerLook.field_1350)).method_1029();
     class_243 right = (new class_243(-playerLook.field_1350, 0.0D, playerLook.field_1352)).method_1029();
     class_243 left = right.method_1021(-1.0D);
     class_243 up = new class_243(0.0D, 1.0D, 0.0D);
     
     List<class_243> dirs = new ArrayList<>();
     
     dirs.add(back.method_1019(up).method_1029());
     dirs.add(back.method_1019(right).method_1019(up).method_1029());
     dirs.add(back.method_1019(left).method_1019(up).method_1029());
     dirs.add(right.method_1019(up).method_1029());
     dirs.add(left.method_1019(up).method_1029());
     dirs.add(back.method_1019(right.method_1021(0.5D)).method_1019(up.method_1021(1.5D)).method_1029());
     dirs.add(back.method_1019(left.method_1021(0.5D)).method_1019(up.method_1021(1.5D)).method_1029());
     dirs.add(back.method_1019(up.method_1021(2.0D)).method_1029());
     dirs.add(right.method_1019(up.method_1021(1.5D)).method_1029());
     dirs.add(left.method_1019(up.method_1021(1.5D)).method_1029());
     dirs.add(back.method_1021(0.7D).method_1019(right.method_1021(0.3D)).method_1019(up.method_1021(1.2D)).method_1029());
     dirs.add(back.method_1021(0.7D).method_1019(left.method_1021(0.3D)).method_1019(up.method_1021(1.2D)).method_1029());
     dirs.add(back.method_1021(0.5D).method_1019(up.method_1021(1.8D)).method_1029());
     dirs.add(right.method_1021(0.8D).method_1019(up.method_1021(1.3D)).method_1029());
     dirs.add(left.method_1021(0.8D).method_1019(up.method_1021(1.3D)).method_1029());
     
     if (velocity.method_1027() > 0.01D) {
       class_243 perpendicular = (new class_243(-velocity.field_1350, 0.0D, velocity.field_1352)).method_1029();
       dirs.add(perpendicular.method_1019(up).method_1029());
       dirs.add(perpendicular.method_1021(-1.0D).method_1019(up).method_1029());
       dirs.add(perpendicular.method_1019(up.method_1021(1.5D)).method_1029());
       dirs.add(perpendicular.method_1021(-1.0D).method_1019(up.method_1021(1.5D)).method_1029());
     } 
     
     return dirs.<class_243>toArray(new class_243[0]);
   }
   
   private double calculateEscapeScore(class_243 direction, class_243 playerLook, class_243 velocity, double distance, class_243 finalPos) {
     double score = 0.0D;
     
     double backwardBonus = -direction.method_1026((new class_243(playerLook.field_1352, 0.0D, playerLook.field_1350)).method_1029());
     score += backwardBonus * 30.0D;
     
     score += direction.field_1351 * 25.0D;
     
     score += distance * 2.0D;
     
     if (velocity.method_1027() > 0.01D) {
       class_243 velNorm = velocity.method_1029();
       double perpendicular = Math.abs(direction.method_1026(new class_243(-velNorm.field_1350, 0.0D, velNorm.field_1352)));
       score += perpendicular * 15.0D;
     } 
     
     if (this.lastEscapeDirection != null) {
       double similarity = direction.method_1026(this.lastEscapeDirection);
       if (similarity > 0.7D) {
         score -= 20.0D;
       }
     } 
     
     double groundDistance = finalPos.field_1351 - mc.field_1687.method_31607();
     if (groundDistance < 10.0D) {
       score -= (10.0D - groundDistance) * 5.0D;
     }
     
     return score;
   }
   
   private void useFirework() {
     if (mc.field_1724 == null)
       return;  int slotFirework = InventoryUtils.getItemSlot(class_1802.field_8639);
     if (slotFirework != -1)
       InventoryUtils.swapAndUseHvH(class_1802.field_8639); 
   }
   
   private static class EscapePoint
   {
     class_243 pos;
     double distance;
     double score;
     
     EscapePoint(class_243 pos, double distance, double score) {
       this.pos = pos;
       this.distance = distance;
       this.score = score;
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\ElytraResolver.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */