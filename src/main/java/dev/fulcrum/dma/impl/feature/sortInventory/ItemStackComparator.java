package dev.fulcrum.dma.impl.feature.sortInventory;

import com.google.common.collect.Maps;
import dev.fulcrum.dma.Configs;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

class ItemStackComparator implements Comparator<ObjectIntPair<ItemStack>> {

    private static final Map<DyeColor, Integer> DYE_COLOR_MAPPING = Maps.newHashMap();
    private static final Map<MapColor, Integer> MAP_COLOR_MAPPING = Maps.newHashMap();

    static {
        DYE_COLOR_MAPPING.put(null, 0);
        DYE_COLOR_MAPPING.put(DyeColor.WHITE, 1);
        DYE_COLOR_MAPPING.put(DyeColor.LIGHT_GRAY, 2);
        DYE_COLOR_MAPPING.put(DyeColor.GRAY, 3);
        DYE_COLOR_MAPPING.put(DyeColor.BLACK, 4);
        DYE_COLOR_MAPPING.put(DyeColor.BROWN, 5);
        DYE_COLOR_MAPPING.put(DyeColor.RED, 6);
        DYE_COLOR_MAPPING.put(DyeColor.ORANGE, 7);
        DYE_COLOR_MAPPING.put(DyeColor.YELLOW, 8);
        DYE_COLOR_MAPPING.put(DyeColor.LIME, 9);
        DYE_COLOR_MAPPING.put(DyeColor.GREEN, 10);
        DYE_COLOR_MAPPING.put(DyeColor.CYAN, 11);
        DYE_COLOR_MAPPING.put(DyeColor.LIGHT_BLUE, 12);
        DYE_COLOR_MAPPING.put(DyeColor.BLUE, 13);
        DYE_COLOR_MAPPING.put(DyeColor.PURPLE, 14);
        DYE_COLOR_MAPPING.put(DyeColor.MAGENTA, 15);
        DYE_COLOR_MAPPING.put(DyeColor.PINK, 16);
        MAP_COLOR_MAPPING.put(null, 0);
        MAP_COLOR_MAPPING.put(MapColor.SNOW, 1);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_LIGHT_GRAY, 2);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_GRAY, 3);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_BLACK, 4);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_BROWN, 5);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_RED, 6);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_ORANGE, 7);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_YELLOW, 8);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_LIGHT_GREEN, 9);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_GREEN, 10);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_CYAN, 11);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_LIGHT_BLUE, 12);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_BLUE, 13);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_PURPLE, 14);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_MAGENTA, 15);
        MAP_COLOR_MAPPING.put(MapColor.COLOR_PINK, 16);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_WHITE, 1);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_LIGHT_GRAY, 2);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_GRAY, 3);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_BLACK, 4);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_BROWN, 5);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_RED, 6);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_ORANGE, 7);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_YELLOW, 8);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_LIGHT_GREEN, 9);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_GREEN, 10);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_CYAN, 11);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_LIGHT_BLUE, 12);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_BLUE, 13);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_PURPLE, 14);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_MAGENTA, 15);
        MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_PINK, 16);
    }

    private final boolean allShulkerBox;

    public ItemStackComparator(boolean allShulkerBox) {
        this.allShulkerBox = allShulkerBox;
    }

    @Override
    public int compare(ObjectIntPair<ItemStack> infoA, ObjectIntPair<ItemStack> infoB) {
        ItemStack a = infoA.left(), b = infoB.left();
        int aId = Item.getId(a.getItem()), bId = Item.getId(b.getItem());

        if (Configs.sortInventoryShulkerBoxLast.getBooleanValue() && !allShulkerBox)
            if (ShulkerBoxItemHelper.isShulkerBoxBlockItem(a) && !ShulkerBoxItemHelper.isShulkerBoxBlockItem(b))
                return 1;
            else if (!ShulkerBoxItemHelper.isShulkerBoxBlockItem(a) && ShulkerBoxItemHelper.isShulkerBoxBlockItem(b))
                return -1;

        if (a.isEmpty() && !b.isEmpty()) return 1;
        else if (!a.isEmpty() && b.isEmpty()) return -1;
        else if (a.isEmpty()) return 0;

        if (ShulkerBoxItemHelper.isShulkerBoxBlockItem(a) && ShulkerBoxItemHelper.isShulkerBoxBlockItem(b) && a.getItem() == b.getItem())
            return -ShulkerBoxItemHelper.compareShulkerBox(a.get(DataComponents.CONTAINER), b.get(DataComponents.CONTAINER));

        if (a.getItem() instanceof BlockItem blockItemA && b.getItem() instanceof BlockItem blockItemB) {
            Block blockA = blockItemA.getBlock(), blockB = blockItemB.getBlock();

            if (blockA instanceof ColorAccessor accessorA && blockB instanceof ColorAccessor accessorB)
                return DYE_COLOR_MAPPING.get(accessorA.fma$getColor()) - DYE_COLOR_MAPPING.get(accessorB.fma$getColor());

            String ida = BuiltInRegistries.BLOCK.getKey(blockA).getPath();
            String idb = BuiltInRegistries.BLOCK.getKey(blockB).getPath();

            if (bothContains("wool", ida, idb)
                    || bothContains("terracotta", ida, idb)
                    || bothContains("concrete", ida, idb)
                    || bothContains("candle", ida, idb)) {
                return MAP_COLOR_MAPPING.getOrDefault(blockA.defaultMapColor(), 0)
                        - MAP_COLOR_MAPPING.getOrDefault(blockB.defaultMapColor(), 0);
            }
        }

        if (a.getItem() instanceof DyeItem dyeA && b.getItem() instanceof DyeItem dyeB)
            return DYE_COLOR_MAPPING.get(dyeA.getDyeColor()) - DYE_COLOR_MAPPING.get(dyeB.getDyeColor());

        if (aId == bId) {
            var patchA = a.getComponentsPatch();
            var patchB = b.getComponentsPatch();
            boolean hasPatchA = patchA != DataComponentPatch.EMPTY;
            boolean hasPatchB = patchB != DataComponentPatch.EMPTY;

            if (hasPatchA && !hasPatchB) return -1;
            else if (!hasPatchA && hasPatchB) return 1;
            else if (hasPatchA)
                return Objects.compare(patchA, patchB, Comparator.comparingInt(DataComponentPatch::hashCode));

            // 物品少的排在后面
            return b.getCount() - a.getCount();
        }
        return aId - bId;
    }

    private static boolean bothContains(String target, @NotNull String a, @NotNull String b) {
        return a.contains(target) && b.contains(target);
    }
}
