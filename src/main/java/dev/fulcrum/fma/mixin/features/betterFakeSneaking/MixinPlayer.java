package dev.fulcrum.fma.mixin.features.betterFakeSneaking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.fulcrum.fma.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class MixinPlayer extends Entity {
    private MixinPlayer(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDD)Z"))
    private boolean betterFakeSneaking(Player player, double x, double z, double distance, Operation<Boolean> original) {
        // Patched value if betterSneak is enabled, otherwise vanilla value.
        boolean ori = original.call(player, x, z, distance);
        if (shouldApplyTweak()) {
            return canFallAtLeastWithLiquid(x, z);
        }
        return ori;
    }

    /// Modified version of canFallAtLeast, adding fluid check and increasing the distance of downward check
    @Unique
    private boolean canFallAtLeastWithLiquid(double x, double z) {
        AABB aABB = this.getBoundingBox();
        AABB collisionBox = new AABB(aABB.minX + 1.0E-7 + x, aABB.minY - 1.2 - 1.0E-7, aABB.minZ + 1.0E-7 + z, aABB.maxX - 1.0E-7 + x, aABB.minY, aABB.maxZ - 1.0E-7 + z);
        return this.level().noCollision(this, collisionBox, true);
    }

    @Unique
    private boolean shouldApplyTweak() {
        return Configs.betterFakeSneaking.getBooleanValue() && FeatureToggle.TWEAK_FAKE_SNEAKING.getBooleanValue() && level().isClientSide;
    }

}

