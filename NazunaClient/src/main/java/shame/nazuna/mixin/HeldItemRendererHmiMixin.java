package shame.nazuna.mixin;
 import java.util.Random;
 import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
 import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
 import net.minecraft.Hand;
 import net.minecraft.Arm;
 import net.minecraft.LivingEntity;
 import net.minecraft.BlockItem;
 import net.minecraft.CrossbowItem;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.UseAction;
 import net.minecraft.Blocks;
 import net.minecraft.Block;
 import net.minecraft.ParticleEffect;
 import net.minecraft.Vec3d;
 import net.minecraft.BlockState;
 import net.minecraft.Property;
 import net.minecraft.MinecraftClient;
 import net.minecraft.BlockTags;
 import net.minecraft.ItemTags;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.VertexConsumerProvider;
 import net.minecraft.AbstractClientPlayerEntity;
 import net.minecraft.ClientPlayerEntity;
 import net.minecraft.HeldItemRenderer;
 import net.minecraft.BlockRenderManager;
 import net.minecraft.RotationAxis;
 import net.minecraft.ModelTransformationMode;
 import net.minecraft.AttributeModifiersComponent;
 import net.minecraft.DataComponentTypes;
 import org.spongepowered.asm.mixin.Final;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.impl.render.SwingAnimations;
 import shame.nazuna.client.modules.impl.render.ViewModel;
 
 @Mixin({HeldItemRenderer.class})
 public abstract class HeldItemRendererHmiMixin {
   private boolean repPower = false;
   private float prevAge = 0.0F;
   private double previousRotation = 0.0D;
   private float swingAngleY = 0.0F;
   private float swingAngleX = 0.0F;
   private float swingVelocityY = 0.0F;
   private float swingVelocityX = 0.0F;
   private float swingVelocityZ = 0.0F;
   private static final float GRAVITY = 0.1F;
   private static final float DAMPING = 0.88F;
   private static final float SENSITIVITY = 0.015F;
   private float vertAngleY = 0.0F;
   private float vertVelocityY = 0.0F;
   private float vertVelocityYSlime = 0.0F;
   private float vertAngleYSlime = 0.0F;
   private float riptideCounter = 0.0F;
   private float netherCounter = 0.0F;
   @Shadow
   private ItemStack field_4047;
   @Shadow
   @Final
   private MinecraftClient field_4050;
   private float fallCounter = 0.0F;
   private float inWaterCounter = 0.0F;
   private float inspect = 0.0F;
   private float tilt = 0.0F;
   private float freezeCounter = 0.0F;
   private float clCount = 0.0F;
   private float crawlCount = 0.0F;
   private float directionalCrawlCount = 0.0F;
   private float climbCount = 0.0F;
   private float mouseHolding = 1.0F;
   private boolean isSwinging = false;
   private float swingProgress = 0.0F; private boolean isForward = false; private boolean isAttacking = false; private boolean left = false; @Shadow
   private float field_4043; @Shadow
   private float field_4053; @Shadow
   private float field_4051; @Shadow
   private float field_4052; @Shadow
   private ItemStack field_4048; private float easeInOutBack(float x) {
     float c1 = 1.70158F;
     float c2 = c1 * 1.525F;
     return (float)((x < 0.5D) ? (Math.pow((2.0F * x), 2.0D) * ((c2 + 1.0F) * 2.0F * x - c2) / 2.0D) : ((Math.pow((2.0F * x - 2.0F), 2.0D) * ((c2 + 1.0F) * (x * 2.0F - 2.0F) + c2) + 2.0D) / 2.0D));
   }
   
   private float getAttackDamage(ItemStack stack) {
     AttributeModifiersComponent modifiers = (AttributeModifiersComponent)stack.method_57353().method_57829(DataComponentTypes.field_49636);
     if (modifiers == null) {
       return 0.0F;
     }
     float totalDamage = 0.0F;
     
     for (AttributeModifiersComponent.class_9287 entry : modifiers.comp_2393()) {
       if (entry.comp_2395().comp_349() == EntityAttributes.field_23721.comp_349()) {
         totalDamage += (float)entry.comp_2396().comp_2449();
       }
     } 
     
     return totalDamage;
   }
 
   
   private boolean isSharpAnimation(SwingAnimations config) {
     return (config != null && config.hmiAnimationType.is("Шарп"));
   }
   
   private void altSwing(MatrixStack matrices, Arm arm, float swingProgress, ItemStack item) {
     int i = (arm == Arm.field_6183) ? 1 : -1;
     float f = MathHelper.method_15374(swingProgress * 3.14F);
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * (45.0F + f * 0.0F)));
     matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * -45.0F));
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   @Inject(method = {"method_3228"}, at = {@At("HEAD")}, cancellable = true)
   private void onRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
     SwingAnimations swings = ModuleClass.swingAnimations;
     if (!swings.isEnable() || !swings.hmiEnable.isState()) {
       return;
     }
     
     boolean isMainHand = (hand == Hand.field_5808);
     Arm arm = isMainHand ? player.method_6068() : player.method_6068().method_5928();
     float sideFactor = isMainHand ? 1.0F : -1.0F;
     
     if (swings.swapHands.isState()) {
       arm = arm.method_5928();
       sideFactor *= -1.0F;
     } 
     
     renderCustomFirstPersonItem(player, tickDelta, pitch, hand, arm, sideFactor, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
     
     ci.cancel();
   }
 
 
 
 
   
   private void renderCustomFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, Arm arm, float sideFactor, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
     SwingAnimations swings = ModuleClass.swingAnimations;
     if (swings.isEnable() && swings.hmiEnable.isState() && 
       !player.method_31550()) {
       float al; SwingAnimations config = ModuleClass.swingAnimations;
       float yaw = player.method_36454();
       double radians = Math.toRadians(yaw);
       double forwardX = -Math.sin(radians);
       double forwardZ = Math.cos(radians);
       Vec3d horizontalVelocity = player.method_18798();
       double dotProduct = horizontalVelocity.field_1352 * forwardX + horizontalVelocity.field_1350 * forwardZ;
       double crossProduct = (player.method_18798().method_61890()).field_1352 * forwardZ - horizontalVelocity.field_1350 * forwardX;
       
       if (player.method_36455() != 0.0F) {
         al = 90.0F / player.method_36455() / 10.0F;
       } else {
         al = 1.0F;
       } 
       
       if (al > 1.0F) {
         al = 1.0F;
       }
       
       if (al < 0.0F) {
         al = 1.0F;
       }
       
       boolean bl = (hand == Hand.field_5808);
       matrices.method_22903();
       matrices.method_22903();
       ViewModel viewModel = (ModuleClass.INSTANCE != null) ? ModuleClass.viewModel : null;
       if (viewModel != null && viewModel.isEnable()) {
         viewModel.applyHandPosition(matrices, arm);
       }
       double tt = astra.deltaTime * 30.0D;
       float smoothness = MathHelper.method_15363(config.hmiSmoothness.get(), 0.35F, 2.5F);
       float hmiProgress = (float)Math.pow(MathHelper.method_15363(swingProgress, 0.0F, 1.0F), smoothness);
       float swing_rot = (hmiProgress < 0.6D) ? MathHelper.method_15374(MathHelper.method_15363(hmiProgress, 0.0F, 0.12506F) * 12.56F) : MathHelper.method_15374(MathHelper.method_15363(hmiProgress, 0.62532F, 0.75038F) * 12.56F);
       float swing = MathHelper.method_15374(hmiProgress * 3.14F);
       swing = easeInOutBack(swing);
       boolean sharpSword = (item.method_31573(ItemTags.field_42611) && isSharpAnimation(config));
       if ((item.method_31574(Items.field_8287) || item.method_31574(Items.field_49098) || item.method_31574(Items.field_8803) || item.method_31574(Items.field_8449) || item.method_31574(Items.field_8543) || item.method_7909() instanceof net.minecraft.SplashPotionItem || item.method_7909() instanceof net.minecraft.LingeringPotionItem) && player.method_6079().method_7960() && item.method_7976() != UseAction.field_8951 && !item.method_31574(Items.field_8814) && !player.method_5681() && !player.method_20448() && !player.method_6101()) {
         if (player.method_6068() == Arm.field_6182) {
           bl = !bl;
         }
         
         matrices.method_22903();
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-25.0F * sideFactor));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-10.0F));
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(25.0F * sideFactor * swing));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F * swing));
         matrices.method_22904(-0.15D * sideFactor, 0.1D, 0.1D);
         matrices.method_22904(0.0D, -0.55D * swing, 0.4D * swing * 3.140000104904175D);
         HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
         acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, 0.0F, arm.method_5928());
         matrices.method_22909();
       } 
       
       if (this.field_4050.field_1690.field_1886.method_1434() && !this.isAttacking && swingProgress == 0.0D) {
         this.left = !this.left;
       }
       
       if (!item.method_7960()) {
         if (player.method_6068() == Arm.field_6182) {
           bl = !bl;
         }
 
         
         if ((this.left || item.method_31573(ItemTags.field_42612) || item.method_7976() == UseAction.field_8951 || item.method_7976() == UseAction.field_8949) && !item.method_31573(ItemTags.field_42615)) {
           if (sharpSword) {
             matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.5D * swing);
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-20.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
           } else if (!item.method_31573(ItemTags.field_42611) && !item.method_31573(ItemTags.field_42612)) {
             if (item.method_7976() == UseAction.field_8951) {
               matrices.method_22904(0.0D, 0.0D, 0.45D * swing_rot);
               matrices.method_22904(-0.25D * sideFactor * swing, -0.35D * swing_rot, -0.6D * swing);
               matrices.method_22904(0.0D, 0.1D * swing, 0.0D);
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees(15.0F * swing_rot * sideFactor));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees(30.0F * swing_rot * sideFactor));
             } else if (item.method_31573(ConventionalItemTags.TOOLS) && item.method_7976() != UseAction.field_8949 && !item.method_31573(ItemTags.field_42615)) {
               matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.5D * swing);
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-20.0F * swing_rot * sideFactor));
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
             } else if (item.method_7976() != UseAction.field_8949) {
               matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.1D * swing);
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-10.0F * swing_rot * sideFactor));
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees(10.0F * swing * sideFactor));
             } else {
               matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.2D * swing);
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-10.0F * swing_rot));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-10.0F * swing_rot * sideFactor));
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(20.0F * swing));
             } 
           } else {
             matrices.method_22904(0.8D * sideFactor * swing_rot, 0.3D * swing_rot, -0.5D * swing);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(15.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-20.0F * swing_rot));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-70.0F * swing_rot * sideFactor));
             if (item.method_31573(ItemTags.field_42611)) {
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
             } else {
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(30.0F * swing));
             } 
           } 
         } else if (!item.method_31573(ItemTags.field_42615)) {
           if (sharpSword) {
             matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.5D * swing);
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-20.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
           } else if (item.method_31573(ItemTags.field_42611)) {
             matrices.method_22904(-0.55D * sideFactor * swing_rot, -0.8D * swing_rot, -0.77D * swing);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(5.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(70.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(50.0F * swing));
           } else if (item.method_31573(ConventionalItemTags.TOOLS) && !item.method_31573(ItemTags.field_42615)) {
             matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.5D * swing);
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-20.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
           } else {
             matrices.method_22904(0.1D * sideFactor * swing_rot, 0.1D * swing_rot, -0.1D * swing);
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(-30.0F * swing_rot));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-10.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40713.rotationDegrees(40.0F * swing));
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(10.0F * swing * sideFactor));
           } 
         } else if (item.method_31573(ItemTags.field_42615)) {
           matrices.method_22904(0.0D, 0.15D * swing_rot, -0.25D * swing_rot);
           matrices.method_22904(0.0D, 0.0D, -0.2D * swing);
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees(15.0F * swing_rot));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-35.0F * swing_rot));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F * swing));
         } 
       } else if (Block.method_9503(item.method_7909()) != Blocks.field_10124 && (!item.method_31573(ConventionalItemTags.TOOLS) || item.method_31573(ItemTags.field_41890) || item.method_31573(ItemTags.field_40109) || item.method_7976() == UseAction.field_8950 || !item.method_7923()) && item.method_7976() != UseAction.field_8953 && item.method_7976() != UseAction.field_27079 && getAttackDamage(item) == 0.0F && item.method_7976() != UseAction.field_8949 && !item.method_31574(Items.field_23254) && !item.method_31574(Items.field_8184) && !item.method_31574(Items.field_8378) && !item.method_31574(Items.field_8868)) {
         swingProgress = (float)(swingProgress * 1.2D);
         if (swingProgress > 1.0F) {
           swingProgress = 0.0F;
         }
       } else if (!item.method_31573(ItemTags.field_42615)) {
         swingProgress = (float)(swingProgress * 1.5D);
         if (swingProgress > 1.0F) {
           swingProgress = 0.0F;
         }
       } 
       
       if (player.method_18798().method_1033() >= 0.08D) {
         this.crawlCount = (float)(this.crawlCount + 0.1D * player.method_18798().method_1033() * 2.0D * tt);
         this.directionalCrawlCount = (float)(this.directionalCrawlCount + 0.1D * dotProduct * 4.0D * tt);
         this.directionalCrawlCount = (float)(this.directionalCrawlCount + ((dotProduct > 0.0D) ? (0.1D * Math.abs(crossProduct) * 4.0D * tt) : (0.1D * Math.abs(crossProduct) * -1.0D * 4.0D * tt)));
       } 
       
       if (player.method_18798().method_10214() > 0.0D) {
         this.climbCount = (float)(this.climbCount + 0.1D * tt);
       }
       
       if (player.method_18798().method_10214() < 0.0D) {
         this.climbCount = (float)(this.climbCount - 0.1D * tt);
       }
       
       if (((player.method_20448() && config.climbAndCrawl) || (player.method_6101() && !player.method_24828() && Math.abs(player.method_18798().method_10214()) > 0.0D && config.climbAndCrawl)) && !player.method_6115() && swingProgress == 0.0F) {
         this.clCount = (float)(this.clCount + 0.1D * tt);
         if (this.clCount > 1.0F) {
           this.clCount = 1.0F;
         }
         
         if (!item.method_31574(Items.field_16539) && !item.method_31574(Items.field_22016)) {
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-20.0F * this.clCount));
         }
       } else {
         this.clCount = (float)(this.clCount * Math.pow(0.8799999952316284D, tt));
       } 
       
       if (swingProgress == 0.0F) {
         matrices.method_46416(bl ? (player.method_36455() / 650.0F * this.clCount * -1.0F) : (player.method_36455() / 650.0F * this.clCount), 0.0F, 0.0F);
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(player.method_36455() * this.clCount));
       } 
       
       if (!item.method_31574(Items.field_16539) && !item.method_31574(Items.field_22016)) {
         matrices.method_46416(0.0F, 0.0F, player.method_36455() / 120.0F * this.clCount);
       } else if (swingProgress == 0.0F) {
         matrices.method_46416(0.0F, 0.0F, player.method_36455() / 80.0F * this.clCount);
       } 
       
       if (player.method_6101() && config.climbAndCrawl && !player.method_24828() && !item.method_31574(Items.field_16539) && !item.method_31574(Items.field_22016) && !player.method_6115()) {
         matrices.method_22904(0.0D, 0.1D, -0.2D);
       }
       
       if ((player.method_52535() || player.field_27857) && !player.method_5681() && !player.method_5869()) {
         this.inWaterCounter = (float)(this.inWaterCounter + 0.1D * tt);
         if (this.inWaterCounter >= 1.0F) {
           this.inWaterCounter = 1.0F;
         }
       } else {
         this.inWaterCounter = (float)(this.inWaterCounter * Math.pow(0.8799999952316284D, tt));
       } 
       
       if (player.field_27857 && player.method_32313() > 0.1D) {
         this.freezeCounter = (float)(this.freezeCounter + 0.1D * tt);
       } else {
         this.freezeCounter = (float)(this.freezeCounter * Math.pow(0.8799999952316284D, tt));
       } 
       
       matrices.method_22904(0.0D, 0.02D * this.inWaterCounter, 0.0D);
       matrices.method_22907(RotationAxis.field_40718.rotationDegrees(8.0F * sideFactor * this.inWaterCounter));
       matrices.method_22907(RotationAxis.field_40714.rotationDegrees(0.3F * MathHelper.method_15374(this.freezeCounter * 5.0F)));
       if (player.method_18798().method_10214() < -0.85D && item.method_31574(Items.field_49814) && player.method_6047() == item) {
         this.fallCounter = (float)(this.fallCounter + 0.1D * tt);
         if (this.fallCounter >= 1.0F) {
           this.fallCounter = 1.0F;
         }
       } else {
         this.fallCounter = (float)(this.fallCounter * Math.pow(0.8799999952316284D, tt));
       } 
       
       if (bl) {
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(45.0F * this.fallCounter));
         matrices.method_22904(0.0D, -0.2D * this.fallCounter, 0.0D);
       } 
       
       this.vertAngleY = (float)(this.vertAngleY + player.method_18798().method_10214() * 0.014999999664723873D * tt);
       this.vertAngleY = (float)(this.vertAngleY - (0.1F * this.vertAngleY) * tt);
       this.vertAngleY = (float)(this.vertAngleY * Math.pow(0.8799999952316284D, tt));
       this.vertVelocityYSlime = (float)(this.vertVelocityYSlime + player.method_18798().method_10214() * 0.014999999664723873D * tt);
       this.vertVelocityYSlime = (float)(this.vertVelocityYSlime - (0.1F * this.vertAngleYSlime) * tt);
       this.vertVelocityYSlime = (float)(this.vertVelocityYSlime * Math.pow(0.8799999952316284D, tt));
       this.vertAngleYSlime = (float)(this.vertAngleYSlime + this.vertVelocityYSlime * tt);
       matrices.method_46416(0.0F, this.vertAngleY * -1.0F, 0.0F);
       matrices.method_22904(0.0D, Math.sin(player.field_6012 * 0.1D) * 0.007D * sideFactor, 0.0D);
       matrices.method_22907(RotationAxis.field_40716.rotationDegrees(0.15F * MathHelper.method_15374(player.field_6012 * 0.15F) * sideFactor));
       if (!item.method_7960() || player.method_20448() || (player.method_6101() && !player.method_24828()) || player.method_5681()) {
         if (player.method_6068() == Arm.field_6182) {
           bl = !bl;
         }
         
         if (item.method_7976() == UseAction.field_8949) {
           matrices.method_46416(0.0F, 0.0F, 0.0F);
         } else {
           matrices.method_22904(0.0D, -0.1D, 0.1D);
         } 
       } 
       
       if (item.method_31574(Items.field_16539) || item.method_31574(Items.field_22016) || item.method_31573(ItemTags.field_40108)) {
         matrices.method_22904(0.0D, 0.1D, 0.0D);
         if (player.method_5681()) {
           matrices.method_22904(0.0D, -0.1D, 0.1D);
         }
       } 
       
       if (player.method_5681() && swingProgress == 0.0F && config.swimmingAnimation) {
         double distance = this.crawlCount;
         double swingAmplitude = 1.5D;
         double frequency = 2.0D;
         double s = distance * frequency;
         double handRotation = Math.sin(s) * swingAmplitude;
         double smoothRotation = handRotation * 0.8D + this.previousRotation * 0.2D;
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((float)(bl ? smoothRotation : -smoothRotation)));
         matrices.method_22904(0.0D, 0.0D, smoothRotation * 0.20000000298023224D);
         double k = (this.crawlCount * 2.0F);
         double a = Math.cos(k);
         double b = a;
         if (a <= 0.0D) {
           b = a * 0.5D;
         }
         
         matrices.method_22907(RotationAxis.field_40715.rotationDegrees((float)(bl ? (b * 30.0D) : (b * 30.0D * -1.0D))));
         matrices.method_22904(0.0D, 0.0D, a * 0.20000000298023224D);
         if (item.method_7960() && !bl && !player.method_5767()) {
           matrices.method_22904((1.0F * sideFactor), 0.0D - equipProgress * 0.3D, 0.3D);
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees(45.0F * sideFactor));
           matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-40.0F * sideFactor));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
           altSwing(matrices, arm, swingProgress, item);
           float c = MathHelper.method_15374(equipProgress * 3.14F);
           matrices.method_22905(0.9F, 0.9F, 0.9F);
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
         } 
         
         this.previousRotation = smoothRotation;
       } 
       
       if (((player.method_6101() && !player.method_24828()) || (player.method_20448() && swingProgress == 0.0F)) && !player.method_6115()) {
         double s = this.climbCount;
         float v = (float)player.method_18798().method_10214();
         float a = MathHelper.method_15362((float)s * 2.0F);
         if (player.method_6101()) {
           if (!item.method_31574(Items.field_16539) && !item.method_31574(Items.field_22016)) {
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(20.0F * a * sideFactor));
           } else {
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(1.0F * a * sideFactor));
           } 
         }
         
         if (player.method_20448() && !player.method_6115() && swingProgress == 0.0F) {
           float crawlProgress = MathHelper.method_15374(this.directionalCrawlCount * 4.0F * this.mouseHolding);
           float upAndDown = MathHelper.method_15362(this.directionalCrawlCount * 4.0F * this.mouseHolding);
           if (item.method_31574(Items.field_16539) || item.method_31574(Items.field_22016)) {
             crawlProgress *= 0.14F;
             upAndDown *= 0.14F;
           } 
           
           matrices.method_22904(0.2D * crawlProgress, 0.3D * crawlProgress * sideFactor, -0.2D * crawlProgress * sideFactor * al);
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees(25.0F * crawlProgress));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(MathHelper.method_15363(20.0F * upAndDown * sideFactor, 0.0F, 20.0F)));
         } 
         
         if (item.method_7960() && !bl && !player.method_5767() && ((!player.method_24828() && player.method_6101()) || player.method_20448())) {
           matrices.method_22904((1.0F * sideFactor), 0.0D - equipProgress * 0.3D, 0.3D);
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees(45.0F * sideFactor));
           matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-40.0F * sideFactor));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
           altSwing(matrices, arm, swingProgress, item);
           matrices.method_22905(0.9F, 0.9F, 0.9F);
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
         } 
       } 
       
       if (item.method_7960()) {
         if (bl && !player.method_5767()) {
           if ((player.method_24828() || !player.method_6101()) && !player.method_5681() && !player.method_20448()) {
             if (player.method_6068() == Arm.field_6182) {
               bl = !bl;
             }
 
             
             matrices.method_22904(0.0D, 0.2D * swing_rot, 0.15D * swing_rot);
             matrices.method_22904(0.1D * sideFactor * swing, 0.15D * swing, -0.45D * swing);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(35.0F * swing * sideFactor));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-30.0F * swing));
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(-10.0F * swing_rot * sideFactor));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(10.0F * swing_rot));
             HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
             acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
           } else {
             matrices.method_22904((1.0F * sideFactor), 0.0D - equipProgress * 0.3D, 0.3D);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(45.0F * sideFactor));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-40.0F * sideFactor));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
             altSwing(matrices, arm, swingProgress, item);
             float c = MathHelper.method_15374(equipProgress * 3.14F);
             matrices.method_22905(0.9F, 0.9F, 0.9F);
             HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
             acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
           } 
         }
       } else if (item.method_57826(DataComponentTypes.field_49646)) {
         if (bl && this.field_4047.method_7960()) {
           matrices.method_22904(0.0D, 0.1D, 0.0D);
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
         } else {
           matrices.method_22904(bl ? -0.1D : 0.1D, 0.1D, 0.0D);
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
         } 
       } else if (item.method_7976() == UseAction.field_8947) {
         matrices.method_22903();
         boolean bl2 = CrossbowItem.method_7781(item);
         boolean bl3 = (arm == Arm.field_6183);
         int i = bl3 ? 1 : -1;
         if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
           HeldItemRendererAccessor heldItemRendererAccessor = (HeldItemRendererAccessor)this;
           heldItemRendererAccessor.invokeApplyEquipOffset(matrices, arm, equipProgress);
           matrices.method_46416(i * -0.4785682F, -0.24387F, 0.05731531F);
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-11.935F));
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * 65.3F));
           matrices.method_22907(RotationAxis.field_40718.rotationDegrees(i * 9.785F));
           float f = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
           float g = f / CrossbowItem.method_7775(item, (LivingEntity)player);
           if (g > 1.0F) {
             g = 1.0F;
           }
           
           if (g > 0.1F) {
             float h = MathHelper.method_15374((f - 0.1F) * 1.3F);
             float j = g - 0.1F;
             float k = h * j;
             matrices.method_46416(k * 0.0F, k * 0.004F, k * 0.0F);
           } 
           
           matrices.method_46416(g * 0.0F, g * 0.0F, g * 0.04F);
           matrices.method_22905(1.0F, 1.0F, 1.0F);
           matrices.method_22907(RotationAxis.field_40715.rotationDegrees(i * 45.0F));
         } else {
           ((HeldItemRendererAccessor)this).invokeSwingArm(swingProgress, equipProgress, matrices, i, arm);
           
           if (bl2 && swingProgress < 0.001F && bl) {
             matrices.method_46416(i * -0.341864F, 0.0F, 0.0F);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees(i * 10.0F));
           } 
         } 
         
         matrices.method_46416(0.0F, 0.0F, -1.0F);
         matrices.method_22904(-0.45D * i, 0.45D, 1.7D);
         matrices.method_22904((1.0F * sideFactor), 0.0D - equipProgress * 0.3D, 0.3D);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees(45.0F * sideFactor));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees(-40.0F * sideFactor));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
         altSwing(matrices, arm, swingProgress, item);
         float c = MathHelper.method_15374(equipProgress * 3.14F);
         matrices.method_22905(0.9F, 0.9F, 0.9F);
         HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
         acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
         matrices.method_22904(-0.25D * i, 1.25D, 0.05D);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-90 * i)));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(77.0F));
         matrices.method_22907(RotationAxis.field_40718.rotationDegrees((85 * i)));
         matrices.method_22905(1.2F, 1.2F, 1.2F);
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-10.0F));
         matrices.method_22904(0.0D, -0.15D, 0.15D);
         acc.invokeRenderItem((LivingEntity)player, item, bl3 ? ModelTransformationMode.field_4322 : ModelTransformationMode.field_4321, !bl3, matrices, vertexConsumers, light);
         matrices.method_22909();
         if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
           float f = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
           float g = f / CrossbowItem.method_7775(item, (LivingEntity)player);
           if (g > 1.0F) {
             g = 1.0F;
           }
           
           if (g > 0.1F) {
             float h = MathHelper.method_15374((f - 0.1F) * 1.3F);
             float j = g - 0.1F;
             float k = h * j;
             matrices.method_46416(k * 0.0F, k * 0.004F, k * 0.0F);
           } 
           
           matrices.method_22907(RotationAxis.field_40715.rotationDegrees((g <= 0.2D) ? (75.0F * g * 5.0F * i) : (75 * i)));
           matrices.method_22907(RotationAxis.field_40713.rotationDegrees(10.0F * g * 1.5F));
           matrices.method_22904(-0.37D * i, 0.0D, 0.6D);
           matrices.method_22904(0.15D * g * i, 0.0D, 0.0D);
           acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm.method_5928());
         } 
       } else {
         boolean bl2 = (arm == Arm.field_6183);
         int l = bl2 ? 1 : -1;
         if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
           HeldItemRendererAccessor acc; float u; float y; float q; float c; HeldItemRendererAccessor acc4; float k; float s; float s2; HeldItemRendererAccessor acc5; float m1; float f1; float f; HeldItemRendererAccessor acc1; float m; HeldItemRendererAccessor acc0; float f5; float g5; float h5; float n; float z; float x; HeldItemRendererAccessor acc78; HeldItemRendererAccessor acc67; switch (item.method_7976()) {
             case field_8952:
               acc = (HeldItemRendererAccessor)this;
               acc.invokeApplyEquipOffset(matrices, arm, equipProgress);
               break;
             case field_8950:
             case field_8946:
               u = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
               y = u / 5.0F;
               if (y > 1.0F) {
                 y = 1.0F;
               }
               
               q = MathHelper.method_15374(u / 2.0F * 3.14F);
               q /= 10.0F;
               matrices.method_22904((1 * l), 0.1D, 0.3D);
               matrices.method_22904(0.2D * l * y, -0.7D * y, -0.2D * y);
               matrices.method_22904(0.0D, -0.2D * q, -0.2D * q);
               matrices.method_22904(0.0D, 0.1D * easeInOutBack(MathHelper.method_15374(y * 3.14F)), 0.0D);
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((45 * l)));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-40 * l)));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
               altSwing(matrices, arm, swingProgress, item);
               c = MathHelper.method_15374(equipProgress * 3.14F);
               matrices.method_22905(0.9F, 0.9F, 0.9F);
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees(45.0F * y * l));
               acc4 = (HeldItemRendererAccessor)this;
               acc4.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, swingProgress, arm);
               break;
             case field_8949:
               k = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
               s = k / 4.0F;
               s2 = k / 6.0F;
               if (s > 1.0F) {
                 s = 1.0F;
               }
               
               if (s2 > 1.0F) {
                 s2 = 1.0F;
               }
               
               matrices.method_22904(0.0D, -0.2D, 0.0D);
               matrices.method_22904((1 * l), 0.0D, 0.3D);
               matrices.method_22904(0.7D * s * l, 0.0D, -1.3D * s);
               matrices.method_22904(-0.2D * l * s2, 0.0D, 0.0D);
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees((float)(10.0D * Math.sin(s2 * 3.14D))));
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees(70.0F * s * l));
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((45 * l)));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-40 * l)));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((5 * l) * s));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-10.0F * s));
               matrices.method_22904(0.0D, 0.0D, -0.2D * s);
               altSwing(matrices, arm, swingProgress, item);
               matrices.method_22905(0.9F, 0.9F, 0.9F);
               acc5 = (HeldItemRendererAccessor)this;
               acc5.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, swingProgress, arm);
               matrices.method_22904(0.35D * l, -0.13D, -0.12D);
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees(10.0F * l));
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees(10.0F * l));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(0.0F));
               matrices.method_22904(-0.2D * l, -0.04D, 0.15D);
               matrices.method_22905(1.0F, 1.0F, 1.0F);
               break;
             case field_8953:
               matrices.method_22903();
               if (player.method_6068() == Arm.field_6182) {
                 bl = !bl;
               }
               
               m1 = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
               f1 = m1 / 20.0F;
               f = (f1 * f1 + f1 * 2.0F) / 3.0F;
               if (f1 > 1.0F) {
                 f1 = 1.0F;
               }
               
               if (f1 > 0.1F) {
                 float g1 = MathHelper.method_15374((m1 - 0.1F) * 1.3F);
                 float j1 = g1 * f1;
                 matrices.method_46416(j1 * 0.0F, j1 * 0.004F, j1 * 0.0F);
               } 
               
               matrices.method_22904(bl ? -0.1D : 0.1D, 0.0D, f1 * 0.15D);
               acc1 = (HeldItemRendererAccessor)this;
               acc1.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
               matrices.method_22909();
               matrices.method_22904(bl ? -0.5D : 0.5D, -0.45D, 0.1D);
               matrices.method_22907(RotationAxis.field_40714.rotation(0.3F));
               if (bl) {
                 matrices.method_22907(RotationAxis.field_40717.rotation(-0.3F));
                 matrices.method_22907(RotationAxis.field_40715.rotation(1.0F));
               } else {
                 matrices.method_22907(RotationAxis.field_40718.rotation(-0.3F));
                 matrices.method_22907(RotationAxis.field_40716.rotation(1.0F));
               } 
               
               acc1.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm.method_5928());
               if (bl) {
                 matrices.method_22907(RotationAxis.field_40715.rotation(2.5F));
               } else {
                 matrices.method_22907(RotationAxis.field_40716.rotation(2.5F));
               } 
               
               matrices.method_22904(bl ? -0.65D : 0.65D, -0.35D, 0.27D);
               if (f1 > 1.0F) {
                 f1 = 1.0F;
               }
               
               matrices.method_22909();
               if (config.mb3DCompat) {
                 matrices.method_22907(RotationAxis.field_40716.rotationDegrees((10 * l)));
               }
               
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(75.0F));
               matrices.method_22907(RotationAxis.field_40717.rotationDegrees((-15 * l)));
               matrices.method_22904(0.8D * l, (0.0F - equipProgress * 0.3F), -0.1D);
               if (f > 0.1F) {
                 float g1 = MathHelper.method_15374((m1 - 0.1F) * 1.3F);
                 float h1 = f1 - 0.1F;
                 float j1 = g1 * h1;
                 matrices.method_46416(j1 * 0.0F, j1 * 0.004F, j1 * 0.0F);
               } 
               
               matrices.method_22903();
               break;
             case field_8951:
               if (player.method_6079().method_7960() && !player.method_20448() && !player.method_5681() && !player.method_6101()) {
                 matrices.method_22903();
                 matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-25 * l)));
                 matrices.method_22904(-0.15D * l, 0.1D, 0.1D);
                 HeldItemRendererAccessor acc8 = (HeldItemRendererAccessor)this;
                 acc8.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm.method_5928());
                 matrices.method_22909();
               } 
               
               m = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
               f = m / 10.0F;
               if (f > 1.0F) {
                 f = 1.0F;
               }
               
               if (f > 0.1F) {
                 float g = MathHelper.method_15374((m - 0.1F) * 1.3F);
                 float h = f - 0.1F;
                 float j = g * h;
                 matrices.method_46416(j * 0.0F, j * 0.004F, j * 0.0F);
               } 
               
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(45.0F));
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((25 * l)));
               matrices.method_22904(0.2D * l, 0.0D, 0.8D);
               acc0 = (HeldItemRendererAccessor)this;
               acc0.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(135.0F));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-65 * l)));
               matrices.method_22904((0.65F * l), -1.0D, -0.6D);
               break;
             case field_42717:
               f5 = (player.method_6014() % 10);
               g5 = f5 - tickDelta + 1.0F;
               h5 = 1.0F - g5 / 10.0F;
               n = -15.0F + 75.0F * MathHelper.method_15362(h5 * 2.0F * 3.1415927F);
               z = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
               x = z / 4.0F;
               if (x > 1.0F) {
                 x = 1.0F;
               }
               
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((25 * l) * x));
               matrices.method_22904((0.3F * l * x), 0.3D * x, 0.1D * x);
               if (x == 1.0F) {
                 matrices.method_22907(RotationAxis.field_40716.rotationDegrees(n / 20.0F));
               }
               
               acc78 = (HeldItemRendererAccessor)this;
               acc78.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
               break;
             case field_55494:
               matrices.method_22904((1 * l), 0.0D - equipProgress * 0.3D, 0.3D);
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((45 * l)));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-40 * l)));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
               altSwing(matrices, arm, swingProgress, item);
               matrices.method_22905(0.9F, 0.9F, 0.9F);
               acc67 = (HeldItemRendererAccessor)this;
               acc67.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm); break;
           } 
         } else if (player.method_6123() && item.method_7976() == UseAction.field_8951) {
           this.riptideCounter = (float)(this.riptideCounter + 0.15D * tt);
           float m = item.method_7935((LivingEntity)player) - player.method_6014() - tickDelta + 1.0F;
           float f = m / 10.0F;
           if (f > 1.0F) {
             f = 1.0F;
           }
           
           if (f > 0.1F) {
             float g = MathHelper.method_15374((m - 0.1F) * 1.3F);
             float h = f - 0.1F;
             float j = g * h;
             matrices.method_46416(j * 0.0F, j * 0.004F, j * 0.0F);
           } 
           
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(45.0F - this.riptideCounter * 2.0F));
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees((25 * l)));
           matrices.method_22904(0.2D * l, 0.0D, 0.75D);
           matrices.method_22904(0.0D, 0.0D, 0.01D * MathHelper.method_15374(this.riptideCounter * 6.28F));
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(135.0F));
           matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-65 * l)));
           matrices.method_22904((0.65F * l), -1.0D, -0.6D);
         } else {
           this.riptideCounter = 0.0F;
           if (!item.method_31574(Items.field_16539) && !item.method_31574(Items.field_22016) && !item.method_31573(ItemTags.field_40108)) {
             if (item.method_7976() == UseAction.field_8949) {
               matrices.method_22904(0.0D, -0.2D, 0.0D);
             }
           } else {
             matrices.method_22904(0.1D * l, 0.0D, -0.1D);
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(10.0F));
           } 
           
           matrices.method_22904((1 * l), 0.0D - equipProgress * 0.3D, 0.3D);
           matrices.method_22907(RotationAxis.field_40716.rotationDegrees((45 * l)));
           matrices.method_22907(RotationAxis.field_40718.rotationDegrees((-40 * l)));
           matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F));
           altSwing(matrices, arm, swingProgress, item);
           matrices.method_22905(0.9F, 0.9F, 0.9F);
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderArmHoldingItem(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
         } 
         
         matrices.method_22904(-0.3D * l, 0.65D, -0.1D);
         matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-65 * l)));
         matrices.method_22907(RotationAxis.field_40714.rotationDegrees(10.0F));
         if (item.method_31573(ItemTags.field_15542)) {
           matrices.method_22904(0.2D * l, -0.1D, 0.0D);
         }
         
         if (Block.method_9503(item.method_7909()) != Blocks.field_10124 && item.method_7976() != UseAction.field_8950 && !item.method_31573(ConventionalItemTags.BUCKETS)) {
           if (item.method_7964().toString().toLowerCase().contains("TORCH".toLowerCase())) {
             matrices.method_22905(1.5F, 1.5F, 1.5F);
             matrices.method_22907(RotationAxis.field_40715.rotationDegrees((25 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(5.0F));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((75 * l)));
             matrices.method_22904(0.2D * l, 0.2D, 0.05D);
           } else if ((item.method_31574(Items.field_8276) || item.method_31574(Items.field_8725) || item.method_31574(Items.field_8865) || item.method_31574(Items.field_8366) || Block.method_9503(item.method_7909()).method_9564().method_26164(ConventionalBlockTags.GLASS_PANES) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15463) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_22414) || item.method_31573(ItemTags.field_15553)) && !Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15503) && !Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_43170) && !Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15501)) {
             matrices.method_22904(0.0D, 0.0D, -0.1D);
             matrices.method_22907(RotationAxis.field_40715.rotationDegrees((5 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(15.0F));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((75 * l)));
           } else if (!item.method_31574(Items.field_16539) && !item.method_31574(Items.field_22016) && !item.method_31573(ItemTags.field_40108)) {
             matrices.method_22907(RotationAxis.field_40715.rotationDegrees((25 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(5.0F));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((75 * l)));
             matrices.method_22904(0.2D * l, 0.2D, 0.05D);
             if (Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15501)) {
               matrices.method_22904(-0.2D * l, 0.0D, 0.0D);
               matrices.method_22905(1.1F, 1.1F, 1.1F);
             } 
           } else {
             float dt = (float)(astra.deltaTime * 30.0D);
             float yawDelta = player.field_6259 - player.method_5791();
             float pitchDelta = player.field_6004 - player.method_36455();
             this.swingVelocityY += yawDelta * 0.015F * dt;
             this.swingVelocityY += swingProgress * 2.0F * dt;
             this.swingVelocityX += pitchDelta * 0.015F * dt;
             this.swingVelocityY -= 0.1F * this.swingAngleY * dt;
             this.swingVelocityX -= 0.1F * this.swingAngleX * dt;
             this.swingVelocityY = (float)(this.swingVelocityY * Math.pow(0.8799999952316284D, dt));
             this.swingVelocityX = (float)(this.swingVelocityX * Math.pow(0.8799999952316284D, dt));
             this.swingAngleY += this.swingVelocityY * dt;
             this.swingAngleX += this.swingVelocityX * dt;
             double currentSpeed = player.method_18798().method_1033();
             this.swingVelocityZ = (float)(this.swingVelocityZ + (bl ? ((currentSpeed * -1.0D * 15.0D - this.swingVelocityZ) * 0.10000000149011612D * dt) : ((currentSpeed * 15.0D - this.swingVelocityZ) * 0.10000000149011612D * dt)));
             if (((currentSpeed > 0.09D && player.method_24828()) || player.method_5681() || (player.method_6101() && !player.method_24828())) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
               Random random = new Random();
               boolean randomBoolean = random.nextBoolean();
               this.swingVelocityY += (float)(randomBoolean ? (-5.5D * currentSpeed * dt) : (5.5D * currentSpeed * dt));
             } 
             
             matrices.method_22904(0.0D, 0.0D, -0.1D);
             matrices.method_22907(RotationAxis.field_40715.rotationDegrees((35 * l) + this.swingAngleY));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(15.0F + this.swingAngleX));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((75 * l) + this.swingVelocityZ));
             if (item.method_31573(ItemTags.field_40108)) {
               matrices.method_22904(0.0D, -0.1D, 0.0D);
               matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-45 * l)));
             } 
             
             matrices.method_22904(0.3D * l, -0.35D, 0.0D);
             matrices.method_22904(0.0D, 0.0D, 0.1D);
             matrices.method_22905(1.5F, 1.5F, 1.5F);
           } 
         } else {
           if ((!item.method_31573(ConventionalItemTags.TOOLS) || item.method_31573(ItemTags.field_41890) || item.method_31573(ItemTags.field_40109) || item.method_7976() == UseAction.field_8950 || !item.method_7923()) && item.method_7976() != UseAction.field_8953 && item.method_7976() != UseAction.field_27079 && getAttackDamage(item) == 0.0F && item.method_7976() != UseAction.field_8949 && !item.method_31574(Items.field_23254) && !item.method_31574(Items.field_8184) && !item.method_31574(Items.field_8378) && !item.method_31574(Items.field_8868) && !item.method_31573(ItemTags.field_42613) && !config.mb3DCompat) {
             if (item.method_7976() == UseAction.field_42717) {
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(25.0F));
               matrices.method_22904(bl ? 0.0D : 0.35D, bl ? 0.0D : 0.25D, bl ? 0.0D : 0.37D);
               if (!bl) {
                 matrices.method_22905(0.75F, 0.75F, 0.75F);
               }
               
               matrices.method_22907(RotationAxis.field_40717.rotationDegrees((-75 * l)));
               matrices.method_22907(RotationAxis.field_40713.rotationDegrees(35.0F));
               matrices.method_22904(bl ? -0.05D : 0.85D, bl ? 0.0D : 0.05D, bl ? 0.08D : -0.2D);
             } else {
               matrices.method_22907(RotationAxis.field_40715.rotationDegrees((5 * l)));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(15.0F));
               matrices.method_22907(RotationAxis.field_40718.rotationDegrees((75 * l)));
               matrices.method_22904(0.0D, -0.05D, -0.1D);
               matrices.method_22905(0.7F, 0.7F, 0.7F);
             } 
             
             if (item.method_31574(Items.field_8153) || item.method_31574(Items.field_8777) || item.method_31574(Items.field_8323)) {
               this.vertVelocityYSlime = (float)(this.vertVelocityYSlime + swingProgress * 0.03D * astra.deltaTime * 30.0D);
               if (((player.method_18798().method_1033() > 0.09D && player.method_24828()) || player.method_5681() || player.method_20448() || (player.method_6101() && !player.method_24828())) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
                 Random random = new Random();
                 boolean randomBoolean = random.nextBoolean();
                 this.vertVelocityYSlime += (float)(-0.05D * player.method_18798().method_1033() * astra.deltaTime * 30.0D);
               } 
               
               matrices.method_22905(1.0F, 1.0F + this.vertAngleYSlime * -2.0F, 1.0F);
             } 
           } else if (item.method_7976() == UseAction.field_8949 && item.method_7976() != UseAction.field_8951) {
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((160 * l)));
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-60 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-70.0F));
             matrices.method_22905(0.75F, 0.75F, 0.75F);
             matrices.method_22904(0.15D * l, bl ? 0.35D : 0.45D, bl ? -0.15D : -0.1D);
             matrices.method_22904(0.17D * l, 0.0D, 0.3D);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees((-90 * l)));
           } else if (item.method_7976() == UseAction.field_8951) {
             matrices.method_22907(RotationAxis.field_40715.rotationDegrees((75 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(90.0F));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((45 * l)));
             matrices.method_46416(-0.3F * l, 0.0F, 0.0F);
           } else if (item.method_7976() != UseAction.field_8951) {
             matrices.method_22907(RotationAxis.field_40715.rotationDegrees((75 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(70.0F));
             matrices.method_22907(RotationAxis.field_40718.rotationDegrees((45 * l)));
           } 
           
           if (item.method_7976() != UseAction.field_8949) {
             matrices.method_22905(1.2F, 1.2F, 1.2F);
           }
           
           if (item.method_7976() == UseAction.field_8953 && !player.method_6115()) {
             matrices.method_22904(-0.1D * l, -0.2D, 0.0D);
           }
           
           if (item.method_31574(Items.field_49814)) {
             if (config.mb3DCompat) {
               matrices.method_22904(-0.08D, 0.17D, 0.0D);
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(40.0F));
             } 
             
             matrices.method_22904(0.1D * l, 0.0D, 0.0D);
             matrices.method_22905(0.9F, 0.9F, 0.9F);
           } 
         } 
         
         if (item.method_7909() instanceof BlockItem && ((!item.method_31573(ConventionalItemTags.BUCKETS) && item.method_7976() != UseAction.field_8950 && !item.method_31573(ItemTags.field_15556) && !item.method_31574(Items.field_8276) && !item.method_31574(Items.field_8725) && !item.method_31574(Items.field_8865) && !item.method_31574(Items.field_8366) && !Block.method_9503(item.method_7909()).method_9564().method_26164(ConventionalBlockTags.GLASS_PANES) && !Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15463) && !Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_22414) && !item.method_31573(ItemTags.field_15553)) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15503)) && !Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_43170)) {
           BlockItem blockItem = (BlockItem)item.method_7909();
           BlockRenderManager blockRenderManager = MinecraftClient.method_1551().method_1541();
           blockRenderManager.method_3349(blockItem.method_7711().method_9564());
           matrices.method_22903();
           if (!bl2) {
             matrices.method_46416(-0.4F, 0.0F, 0.0F);
           }
           
           matrices.method_22905(0.4F, 0.4F, 0.4F);
           matrices.method_22904(-0.9D * l, -0.45D, -0.5D);
           if (Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15493)) {
             matrices.method_22904(0.2D * l, -0.15D, -0.2D);
           }
           
           if (Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_24076)) {
             matrices.method_22904(0.0D, 0.1D, 0.0D);
           }
           
           if (item.method_31574(Items.field_8828) || item.method_31574(Items.field_21086) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_20339) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15503) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_15462) || Block.method_9503(item.method_7909()).method_9564().method_26164(BlockTags.field_44469)) {
             this.vertVelocityYSlime = (float)(this.vertVelocityYSlime + swingProgress * 0.03D * astra.deltaTime * 30.0D);
             if (((player.method_18798().method_1033() > 0.09D && player.method_24828()) || player.method_5681() || player.method_20448() || (player.method_6101() && !player.method_24828())) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
               Random random = new Random();
               boolean randomBoolean = random.nextBoolean();
               this.vertVelocityYSlime += (float)(-0.05D * player.method_18798().method_1033() * astra.deltaTime * 30.0D);
             } 
             
             matrices.method_22905(1.0F, 1.0F + this.vertAngleYSlime * -2.0F, 1.0F);
           } 
           
           BlockState blockState = blockItem.method_7711().method_9564();
           if (player.field_6012 - this.prevAge >= 100.0F) {
             this.repPower = !this.repPower;
             this.prevAge = player.field_6012;
           } 
           
           if (blockItem.method_7711() == Blocks.field_10450 && this.repPower) {
             blockState = (BlockState)blockState.method_11657((Property)RepeaterBlock.field_10911, Boolean.valueOf(true));
           }
           
           if (blockItem.method_7711() == Blocks.field_10377 && this.repPower) {
             blockState = (BlockState)blockState.method_11657((Property)ComparatorBlock.field_10911, Boolean.valueOf(true));
           }
           
           if (blockItem.method_7711() == Blocks.field_10523 && player.method_5869()) {
             blockState = (BlockState)blockState.method_11657((Property)RedstoneTorchBlock.field_11446, Boolean.valueOf(false));
           }
           
           if ((blockItem.method_7711() == Blocks.field_17350 || blockItem.method_7711() == Blocks.field_23860) && player.method_5869()) {
             blockState = (BlockState)blockState.method_11657((Property)CampfireBlock.field_17352, Boolean.valueOf(false));
           }
           
           if (item.method_31573(ItemTags.field_16444)) {
             if (bl) {
               matrices.method_22904(0.9D, 0.0D, 0.8D);
             }
             
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees((90 * l)));
           } 
           
           blockRenderManager.method_3353(blockState, matrices, vertexConsumers, light, OverlayTexture.field_21444);
           matrices.method_22909();
         } else {
           if ((item.method_31573(ConventionalItemTags.TOOLS) && !item.method_31573(ItemTags.field_41890) && !item.method_31573(ItemTags.field_40109) && item.method_7976() != UseAction.field_8950 && item.method_7923()) || item.method_7976() == UseAction.field_8953 || item.method_7976() == UseAction.field_27079 || getAttackDamage(item) != 0.0F || item.method_7976() == UseAction.field_8949 || item.method_31574(Items.field_23254) || item.method_31574(Items.field_8184) || item.method_31574(Items.field_8378) || item.method_31574(Items.field_8868)) {
             if (item.method_31573(ItemTags.field_42611) && !sharpSword) {
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-60.0F * swing));
               matrices.method_22904(0.0D, 0.1D * swing, -0.1D * swing);
             } 
             
             if (item.method_31573(ItemTags.field_42615)) {
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-80.0F * swing_rot));
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(30.0F * swing));
             } else if (item.method_7976() == UseAction.field_8951) {
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-40.0F * swing_rot));
               matrices.method_22904(0.0D, 0.1D * swing_rot, -0.1D * swing_rot);
             } else if (item.method_7976() != UseAction.field_8949) {
               matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-25.0F * swing));
               matrices.method_22904(0.0D, 0.05D * swing, -0.05D * swing);
             } 
           } 
           
           if (!item.method_31574(Items.field_8137) && (!item.method_31574(Items.field_8301) || !config.mb3DCompat)) {
             this.netherCounter = 0.0F;
           } else {
             this.netherCounter = (float)(this.netherCounter + 0.9D * tt);
             matrices.method_22904(0.0D, 0.25D + 0.02D * MathHelper.method_15374(this.netherCounter * 0.1F), 0.0D);
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(3.0F * MathHelper.method_15374(this.netherCounter * 0.2F)));
             matrices.method_22905(1.0F + 0.01F * MathHelper.method_15374(this.netherCounter), 1.0F + 0.01F * MathHelper.method_15374(this.netherCounter), 1.0F + 0.01F * MathHelper.method_15374(this.netherCounter));
           } 
           
           if (config.mb3DCompat) {
             if (item.method_31573(ItemTags.field_42611)) {
               matrices.method_22904(0.0D, 0.2D, 0.0D);
             }
             
             if (item.method_31574(Items.field_8153) || item.method_31574(Items.field_8777) || item.method_31574(Items.field_8323)) {
               this.vertVelocityYSlime = (float)(this.vertVelocityYSlime + swingProgress * 0.03D * astra.deltaTime * 30.0D);
               if (((player.method_18798().method_1033() > 0.09D && player.method_24828()) || player.method_5681() || player.method_20448() || (player.method_6101() && !player.method_24828())) && ((Boolean)this.field_4050.field_1690.method_42448().method_41753()).booleanValue()) {
                 Random random = new Random();
                 boolean randomBoolean = random.nextBoolean();
                 this.vertVelocityYSlime += (float)(-0.05D * player.method_18798().method_1033() * astra.deltaTime * 30.0D);
               } 
               
               matrices.method_22905(1.0F, 1.0F + this.vertAngleYSlime * -2.0F, 1.0F);
             } 
           } 
           
           if (item.method_31573(ItemTags.field_42615)) {
             matrices.method_22904(0.07D * l, 0.0D, 0.05D);
             matrices.method_22907(RotationAxis.field_40716.rotationDegrees((90 * l)));
             matrices.method_22907(RotationAxis.field_40714.rotationDegrees(-15.0F));
           } 
           
           if (item.method_31574(Items.field_8810)) {
             player.method_37908().method_8406((ParticleEffect)ParticleTypes.field_11246, player.method_19538().method_10216(), player.method_19538().method_10214(), player.method_19538().method_10215(), 0.1D, 0.1D, 0.1D);
           }
           
           HeldItemRendererAccessor acc = (HeldItemRendererAccessor)this;
           acc.invokeRenderItem((LivingEntity)player, item, bl2 ? ModelTransformationMode.field_4322 : ModelTransformationMode.field_4321, !bl2, matrices, vertexConsumers, light);
         } 
       } 
       
       matrices.method_22909();
       matrices.method_22909();
       this.isAttacking = this.field_4050.field_1690.field_1886.method_1434();
     } 
   }
 
   
   @Shadow
   protected abstract void method_3228(AbstractClientPlayerEntity paramclass_742, float paramFloat1, float paramFloat2, Hand paramclass_1268, float paramFloat3, ItemStack paramclass_1799, float paramFloat4, MatrixStack paramclass_4587, VertexConsumerProvider paramclass_4597, int paramInt);
 
   
   @Shadow
   protected abstract void method_65816(float paramFloat1, float paramFloat2, MatrixStack paramclass_4587, int paramInt, Arm paramclass_1306);
 
   
   @Shadow
   private static HeldItemRenderer.class_5773 method_33303(ClientPlayerEntity player) {
     throw new AssertionError();
   }
 }

