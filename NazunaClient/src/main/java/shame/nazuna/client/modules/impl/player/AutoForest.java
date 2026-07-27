package shame.nazuna.client.modules.impl.player;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import net.minecraft.Hand;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.Vec3i;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.BlockUpdateS2CPacket;
 import net.minecraft.BlockState;
 import net.minecraft.PlayerActionC2SPacket;
 import net.minecraft.BlockTags;
 import net.minecraft.RaycastContext;
 import net.minecraft.BlockHitResult;
 import net.minecraft.ClientPlayNetworkHandler;
 import net.minecraft.ClientPlayerInteractionManager;
 import net.minecraft.ClientWorld;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.bot.BotSessionManager;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class AutoForest extends Module {
   public static AutoForest INSTANCE = new AutoForest();
   
   private static final double MAX_RANGE = 4.0D;
   
   private static final double MAX_RANGE_SQ = 16.0D;
   private static final long DEFAULT_BREAK_DELAY_MS = 3L;
   private static final float AUTO_FAST_BREAK_SPEED = 1.0F;
   private static final float DEFAULT_PACKETS_PER_SECOND = 100.0F;
   private static final long VISUAL_TTL_MS = 300000L;
   private static final long NICK_REMINDER_DELAY_MS = 5000L;
   private static final String MODE_NORMAL_ALIAS = "normal";
   private static final String MODE_FAST_ALIAS = "fast";
   private final ModeSetting breakMode = new ModeSetting("Режим ломания", "Обычный", new String[] { "Обычный", "Быстрый" });
   private final FloatSetting packetsPerSecond = (new FloatSetting("Пакетов в секунду", 100.0F, 1.0F, 100.0F, 1.0F))
     .visible(() -> Boolean.valueOf(this.breakMode.is("Быстрый")));
   private final FloatSetting breakRadius = new FloatSetting("Радиус", 4.0F, 1.0F, 6.0F, 0.5F);
   private final BooleanSetting swing = new BooleanSetting("Махать рукой", true);
   private final BooleanSetting autoSell = new BooleanSetting("Авто продажа дерева", true);
   private final BooleanSetting autoPay = new BooleanSetting("AutoPay", false);
   private final BooleanSetting preserveVisuals = new BooleanSetting("Сохранять визуализацию", true);
   
   private final FloatSetting payAmount;
   
   private final FloatSetting intervalSeconds;
   
   private final Map<BlockPos, BlockState> preservedBlocks;
   private final Map<BlockPos, Long> lastUpdateTime;
   private final Set<BlockPos> managedBlocks;
   private boolean currentSessionEnabled;
   private BlockPos targetPos;
   private String payTarget;
   private long lastBreakTime;
   private long lastPacketTime;
   private long lastSellTime;
   private long lastPayTime;
   private long lastNickReminderTime;
   
   public AutoForest() {
     super("AutoForest", "Автоматически ломает бревна и переводит деньги", Module.ModuleCategory.PLAYER); Objects.requireNonNull(this.autoPay); this.payAmount = (new FloatSetting("Сумма перевода", 1000.0F, 500.0F, 25000.0F, 500.0F)).visible(this.autoPay::isState); this.intervalSeconds = new FloatSetting("Задержка", 20.0F, 1.0F, 60.0F, 1.0F); this.preservedBlocks = new HashMap<>(); this.lastUpdateTime = new HashMap<>(); this.managedBlocks = new HashSet<>(); this.payTarget = "";
     addSettings(new Setting[] { (Setting)this.breakMode, (Setting)this.packetsPerSecond, (Setting)this.breakRadius, (Setting)this.swing, (Setting)this.autoSell, (Setting)this.autoPay, (Setting)this.preserveVisuals, (Setting)this.payAmount, (Setting)this.intervalSeconds });
     EventInvoker.register(this);
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (this.currentSessionEnabled && mc.field_1724 != null && mc.field_1687 != null) {
       tickCurrentSession();
     }
     
     tickFrozenBots();
   }
   
   private void tickCurrentSession() {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.method_1562() == null) {
       this.targetPos = null;
       
       return;
     } 
     long now = System.currentTimeMillis();
     long scheduleDelay = Math.max(1000L, (long)(this.intervalSeconds.get() * 500.0F));
     
     if (this.autoSell.isState() && now - this.lastSellTime >= scheduleDelay) {
       mc.method_1562().method_45730("sellwood");
       this.lastSellTime = now;
     } 
     
     if (this.autoPay.isState()) {
       if (this.payTarget.isBlank()) {
         if (now - this.lastNickReminderTime >= 5000L) {
           this.lastNickReminderTime = now;
           ChatUtils.sendMessage("Укажите ник для перевода через .autoles pay <nick>");
         } 
       } else if (now - this.lastPayTime >= scheduleDelay + 200L) {
         mc.method_1562().method_45730("pay " + this.payTarget + " " + (int)this.payAmount.get());
         this.lastPayTime = now;
       } 
     }
     
     if (this.targetPos != null && (!isLog(this.targetPos) || !isInRange(this.targetPos) || !isVisible(this.targetPos))) {
       this.targetPos = null;
     }
     
     if (this.targetPos == null) {
       this.targetPos = findNearestLog();
     }
     
     if (this.targetPos != null) {
       breakTarget(now);
     }
     
     if (this.preserveVisuals.isState()) {
       updateVisualization(now);
     }
   }
   
   private void tickFrozenBots() {
     for (BotSessionManager.BotConnection bot : BotSessionManager.getConnections()) {
       SessionState state = bot.autoForestState();
       if (state == null || !state.enabled() || bot.player() == null || bot.world() == null || bot.handler() == null) {
         continue;
       }
       
       try {
         tickBotSession(bot, state);
       } catch (Exception ignored) {
         state.enabled(false);
         state.targetPos(null);
       } 
     } 
   }
   
   private void tickBotSession(BotSessionManager.BotConnection bot, SessionState state) {
     if (bot.player().method_31481() || !bot.player().method_5805()) {
       state.enabled(false);
       state.targetPos(null);
       
       return;
     } 
     long now = System.currentTimeMillis();
     long scheduleDelay = Math.max(1000L, (long)(Math.max(1.0F, state.intervalSeconds()) * 500.0F));
     
     if (state.autoSell() && now - state.lastSellTime() >= scheduleDelay) {
       bot.handler().method_45730("sellwood");
       state.lastSellTime(now);
     } 
     
     if (state.autoPay()) {
       if (state.payTarget().isBlank()) {
         if (now - state.lastNickReminderTime() >= 5000L) {
           state.lastNickReminderTime(now);
         }
       } else if (now - state.lastPayTime() >= scheduleDelay + 200L) {
         bot.handler().method_45730("pay " + state.payTarget() + " " + (int)state.payAmount());
         state.lastPayTime(now);
       } 
     }
     
     if (state.targetPos() != null && (!isLog(bot.world(), state.targetPos()) || !isInRange(bot.player(), state.targetPos()) || !isVisible(bot.world(), bot.player(), state.targetPos()))) {
       state.targetPos(null);
     }
     
     if (state.targetPos() == null) {
       state.targetPos(findNearestLog(bot.world(), bot.player(), state.breakRadius()));
     }
     
     if (state.targetPos() == null) {
       return;
     }
     
     if ("fast".equals(state.modeAlias())) {
       long interval = Math.max(1L, (long)(1000.0F / Math.max(1.0F, state.packetsPerSecond())));
       if (now - state.lastPacketTime() < interval) {
         return;
       }
       
       performFastBreak(bot.handler(), bot.interactionManager(), bot.player(), bot.world(), state.targetPos(), state.swing());
       state.lastPacketTime(now);
       
       return;
     } 
     if (now - state.lastBreakTime() < 3L) {
       return;
     }
     
     if (bot.interactionManager() != null) {
       bot.interactionManager().method_2910(state.targetPos(), Direction.field_11036);
       bot.interactionManager().method_2902(state.targetPos(), Direction.field_11036);
     } else {
       performFastBreak(bot.handler(), bot.interactionManager(), bot.player(), bot.world(), state.targetPos(), state.swing());
     } 
     
     if (state.swing()) {
       bot.handler().method_52787((Packet)new HandSwingC2SPacket(Hand.field_5808));
     }
     state.lastBreakTime(now);
   }
   
   @EventLink
   public void onPacket(EventPacket event) {
     if (!this.currentSessionEnabled || !this.preserveVisuals.isState() || mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     if (event.getType() == EventPacket.Type.SEND) { Packet Packet = event.getPacket(); if (Packet instanceof PlayerActionC2SPacket) { PlayerActionC2SPacket packet = (PlayerActionC2SPacket)Packet;
         handleDigPacket(packet);
         return; }
        }
     
     if (event.getType() == EventPacket.Type.RECEIVE) { Packet Packet = event.getPacket(); if (Packet instanceof BlockUpdateS2CPacket) { BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket)Packet;
         BlockPos pos = packet.method_11309();
         BlockState savedState = this.preservedBlocks.get(pos);
         if (savedState == null) {
           return;
         }
         
         BlockState serverState = packet.method_11308();
         if (serverState.method_26215() || !serverState.equals(savedState)) {
           event.cancel();
           setClientBlock(pos, savedState);
           this.lastUpdateTime.put(pos, Long.valueOf(System.currentTimeMillis()));
         }  }
        }
   
   }
   private void breakTarget(long now) {
     if (this.targetPos == null || mc.field_1724 == null || mc.field_1724.field_3944 == null || mc.field_1761 == null) {
       return;
     }
     
     if (this.breakMode.is("Быстрый")) {
       long interval = Math.max(1L, (long)(1000.0F / Math.max(1.0F, this.packetsPerSecond.get())));
       if (now - this.lastPacketTime < interval) {
         return;
       }
       
       performFastBreak(this.targetPos);
       this.lastPacketTime = now;
       
       return;
     } 
     if (now - this.lastBreakTime < 3L) {
       return;
     }
     
     mc.field_1761.method_2910(this.targetPos, Direction.field_11036);
     mc.field_1761.method_2902(this.targetPos, Direction.field_11036);
     if (this.swing.isState()) {
       mc.field_1724.method_6104(Hand.field_5808);
     }
     this.lastBreakTime = now;
   }
   
   private void performFastBreak(BlockPos pos) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1724.field_3944 == null) {
       return;
     }
     
     performFastBreak(mc.field_1724.field_3944, mc.field_1761, mc.field_1724, mc.field_1687, pos, this.swing.isState());
   }
 
 
 
 
 
   
   private void performFastBreak(ClientPlayNetworkHandler handler, ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, BlockPos pos, boolean shouldSwing) {
     if (handler == null || player == null || pos == null) {
       return;
     }
     
     boolean accelerated = false;
     if (interactionManager != null && world != null) {
       interactionManager.method_2910(pos, Direction.field_11036);
       accelerated = FastBreak.accelerateClientBreak(interactionManager, player, world, pos, Direction.field_11036, 1.0F, shouldSwing);
     } 
 
 
 
 
 
 
 
 
     
     if (!accelerated) {
       FastBreak.packetBreak(handler, player, pos, Direction.field_11036, shouldSwing);
     }
   }
   
   private BlockPos findNearestLog() {
     return findNearestLog(mc.field_1687, mc.field_1724, this.breakRadius.get());
   }
   
   private BlockPos findNearestLog(ClientWorld world, ClientPlayerEntity player, float radiusValue) {
     if (player == null || world == null) {
       return null;
     }
     
     BlockPos playerPos = player.method_24515();
     int radius = Math.round(radiusValue);
     
     return BlockPos.method_20437(playerPos
         .method_10069(-radius, -radius, -radius), playerPos
         .method_10069(radius, radius, radius))
       
       .map(BlockPos::method_10062)
       .filter(pos -> isLog(world, pos))
       .filter(pos -> isInRange(player, pos))
       .filter(pos -> isVisible(world, player, pos))
       .min(Comparator.comparingDouble(pos -> player.method_5707(Vec3d.method_24953((Vec3i)pos))))
       .orElse(null);
   }
   
   private boolean isInRange(BlockPos pos) {
     return isInRange(mc.field_1724, pos);
   }
   
   private boolean isInRange(ClientPlayerEntity player, BlockPos pos) {
     return (player != null && player.method_5707(Vec3d.method_24953((Vec3i)pos)) <= 16.0D);
   }
   
   private boolean isVisible(BlockPos pos) {
     return isVisible(mc.field_1687, mc.field_1724, pos);
   }
   
   private boolean isVisible(ClientWorld world, ClientPlayerEntity player, BlockPos pos) {
     if (player == null || world == null) {
       return false;
     }
     
     Vec3d eyePos = player.method_33571();
     Vec3d targetCenter = Vec3d.method_24953((Vec3i)pos);
     BlockHitResult hit = world.method_17742(new RaycastContext(eyePos, targetCenter, RaycastContext.class_3960.field_17558, RaycastContext.class_242.field_1348, (Entity)player));
 
 
 
 
 
 
     
     return (hit == null || hit.method_17783() == HitResult.class_240.field_1333 || pos.equals(hit.method_17777()));
   }
   
   private boolean isLog(BlockPos pos) {
     return isLog(mc.field_1687, pos);
   }
   
   private boolean isLog(ClientWorld world, BlockPos pos) {
     return (world != null && world.method_8320(pos).method_26164(BlockTags.field_15475));
   }
   
   private void handleDigPacket(PlayerActionC2SPacket packet) {
     PlayerActionC2SPacket.class_2847 action = packet.method_12363();
     if (action != PlayerActionC2SPacket.class_2847.field_12968 && action != PlayerActionC2SPacket.class_2847.field_12973) {
       return;
     }
 
     
     BlockPos pos = packet.method_12362();
     if (!isLog(pos)) {
       return;
     }
     
     BlockState state = mc.field_1687.method_8320(pos);
     if (state.method_26215()) {
       return;
     }
     
     this.preservedBlocks.put(pos, state);
     this.managedBlocks.add(pos);
     this.lastUpdateTime.put(pos, Long.valueOf(System.currentTimeMillis()));
     setClientBlock(pos, state);
   }
   
   private void updateVisualization(long now) {
     ClientWorld clientWorld, NarrationPart = mc.field_1687; if (NarrationPart instanceof ClientWorld) { clientWorld = NarrationPart; }
     else
     { return; }
     
     Set<BlockPos> toRemove = new HashSet<>();
     
     for (Map.Entry<BlockPos, BlockState> entry : this.preservedBlocks.entrySet()) {
       BlockPos pos = entry.getKey();
       BlockState savedState = entry.getValue();
       BlockState currentState = clientWorld.method_8320(pos);
       
       if (currentState == null || !currentState.equals(savedState)) {
         clientWorld.method_8652(pos, savedState, 0);
         this.lastUpdateTime.put(pos, Long.valueOf(now));
       } 
       
       Long lastSeen = this.lastUpdateTime.get(pos);
       if (lastSeen != null && now - lastSeen.longValue() > 300000L) {
         toRemove.add(pos);
       }
     } 
     
     for (BlockPos pos : toRemove) {
       this.preservedBlocks.remove(pos);
       this.lastUpdateTime.remove(pos);
       this.managedBlocks.remove(pos);
     } 
   }
   
   private void restoreVisualState() {
     ClientWorld clientWorld, NarrationPart = mc.field_1687; if (NarrationPart instanceof ClientWorld) { clientWorld = NarrationPart; }
     else { this.preservedBlocks.clear();
       this.lastUpdateTime.clear();
       this.managedBlocks.clear();
       
       return; }
     
     for (BlockPos pos : this.managedBlocks) {
       clientWorld.method_8652(pos, mc.field_1687.method_8320(pos), 0);
     }
     
     this.preservedBlocks.clear();
     this.lastUpdateTime.clear();
     this.managedBlocks.clear();
   }
   
   private void setClientBlock(BlockPos pos, BlockState state) {
     ClientWorld ClientWorld = mc.field_1687; if (ClientWorld instanceof ClientWorld) { ClientWorld clientWorld = ClientWorld;
       clientWorld.method_8652(pos, state, 0); }
   
   }
   
   public List<String> getModeSuggestions() {
     return List.of("normal", "fast");
   }
   
   public boolean setModeAlias(String alias) {
     if (alias == null || alias.isBlank()) {
       return false;
     }
     
     switch (alias.trim().toLowerCase(Locale.ROOT)) { case "normal": case "default":
       case "обычный":
         this.breakMode.set(this.breakMode.getMods().get(0));
 
 
       
       case "fast":
       case "quick":
       case "быстрый":
         this.breakMode.set(this.breakMode.getMods().get(1));
         return !(this.breakMode.getMods().size() < 2); }
     
     return false;
   }
 
   
   public String getModeAlias() {
     if (this.breakMode.getMods().size() > 1 && this.breakMode.is(this.breakMode.getMods().get(1))) {
       return "fast";
     }
     return "normal";
   }
   
   public void enableForCurrentSession() {
     this.currentSessionEnabled = true;
     resetRuntimeState();
     restoreVisualState();
   }
   
   public void disableForCurrentSession() {
     this.currentSessionEnabled = false;
     restoreVisualState();
     resetRuntimeState();
   }
   
   public boolean isCurrentSessionEnabled() {
     return this.currentSessionEnabled;
   }
   
   public void setSwingEnabled(boolean value) {
     this.swing.setState(value);
   }
   
   public boolean isSwingEnabled() {
     return this.swing.isState();
   }
   
   public void setAutoSellEnabled(boolean value) {
     this.autoSell.setState(value);
   }
   
   public boolean isAutoSellEnabled() {
     return this.autoSell.isState();
   }
   
   public void setAutoPayEnabled(boolean value) {
     this.autoPay.setState(value);
     if (!value) {
       this.lastNickReminderTime = 0L;
     }
   }
   
   public boolean isAutoPayEnabled() {
     return this.autoPay.isState();
   }
   
   public void setPreserveVisualsEnabled(boolean value) {
     this.preserveVisuals.setState(value);
   }
   
   public boolean isPreserveVisualsEnabled() {
     return this.preserveVisuals.isState();
   }
   
   public void setPacketsPerSecond(float value) {
     this.packetsPerSecond.setValue(value);
   }
   
   public float getPacketsPerSecond() {
     return this.packetsPerSecond.get();
   }
   
   public void setBreakRadius(float value) {
     this.breakRadius.setValue(value);
   }
   
   public float getBreakRadius() {
     return this.breakRadius.get();
   }
   
   public void setPayAmount(float value) {
     this.payAmount.setValue(value);
   }
   
   public float getPayAmount() {
     return this.payAmount.get();
   }
   
   public void setIntervalSeconds(float value) {
     this.intervalSeconds.setValue(value);
   }
   
   public float getIntervalSeconds() {
     return this.intervalSeconds.get();
   }
   
   public boolean setPayTarget(String target) {
     String trimmed = (target == null) ? "" : target.trim();
     if (trimmed.isEmpty()) {
       return false;
     }
     
     this.payTarget = trimmed;
     this.lastNickReminderTime = 0L;
     return true;
   }
   
   public String getPayTarget() {
     return this.payTarget;
   }
   
   public boolean capturePayTargetFromChat(String message) {
     return false;
   }
   
   public void clearPayTarget() {
     this.payTarget = "";
     this.lastNickReminderTime = 0L;
   }
   
   public SessionState captureState() {
     SessionState state = new SessionState();
     state.enabled(this.currentSessionEnabled);
     state.modeAlias(getModeAlias());
     state.packetsPerSecond(this.packetsPerSecond.get());
     state.breakRadius(this.breakRadius.get());
     state.swing(this.swing.isState());
     state.autoSell(this.autoSell.isState());
     state.autoPay(this.autoPay.isState());
     state.preserveVisuals(this.preserveVisuals.isState());
     state.payAmount(this.payAmount.get());
     state.intervalSeconds(this.intervalSeconds.get());
     state.payTarget(this.payTarget);
     state.targetPos(this.targetPos);
     state.lastBreakTime(this.lastBreakTime);
     state.lastPacketTime(this.lastPacketTime);
     state.lastSellTime(this.lastSellTime);
     state.lastPayTime(this.lastPayTime);
     state.lastNickReminderTime(this.lastNickReminderTime);
     state.preservedBlocks(new HashMap<>(this.preservedBlocks));
     state.lastUpdateTime(new HashMap<>(this.lastUpdateTime));
     state.managedBlocks(new HashSet<>(this.managedBlocks));
     return state;
   }
   
   public void applyState(SessionState state) {
     if (state == null) {
       resetToDefaults();
       
       return;
     } 
     this.currentSessionEnabled = state.enabled();
     setModeAlias(state.modeAlias());
     this.packetsPerSecond.setValue(state.packetsPerSecond());
     this.breakRadius.setValue(state.breakRadius());
     this.swing.setState(state.swing());
     this.autoSell.setState(state.autoSell());
     this.autoPay.setState(state.autoPay());
     this.preserveVisuals.setState(state.preserveVisuals());
     this.payAmount.setValue(state.payAmount());
     this.intervalSeconds.setValue(state.intervalSeconds());
     this.payTarget = state.payTarget();
     this.targetPos = state.targetPos();
     this.lastBreakTime = state.lastBreakTime();
     this.lastPacketTime = state.lastPacketTime();
     this.lastSellTime = state.lastSellTime();
     this.lastPayTime = state.lastPayTime();
     this.lastNickReminderTime = state.lastNickReminderTime();
     this.preservedBlocks.clear();
     this.preservedBlocks.putAll(state.preservedBlocks());
     this.lastUpdateTime.clear();
     this.lastUpdateTime.putAll(state.lastUpdateTime());
     this.managedBlocks.clear();
     this.managedBlocks.addAll(state.managedBlocks());
   }
   
   public void resetToDefaults() {
     this.currentSessionEnabled = false;
     setModeAlias("normal");
     this.packetsPerSecond.setValue(100.0F);
     this.breakRadius.setValue(4.0F);
     this.swing.setState(true);
     this.autoSell.setState(true);
     this.autoPay.setState(false);
     this.preserveVisuals.setState(true);
     this.payAmount.setValue(1000.0F);
     this.intervalSeconds.setValue(20.0F);
     this.payTarget = "";
     restoreVisualState();
     resetRuntimeState();
   }
   
   private void resetRuntimeState() {
     this.targetPos = null;
     this.lastBreakTime = 0L;
     this.lastPacketTime = 0L;
     this.lastSellTime = 0L;
     this.lastPayTime = 0L;
     this.lastNickReminderTime = 0L;
     this.preservedBlocks.clear();
     this.lastUpdateTime.clear();
     this.managedBlocks.clear();
   }
   
   public static final class SessionState {
     private boolean enabled;
     private String modeAlias = "normal";
     private float packetsPerSecond = 100.0F;
     private float breakRadius = 4.0F;
     private boolean swing = true;
     private boolean autoSell = true;
     private boolean autoPay;
     private boolean preserveVisuals = true;
     private float payAmount = 1000.0F;
     private float intervalSeconds = 20.0F;
     private String payTarget = "";
     private BlockPos targetPos;
     private long lastBreakTime;
     private long lastPacketTime;
     private long lastSellTime;
     private long lastPayTime;
     private long lastNickReminderTime;
     private Map<BlockPos, BlockState> preservedBlocks = new HashMap<>();
     private Map<BlockPos, Long> lastUpdateTime = new HashMap<>();
     private Set<BlockPos> managedBlocks = new HashSet<>();
     
     public boolean enabled() { return this.enabled; }
     public void enabled(boolean value) { this.enabled = value; }
     public String modeAlias() { return this.modeAlias; }
     public void modeAlias(String value) { this.modeAlias = (value == null) ? "normal" : value; }
     public float packetsPerSecond() { return this.packetsPerSecond; }
     public void packetsPerSecond(float value) { this.packetsPerSecond = value; }
     public float breakRadius() { return this.breakRadius; }
     public void breakRadius(float value) { this.breakRadius = value; }
     public boolean swing() { return this.swing; }
     public void swing(boolean value) { this.swing = value; }
     public boolean autoSell() { return this.autoSell; }
     public void autoSell(boolean value) { this.autoSell = value; }
     public boolean autoPay() { return this.autoPay; }
     public void autoPay(boolean value) { this.autoPay = value; }
     public boolean preserveVisuals() { return this.preserveVisuals; }
     public void preserveVisuals(boolean value) { this.preserveVisuals = value; }
     public float payAmount() { return this.payAmount; }
     public void payAmount(float value) { this.payAmount = value; }
     public float intervalSeconds() { return this.intervalSeconds; }
     public void intervalSeconds(float value) { this.intervalSeconds = value; }
     public String payTarget() { return (this.payTarget == null) ? "" : this.payTarget; }
     public void payTarget(String value) { this.payTarget = (value == null) ? "" : value; }
     public BlockPos targetPos() { return this.targetPos; }
     public void targetPos(BlockPos value) { this.targetPos = value; }
     public long lastBreakTime() { return this.lastBreakTime; }
     public void lastBreakTime(long value) { this.lastBreakTime = value; }
     public long lastPacketTime() { return this.lastPacketTime; }
     public void lastPacketTime(long value) { this.lastPacketTime = value; }
     public long lastSellTime() { return this.lastSellTime; }
     public void lastSellTime(long value) { this.lastSellTime = value; }
     public long lastPayTime() { return this.lastPayTime; }
     public void lastPayTime(long value) { this.lastPayTime = value; }
     public long lastNickReminderTime() { return this.lastNickReminderTime; }
     public void lastNickReminderTime(long value) { this.lastNickReminderTime = value; }
     public Map<BlockPos, BlockState> preservedBlocks() { return this.preservedBlocks; }
     public void preservedBlocks(Map<BlockPos, BlockState> value) { this.preservedBlocks = (value == null) ? new HashMap<>() : value; }
     public Map<BlockPos, Long> lastUpdateTime() { return this.lastUpdateTime; }
     public void lastUpdateTime(Map<BlockPos, Long> value) { this.lastUpdateTime = (value == null) ? new HashMap<>() : value; }
     public Set<BlockPos> managedBlocks() { return this.managedBlocks; } public void managedBlocks(Set<BlockPos> value) {
       this.managedBlocks = (value == null) ? new HashSet<>() : value;
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\AutoForest.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */