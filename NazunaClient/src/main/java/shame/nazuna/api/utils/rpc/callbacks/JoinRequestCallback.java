package shame.nazuna.api.utils.rpc.callbacks;

import com.sun.jna.Callback;
import shame.nazuna.api.utils.rpc.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
  void apply(DiscordUser paramDiscordUser);
}

