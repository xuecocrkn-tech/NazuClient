package shame.nazuna.client.modules.impl.misc;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.UUID;
 import net.minecraft.Formatting;
 import net.minecraft.PlayerEntity;
 import net.minecraft.BlockPos;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.Module;
 
 public class LeaveTracker extends Module {
   public static LeaveTracker INSTANCE = new LeaveTracker();
   
   private final Map<UUID, TrackedPlayer> trackedPlayers = new HashMap<>();
   private ClientWorld lastWorld;
   private boolean initialized;
   
   public LeaveTracker() {
     super("LeaveTracker", "Пишет координаты ливнутых игроков из прогрузки", Module.ModuleCategory.MISC);
   }
 
   
   public void onDisable() {
     this.trackedPlayers.clear();
     this.initialized = false;
     super.onDisable();
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     if (mc.field_1687 != this.lastWorld) {
       this.lastWorld = mc.field_1687;
       this.trackedPlayers.clear();
       this.initialized = false;
     } 
     
     if (!this.initialized) {
       snapshotPlayers();
       this.initialized = true;
       
       return;
     } 
     Set<UUID> seenPlayers = new HashSet<>();
     
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (player == mc.field_1724 || !player.method_5805())
         continue; 
       UUID uuid = player.method_5667();
       seenPlayers.add(uuid);
       this.trackedPlayers.put(uuid, new TrackedPlayer(player.method_5477().getString(), player.method_24515()));
     } 
     
     Iterator<Map.Entry<UUID, TrackedPlayer>> iterator = this.trackedPlayers.entrySet().iterator();
     while (iterator.hasNext()) {
       Map.Entry<UUID, TrackedPlayer> entry = iterator.next();
       
       if (seenPlayers.contains(entry.getKey()))
         continue; 
       TrackedPlayer tracked = entry.getValue();
       double distSq = mc.field_1724.method_5649(tracked.pos
           .method_10263(), tracked.pos
           .method_10264(), tracked.pos
           .method_10260());
 
       
       if (distSq < 65536.0D) {
         ChatUtils.sendMessage(String.valueOf(Formatting.field_1080) + String.valueOf(Formatting.field_1080) + tracked.name + " ливнул на " + String.valueOf(Formatting.field_1068) + String.valueOf(Formatting.field_1080) + " " + tracked.pos
 
             
             .method_10263() + " " + tracked.pos
             .method_10264());
       }
 
       
       iterator.remove();
     } 
   }
   
   private void snapshotPlayers() {
     this.trackedPlayers.clear();
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (player == mc.field_1724 || !player.method_5805())
         continue;  this.trackedPlayers.put(player
           .method_5667(), new TrackedPlayer(player
             .method_5477().getString(), player.method_24515()));
     } 
   }
   private static final class TrackedPlayer extends Record { private final String name; private final BlockPos pos;
     
     private TrackedPlayer(String name, BlockPos pos) { this.name = name; this.pos = pos; } public final String toString() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> toString : (Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;)Ljava/lang/String;
       //   6: areturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #96	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer; } public String name() { return this.name; } public final int hashCode() { // Byte code:
       //   0: aload_0
       //   1: <illegal opcode> hashCode : (Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;)I
       //   6: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #96	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	7	0	this	Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer; } public final boolean equals(Object o) { // Byte code:
       //   0: aload_0
       //   1: aload_1
       //   2: <illegal opcode> equals : (Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;Ljava/lang/Object;)Z
       //   7: ireturn
       // Line number table:
       //   Java source line number -> byte code offset
       //   #96	-> 0
       // Local variable table:
       //   start	length	slot	name	descriptor
       //   0	8	0	this	Lshame/astra/client/modules/impl/misc/LeaveTracker$TrackedPlayer;
       //   0	8	1	o	Ljava/lang/Object; } public BlockPos pos() { return this.pos; }
      }
 
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\misc\LeaveTracker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */