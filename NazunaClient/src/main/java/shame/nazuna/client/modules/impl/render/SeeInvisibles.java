package shame.nazuna.client.modules.impl.render;
 
 import net.minecraft.StatusEffects;
 import net.minecraft.PlayerEntity;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.client.modules.Module;
 
 public class SeeInvisibles
   extends Module {
   public static final float INVISIBLE_ALPHA = 0.7F;
   public static final int INVISIBLE_COLOR = Math.round(178.5F) << 24 | 0xFFFFFF;
   public static SeeInvisibles INSTANCE = new SeeInvisibles();
   
   public SeeInvisibles() {
     super("SeeInvisibles", "Показывает невидимых игроков", Module.ModuleCategory.RENDER);
   }
   
   @EventLink
   public void onUpdate(EventUpdate event) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     for (PlayerEntity player : mc.field_1687.method_18456()) {
       if (shouldRenderInvisible(player)) {
         player.method_5648(false);
       }
     } 
   }
   
   public boolean shouldRenderInvisible(PlayerEntity player) {
     return (isEnable() && mc.field_1724 != null && player != null && player != mc.field_1724 && (player
 
 
       
       .method_5767() || player.method_6059(StatusEffects.field_5905)));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\SeeInvisibles.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */