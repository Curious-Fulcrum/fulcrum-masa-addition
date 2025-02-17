package dev.fulcrum.dma.impl.feature.sortInventory;

import dev.fulcrum.dma.mixin.accessor.AbstractContainerScreenAccessor;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
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
        var sortRange = getSortRange(container, hoveredSlot);
        if (sortRange == null) return;

        var cursorStack = container.getCarried().copy();
        var itemStacks = container.slots.stream().map(slot -> slot.getItem().copy()).collect(Collectors.toList());

        var mergeQueue = mergeItems(itemStacks, cursorStack, sortRange);
        var sortQueue = quickSort(Collections.unmodifiableList(itemStacks), sortRange);

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
    private static IntIntPair getSortRange(AbstractContainerMenu container, @NotNull Slot hoveredSlot) {
        int mouseIdx = hoveredSlot.index;

        if (mouseIdx == 0 && hoveredSlot.getContainerSlot() != 0) mouseIdx = hoveredSlot.getContainerSlot();

        int left = mouseIdx;
        int right = mouseIdx + 1;

        Class<?> clazz = container.slots.get(mouseIdx).container.getClass();

        for (int i = mouseIdx - 1; i >= 0; i--) {
            if (clazz != container.slots.get(i).container.getClass()) {
                left = i + 1;
                break;
            } else if (i == 0) left = 0;
        }

        var limit = container.slots.size();
        for (int i = right; i < limit; i++) {
            if (clazz != container.slots.get(i).container.getClass()) {
                right = i;
                break;
            } else if (i == limit - 1) {
                right = limit;
            }
        }

        if (hoveredSlot.container instanceof Inventory) {
            if (left == 5 && right == 46) {
                if (mouseIdx >= 9 && mouseIdx < 36) return new IntIntImmutablePair(9, 36);
                else if (mouseIdx >= 36 && mouseIdx < 45) return new IntIntImmutablePair(36, 45);
                return null;
            } else if (right - left == 36) {
                if (mouseIdx >= left && mouseIdx < left + 27) return new IntIntImmutablePair(left, left + 27);
                else return new IntIntImmutablePair(left + 27, right);
            }
        }

        return left + 1 == right ? null : new IntIntImmutablePair(left, right);
    }

    private static @NotNull IntList mergeItems(List<ItemStack> targetStacks, @NotNull ItemStack cursorStack, IntIntPair range) {
        int begin = range.leftInt(), end = range.rightInt();
        var mergeQueue = new IntArrayList();
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

    /**
     * @return 物品合并记录
     */
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

    private static @NotNull IntList quickSort(List<ItemStack> stacks, IntIntPair range) {
        // sort [start, end)
        int start = range.leftInt(), end = range.rightInt(), size = end - start;
        IntList sortQueue = new IntArrayList();
        List<ObjectIntPair<ItemStack>> sorted = new ArrayList<>(size);

        boolean allShulkerBox = true;
        for (int i = start; i < end; i++) {
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
                map.put(stackInfo.rightInt(), i + start);
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
