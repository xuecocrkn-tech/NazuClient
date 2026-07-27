package shame.nazuna.api.utils.rpc.utils;
 
 import com.sun.jna.Structure;
 import java.util.Arrays;
 import java.util.List;
 
 public class DiscordUser
   extends Structure {
   public String userId;
   public String username;
   @Deprecated
   public String discriminator;
   public String avatar;
   
   protected List<String> getFieldOrder() {
     return Arrays.asList(new String[] { "userId", "username", "discriminator", "avatar" });
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rp\\utils\DiscordUser.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */