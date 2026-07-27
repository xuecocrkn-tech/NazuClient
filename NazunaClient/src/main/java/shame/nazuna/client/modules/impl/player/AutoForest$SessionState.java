package shame.nazuna.client.modules.impl.player;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 import net.minecraft.class_2338;
 import net.minecraft.class_2680;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class SessionState
 {
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
   private class_2338 targetPos;
   private long lastBreakTime;
   private long lastPacketTime;
   private long lastSellTime;
   private long lastPayTime;
   private long lastNickReminderTime;
   private Map<class_2338, class_2680> preservedBlocks = new HashMap<>();
   private Map<class_2338, Long> lastUpdateTime = new HashMap<>();
   private Set<class_2338> managedBlocks = new HashSet<>();
   
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
   public class_2338 targetPos() { return this.targetPos; }
   public void targetPos(class_2338 value) { this.targetPos = value; }
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
   public Map<class_2338, class_2680> preservedBlocks() { return this.preservedBlocks; }
   public void preservedBlocks(Map<class_2338, class_2680> value) { this.preservedBlocks = (value == null) ? new HashMap<>() : value; }
   public Map<class_2338, Long> lastUpdateTime() { return this.lastUpdateTime; }
   public void lastUpdateTime(Map<class_2338, Long> value) { this.lastUpdateTime = (value == null) ? new HashMap<>() : value; }
   public Set<class_2338> managedBlocks() { return this.managedBlocks; } public void managedBlocks(Set<class_2338> value) {
     this.managedBlocks = (value == null) ? new HashSet<>() : value;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\AutoForest$SessionState.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */