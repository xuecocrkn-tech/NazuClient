package shame.nazuna.api.utils.rpc.utils;
 
 import shame.nazuna.api.utils.rpc.callbacks.DisconnectedCallback;
 import shame.nazuna.api.utils.rpc.callbacks.ErroredCallback;
 import shame.nazuna.api.utils.rpc.callbacks.JoinGameCallback;
 import shame.nazuna.api.utils.rpc.callbacks.JoinRequestCallback;
 import shame.nazuna.api.utils.rpc.callbacks.ReadyCallback;
 import shame.nazuna.api.utils.rpc.callbacks.SpectateGameCallback;
 
 
 
 
 
 
 
 
 
 
 
 public class Builder
 {
   private final DiscordEventHandlers handlers = new DiscordEventHandlers();
   
   public DiscordEventHandlers build() {
     return this.handlers;
   }
   
   public Builder disconnected(DisconnectedCallback var1) {
     this.handlers.disconnected = var1;
     return this;
   }
   
   public Builder errored(ErroredCallback var1) {
     this.handlers.errored = var1;
     return this;
   }
   
   public Builder ready(ReadyCallback var1) {
     this.handlers.ready = var1;
     return this;
   }
   
   public Builder joinRequest(JoinRequestCallback var1) {
     this.handlers.joinRequest = var1;
     return this;
   }
   
   public Builder joinGame(JoinGameCallback var1) {
     this.handlers.joinGame = var1;
     return this;
   }
   
   public Builder spectateGame(SpectateGameCallback var1) {
     this.handlers.spectateGame = var1;
     return this;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rp\\utils\DiscordEventHandlers$Builder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */