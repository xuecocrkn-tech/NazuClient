package shame.nazuna.client.ui.autobuy;
 
 import net.minecraft.class_2561;
 import net.minecraft.class_332;
 import net.minecraft.class_437;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 
 public class AutoBuy
   extends class_437 implements QClient {
   private final float WIDTH = 170.0F, HEIGHT = 240.0F;
 
   
   public AutoBuy() {
     super(class_2561.method_30163("AutoBuy"));
   }
 
 
   
   public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {}
 
   
   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
     float X = mw.method_4486() / 2.0F - 170.0F;
     float Y = mw.method_4502() / 2.0F - 240.0F;
     
     RenderUtils.drawGradientRect(context.method_51448(), X, Y, 170.0F, 240.0F, 5.0F, ColorUtils.getThemeColor(), ColorUtils.darken(ColorUtils.getThemeColor(), 0.5F), true);
 
 
 
     
     super.method_25394(context, mouseX, mouseY, delta);
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\clien\\ui\autobuy\AutoBuy.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */