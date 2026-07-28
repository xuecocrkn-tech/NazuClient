package shame.nazuna.client.modules.impl.combat;
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.Hand;
 import net.minecraft.StatusEffects;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.SlotActionType;
 import net.minecraft.Item;
 import net.minecraft.Items;
 import net.minecraft.BlockView;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.Vec3i;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.BlockState;
 import net.minecraft.MathHelper;
 import net.minecraft.BlockHitResult;
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
   
   private PlayerEntity target;
   
   private int oldSlot;
   
   private int inventorySlot;
   
   private boolean placing;
   
   private boolean use;
   private final List<BlockPos> blocksToPlace;
   private int placeIndex;
   private BlockPos currentBlock;
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
 
   
   private void rotateToBlock(BlockPos pos) {
     Direction side = getPlaceSide(pos);
     if (side == null)
       return; 
     BlockPos neighbor = pos.method_10093(side);
     Direction opposite = side.method_10153();
     Vec3d hitVec = getHitVec(neighbor, opposite);
     
     Vec2f targetRot = RotationUtils.getRotations(hitVec);
     
     RotationStorage.update(new Rotation(targetRot.field_1343, targetRot.field_1342), 360.0F, 360.0F, 360.0F, 360.0F, 5, 1, false);
   }
 
 
 
 
 
   
   private Vec3d getHitVec(BlockPos neighbor, Direction face) {
     Vec3d center = Vec3d.method_24953((Vec3i)neighbor);
     return center.method_1031(face
         .method_10148() * 0.5D, face
         .method_10164() * 0.5D, face
         .method_10165() * 0.5D);
   }
 
   
   private boolean isRotatedToBlock(BlockPos pos) {
     if (!this.rotation.isState()) return true;
     
     Direction side = getPlaceSide(pos);
     if (side == null) return false;
     
     BlockPos neighbor = pos.method_10093(side);
     Direction opposite = side.method_10153();
     Vec3d hitVec = getHitVec(neighbor, opposite);
     
     Vec2f targetRot = RotationUtils.getRotations(hitVec);
     float yawDiff = Math.abs(MathHelper.method_15393(targetRot.field_1343 - mc.field_1724.method_36454()));
     float pitchDiff = Math.abs(MathHelper.method_15393(targetRot.field_1342 - mc.field_1724.method_36455()));
     
     return (yawDiff < 5.0F && pitchDiff < 5.0F);
   }
   
   private void startPlacing() {
     this.blocksToPlace.clear();
     this.placeIndex = 0;
     this.waitingForRotation = false;
     this.rotationTicks = 0;
     
     BlockPos targetPos = this.target.method_24515();
     
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
         mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot, this.oldSlot, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
       } 
     } 
     
     this.placing = true;
   }
   
   private void processPlacing() {
     if (this.target != null && (!this.target.method_5805() || AntiBot.checkBot((LivingEntity)this.target) || mc.field_1724.method_5739((Entity)this.target) > this.distance.getValue().floatValue())) {
       finishPlacing();
       
       return;
     } 
     if (this.placeIndex >= this.blocksToPlace.size()) {
       finishPlacing();
       
       return;
     } 
     BlockPos pos = this.blocksToPlace.get(this.placeIndex);
     this.currentBlock = pos;
     
     if (!mc.field_1687.method_8320(pos).method_45474()) {
       this.placeIndex++;
       this.waitingForRotation = false;
       this.rotationTicks = 0;
       
       return;
     } 
     Direction side = getPlaceSide(pos);
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
         mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, this.inventorySlot, this.oldSlot, SlotActionType.field_7791, (PlayerEntity)mc.field_1724);
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
   
   private Direction getPlaceSide(BlockPos pos) {
     Direction[] priority = { Direction.field_11033, Direction.field_11036, Direction.field_11043, Direction.field_11035, Direction.field_11039, Direction.field_11034 };
     for (Direction dir : priority) {
       BlockPos neighbor = pos.method_10093(dir);
       BlockState state = mc.field_1687.method_8320(neighbor);
       if (!state.method_45474() && !state.method_51176() && state.method_26212((BlockView)mc.field_1687, neighbor)) {
         return dir;
       }
     } 
     for (Direction dir : priority) {
       BlockPos neighbor = pos.method_10093(dir);
       BlockState state = mc.field_1687.method_8320(neighbor);
       if (!state.method_45474() && !state.method_51176()) {
         return dir;
       }
     } 
     return null;
   }
 
   
   private void placeBlock(BlockPos pos) {
     Direction side = getPlaceSide(pos);
     if (side == null)
       return; 
     BlockPos neighbor = pos.method_10093(side);
     Direction opposite = side.method_10153();
     Vec3d hitVec = getHitVec(neighbor, opposite);
     
     BlockHitResult result = new BlockHitResult(hitVec, opposite, neighbor, false);
     mc.field_1761.method_2896(mc.field_1724, Hand.field_5808, result);
     mc.field_1724.method_6104(Hand.field_5808);
   }
   
   private int findItemSlot() {
     Item item = this.mode.is("Obsidian") ? Items.field_8281 : Items.field_8786;
     for (int i = 0; i < 36; i++) {
       if (mc.field_1724.method_31548().method_5438(i).method_7909() == item) {
         return i;
       }
     } 
     return -1;
   }
   
   private PlayerEntity findTarget() {
     if (this.targets.is("Себя")) {
       return (PlayerEntity)mc.field_1724;
     }
     
     List<PlayerEntity> playerTargets = new ArrayList<>();
     for (Entity entity : mc.field_1687.method_18112()) {
       if (entity instanceof PlayerEntity) { PlayerEntity player = (PlayerEntity)entity;
         if (player == mc.field_1724 || 
           !player.method_5805() || 
           AntiBot.checkBot((LivingEntity)player) || 
           !this.targets.is("Игроки") || (
           player.method_6059(StatusEffects.field_5905) && !this.targets.is("Невидимые")) || 
           NazunaClient.INSTANCE.friendStorage.isFriend(player.method_5477().getString()) || 
           mc.field_1724.method_5739((Entity)player) > this.distance.getValue().floatValue())
           continue;  playerTargets.add(player); }
     
     }  if (playerTargets.isEmpty()) return null; 
     playerTargets.sort(Comparator.comparingDouble(p -> mc.field_1724.method_5739((Entity)p)));
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

