package shame.nazuna.api.utils.rpc.utils;
 
 import java.io.Serializable;
 
 public class RPCButton implements Serializable {
   private final String url;
   private final String label;
   
   public static RPCButton create(String label, String url) {
     label = label.substring(0, Math.min(label.length(), 31));
     return new RPCButton(label, url);
   }
   
   protected RPCButton(String label, String url) {
     this.label = label;
     this.url = url;
   }
   
   public String getUrl() {
     return this.url;
   }
   
   public String getLabel() {
     return this.label;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\rp\\utils\RPCButton.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */