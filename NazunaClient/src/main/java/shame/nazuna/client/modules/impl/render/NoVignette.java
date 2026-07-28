package shame.nazuna.client.modules.impl.render;
 
 import shame.nazuna.client.modules.Module;
 
 public class NoVignette
   extends Module {
   public static NoVignette INSTANCE = new NoVignette();
   public NoVignette() {
     super("NoVignette", "Убирает затемнения на краях экрана", Module.ModuleCategory.RENDER);
   }
 }

