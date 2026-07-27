package shame.nazuna.api.utils.rpc.utils;
 
 import java.time.OffsetDateTime;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Builder
 {
   private final DiscordRichPresence richPresence = new DiscordRichPresence();
   
   public Builder setSmallImage(String var1) {
     return setSmallImage(var1, "");
   }
   
   public Builder setDetails(String var1) {
     if (var1 != null && !var1.isEmpty()) {
       this.richPresence.details = var1.substring(0, Math.min(var1.length(), 128));
     }
     
     return this;
   }
   
   public Builder setLargeImage(String var1, String var2) {
     this.richPresence.largeImageKey = var1;
     this.richPresence.largeImageText = var2;
     return this;
   }
   
   public Builder setState(String var1) {
     if (var1 != null && !var1.isEmpty()) {
       this.richPresence.state = var1.substring(0, Math.min(var1.length(), 128));
     }
     
     return this;
   }
   
   public Builder setInstance(boolean var1) {
     if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty()) && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
       this.richPresence.instance = var1 ? 1 : 0;
     }
     return this;
   }
   
   public Builder setButtons(RPCButton var1) {
     return setButtons(Collections.singletonList(var1));
   }
   
   public Builder setSmallImage(String var1, String var2) {
     this.richPresence.smallImageKey = var1;
     this.richPresence.smallImageText = var2;
     return this;
   }
   
   public Builder setParty(String var1, int var2, int var3) {
     if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty()) && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
       this.richPresence.partyId = var1;
       this.richPresence.partySize = var2;
       this.richPresence.partyMax = var3;
     } 
     return this;
   }
   
   public Builder setButtons(List<RPCButton> var1) {
     if (var1 != null && !var1.isEmpty()) {
       int var2 = Math.min(var1.size(), 2);
       this.richPresence.button_label_1 = ((RPCButton)var1.get(0)).getLabel();
       this.richPresence.button_url_1 = ((RPCButton)var1.get(0)).getUrl();
       if (var2 == 2) {
         this.richPresence.button_label_2 = ((RPCButton)var1.get(1)).getLabel();
         this.richPresence.button_url_2 = ((RPCButton)var1.get(1)).getUrl();
       } 
     } 
     
     return this;
   }
   
   public Builder setStartTimestamp(OffsetDateTime var1) {
     this.richPresence.startTimestamp = var1.toEpochSecond();
     return this;
   }
   
   public Builder setSecrets(String var1, String var2, String var3) {
     if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty()) && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
       this.richPresence.matchSecret = var1;
       this.richPresence.joinSecret = var2;
       this.richPresence.spectateSecret = var3;
     } 
     return this;
   }
   
   public void setButtons(RPCButton var1, RPCButton var2) {
     setButtons(Arrays.asList(new RPCButton[] { var1, var2 }));
   }
   
   public void setStartTimestamp(long var1) {
     this.richPresence.startTimestamp = var1;
   }
   
   public Builder setSecrets(String var1, String var2) {
     if ((this.richPresence.button_label_1 == null || !this.richPresence.button_label_1.isEmpty()) && (this.richPresence.button_label_2 == null || !this.richPresence.button_label_2.isEmpty())) {
       this.richPresence.joinSecret = var1;
       this.richPresence.spectateSecret = var2;
     } 
     return this;
   }
   
   public Builder setEndTimestamp(long var1) {
     this.richPresence.endTimestamp = var1;
     return this;
   }
   
   public Builder setEndTimestamp(OffsetDateTime var1) {
     this.richPresence.endTimestamp = var1.toEpochSecond();
     return this;
   }
   
   public Builder setLargeImage(String var1) {
     return setLargeImage(var1, "");
   }
   
   public DiscordRichPresence build() {
     return this.richPresence;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rp\\utils\DiscordRichPresence$Builder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */