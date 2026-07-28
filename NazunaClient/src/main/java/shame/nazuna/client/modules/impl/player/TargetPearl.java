package shame.nazuna.client.modules.impl.player;
 
 import java.util.Comparator;
 import net.minecraft.Entity;
 import net.minecraft.LivingEntity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.EnderPearlEntity;
 import net.minecraft.ItemStack;
 import net.minecraft.Items;
 import net.minecraft.ItemConvertible;
 import net.minecraft.Blocks;
 import net.minecraft.BlockPos;
 import net.minecraft.Position;
 import net.minecraft.Box;
 import net.minecraft.HitResult;
 import net.minecraft.Vec2f;
 import net.minecraft.Vec3d;
 import net.minecraft.Packet;
 import net.minecraft.PlayerMoveC2SPacket;
 import net.minecraft.MathHelper;
 import net.minecraft.RaycastContext;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.EventBinding;
 import shame.nazuna.api.events.implement.EventMoveInput;
 import shame.nazuna.api.events.implement.EventUpdate;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.math.TimerUtils;
 import shame.nazuna.api.utils.player.InventoryUtils;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class TargetPearl extends Module {
   private static final double MAX_TRACK_DISTANCE = 256.0D;
   private static final double MIN_LANDING_DISTANCE = 11.0D;
   private static final long LOCAL_THROW_COOLDOWN_MS = 2500L;
   public static final TargetPearl INSTANCE = new TargetPearl();
   private static final float DIRECT_MIN_PITCH = -25.0F;
   private final ModeSetting mode = new ModeSetting("Тип", "Автоматический", new String[] { "По бинду", "Автоматический" }); private static final float DIRECT_MAX_PITCH = 35.0F; private static final float PITCH_STEP = 0.25F;
   private final BindSetting bind = (new BindSetting("Бинд", -1))
     .visible(() -> Boolean.valueOf(this.mode.is("По бинду")));
   private final BooleanSetting onlyTarget = new BooleanSetting("Только за противником", false);
   private final BooleanSetting ignoreFriends = new BooleanSetting("Игнорировать друзей", true);
   
   private final TimerUtils timer = new TimerUtils();
   
   private EnderPearlEntity targetPearl;
   private int lastHandledPearlId = -1;
   private long nextThrowAt;
   private boolean isThrowing;
   private Vec2f serverRotation;
   
   public TargetPearl() {
     super("TargetPearl", "Автоматически бросает жемчуг в цель", Module.ModuleCategory.PLAYER);
     addSettings(new Setting[] { (Setting)this.mode, (Setting)this.bind, (Setting)this.onlyTarget, (Setting)this.ignoreFriends });
   }
   
   @EventLink
   public void onBinding(EventBinding event) {
     if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1755 != null) {
       return;
     }
     
     if (!this.mode.is("По бинду") || event.getKey() != this.bind.getKey()) {
       return;
     }
     
     if (canThrowNow()) {
       aimAndThrowPearl();
     }
   }
 
 
 
 
 
 
 
   
   @EventLink
   public void onUpdate(EventUpdate event) {
}
}
