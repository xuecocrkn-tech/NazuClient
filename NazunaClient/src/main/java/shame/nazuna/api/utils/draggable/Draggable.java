package shame.nazuna.api.utils.draggable;
 
 import com.google.gson.annotations.Expose;
 import com.google.gson.annotations.SerializedName;
 import net.minecraft.Window;
 import net.minecraft.MinecraftClient;
 import net.minecraft.MatrixStack;
 import net.minecraft.RotationAxis;
 import shame.nazuna.api.utils.math.HoveringUtils;
 import shame.nazuna.api.utils.math.MathUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.client.modules.Module;
 
 public class Draggable implements QClient {
   @Expose
   @SerializedName("x")
   private float xPos;
   @Expose
   @SerializedName("y")
   private float yPos;
   public float initialXVal;
   public float initialYVal;
   private float startX;
   private float startY;
   private boolean dragging;
   
   public float getWidth() { return this.width; }
   public String getName() {
     return this.name;
   }
   public Module getModule() {
     return this.module;
   }
 
 
 
 
 
 
   
   private float lineAlpha = 0.0F; private long lastUpdateTime; private boolean snapToCenter; private boolean snapToCenterx; private boolean snapToCenter2x; private boolean snapToCenter3x; private boolean snapToCenter4x;
   private boolean snapToCenter5x;
   private boolean snapToCenter2;
   private boolean snapToCenter3;
   private boolean snapToCenter4;
   private boolean snapToCenter5;
   private static final float LERP_SPEED = 0.19F;
   private static final float MAX_TILT_DEGREES = 25.0F;
   private static final float TILT_FROM_MOUSE_DELTA = 4.0F;
   private static final float DRAG_TILT_LERP = 0.14F;
   private static final float RELEASE_TILT_LERP = 0.1F;
   private static final float TILT_DELTA_SMOOTHING = 0.18F;
   private static final float TILT_TARGET_SMOOTHING = 0.22F;
   private static final float TILT_DEADZONE = 0.18F;
   private static final float DRAG_SCALE_MULTIPLIER = 1.01F;
   private static final float DRAG_SCALE_LERP = 0.1F;
   private static final float RELEASE_SCALE_LERP = 0.02F;
   private float dragTiltDegrees;
   private float targetTiltDegrees;
   private float smoothedMouseDeltaX;
   private float lastDragMouseX;
   private boolean hasLastDragMouseX;
   private boolean tiltMatrixPushed;
   private float dragScale = 1.0F;
   private float targetScale = 1.0F;
   
   public Draggable(Module module, String name, float initialXVal, float initialYVal) {
     this.module = module;
     this.name = name;
     this.xPos = initialXVal;
     this.yPos = initialYVal;
     this.initialXVal = initialXVal;
     this.initialYVal = initialYVal;
   }
   
   public float getX() {
     return this.xPos;
   }
   
   public void setX(float x) {
     this.xPos = x;
   }
   
   public float getY() {
     return this.yPos;
   }
   
   public void setY(float y) {
     this.yPos = y;
   }
   
   private Vec2i getMouse(int mouseX, int mouseY) {
     MinecraftClient client = MinecraftClient.method_1551();
     Window window = (client == null) ? null : client.method_22683();
     double scaleFactor = (window == null) ? 1.0D : window.method_4495();
     return new Vec2i((int)(mouseX * scaleFactor / 2.0D), (int)(mouseY * scaleFactor / 2.0D));
   }
   
   public final void onDraw(int mouseX, int mouseY, Window res, MatrixStack ms) {
     Vec2i fixed = getMouse(mouseX, mouseY);
     mouseX = fixed.getX();
     mouseY = fixed.getY();
     
     float centerX = res.method_4486() / 2.0F;
     float centerY = res.method_4502() / 2.0F;
     
     float centerX2 = res.method_4486() / 4.0F;
     float centerY2 = res.method_4502() / 4.0F;
     float centerX3 = res.method_4486() / 8.0F;
     float centerY3 = res.method_4502() / 8.0F;
     
     float centerX4 = res.method_4486() / 1.15F;
     float centerY4 = res.method_4502() / 1.15F;
     float centerX5 = res.method_4486() / 1.35F;
     float centerY5 = res.method_4502() / 1.35F;
     
     this.snapToCenter = this.snapToCenterx = this.snapToCenter2x = this.snapToCenter3x = this.snapToCenter4x = this.snapToCenter5x = this.snapToCenter2 = this.snapToCenter3 = this.snapToCenter4 = this.snapToCenter5 = false;
     
     if (this.dragging) {
       this.targetScale = 1.01F;
       if (this.hasLastDragMouseX) {
         float mouseDeltaX = mouseX - this.lastDragMouseX;
         if (Math.abs(mouseDeltaX) < 0.18F) {
           mouseDeltaX = 0.0F;
         }
         
         this.smoothedMouseDeltaX = MathUtils.lerp(this.smoothedMouseDeltaX, mouseDeltaX, 0.18F);
         float desiredTilt = Math.max(-25.0F, Math.min(25.0F, this.smoothedMouseDeltaX * 4.0F));
         this.targetTiltDegrees = MathUtils.lerp(this.targetTiltDegrees, desiredTilt, 0.22F);
       } 
       this.lastDragMouseX = mouseX;
       this.hasLastDragMouseX = true;
       
       this.targetXPos = mouseX - this.startX;
       this.targetYPos = mouseY - this.startY;
       
       boolean snapped = false;
       
       if (Math.abs(this.targetXPos + this.width / 2.0F - centerX) < 10.0F) {
         this.targetXPos = centerX - this.width / 2.0F;
         this.snapToCenterx = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetYPos + this.height / 2.0F - centerY) < 10.0F) {
         this.targetYPos = centerY - this.height / 2.0F;
         this.snapToCenter = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetXPos + this.width / 2.0F - centerX2) < 10.0F) {
         this.targetXPos = centerX2 - this.width / 2.0F;
         this.snapToCenter2x = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetYPos + this.height / 2.0F - centerY2) < 10.0F) {
         this.targetYPos = centerY2 - this.height / 2.0F;
         this.snapToCenter2 = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetXPos + this.width / 2.0F - centerX3) < 10.0F) {
         this.targetXPos = centerX3 - this.width / 2.0F;
         this.snapToCenter3x = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetYPos + this.height / 2.0F - centerY3) < 10.0F) {
         this.targetYPos = centerY3 - this.height / 2.0F;
         this.snapToCenter3 = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetXPos + this.width / 2.0F - centerX4) < 10.0F) {
         this.targetXPos = centerX4 - this.width / 2.0F;
         this.snapToCenter4x = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetYPos + this.height / 2.0F - centerY4) < 10.0F) {
         this.targetYPos = centerY4 - this.height / 2.0F;
         this.snapToCenter4 = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetXPos + this.width / 2.0F - centerX5) < 10.0F) {
         this.targetXPos = centerX5 - this.width / 2.0F;
         this.snapToCenter5x = true;
         snapped = true;
       } 
       
       if (Math.abs(this.targetYPos + this.height / 2.0F - centerY5) < 10.0F) {
         this.targetYPos = centerY5 - this.height / 2.0F;
         this.snapToCenter5 = true;
         snapped = true;
       } 
       
       if (this.targetXPos + this.width > res.method_4486()) {
         this.targetXPos = res.method_4486() - this.width;
       }
       if (this.targetYPos + this.height > res.method_4502()) {
         this.targetYPos = res.method_4502() - this.height;
       }
       if (this.targetXPos < 0.0F) {
         this.targetXPos = 0.0F;
       }
       if (this.targetYPos < 0.0F) {
         this.targetYPos = 0.0F;
       }
       
       this.xPos = MathUtils.lerp(this.xPos, this.targetXPos, 0.19F);
       this.yPos = MathUtils.lerp(this.yPos, this.targetYPos, 0.19F);
       
       updateLineAlpha(snapped);
     } else {
       this.targetScale = 1.0F;
       this.targetTiltDegrees = 0.0F;
       this.smoothedMouseDeltaX = MathUtils.lerp(this.smoothedMouseDeltaX, 0.0F, 0.18F);
       this.hasLastDragMouseX = false;
       updateLineAlpha(false);
     } 
     updateTilt();
     
     drawCenterLines(ms, res);
   }
   
   private void updateTilt() {
     float lerp = this.dragging ? 0.14F : 0.1F;
     this.dragTiltDegrees = MathUtils.lerp(this.dragTiltDegrees, this.targetTiltDegrees, lerp);
     if (!this.dragging && Math.abs(this.dragTiltDegrees) < 0.02F) {
       this.dragTiltDegrees = 0.0F;
     }
     
     float scaleLerp = this.dragging ? 0.1F : 0.02F;
     this.dragScale = MathUtils.lerp(this.dragScale, this.targetScale, scaleLerp);
     if (!this.dragging && Math.abs(this.dragScale - 1.0F) < 0.002F) {
       this.dragScale = 1.0F;
     }
   }
   
   public void beginRenderTilt(MatrixStack ms) {
     updateTilt();
     this.tiltMatrixPushed = false;
     if (Math.abs(this.dragTiltDegrees) < 0.05F && Math.abs(this.dragScale - 1.0F) < 0.002F) {
       return;
     }
     
     float centerX = this.xPos + this.width / 2.0F;
     float centerY = this.yPos + this.height / 2.0F;
     
     ms.method_22903();
     ms.method_46416(centerX, centerY, 0.0F);
     ms.method_22907(RotationAxis.field_40718.rotationDegrees(this.dragTiltDegrees));
     ms.method_22905(this.dragScale, this.dragScale, 1.0F);
     ms.method_46416(-centerX, -centerY, 0.0F);
     this.tiltMatrixPushed = true;
   }
   
   public void endRenderTilt(MatrixStack ms) {
     if (this.tiltMatrixPushed) {
       ms.method_22909();
       this.tiltMatrixPushed = false;
     } 
   }
   
   private void updateLineAlpha(boolean active) {
     long currentTime = System.currentTimeMillis();
     float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0F;
     this.lastUpdateTime = currentTime;
     
     float fadeSpeed = 2.0F;
     float fadeOutSpeed = 2.0F;
     
     if (active) {
       this.lineAlpha += deltaTime * fadeSpeed;
       if (this.lineAlpha > 1.0F) {
         this.lineAlpha = 1.0F;
       }
     } else {
       this.lineAlpha -= deltaTime * fadeOutSpeed;
       if (this.lineAlpha < 0.0F) {
         this.lineAlpha = 0.0F;
       }
     } 
   }
   
   private void drawCenterLines(MatrixStack ms, Window res) {
     if (this.lineAlpha > 0.0F) {
       float centerX = res.method_4486() / 2.0F;
       float centerY = res.method_4502() / 2.0F;
       float centerX2 = res.method_4486() / 4.0F;
       float centerY2 = res.method_4502() / 4.0F;
       float centerX3 = res.method_4486() / 8.0F;
       float centerY3 = res.method_4502() / 8.0F;
       float centerX4 = res.method_4486() / 1.15F;
       float centerY4 = res.method_4502() / 1.15F;
       float centerX5 = res.method_4486() / 1.35F;
       float centerY5 = res.method_4502() / 1.35F;
       
       int color = (int)(this.lineAlpha * 255.0F) << 24 | 0xFFFFFF;
       if (this.snapToCenterx) {
         RenderUtils.drawRoundedRect(ms, centerX - 0.33333334F, 0.0F, 1.0F, res.method_4502(), 1.0F, color);
       }
       if (this.snapToCenter) {
         RenderUtils.drawRoundedRect(ms, 0.0F, centerY - 0.33333334F, res.method_4486(), 1.0F, 1.0F, color);
       }
       if (this.snapToCenter2x) {
         RenderUtils.drawRoundedRect(ms, centerX2 - 0.33333334F, 0.0F, 1.0F, res.method_4502(), 1.0F, color);
       }
       if (this.snapToCenter2) {
         RenderUtils.drawRoundedRect(ms, 0.0F, centerY2 - 0.33333334F, res.method_4486(), 1.0F, 1.0F, color);
       }
       if (this.snapToCenter3x) {
         RenderUtils.drawRoundedRect(ms, centerX3 - 0.33333334F, 0.0F, 1.0F, res.method_4502(), 1.0F, color);
       }
       if (this.snapToCenter3) {
         RenderUtils.drawRoundedRect(ms, 0.0F, centerY3 - 0.33333334F, res.method_4486(), 1.0F, 1.0F, color);
       }
       if (this.snapToCenter4x) {
         RenderUtils.drawRoundedRect(ms, centerX4 - 0.33333334F, 0.0F, 1.0F, res.method_4502(), 1.0F, color);
       }
       if (this.snapToCenter4) {
         RenderUtils.drawRoundedRect(ms, 0.0F, centerY4 - 0.33333334F, res.method_4486(), 1.0F, 1.0F, color);
       }
       if (this.snapToCenter5x) {
         RenderUtils.drawRoundedRect(ms, centerX5 - 0.33333334F, 0.0F, 1.0F, res.method_4502(), 1.0F, color);
       }
       if (this.snapToCenter5)
         RenderUtils.drawRoundedRect(ms, 0.0F, centerY5 - 0.33333334F, res.method_4486(), 1.0F, 1.0F, color); 
     } 
   }
   
   public final boolean onClick(double mouseX, double mouseY, int button) {
     if (button == 0 && HoveringUtils.isInRegion(mouseX, mouseY, this.xPos, this.yPos, this.width, this.height)) {
       this.dragging = true;
       this.targetScale = 1.01F;
       this.startX = (int)(mouseX - this.xPos);
       this.startY = (int)(mouseY - this.yPos);
       this.smoothedMouseDeltaX = 0.0F;
       this.hasLastDragMouseX = false;
       this.lastUpdateTime = System.currentTimeMillis();
       return true;
     } 
     return false;
   }
   
   public final void onRelease(int button) {
     if (button == 0) {
       this.dragging = false;
       this.targetScale = 1.0F;
       this.targetTiltDegrees = 0.0F;
       this.smoothedMouseDeltaX = 0.0F;
       this.hasLastDragMouseX = false;
     } 
   }
 }

