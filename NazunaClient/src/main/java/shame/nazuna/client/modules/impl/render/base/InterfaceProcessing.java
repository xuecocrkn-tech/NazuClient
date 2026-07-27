package shame.nazuna.client.modules.impl.render.base;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.utils.draggable.Draggable;
 
 public class InterfaceProcessing implements QClient {
   public final Draggable draggable;
   
   public InterfaceProcessing(Draggable draggable) {
     this.unusualRectType = true;
     this.draggable = draggable;
   } private boolean unusualRectType; public boolean isUnusualRectType() {
     return this.unusualRectType;
   }
   
   public void setUnusualRectType(boolean unusualRectType) {
     this.unusualRectType = unusualRectType;
   }
   
   public void onUpdate(EventUpdate eventUpdate) {}
   
   public void onRender(EventRender.Default eventRender) {}
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\InterfaceProcessing.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */