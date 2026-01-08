package dev.fulcrum.fma.mixin.features.betterFakeSneaking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.fulcrum.fma.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class MixinPlayer extends Entity {
    @Unique private static final float MAX_STEP_HEIGHT = 1.2F;
    @Unique private float originalStepHeight = 0.0F;

    private MixinPlayer(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract boolean canFallAtLeast(double dx, double dz, double dy);


    @WrapOperation(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;maxUpStep()F"))
    private float fakeStepHeight(Player player, Operation<Float> original) {
        originalStepHeight = original.call(player);
        return shouldApplyTweak() ? MAX_STEP_HEIGHT : original.call(player);
    }

    @WrapOperation(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDD)Z"))
    private boolean checkFallAtLava(Player player, double dx, double dz, double dy, Operation<Boolean> original) {
        // Patched value if betterSneak is enabled, otherwise vanilla value.
        boolean result = original.call(player, dx, dz, dy);

        if (!shouldApplyTweak()) return result;

        // Always vanilla value and bypass WrapOperation chain invoke.
        boolean vanillaResult = canFallAtLeast(dx, dz, originalStepHeight);
        if (vanillaResult && !result && player.level().getFluidState(player.blockPosition().below()).getType() instanceof LavaFluid)
            return true;
        return result;
    }

    @Unique
    private boolean shouldApplyTweak() {
        return Configs.betterFakeSneaking.getBooleanValue()
                && FeatureToggle.TWEAK_FAKE_SNEAKING.getBooleanValue()
                && level().isClientSide;
    }

}

