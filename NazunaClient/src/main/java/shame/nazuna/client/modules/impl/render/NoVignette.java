package shame.nazuna.client.modules.impl.render;
 
 import shame.nazuna.client.modules.Module;
 
 public class NoVignette
   extends Module {
   public static NoVignette INSTANCE = new NoVignette();
   public NoVignette() {
     super("NoVignette", "Убирает затемнения на краях экрана", Module.ModuleCategory.RENDER);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\NoVignette.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */