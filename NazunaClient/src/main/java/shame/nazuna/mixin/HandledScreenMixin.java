package shame.nazuna.mixin;
 
 import net.minecraft.SlotActionType;
 import net.minecraft.Slot;
 import net.minecraft.MinecraftClient;
 import net.minecraft.DrawContext;
 import net.minecraft.HandledScreen;
 import org.jetbrains.annotations.Nullable;
 import org.lwjgl.glfw.GLFW;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.client.modules.impl.player.ItemScroller;
 
 @Mixin({HandledScreen.class})
 public abstract class HandledScreenMixin
 {
   @Shadow
   @Nullable
   protected abstract Slot method_64240(double paramDouble1, double paramDouble2);
   
   @Shadow
   protected abstract void method_2383(@Nullable Slot paramclass_1735, int paramInt1, int paramInt2, SlotActionType paramclass_1713);
   
   @Inject(method = {"method_25394"}, at = {@At("HEAD")})
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
     MinecraftClient mc = MinecraftClient.method_1551();
     ItemScroller itemScroller = ItemScroller.INSTANCE;
     
     if (!itemScroller.isEnable() || mc.field_1724 == null || mc.field_1761 == null) {
       return;
     }
     
     long window = mc.method_22683().method_4490();
     boolean leftMousePressed = (GLFW.glfwGetMouseButton(window, 0) == 1);
     
     boolean shiftPressed = (GLFW.glfwGetKey(window, 340) == 1 || GLFW.glfwGetKey(window, 344) == 1);
     
     if (!leftMousePressed || !shiftPressed) {
       itemScroller.resetTimer();
       
       return;
     } 
     Slot slot = method_64240(mouseX, mouseY);
     if (slot == null || !slot.method_7681()) {
       return;
     }
     
     if (!itemScroller.canQuickMove()) {
       return;
     }
     
     method_2383(slot, slot.field_7874, 0, SlotActionType.field_7794);
   }
 }

