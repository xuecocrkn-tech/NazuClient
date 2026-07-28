package shame.nazuna.client.modules.impl.combat;
 
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.Entity;
 import net.minecraft.Items;
 import net.minecraft.HitResult;
 import net.minecraft.Vec3d;
 import net.minecraft.RaycastContext;
 import net.minecraft.BlockHitResult;
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
   private Vec3d escapePos;
   private long escapeStartTime;
   private int returnFireworkTicks = -1;
   private Vec3d lastEscapeDirection;
   
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
     Vec3d bestPos = calculateSmartEscape(mc.field_1724.method_19538(), this.distance.get());
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
   
   public Vec3d getEscapePos() {
     return this.escapePos;
   }
   
   private Vec3d calculateSmartEscape(Vec3d pPos, float d) {
     Vec3d playerLook = mc.field_1724.method_5720();
     Vec3d playerVelocity = mc.field_1724.method_18798();
     
     Vec3d[] directions = generateSmartDirections(playerLook, playerVelocity);
     List<EscapePoint> validPoints = new ArrayList<>();
     
     for (Vec3d dir : directions) {
       Vec3d target = pPos.method_1019(dir.method_1021(d));
       
       if (target.field_1351 < pPos.field_1351 + 4.0D) {
         continue;
       }
       
       RaycastContext context = new RaycastContext(pPos, target, RaycastContext.class_3960.field_17558, RaycastContext.class_242.field_1348, (Entity)mc.field_1724);
 
 
 
 
 
       
       BlockHitResult hit = mc.field_1687.method_17742(context);
       double actualDistance = d;
       Vec3d finalPos = target;
       
       if (hit.method_17783() != HitResult.class_240.field_1333) {
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
   
   private Vec3d[] generateSmartDirections(Vec3d playerLook, Vec3d velocity) {
     Vec3d back = (new Vec3d(-playerLook.field_1352, 0.0D, -playerLook.field_1350)).method_1029();
     Vec3d right = (new Vec3d(-playerLook.field_1350, 0.0D, playerLook.field_1352)).method_1029();
     Vec3d left = right.method_1021(-1.0D);
     Vec3d up = new Vec3d(0.0D, 1.0D, 0.0D);
     
     List<Vec3d> dirs = new ArrayList<>();
     
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
       Vec3d perpendicular = (new Vec3d(-velocity.field_1350, 0.0D, velocity.field_1352)).method_1029();
       dirs.add(perpendicular.method_1019(up).method_1029());
       dirs.add(perpendicular.method_1021(-1.0D).method_1019(up).method_1029());
       dirs.add(perpendicular.method_1019(up.method_1021(1.5D)).method_1029());
       dirs.add(perpendicular.method_1021(-1.0D).method_1019(up.method_1021(1.5D)).method_1029());
     } 
     
     return dirs.<Vec3d>toArray(new Vec3d[0]);
   }
   
   private double calculateEscapeScore(Vec3d direction, Vec3d playerLook, Vec3d velocity, double distance, Vec3d finalPos) {
     double score = 0.0D;
     
     double backwardBonus = -direction.method_1026((new Vec3d(playerLook.field_1352, 0.0D, playerLook.field_1350)).method_1029());
     score += backwardBonus * 30.0D;
     
     score += direction.field_1351 * 25.0D;
     
     score += distance * 2.0D;
     
     if (velocity.method_1027() > 0.01D) {
       Vec3d velNorm = velocity.method_1029();
       double perpendicular = Math.abs(direction.method_1026(new Vec3d(-velNorm.field_1350, 0.0D, velNorm.field_1352)));
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
       return;  int slotFirework = InventoryUtils.getItemSlot(Items.field_8639);
     if (slotFirework != -1)
       InventoryUtils.swapAndUseHvH(Items.field_8639); 
   }
   
   private static class EscapePoint
   {
     Vec3d pos;
     double distance;
     double score;
     
     EscapePoint(Vec3d pos, double distance, double score) {
       this.pos = pos;
       this.distance = distance;
       this.score = score;
     }
   }
 }

