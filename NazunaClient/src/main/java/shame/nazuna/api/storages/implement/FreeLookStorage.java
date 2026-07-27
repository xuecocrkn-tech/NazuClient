package shame.nazuna.api.storages.implement;
 import net.minecraft.MathHelper;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventLook;
 import shame.nazuna.api.events.implement.EventRotation;
 
 public class FreeLookStorage implements QClient {
   private static boolean active;
   private static float freeYaw;
   private static float freePitch;
   
   public FreeLookStorage() {
     EventInvoker.register(this);
   }
    public static boolean isActive() {
     return active;
   }
   
   @EventLink
   public void onLook(EventLook event) {
     if (active) {
       rotateTowards(event.getYaw(), event.getPitch());
       event.cancel();
     } 
   }
   
   @EventLink
   public void onRotation(EventRotation event) {
     if (active) {
       event.setYaw(freeYaw);
       event.setPitch(freePitch);
     } else {
       freeYaw = event.getYaw();
       freePitch = event.getPitch();
     } 
   }
   
   private void rotateTowards(double targetYaw, double targetPitch) {
     freePitch = MathHelper.method_15363((float)(freePitch + targetPitch * 0.15D), -90.0F, 90.0F);
     freeYaw = (float)(freeYaw + targetYaw * 0.15D);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\FreeLookStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */