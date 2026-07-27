package shame.nazuna.api.storages.implement;

import java.io.Serializable;
import java.util.List;
import shame.nazuna.api.storages.implement.helpertstorages.NeuroPattern;

class SaveData implements Serializable {
  private static final long serialVersionUID = 7L;
  
  List<NeuroPattern> patterns;
  
  List<NeuroAuraStorage.Frame> frames;
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\NeuroAuraStorage$SaveData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */