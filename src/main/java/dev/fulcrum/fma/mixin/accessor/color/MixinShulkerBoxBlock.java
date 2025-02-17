package dev.fulcrum.fma.mixin.accessor.color;

import dev.fulcrum.fma.features.sortInventory.ColorAccessor;import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBoxBlock.class)
public class MixinShulkerBoxBlock implements ColorAccessor {
    @Shadow
    @Final
    private @Nullable DyeColor color;

    @Override
    public DyeColor fma$getColor() {
        return this.color;
    }
}
