package shame.nazuna.api.utils.rpc.callbacks;

import com.sun.jna.Callback;
import shame.nazuna.api.utils.rpc.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
  void apply(DiscordUser paramDiscordUser);
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rpc\callbacks\JoinRequestCallback.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */