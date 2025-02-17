package dev.fulcrum.dma.mixin.accessor.color;

import dev.fulcrum.dma.features.sortInventory.ColorAccessor;import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StainedGlassPaneBlock.class)
public class MixinStainedGlassPaneBlock implements ColorAccessor {
    @Shadow
    @Final
    private DyeColor color;

    @Override
    public DyeColor fma$getColor() {
        return this.color;
    }
}
