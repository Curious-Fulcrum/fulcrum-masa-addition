package dev.fulcrum.dma.impl.feature.sortInventory;

import dev.fulcrum.dma.Configs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxItemHelper {

    public static boolean isEmptyShulkerBoxItem(ItemStack itemStack) {
        if (!ShulkerBoxItemHelper.isShulkerBoxBlockItem(itemStack)) {
            return false;
        }


        ItemContainerContents icc = itemStack.get(DataComponents.CONTAINER);
        if (icc != null) {
            icc.stream().allMatch(ItemStack::isEmpty);
        }
        return true;
    }

    public static boolean isShulkerBoxBlockItem(@NotNull ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem &&
                ((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }

    public static int compareShulkerBox(@Nullable ItemContainerContents a, @Nullable ItemContainerContents b) {
        int aSize = 0, bSize = 0;
        if (a != null) aSize = a.stream().toList().size();
        if (b != null) bSize = b.stream().toList().size();
        return aSize - bSize;
    }

    public static int getMaxCount(ItemStack itemStack) {
        return Configs.sortInventorySupportEmptyShulkerBoxStack.getBooleanValue() &&
                ShulkerBoxItemHelper.isEmptyShulkerBoxItem(itemStack) ? 64 : itemStack.getMaxStackSize();
    }

    public static boolean isStackable(ItemStack itemStack) {
        return getMaxCount(itemStack) > 1 && (!itemStack.isDamageableItem() || !itemStack.isDamaged());
    }
}