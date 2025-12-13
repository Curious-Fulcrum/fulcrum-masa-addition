package dev.fulcrum.fma;

import dev.fulcrum.fma.features.sortInventory.SortInventoryHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.malilib.annotation.Config;
import top.hendrixshen.magiclib.api.malilib.annotation.Statistic;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigFactory;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigBoolean;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigHotkey;

import static dev.fulcrum.fma.SharedConstants.getConfigGui;

public class Configs {
    private static final MagicConfigManager cm = SharedConstants.CONFIG_MANAGER;
    private static final MagicConfigFactory cf = cm.getConfigFactory();


    @Statistic(hotkey = false)
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey openConfigGui = Configs.cf.newConfigHotkey("openConfigGui", "F,C");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey sortInventory = cf.newConfigHotkey("sortInventory", "R");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean sortInventoryShulkerBoxLast = cf.newConfigBoolean("sortInventoryShulkerBoxLast", true);

    @Dependencies(require = {@Dependency("tweakeroo"), @Dependency("fca")})
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean sortInventorySupportEmptyShulkerBoxStack = cf.newConfigBoolean("sortInventorySupportEmptyShulkerBoxStack", false);

    @Dependencies(require = @Dependency("tweakeroo"))
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBoolean betterSneaking = cf.newConfigBoolean("betterFakeSneaking", true);

    public static void init() {
        cm.parseConfigClass(Configs.class);
        SharedConstants.CONFIG_HANDLER.setPostDeserializeCallback(_ -> sortInventory.getKeybind().setSettings(KeybindSettings.GUI));

        MagicConfigManager.setHotkeyCallback(Configs.openConfigGui, () -> GuiBase.openGui(getConfigGui()), true);
        MagicConfigManager.setHotkeyCallback(Configs.sortInventory, SortInventoryHelper::sort, false);
    }

    private interface ConfigCategory {
        String GENERIC = "generic";
        String FEATURE = "feature";
    }
}
