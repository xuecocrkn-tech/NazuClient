package shame.nazuna.client.modules.impl.render;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 import java.util.UUID;
 import net.minecraft.Entity;
 import net.minecraft.Vec3d;
 import net.minecraft.Identifier;
 import net.minecraft.MathHelper;
 import net.minecraft.MatrixStack;
 import net.minecraft.Perspective;
 import net.minecraft.AbstractClientPlayerEntity;
 import net.minecraft.RotationAxis;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.FreeLookStorage;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.api.utils.math.MathUtils;
 import shame.nazuna.api.utils.render.RenderUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class Arrows
   extends Module {
   public static Arrows INSTANCE = new Arrows();
   private static final Identifier FIRST_ARROW_TEXTURE = Identifier.method_60655("astra", "textures/arrows/arrow.png");
   private static final Identifier SECOND_ARROW_TEXTURE = Identifier.method_60655("astra", "textures/arrows/arr.png");
   private static final Identifier MAMA_ARROW_TEXTURE = Identifier.method_60655("astra", "textures/arrows/arrowsnurik.png");
   
   private final ModeSetting type = new ModeSetting("Вид", "Первый", new String[] { "Первый", "Второй", "Третий" });
   private final FloatSetting radius = new FloatSetting("Радиус", 58.0F, 30.0F, 120.0F, 1.0F);
   private final FloatSetting size = new FloatSetting("Размер", 13.0F, 8.0F, 28.0F, 0.5F);
   private final FloatSetting glowRadius = new FloatSetting("Свечение", 7.5F, 0.0F, 20.0F, 0.5F);
   
   private final Map<UUID, ArrowState> states = new HashMap<>();
   private final Set<UUID> seenPlayers = new HashSet<>();
   
   public Arrows() {
     super("Arrows", "Красивые стрелочки на энтити", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.type, (Setting)this.radius, (Setting)this.size, (Setting)this.glowRadius });
   }
   
   @EventLink
   public void onRender(EventRender.Default event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1690.field_1842) {
       this.states.clear();
       return;
     } 
     if (mc.field_1690.method_31044() != Perspective.field_26664) {
       fadeAllStates();
       
       return;
     } 
     float partialTicks = event.getPartialTicks();
     float centerX = mc.method_22683().method_4486() * 0.5F;
     float centerY = mc.method_22683().method_4502() * 0.5F;
     float arrowSize = this.size.get();
     float y = centerY - this.radius.get();
     float playerYaw = getReferenceYaw(partialTicks);
     Vec3d selfPos = getReferencePos(partialTicks);
     
     this.seenPlayers.clear();
     for (AbstractClientPlayerEntity player : mc.field_1687.method_18456()) {
       if (player == mc.field_1724 || !player.method_5805() || player.method_7325() || isGhostPlayer(player)) {
         continue;
       }
       
       UUID uuid = player.method_5667();
       ArrowState state = this.states.computeIfAbsent(uuid, id -> new ArrowState());
       this.seenPlayers.add(uuid);
       
       int color = getPlayerColor(player);
       float targetYaw = getRelativeYaw((Entity)player, partialTicks, playerYaw, selfPos);
       state.rotation = interpolateAngle(state.rotation, targetYaw, 0.18F);
       state.alpha = approach(state.alpha, 1.0F, 0.12F);
       float alpha = MathHelper.method_15363(state.alpha, 0.0F, 1.0F);
       if (alpha <= 0.01F) {
         continue;
       }
       
       int drawColor = ColorUtils.applyAlpha(color, alpha);
       int shadowColor = ColorUtils.applyAlpha(ColorUtils.darken(color, 0.55F), alpha * 0.65F);
       renderArrow(event.getContext().method_51448(), centerX, centerY, y, arrowSize, state.rotation, drawColor, shadowColor);
     } 
     
     this.states.entrySet().removeIf(entry -> {
           if (this.seenPlayers.contains(entry.getKey())) {
             return false;
           }
           ArrowState state = (ArrowState)entry.getValue();
           state.alpha = approach(state.alpha, 0.0F, 0.1F);
           return (state.alpha <= 0.02F);
         });
   }
 
   
   private void renderArrow(MatrixStack matrices, float centerX, float centerY, float y, float size, float rotation, int color, int shadowColor) {
     Identifier ARROW;
     if (this.type.getIndex() == 0) {
       ARROW = FIRST_ARROW_TEXTURE;
     } else if (this.type.getIndex() == 1) {
       ARROW = SECOND_ARROW_TEXTURE;
     } else {
       ARROW = MAMA_ARROW_TEXTURE;
     } 
     
     Identifier ARROW_TEXTURE = ARROW;
     matrices.method_22903();
     matrices.method_46416(centerX, centerY, 0.0F);
     matrices.method_22907(RotationAxis.field_40718.rotationDegrees(rotation));
     matrices.method_46416(-centerX, -centerY, 0.0F);
     
     float x = centerX - size * 0.5F;
     RenderUtils.drawImage(matrices, ARROW_TEXTURE, x, y + size * 0.08F, size, size, shadowColor);
     RenderUtils.drawImage(matrices, ARROW_TEXTURE, x, y, size, size, color);
     matrices.method_22909();
   }
   
   private void fadeAllStates() {
     this.states.entrySet().removeIf(entry -> {
           ArrowState state = (ArrowState)entry.getValue();
           state.alpha = approach(state.alpha, 0.0F, 0.1F);
           return (state.alpha <= 0.02F);
         });
   }
   
   private float approach(float current, float target, float factor) {
     factor = MathHelper.method_15363(factor, 0.0F, 1.0F);
     return MathHelper.method_16439(factor, current, target);
   }
   
   private int getPlayerColor(AbstractClientPlayerEntity player) {
     String name = player.method_5477().getString();
     boolean isFriend = (astra.INSTANCE.friendStorage != null && astra.INSTANCE.friendStorage.isFriend(name));
     return isFriend ? ColorUtils.rgb(80, 170, 255) : ColorUtils.getThemeColor();
   }
   
   private float getRelativeYaw(Entity entity, float partialTicks, float playerYaw, Vec3d selfPos) {
     Vec3d entityPos = MathUtils.interpolate(entity, partialTicks);
     
     double dx = entityPos.field_1352 - selfPos.field_1352;
     double dz = entityPos.field_1350 - selfPos.field_1350;
     float yaw = (float)-Math.toDegrees(Math.atan2(dx, dz));
     return MathHelper.method_15393(yaw - playerYaw);
   }
   
   private float getReferenceYaw(float partialTicks) {
     if (FreeLookStorage.isActive()) {
       return FreeLookStorage.getFreeYaw();
     }
     return MathHelper.method_16439(partialTicks, mc.field_1724.field_5982, mc.field_1724.method_36454());
   }
   
   private Vec3d getReferencePos(float partialTicks) {
     if (FreeLookStorage.isActive() && mc.field_1773 != null && mc.field_1773.method_19418() != null) {
       return mc.field_1773.method_19418().method_19326();
     }
     return MathUtils.interpolate((Entity)mc.field_1724, partialTicks);
   }
   
   private float interpolateAngle(float current, float target, float factor) {
     float delta = MathHelper.method_15393(target - current);
     return current + delta * factor;
   }
   
   private boolean isGhostPlayer(AbstractClientPlayerEntity player) {
     if (player.method_5797() != null) {
       String name = player.method_5797().getString();
       if (name != null && name.startsWith("Ghost_")) {
         return true;
       }
     } 
     return ("OtherClientPlayerEntity".equals(player.getClass().getSimpleName()) && player.method_36455() == -30.0F);
   }
   
   private static final class ArrowState {
     private float alpha;
     private float rotation;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Arrows.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */