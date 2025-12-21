package dev.fulcrum.fma.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.npc.Villager.class)
public interface VillagerAccessor {
    @Accessor
    int getNumberOfRestocksToday();

    @Accessor
    long getLastRestockGameTime();
}
