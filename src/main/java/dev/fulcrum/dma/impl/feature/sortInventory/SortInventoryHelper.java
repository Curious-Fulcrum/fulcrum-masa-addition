package dev.fulcrum.dma.impl.feature.sortInventory;

import dev.fulcrum.dma.SharedConstants;
import dev.fulcrum.dma.mixin.accessor.AbstractContainerScreenAccessor;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.compat.minecraft.world.item.ItemStackCompat;

import java.util.*;
import java.util.stream.Collectors;

public class SortInventoryHelper {
    private static final int SLOT_CLICKED_OUTSIDE = -999;

    public static void sort() {
        // check
        var client = Minecraft.getInstance();
        var currentScreen = client.screen;
        var player = client.player;
        if (!(currentScreen instanceof AbstractContainerScreen<?> screen) || currentScreen instanceof CreativeModeInventoryScreen)
            return;
        var hoveredSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
        if (hoveredSlot == null || client.gameMode == null || player == null) return;

        var container = player.containerMenu;
        var sortRangeAndIgnoreList = getSortRangeAndIgnoreList(container, hoveredSlot);
        if (sortRangeAndIgnoreList == null) return;
        var sortRange = sortRangeAndIgnoreList.left();
        var ignoreList = sortRangeAndIgnoreList.right();

        var cursorStack = container.getCarried().copy();
        var stacks = container.slots.stream().map(slot -> slot.getItem().copy()).collect(Collectors.toList());
        IntList mergeQueue, sortQueue;
        int begin = sortRange.leftInt(), end = sortRange.rightInt();

        if (ignoreList.isEmpty()) {
            mergeQueue = mergeItems(stacks, cursorStack, begin, end);
            sortQueue = quickSort(stacks, begin, end);
        } else {
            mergeQueue = new IntArrayList(mergeItems(stacks, cursorStack, begin, ignoreList.getFirst()));
            sortQueue = new IntArrayList(quickSort(stacks, begin, ignoreList.getFirst()));
            for (int i = 1, limit = ignoreList.size(); i < limit; i++) {
                mergeQueue.addAll(mergeItems(stacks, cursorStack, ignoreList.get(i - 1) + 1, ignoreList.get(i)));
                sortQueue.addAll(quickSort(stacks, ignoreList.get(i - 1) + 1, ignoreList.get(i)));
            }
            mergeQueue.addAll(mergeItems(stacks, cursorStack, ignoreList.getLast() + 1, end));
            sortQueue.addAll(quickSort(stacks, ignoreList.getLast() + 1, end));
        }

        // do click
        for (int slotId : mergeQueue)
            // 放入打捆包需要右键
            if (slotId < 0 && slotId != SLOT_CLICKED_OUTSIDE)
                client.gameMode.handleInventoryMouseClick(container.containerId, -slotId, 1, ClickType.PICKUP, player);
            else
                client.gameMode.handleInventoryMouseClick(container.containerId, slotId, 0, ClickType.PICKUP, player);
        for (int slotId : sortQueue) {
            client.gameMode.handleInventoryMouseClick(container.containerId, slotId, 0, ClickType.PICKUP, player);
        }

        client.getSoundManager().play(mergeQueue.isEmpty() && sortQueue.isEmpty() ?
                SimpleSoundInstance.forUI(SoundEvents.DISPENSER_FAIL, 1.0F) :
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Nullable
    private static Pair<IntIntPair, List<Integer>> getSortRangeAndIgnoreList(AbstractContainerMenu container, @NotNull Slot hoveredSlot) {
        int mouseIdx = hoveredSlot.index;
        if (mouseIdx == 0 && hoveredSlot.getContainerSlot() != 0) mouseIdx = hoveredSlot.getContainerSlot();

        int left = mouseIdx, right = mouseIdx + 1;

        Class<?> clazz = container.slots.get(mouseIdx).container.getClass();
        IntList ignoreList1 = new IntArrayList(), ignoreList2 = new IntArrayList();
        if (SharedConstants.HAS_GCA && shouldIgnore(hoveredSlot.getItem())) ignoreList2.add(mouseIdx);

        for (int i = mouseIdx - 1; i >= 0; i--) {
            var slot = container.slots.get(i);
            if (clazz != slot.container.getClass()) {
                left = i + 1;
                break;
            } else if (i == 0) left = 0;
            else if (SharedConstants.HAS_GCA && shouldIgnore(slot.getItem())) ignoreList1.add(i);
        }

        var limit = container.slots.size();
        for (int i = right; i < limit; i++) {
            var slot = container.slots.get(i);
            if (clazz != slot.container.getClass()) {
                right = i;
                break;
            } else if (i == limit - 1) right = limit;
            else if (SharedConstants.HAS_GCA && shouldIgnore(slot.getItem())) ignoreList2.add(i);
        }

        if (hoveredSlot.container instanceof Inventory) {
            if (left == 5 && right == 46) {
                if (mouseIdx >= 9 && mouseIdx < 36) {
                    left = 9;
                    right = 36;
                } else if (mouseIdx >= 36 && mouseIdx < 45) {
                    left = 36;
                    right = 45;
                } else return null;
            } else if (right - left == 36) {
                if (mouseIdx >= left && mouseIdx < left + 27)
                    right = left + 27;
                else left += 27;
            }
        }
        var result = ignoreList1.reversed();
        result.addAll(ignoreList2);
        return left + 1 == right ? null : Pair.of(IntIntPair.of(left, right), result);
    }

    private static boolean shouldIgnore(ItemStack itemStack) {
        var itemTag = itemStack.get(DataComponents.CUSTOM_DATA);
        return itemTag != null && !itemTag.isEmpty() && itemTag.contains("GcaClear");
    }

    private static @NotNull IntList mergeItems(List<ItemStack> targetStacks, @NotNull ItemStack cursorStack, int begin, int end) {
        var mergeQueue = new IntArrayList();
        if (end - begin <= 0) return mergeQueue;
        // 先把手中的物品尽量地放入背包或容器中，从而保证后续的整理不会被手中物品合并而影响
        if (!cursorStack.isEmpty()) mergeQueue.addAll(tryMergeItem(targetStacks, cursorStack, begin, end));

        for (int i = end - 1; i >= begin; i--) {
            var stack = targetStacks.get(i);
            if (stack.isEmpty()) continue;

            var mergeRecord = tryMergeItem(targetStacks, stack, begin, i);
            if (mergeRecord.isEmpty()) continue;

            mergeQueue.add(i);
            mergeQueue.addAll(mergeRecord);
            mergeQueue.add(i);
        }

        // 在合并完后如果鼠标还有物品则尝试把鼠标的物品放进容器或箱子
        if (!cursorStack.isEmpty()) {
            for (int i = begin; i < end; i++) {
                if (!targetStacks.get(i).isEmpty()) continue;
                mergeQueue.add(i);
                targetStacks.set(i, cursorStack.copy());
                cursorStack.setCount(0);
                break;
            }
        }

        return mergeQueue;
    }

    /// @return 物品合并记录
    private static @NotNull IntList tryMergeItem(List<ItemStack> stacks, ItemStack stackToAdd, int begin, int end) {
        // merge in [begin, end)
        var mergeRecord = new IntArrayList();
        for (int i = begin; i < end; i++) {
            var stack = stacks.get(i);
            if (stack.isEmpty() || !canStackAddMore(stack, stackToAdd)) continue;

            mergeRecord.add(i);
            int available = ShulkerBoxItemHelper.getMaxCount(stack) - stack.getCount();
            int count = stackToAdd.getCount();
            if (available >= count) {
                stack.grow(count);
                stackToAdd.shrink(count);
                break;
            } else {
                stack.grow(available);
                stackToAdd.shrink(available);
            }
        }
        return mergeRecord;
    }

    /// sort [start, end)
    private static @NotNull IntList quickSort(List<ItemStack> stacks, int begin, int end) {
        // uncomment the following line to help check if the `stacks` is illegally modified
        // stacks = Collections.unmodifiableList(stacks);
        int size = end - begin;
        IntList sortQueue = new IntArrayList();
        if (size <= 0) return sortQueue;
        List<ObjectIntPair<ItemStack>> sorted = new ArrayList<>(size);

        boolean allShulkerBox = true;
        for (int i = begin; i < end; i++) {
            var stack = stacks.get(i);
            sorted.add(ObjectIntPair.of(stack, i));
            if (allShulkerBox && !stack.isEmpty() && !ShulkerBoxItemHelper.isShulkerBoxBlockItem(stack))
                allShulkerBox = false;
        }
        sorted.sort(new ItemStackComparator(allShulkerBox));

        // map stack to tuple (oldIndex, newIndex)
        var map = new Int2IntOpenHashMap(size);
        for (int i = 0; i < size; i++) {
            var stackInfo = sorted.get(i);
            if (!stackInfo.left().isEmpty())
                map.put(stackInfo.rightInt(), i + begin);
        }


        int oldIndex = 0;
        boolean interrupted = true;
        while (!map.isEmpty()) {
            if (interrupted) //noinspection OptionalGetWithoutIsPresent
                oldIndex = map.keySet().intStream().findAny().getAsInt();
            int newIndex = map.remove(oldIndex);
            if (oldIndex != newIndex) {
                if (interrupted) sortQueue.add(oldIndex);
                sortQueue.add(newIndex);
                oldIndex = newIndex;
                interrupted = stacks.get(oldIndex).isEmpty();
            } else interrupted = true;
        }
        return sortQueue;
    }

    private static boolean canStackAddMore(@NotNull ItemStack target, ItemStack stack) {
        return ItemStackCompat.isSameItemSameTags(target, stack)
                && ShulkerBoxItemHelper.isStackable(target)
                && target.getCount() < ShulkerBoxItemHelper.getMaxCount(target);
    }
}
