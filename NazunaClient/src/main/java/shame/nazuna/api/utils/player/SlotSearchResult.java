package shame.nazuna.api.utils.player;
 public final class SlotSearchResult extends Record implements QClient {
   private final int slot;
   private final boolean found;
   private final ItemStack stack;
   
   public SlotSearchResult(int slot, boolean found, ItemStack stack) { this.slot = slot; this.found = found; this.stack = stack; } public final String toString() { // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> toString : (Lshame/astra/api/utils/player/SlotSearchResult;)Ljava/lang/String;
     //   6: areturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #7	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/api/utils/player/SlotSearchResult; } public int slot() { return this.slot; } public final int hashCode() { // Byte code:
     //   0: aload_0
     //   1: <illegal opcode> hashCode : (Lshame/astra/api/utils/player/SlotSearchResult;)I
     //   6: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #7	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	7	0	this	Lshame/astra/api/utils/player/SlotSearchResult; } public final boolean equals(Object o) { // Byte code:
     //   0: aload_0
     //   1: aload_1
     //   2: <illegal opcode> equals : (Lshame/astra/api/utils/player/SlotSearchResult;Ljava/lang/Object;)Z
     //   7: ireturn
     // Line number table:
     //   Java source line number -> byte code offset
     //   #7	-> 0
     // Local variable table:
     //   start	length	slot	name	descriptor
     //   0	8	0	this	Lshame/astra/api/utils/player/SlotSearchResult;
     //   0	8	1	o	Ljava/lang/Object; } public boolean found() { return this.found; } public ItemStack stack() { return this.stack; }
    private static final SlotSearchResult NOT_FOUND_RESULT = new SlotSearchResult(-1, false, ItemStack.field_8037);
   
   public static SlotSearchResult notFound() {
     return NOT_FOUND_RESULT;
   }
   @NotNull
   public static SlotSearchResult inOffhand(ItemStack stack) {
     return new SlotSearchResult(999, true, stack);
   }
   
   public boolean isHolding() {
     if (mc.field_1724 == null) return false; 
     return (isOffhand() || (mc.field_1724.method_31548()).field_7545 == this.slot);
   }
   
   public boolean isOffhand() {
     return (this.slot == 999);
   }
   
   public boolean isInHotBar() {
     return (this.slot >= 0 && this.slot < 9);
   }
   
   public void switchTo() {
     if (this.found && isInHotBar()) {
       HotbarUtil.switchTo(this.slot);
     }
   }
   
   public void switchToSilent() {
     if (this.found && isInHotBar())
       HotbarUtil.switchToSilent(this.slot); 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\player\SlotSearchResult.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */