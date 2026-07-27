package shame.nazuna.client.ui;
 
 import net.minecraft.Window;
 import net.minecraft.Text;
 import net.minecraft.DrawContext;
 import net.minecraft.MathHelper;
 import net.minecraft.Screen;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.utils.animation.AnimationUtils;
 import shame.nazuna.api.utils.animation.Easings;
 import shame.nazuna.api.utils.client.ClientSoundPlayer;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.ui.clickgui.ClickGuiInputHandler;
 import shame.nazuna.client.ui.clickgui.ClickGuiRenderer;
 import shame.nazuna.client.ui.clickgui.ClickGuiSettingRenderer;
 import shame.nazuna.client.ui.clickgui.ClickGuiState;
 import shame.nazuna.client.ui.clickgui.ClickGuiThemeSelector;
 
 public class MenuPanel
   extends Screen implements QClient {
   private static final ClickGuiState SHARED_STATE = new ClickGuiState();
   private final int categoryCount = (Module.ModuleCategory.values()).length;
   private final ClickGuiState state = SHARED_STATE;
   private final ClickGuiThemeSelector themeSelector = new ClickGuiThemeSelector();
   private final ClickGuiRenderer renderer = new ClickGuiRenderer(this.state, new ClickGuiSettingRenderer(), this.themeSelector);
   private final ClickGuiInputHandler inputHandler = new ClickGuiInputHandler(this.state, this.themeSelector);
   private final AnimationUtils openAnimation = new AnimationUtils(0.0F, 7.5F, Easings.CUBIC_OUT);
   private boolean closing;
   private boolean closeSoundPlayed;
   
   public MenuPanel() {
     super(Text.method_30163("ClickGui"));
     this.state.refreshModules();
   }
   
   private Window getWindow() {
     return (mc == null) ? null : mc.method_22683();
   }
   
   private void syncLayout() {
     Window window = getWindow();
     if (window != null) {
       this.state.updatePosition(window, this.categoryCount);
     }
   }
 
 
   
   public void method_25420(DrawContext context, int mouseX, int mouseY, float delta) {}
 
   
   public void method_25394(DrawContext context, int mouseX, int mouseY, float delta) {
     Window window = getWindow();
     if (window == null) {
       return;
     }
     
     updateAnimation();
     float progress = getAnimationProgress();
     if (this.closing && progress <= 0.001F) {
       if (mc != null) {
         mc.method_1507(null);
       }
       
       return;
     } 
     this.state.updatePosition(window, this.categoryCount);
     this.state.setRenderOffsetY(getPanelOffsetY(progress));
     this.renderer.render(context, mouseX, mouseY, window, progress);
     
     super.method_25394(context, mouseX, mouseY, delta);
   }
 
   
   public boolean method_25402(double mouseX, double mouseY, int button) {
     if (this.closing) return true; 
     syncLayout();
     this.state.setRenderOffsetY(getPanelOffsetY(getAnimationProgress()));
     return (this.inputHandler.mouseClicked(mouseX, mouseY, button, getWindow()) || super
       .method_25402(mouseX, mouseY, button));
   }
 
   
   public boolean method_25406(double mouseX, double mouseY, int button) {
     if (this.closing) return true; 
     syncLayout();
     return (this.inputHandler.mouseReleased(button) || super.method_25406(mouseX, mouseY, button));
   }
 
   
   public boolean method_25403(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
     if (this.closing) return true; 
     syncLayout();
     this.state.setRenderOffsetY(getPanelOffsetY(getAnimationProgress()));
     return (this.inputHandler.mouseDragged(mouseX, mouseY, button) || super
       .method_25403(mouseX, mouseY, button, deltaX, deltaY));
   }
 
   
   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
     if (this.closing) return true; 
     syncLayout();
     this.state.setRenderOffsetY(getPanelOffsetY(getAnimationProgress()));
     return (this.inputHandler.mouseScrolled(mouseX, mouseY, verticalAmount) || super
       .method_25401(mouseX, mouseY, horizontalAmount, verticalAmount));
   }
 
   
   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
     if (this.closing) return true; 
     if (this.inputHandler.keyPressed(keyCode, modifiers)) {
       return true;
     }
     if (keyCode == 256) {
       startClosing();
       return true;
     } 
     return super.method_25404(keyCode, scanCode, modifiers);
   }
 
   
   public boolean method_25400(char chr, int modifiers) {
     if (this.closing) return true; 
     return (this.inputHandler.charTyped(chr) || super.method_25400(chr, modifiers));
   }
 
   
   public void method_25419() {
     startClosing();
   }
 
   
   public void method_25432() {
     if (!this.closeSoundPlayed) {
       this.closeSoundPlayed = true;
       ClientSoundPlayer.playSound("closegui.wav", 0.6D, 1.0F);
     } 
     super.method_25432();
   }
   
   private void startClosing() {
     if (this.closing) {
       return;
     }
     
     this.closing = true;
     this.openAnimation.setEasing(Easings.CUBIC_IN);
     
     if (!this.closeSoundPlayed) {
       this.closeSoundPlayed = true;
       ClientSoundPlayer.playSound("closegui.wav", 0.6D, 1.0F);
     } 
   }
   
   private void updateAnimation() {
     if (this.closing) {
       this.openAnimation.update(0.0F);
     } else {
       this.openAnimation.setEasing(Easings.CUBIC_OUT);
       this.openAnimation.update(1.0F);
     } 
   }
   
   private float getAnimationProgress() {
     return MathHelper.method_15363(this.openAnimation.getValue(), 0.0F, 1.0F);
   }
   
   private float getPanelOffsetY(float progress) {
     return (1.0F - progress) * 22.0F;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\clien\\ui\MenuPanel.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */