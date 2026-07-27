package shame.nazuna.api.storages.implement;

import java.io.Serializable;

class Frame implements Serializable {
  private static final long serialVersionUID = 7L;
  
  float deltaYaw;
  
  float deltaPitch;
  
  float angleYaw;
  
  float anglePitch;
  
  double distance;
  
  boolean hasTarget;
  
  float smoothness;
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\NeuroAuraStorage$Frame.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */