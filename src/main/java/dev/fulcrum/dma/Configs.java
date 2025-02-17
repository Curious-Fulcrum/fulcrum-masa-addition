package dev.fulcrum.dma;

import dev.fulcrum.dma.impl.feature.sortInventory.SortInventoryHelper;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import top.hendrixshen.magiclib.api.malilib.annotation.Config;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigFactory;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigBoolean;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigBooleanHotkeyed;
import top.hendrixshen.magiclib.impl.malilib.config.option.MagicConfigHotkey;

public class Configs {
    private static final MagicConfigManager cm = SharedConstants.CONFIG_MANAGER;
    private static final MagicConfigFactory cf = cm.getConfigFactory();

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigHotkey sortInventory = cf.newConfigHotkey("sortInventory", "R");
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBoolean sortInventoryShulkerBoxLast = cf.newConfigBoolean("sortInventoryShulkerBoxLast", true);
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed sortInventorySupportEmptyShulkerBoxStack = Configs.cf.newConfigBooleanHotkeyed("sortInventorySupportEmptyShulkerBoxStack", true);

    public static void init() {
        cm.parseConfigClass(Configs.class);
        SharedConstants.CONFIG_HANDLER.setPostDeserializeCallback(_ -> sortInventory.getKeybind().setSettings(KeybindSettings.GUI));
        MagicConfigManager.setHotkeyCallback(Configs.sortInventory, SortInventoryHelper::sort, false);
    }

    private interface ConfigCategory {
        String GENERIC = "generic";
        String FEATURE = "feature";
        String FIXES = "fixes";
    }
}
