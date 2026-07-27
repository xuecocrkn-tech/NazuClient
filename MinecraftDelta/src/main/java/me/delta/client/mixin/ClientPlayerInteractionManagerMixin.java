package me.delta.client.mixin;

import me.delta.client.DeltaClient;
import me.delta.client.module.modules.combat.Criticals;
import me.delta.client.module.modules.misc.AutoTool;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        // Criticals hook
        Criticals criticals = DeltaClient.MODULE_MANAGER.getModule(Criticals.class);
        if (criticals != null && criticals.isEnabled()) {
            criticals.onAttack();
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void onBlockBreakStart(BlockPos pos, Direction direction, CallbackInfo ci) {
        AutoTool autoTool = DeltaClient.MODULE_MANAGER.getModule(AutoTool.class);
        if (autoTool != null) {
            autoTool.onBlockBreakStart(pos);
        }
    }
}
