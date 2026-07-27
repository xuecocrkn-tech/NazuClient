package shame.nazuna.client.modules.impl.combat;
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.class_1268;
 import net.minecraft.class_1294;
 import net.minecraft.class_1297;
 import net.minecraft.class_1309;
 import net.minecraft.class_1657;
 import net.minecraft.class_1713;
 import net.minecraft.class_1792;
 import net.minecraft.class_1802;
 import net.minecraft.class_1922;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_2382;
 import net.minecraft.class_241;
 import net.minecraft.class_243;
 import net.minecraft.class_2680;
 import net.minecraft.class_3532;
 import net.minecraft.class_3965;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventGameUpdate;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.RotationStorage;
 import shame.nazuna.api.utils.rotate.Rotation;
 import shame.nazuna.api.utils.rotate.RotationUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AutoTrap extends Module {
   public static AutoTrap INSTANCE = new AutoTrap();
   
   private final ModeSetting mode = new ModeSetting("Мод", "Obsidian", new String[] { "Obsidian", "CobWeb" });
   private final FloatSetting distance = new FloatSetting("Дистанция", 3.0F, 1.0F, 5.0F, 0.1F);
   private final BindSetting bind = new BindSetting("Бинд", -1);
   private final BooleanSetting fromInventory = new BooleanSetting("Из инвентаря", false);
   private final BooleanSetting rotation = new BooleanSetting("Ротация", true);
   
   private final ListSetting targets = new ListSetting("Таргеты", new BooleanSetting[] { new BooleanSetting("Игроки", true), new BooleanSetting("Невидимые", true), new BooleanSetting("Себя", false) });
   
   private final BooleanSetting reverseRotate;
   
   private class_1657 target;
   
   private int oldSlot;
   
   private int inventorySlot;
   
   private boolean placing;
   
   private boolean use;
   private final List<class_2338> blocksToPlace;
   private int placeIndex;
   private class_2338 currentBlock;
   private boolean waitingForRotation;
   private int rotationTicks;
   private float restoreYaw;
   private float restorePitch;
   
   public AutoTrap() {
     super("AutoTrap", "Автоматически ставит ловушку вокруг игрока", Module.ModuleCategory.COMBAT); Objects.requireNonNull(this.rotation); this.reverseRotate = (new BooleanSetting("Реверс ротейт", true)).visible(this.rotation::isState); this.oldSlot = -1; this.inventorySlot = -1; this.placing = false; this.use = false; this.blocksToPlace = new ArrayList<>(); this.placeIndex = 0; this.currentBlock = null; this.waitingForRotation = false; this.rotationTicks = 0;
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.distance, (Setting)this.bind, (Setting)this.fromInventory, (Setting)this.rotation, (Setting)this.reverseRotate, (Setting)this.targets });
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1755 != null)
       return; 
     if (event.getKey() == this.bind.getKey()) {
       this.use = true;
     }
   }
   
   @EventLink
   public void onGameUpdate(EventGameUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (!this.placing || this.currentBlock == null || !this.rotation.isState())
       return; 
     rotateToBlock(this.currentBlock);
   }
 
   
   @EventLink
   public void onUpdate(EventUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       this.use = false;
       
       return;
     } 
     if (this.use && !this.placing) {
       this.target = findTarget();
       if (this.target != null) {
         startPlacing();
       }
       this.use = false;
     } 
     
     if (this.placing) {
       processPlacing();
     }
   }
 
   
   private void rotateToBlock(class_2338 pos) {
     class_2350 side = getPlaceSide(pos);
     if (side == null)
       return; 
     class_2338 neighbor = pos.method_10093(side);
     class_2350 opposite = side.method_10153();
     class_243 hitVec = getHitVec(neighbor, opposite);
     
     class_241 targetRot = RotationUtils.getRotations(hitVec);
     
     RotationStorage.update(new Rotation(targetRot.field_1343, targetRot.field_1342), 360.0F, 360.0F, 360.0F, 360.0F, 5, 1, false);
   }
 
 
 
 
 
   
   private class_243 getHitVec(class_2338 neighbor, class_2350 face) {
     class_243 center = class_243.method_24953((class_2382)neighbor);
     return center.method_1031(face
         .method_10148() * 0.5D, face
         .method_10164() * 0.5D, face
         .method_10165() * 0.5D);
   }
 
   
   private boolean isRotatedToBlock(class_2338 pos) {
     if (!this.rotation.isState()) return true;
     
     class_2350 side = getPlaceSide(pos);
     if (side == null) return false;
     
     class_2338 neighbor = pos.method_10093(side);
     class_2350 opposite = side.method_10153();
     class_243 hitVec = getHitVec(neighbor, opposite);
     
     class_241 targetRot = RotationUtils.getRotations(hitVec);
     float yawDiff = Math.abs(class_3532.method_15393(targetRot.field_1343 - mc.field_1724.method_36454()));
     float pitchDiff = Math.abs(class_3532.method_15393(targetRot.field_1342 - mc.field_1724.method_36455()));
     
     return (yawDiff < 5.0F && pitchDiff < 5.0F);
   }
   
   private void startPlacing() {
     this.blocksToPlace.clear();
     this.placeIndex = 0;
     this.waitingForRotation = false;
     this.rotationTicks = 0;
     
     class_2338 targetPos = this.target.method_24515();
     
     if (this.mode.is("Obsidian")) {
       this.blocksToPlace.add(targetPos.method_10069(1, 0, 0));
       this.blocksToPlace.add(targetPos.method_10069(-1, 0, 0));
       this.blocksToPlace.add(targetPos.method_10069(0, 0, 1));
       this.blocksToPlace.add(targetPos.method_10069(0, 0, -1));
       this.blocksToPlace.add(targetPos.method_10069(1, 1, 0));
       this.blocksToPlace.add(targetPos.method_10069(-1, 1, 0));
       this.blocksToPlace.add(targetPos.method_10069(0, 1, 1));
       this.blocksToPlace.add(targetPos.method_10069(0, 1, -1));
       this.blocksToPlace.add(targetPos.method_10069(0, 2, 0));
       this.blocksToPlace.add(targetPos.method_10069(1, 2, 0));
       this.blocksToPlace.add(targetPos.method_10069(-1, 2, 0));
       this.blocksToPlace.add(targetPos.method_10069(0, 2, 1));
       this.blocksToPlace.add(targetPos.method_10069(0, 2, -1));
     } else {
       this.blocksToPlace.add(targetPos);
       this.blocksToPlace.add(targetPos.method_10084());
     } 
     
     if (this.fromInventory.isState()) {
       this.oldSlot = (mc.field_1724.method_31548()).field_7545;
       int slot = findItemSlot();
       if (slot == -1) {
         this.placing = false;
         
         return;
       } 
       if (slot < 9) {
         (mc.field_1724.method_31548()).field_7545 = slot;
         this.inventorySlot = -1;
       } else {
         this.inventorySlot = slot;
         mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot, this.oldSlot, class_1713.field_7791, (class_1657)mc.field_1724);
       } 
     } 
     
     this.placing = true;
   }
   
   private void processPlacing() {
     if (this.target != null && (!this.target.method_5805() || AntiBot.checkBot((class_1309)this.target) || mc.field_1724.method_5739((class_1297)this.target) > this.distance.getValue().floatValue())) {
       finishPlacing();
       
       return;
     } 
     if (this.placeIndex >= this.blocksToPlace.size()) {
       finishPlacing();
       
       return;
     } 
     class_2338 pos = this.blocksToPlace.get(this.placeIndex);
     this.currentBlock = pos;
     
     if (!mc.field_1687.method_8320(pos).method_45474()) {
       this.placeIndex++;
       this.waitingForRotation = false;
       this.rotationTicks = 0;
       
       return;
     } 
     class_2350 side = getPlaceSide(pos);
     if (side == null) {
       this.placeIndex++;
       this.waitingForRotation = false;
       this.rotationTicks = 0;
       
       return;
     } 
     if (this.rotation.isState()) {
       if (!this.waitingForRotation) {
         rotateToBlock(pos);
         this.waitingForRotation = true;
         this.rotationTicks = 0;
         
         return;
       } 
       this.rotationTicks++;
       
       if (!isRotatedToBlock(pos) || this.rotationTicks < 2) {
         rotateToBlock(pos);
         
         return;
       } 
     } 
     placeBlock(pos);
     this.placeIndex++;
     this.waitingForRotation = false;
     this.rotationTicks = 0;
   }
   
   private void finishPlacing() {
     if (this.fromInventory.isState()) {
       if (this.inventorySlot != -1) {
         mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, this.inventorySlot, this.oldSlot, class_1713.field_7791, (class_1657)mc.field_1724);
         this.inventorySlot = -1;
       } else if (this.oldSlot != -1) {
         (mc.field_1724.method_31548()).field_7545 = this.oldSlot;
       } 
       this.oldSlot = -1;
     } 
     this.placing = false;
     this.target = null;
     this.currentBlock = null;
     this.blocksToPlace.clear();
     this.placeIndex = 0;
     this.waitingForRotation = false;
     this.rotationTicks = 0;
   }
   
   private class_2350 getPlaceSide(class_2338 pos) {
     class_2350[] priority = { class_2350.field_11033, class_2350.field_11036, class_2350.field_11043, class_2350.field_11035, class_2350.field_11039, class_2350.field_11034 };
     for (class_2350 dir : priority) {
       class_2338 neighbor = pos.method_10093(dir);
       class_2680 state = mc.field_1687.method_8320(neighbor);
       if (!state.method_45474() && !state.method_51176() && state.method_26212((class_1922)mc.field_1687, neighbor)) {
         return dir;
       }
     } 
     for (class_2350 dir : priority) {
       class_2338 neighbor = pos.method_10093(dir);
       class_2680 state = mc.field_1687.method_8320(neighbor);
       if (!state.method_45474() && !state.method_51176()) {
         return dir;
       }
     } 
     return null;
   }
 
   
   private void placeBlock(class_2338 pos) {
     class_2350 side = getPlaceSide(pos);
     if (side == null)
       return; 
     class_2338 neighbor = pos.method_10093(side);
     class_2350 opposite = side.method_10153();
     class_243 hitVec = getHitVec(neighbor, opposite);
     
     class_3965 result = new class_3965(hitVec, opposite, neighbor, false);
     mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, result);
     mc.field_1724.method_6104(class_1268.field_5808);
   }
   
   private int findItemSlot() {
     class_1792 item = this.mode.is("Obsidian") ? class_1802.field_8281 : class_1802.field_8786;
     for (int i = 0; i < 36; i++) {
       if (mc.field_1724.method_31548().method_5438(i).method_7909() == item) {
         return i;
       }
     } 
     return -1;
   }
   
   private class_1657 findTarget() {
     if (this.targets.is("Себя")) {
       return (class_1657)mc.field_1724;
     }
     
     List<class_1657> playerTargets = new ArrayList<>();
     for (class_1297 entity : mc.field_1687.method_18112()) {
       if (entity instanceof class_1657) { class_1657 player = (class_1657)entity;
         if (player == mc.field_1724 || 
           !player.method_5805() || 
           AntiBot.checkBot((class_1309)player) || 
           !this.targets.is("Игроки") || (
           player.method_6059(class_1294.field_5905) && !this.targets.is("Невидимые")) || 
           astra.INSTANCE.friendStorage.isFriend(player.method_5477().getString()) || 
           mc.field_1724.method_5739((class_1297)player) > this.distance.getValue().floatValue())
           continue;  playerTargets.add(player); }
     
     }  if (playerTargets.isEmpty()) return null; 
     playerTargets.sort(Comparator.comparingDouble(p -> mc.field_1724.method_5739((class_1297)p)));
     return playerTargets.get(0);
   }
 
   
   public void onDisable() {
     super.onDisable();
     if (this.placing) {
       finishPlacing();
     }
     this.target = null;
     this.placing = false;
     this.use = false;
     this.currentBlock = null;
     this.blocksToPlace.clear();
     this.placeIndex = 0;
     this.oldSlot = -1;
     this.inventorySlot = -1;
     this.waitingForRotation = false;
     this.rotationTicks = 0;
   }
 
   
   public void onEnable() {
     super.onEnable();
     this.placing = false;
     this.use = false;
     this.currentBlock = null;
     this.blocksToPlace.clear();
     this.placeIndex = 0;
     this.oldSlot = -1;
     this.inventorySlot = -1;
     this.waitingForRotation = false;
     this.rotationTicks = 0;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\combat\AutoTrap.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */