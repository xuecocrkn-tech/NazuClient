package me.delta.client.module.modules.combat;

import me.delta.client.module.Category;
import me.delta.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class KillAura extends Module {
    private final me.delta.client.settings.NumberSetting range = createNumber("Range", "Attack range", 4.5, 1.0, 6.0, 0.1, " blocks");
    private final me.delta.client.settings.NumberSetting cps = createNumber("CPS", "Attacks per second", 12.0, 1.0, 20.0, 1.0);
    private final me.delta.client.settings.ModeSetting targetMode = createMode("Target Mode", "Target selection", "Single", "Single", "Multi", "Switch");
    private final me.delta.client.settings.BooleanSetting players = createBoolean("Players", "Attack other players", true);
    private final me.delta.client.settings.BooleanSetting animals = createBoolean("Animals", "Attack animals", false);
    private final me.delta.client.settings.BooleanSetting monsters = createBoolean("Monsters", "Attack monsters", false);
    private final me.delta.client.settings.BooleanSetting invisibles = createBoolean("Invisibles", "Attack invisible entities", false);
    private final me.delta.client.settings.BooleanSetting autoBlock = createBoolean("Auto Block", "Block with shield/ sword", false);
    private final me.delta.client.settings.BooleanSetting rotate = createBoolean("Rotate", "Smooth rotation to target", true);

    private Entity target;
    private long lastAttackTime = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        target = null;
        lastAttackTime = 0;
    }

    @Override
    protected void onDisable() {
        target = null;
    }

    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        target = findTarget();
        if (target == null) return;

        long now = System.currentTimeMillis();
        long delay = (long) (1000.0 / cps.getValue());

        if (now - lastAttackTime >= delay) {
            attack(target);
            lastAttackTime = now;

            if ("Switch".equals(targetMode.getValue())) {
                target = findTarget();
            }
        }
    }

    private Entity findTarget() {
        return mc.world.getEntities()
                .by(entity -> isValidTarget(entity))
                .stream()
                .min(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)))
                .orElse(null);
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player || !entity.isAlive()) return false;
        if (!invisibles.getValue() && entity.isInvisible()) return false;
        if (entity.squaredDistanceTo(mc.player) > range.getValue() * range.getValue()) return false;

        if (entity instanceof PlayerEntity) return players.getValue();
        if (entity instanceof net.minecraft.entity.mob.Monster) return monsters.getValue();
        if (entity instanceof net.minecraft.entity.passive.PassiveEntity) return animals.getValue();

        return false;
    }

    private void attack(Entity entity) {
        if (rotate.getValue()) {
            rotateTo(entity);
        }

        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (autoBlock.getValue()) {
            mc.options.useKey.setPressed(true);
        }
    }

    private void rotateTo(Entity entity) {
        if (mc.player == null) return;
        Vec3d targetPos = entity.getBoundingBox().getCenter();
        Vec3d playerPos = mc.player.getEyePos();

        double dx = targetPos.x - playerPos.x;
        double dz = targetPos.z - playerPos.z;
        double dy = targetPos.y - playerPos.y;

        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double pitch = -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        mc.player.setYaw((float) yaw);
        mc.player.setPitch((float) MathHelper.clamp(pitch, -90.0, 90.0));
    }

    public Entity getTarget() {
        return target;
    }
}
