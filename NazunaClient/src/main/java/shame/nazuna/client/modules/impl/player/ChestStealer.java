package shame.nazuna.client.modules.impl.player;
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Optional;
 import java.util.concurrent.ThreadLocalRandom;
 import net.minecraft.class_1263;
 import net.minecraft.class_1657;
 import net.minecraft.class_1703;
 import net.minecraft.class_1707;
 import net.minecraft.class_1713;
 import net.minecraft.class_1735;
 import net.minecraft.class_2371;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class ChestStealer extends Module {
   public static ChestStealer INSTANCE = new ChestStealer();
   
   private final FloatSetting stealDelay = new FloatSetting("Задержка", 100.0F, 0.0F, 1000.0F, 1.0F);
   private final BooleanSetting randomize = new BooleanSetting("Рандомизация", false);
   
   private long lastStealTime = 0L;
   
   public ChestStealer() {
     super("ChestStealer", "Автоматически открывает сундуки и забирает из них предметы", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.stealDelay, (Setting)this.randomize });
   }
   
   @EventLink
   private void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1761 == null) {
       return;
     }
     
     class_1703 openContainer = mc.field_1724.field_7512;
     
     if (openContainer == null || openContainer == mc.field_1724.field_7498) {
       return;
     }
     
     if (!(openContainer instanceof class_1707) && !(openContainer instanceof net.minecraft.class_1722)) {
       return;
     }
 
     
     long currentTime = System.currentTimeMillis();
     long delay = (long)this.stealDelay.get();
     
     if (currentTime - this.lastStealTime < delay) {
       return;
     }
     
     class_2371 class_2371 = openContainer.field_7761;
     findValidItem((List<class_1735>)class_2371, openContainer).ifPresent(slot -> {
           if (mc.field_1724.field_7512 == openContainer) {
             mc.field_1761.method_2906(openContainer.field_7763, slot.field_7874, 0, class_1713.field_7794, (class_1657)mc.field_1724);
             this.lastStealTime = currentTime;
           } 
         });
   }
 
 
 
 
 
 
   
   private Optional<class_1735> findValidItem(List<class_1735> slots, class_1703 handler) {
     int containerSlotCount = getContainerSlotCount(handler);
     
     if (containerSlotCount <= 0 || containerSlotCount > slots.size()) {
       return Optional.empty();
     }
     
     List<class_1735> containerSlots = slots.subList(0, containerSlotCount);
     List<class_1735> validSlots = new ArrayList<>();
     
     for (class_1735 slot : containerSlots) {
       if (slot.method_7681() && !slot.method_7677().method_7960() && 
         !mc.field_1724.method_7357().method_7904(slot.method_7677())) {
         validSlots.add(slot);
       }
     } 
 
     
     if (validSlots.isEmpty()) {
       return Optional.empty();
     }
     
     if (this.randomize.isState()) {
       int randomIndex = ThreadLocalRandom.current().nextInt(validSlots.size());
       return Optional.of(validSlots.get(randomIndex));
     } 
     return Optional.of(validSlots.get(0));
   }
 
   
   private int getContainerSlotCount(class_1703 handler) {
     if (handler instanceof class_1707) { class_1707 container = (class_1707)handler;
       class_1263 inventory = container.method_7629();
       return inventory.method_5439(); }
      if (handler instanceof net.minecraft.class_1722) {
       return 5;
     }
     return 0;
   }
 
   
   public void onDisable() {
     this.lastStealTime = 0L;
     super.onDisable();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\player\ChestStealer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */