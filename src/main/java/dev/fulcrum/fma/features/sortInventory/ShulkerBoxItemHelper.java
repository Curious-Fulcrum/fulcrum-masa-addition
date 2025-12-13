package dev.fulcrum.fma.features.sortInventory;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ShulkerBoxItemHelper {

//    static boolean isEmptyShulkerBoxItem(ItemStack itemStack) {
//        if (!ShulkerBoxItemHelper.isShulkerBoxBlockItem(itemStack)) return false;
//        var contents = itemStack.get(DataComponents.CONTAINER);
//        return contents != null && contents.nonEmptyItems().iterator().hasNext();
//    }

    static boolean isShulkerBoxBlockItem(@NotNull ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    static int compareShulkerBox(@Nullable ItemContainerContents a, @Nullable ItemContainerContents b) {
        int aSize = 0, bSize = 0;
        if (a != null) aSize = a.stream().toList().size();
        if (b != null) bSize = b.stream().toList().size();
        return aSize - bSize;
    }

    static int getMaxCount(ItemStack itemStack) {
//        return Configs.sortInventorySupportEmptyShulkerBoxStack.getBooleanValue() &&
//                ShulkerBoxItemHelper.isEmptyShulkerBoxItem(itemStack) ? 64 : itemStack.getMaxStackSize();
        return itemStack.getMaxStackSize();
    }

    static boolean isStackable(ItemStack itemStack) {
        return getMaxCount(itemStack) > 1 && (!itemStack.isDamageableItem() || !itemStack.isDamaged());
    }
}