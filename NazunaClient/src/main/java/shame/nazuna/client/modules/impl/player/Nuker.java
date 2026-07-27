package shame.nazuna.client.modules.impl.player;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Set;
 import net.minecraft.Hand;
 import net.minecraft.BlockView;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.Vec3i;
 import net.minecraft.Vec3d;
 import net.minecraft.BlockState;
 import net.minecraft.Registries;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class Nuker extends Module {
   public static Nuker INSTANCE = new Nuker();
   
   private final FloatSetting radius = new FloatSetting("Дистанция", 3.0F, 1.0F, 5.0F, 1.0F);
   private final BooleanSetting breakAll = new BooleanSetting("Ломать все блоки", false);
   private final BooleanSetting swing = new BooleanSetting("Анимация руки", true);
   
   private final Set<String> targetBlocks = new HashSet<>();
   private BlockPos currentTargetBlock;
   
   public Nuker() {
     super("Nuker", "Автоматически ломает блоки в радиусе", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.radius, (Setting)this.breakAll, (Setting)this.swing });
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
       resetBreaking();
       
       return;
     } 
     if (!this.breakAll.isState() && this.targetBlocks.isEmpty()) {
       resetBreaking();
       
       return;
     } 
     if (!isCurrentTargetValid()) {
       this.currentTargetBlock = findNewTarget();
     }
     
     if (this.currentTargetBlock != null) {
       breakCurrentTarget();
     }
   }
   
   private boolean isCurrentTargetValid() {
     return (this.currentTargetBlock != null && 
       isInRange(this.currentTargetBlock) && 
       shouldBreak(this.currentTargetBlock));
   }
   
   private BlockPos findNewTarget() {
     int range = Math.round(this.radius.get());
     BlockPos playerPos = mc.field_1724.method_24515();
     
     return BlockPos.method_20437(playerPos
         .method_10069(-range, 0, -range), playerPos
         .method_10069(range, range, range))
       
       .map(BlockPos::method_10062)
       .filter(this::isInRange)
       .filter(this::shouldBreak)
       .min(Comparator.comparingDouble(pos -> mc.field_1724.method_5707(Vec3d.method_24953((Vec3i)pos))))
       .orElse(null);
   }
   
   private boolean isInRange(BlockPos pos) {
     double maxDistance = this.radius.get();
     return (mc.field_1724.method_5707(Vec3d.method_24953((Vec3i)pos)) <= maxDistance * maxDistance);
   }
   
   private boolean shouldBreak(BlockPos pos) {
     BlockState state = mc.field_1687.method_8320(pos);
     if (state == null || state.method_26215() || state.method_26214((BlockView)mc.field_1687, pos) < 0.0F) {
       return false;
     }
     
     if (this.breakAll.isState()) {
       return true;
     }
     
     String blockName = Registries.field_41175.method_10221(state.method_26204()).method_12832().toLowerCase();
     return this.targetBlocks.contains(blockName);
   }
   
   private void breakCurrentTarget() {
     if (this.currentTargetBlock == null || mc.field_1724 == null || mc.field_1761 == null) {
       return;
     }
     
     mc.field_1761.method_2910(this.currentTargetBlock, Direction.field_11036);
     mc.field_1761.method_2902(this.currentTargetBlock, Direction.field_11036);
     
     if (this.swing.isState()) {
       mc.field_1724.method_6104(Hand.field_5808);
     }
     
     if (mc.field_1687.method_8320(this.currentTargetBlock).method_26215()) {
       resetBreaking();
     }
   }
   
   private void resetBreaking() {
     this.currentTargetBlock = null;
     if (mc.field_1761 != null) {
       mc.field_1761.method_2925();
     }
   }
   
   public void addBlock(String blockName) {
     this.targetBlocks.add(normalizeBlockName(blockName));
   }
   
   public void removeBlock(String blockName) {
     this.targetBlocks.remove(normalizeBlockName(blockName));
   }
   
   public void clearBlocks() {
     this.targetBlocks.clear();
     resetBreaking();
   }
   
   public boolean isTargetBlock(String blockName) {
     return this.targetBlocks.contains(normalizeBlockName(blockName));
   }
   
   public Set<String> getTargetBlocks() {
     return new HashSet<>(this.targetBlocks);
   }
   
   public static String normalizeBlockName(String blockName) {
     if (blockName == null) {
       return "";
     }
     String normalized = blockName.toLowerCase().trim();
     int namespaceSeparator = normalized.indexOf(':');
     return (namespaceSeparator >= 0) ? normalized.substring(namespaceSeparator + 1) : normalized;
   }
 
   
   public void onDisable() {
     resetBreaking();
     super.onDisable();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\Nuker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */