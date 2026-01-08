package dev.fulcrum.fma.features.sortInventory;

import dev.fulcrum.fma.config.Configs;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ItemStackComparator implements Comparator<ObjectIntPair<ItemStack>> {

    private static final Object2IntMap<DyeColor> DYE_COLOR_MAPPING = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<MapColor> MAP_COLOR_MAPPING = new Object2IntOpenHashMap<>();

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
        Item itemA = a.getItem(), itemB = b.getItem();
        int aId = Item.getId(itemA), bId = Item.getId(itemB);

        if (Configs.sortInventoryShulkerBoxLast.getBooleanValue() && !allShulkerBox)
            if (SortInventoryHelper.isShulkerBoxBlockItem(a) && !SortInventoryHelper.isShulkerBoxBlockItem(b))
                return 1;
            else if (!SortInventoryHelper.isShulkerBoxBlockItem(a) && SortInventoryHelper.isShulkerBoxBlockItem(b))
                return -1;

        // if item is empty, then id always equals 0
        if (aId == 0 && bId != 0) return 1;
        else if (aId != 0 && bId == 0) return -1;
        else if (aId == 0) return 0;

        if (itemA instanceof BlockItem blockItemA && itemB instanceof BlockItem blockItemB) {
            Block blockA = blockItemA.getBlock(), blockB = blockItemB.getBlock();

            if (blockA instanceof AbstractBannerBlock && blockB instanceof AbstractBannerBlock
                    || blockA instanceof BedBlock && blockB instanceof BedBlock
                    || blockA instanceof ShulkerBoxBlock && blockB instanceof ShulkerBoxBlock
                    || blockA instanceof StainedGlassBlock && blockB instanceof StainedGlassBlock
                    || blockA instanceof StainedGlassPaneBlock && blockB instanceof StainedGlassPaneBlock
                    || blockA instanceof WoolCarpetBlock && blockB instanceof WoolCarpetBlock)
                return DYE_COLOR_MAPPING.getInt(((ColorAccessor) blockA).fma$getColor())
                        - DYE_COLOR_MAPPING.getInt(((ColorAccessor) blockB).fma$getColor());

            String ida = BuiltInRegistries.BLOCK.getKey(blockA).getPath();
            String idb = BuiltInRegistries.BLOCK.getKey(blockB).getPath();

            if (bothEndsWith("wool", ida, idb)
                    || bothEndsWith("terracotta", ida, idb)
                    || bothEndsWith("concrete", ida, idb)
                    || bothEndsWith("candle", ida, idb))
                return MAP_COLOR_MAPPING.getOrDefault(blockA.defaultMapColor(), 0)
                        - MAP_COLOR_MAPPING.getOrDefault(blockB.defaultMapColor(), 0);
        }

        if (itemA instanceof DyeItem dyeA && itemB instanceof DyeItem dyeB)
            return DYE_COLOR_MAPPING.getInt(dyeA.getDyeColor()) - DYE_COLOR_MAPPING.getInt(dyeB.getDyeColor());

        if (aId != bId) return aId - bId;

        // 相同物品
        int difference = comparePrimeData(a, b);
        if (difference != 0) return difference;
        // 物品少的在后面
        return (difference = hashCodeOfPatch(a) - hashCodeOfPatch(b)) != 0 ? difference : b.getCount() - a.getCount();
    }

    private static boolean bothEndsWith(String target, @NotNull String a, @NotNull String b) {
        return a.endsWith(target) && b.endsWith(target);
    }

    private static final List<DataComponentType<?>> PRIME_DATA_TYPES = List.of(
            DataComponents.CUSTOM_NAME, DataComponents.ENCHANTMENTS, DataComponents.DAMAGE
    );

    private static int hashCodeOfPatch(ItemStack itemStack) {
        final AtomicInteger keyHash = new AtomicInteger(), valueHash = new AtomicInteger();
        itemStack.getComponentsPatch().entrySet().parallelStream()
                .filter(entry -> !PRIME_DATA_TYPES.contains(entry.getKey()))
                .forEach(entry -> {
                    keyHash.getAndAdd(entry.getKey().hashCode());
                    valueHash.getAndAdd(entry.getValue().hashCode());
                });
        return keyHash.get() * 31 + valueHash.get();
    }

    private static <T> int compareData(ItemStack a, ItemStack b, DataComponentType<T> type,
                                       T defaultValue, Comparator<T> comparator) {
        T dataA = a.getOrDefault(type, defaultValue), dataB = b.getOrDefault(type, defaultValue);
        return comparator.compare(dataA, dataB);
    }

    private static int comparePrimeData(ItemStack a, ItemStack b) {
        int result = compareData(a, b, DataComponents.CUSTOM_NAME, Component.empty(), Comparator.comparing(Component::getString));
        if (result != 0) return result;
        result = compareData(a, b, DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY, new EnchantmentsComparator());
        if (result != 0) return result;
        result = compareData(a, b, DataComponents.DAMAGE, 0, Integer::compareTo);
        return result;
    }
}

class EnchantmentsComparator implements Comparator<ItemEnchantments> {

    @Override
    public int compare(ItemEnchantments e1, ItemEnchantments e2) {
        int differenceOfSize = e2.size() - e1.size(); // 附魔少的在后面
        if (differenceOfSize != 0) return differenceOfSize;
        return hashCode(e1) - hashCode(e2);
    }

    private static int hashCode(ItemEnchantments e) {
        if (e == ItemEnchantments.EMPTY) return -1;
        return e.entrySet().stream().parallel()
                .mapToInt(entry -> {
                    var enchantment = entry.getKey().value();
                    return enchantment.hashCode() + enchantment.getMaxLevel() - entry.getIntValue();
                })
                .sum();
    }

}
