package shame.nazuna.client.ui.autobuy;
 
 import net.minecraft.Text;
 import net.minecraft.DrawContext;
 import net.minecraft.Screen;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 
 public class AutoBuy
   extends Screen implements QClient {
   private final float WIDTH = 170.0F, HEIGHT = 240.0F;
 
   
   public AutoBuy() {
     super(Text.method_30163("AutoBuy"));
   }
 
 
   
   public void method_25420(DrawContext context, int mouseX, int mouseY, float delta) {}
 
   
   public void method_25394(DrawContext context, int mouseX, int mouseY, float delta) {
     float X = mw.method_4486() / 2.0F - 170.0F;
     float Y = mw.method_4502() / 2.0F - 240.0F;
     
     RenderUtils.drawGradientRect(context.method_51448(), X, Y, 170.0F, 240.0F, 5.0F, ColorUtils.getThemeColor(), ColorUtils.darken(ColorUtils.getThemeColor(), 0.5F), true);
 
 
 
     
     super.method_25394(context, mouseX, mouseY, delta);
   }
 }

