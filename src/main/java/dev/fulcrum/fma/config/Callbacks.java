package dev.fulcrum.fma.config;

import dev.fulcrum.fma.features.sortInventory.SortInventoryUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;

public class Callbacks implements IHotkeyCallback {
    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        if (key == Configs.openConfigGui.getKeybind()) {
            GuiBase.openGui(new GuiConfigs());
            return true;
        } else if (key == Configs.sortInventory.getKeybind()) {
            SortInventoryUtils.sort();
            return true;
        }

        return false;
    }
}
