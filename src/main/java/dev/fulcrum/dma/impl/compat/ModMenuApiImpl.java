package dev.fulcrum.dma.impl.compat;

import top.hendrixshen.magiclib.api.compat.modmenu.ModMenuApiCompat;
import top.hendrixshen.magiclib.impl.malilib.config.gui.MagicConfigGui;

import static dev.fulcrum.dma.SharedConstants.CONFIG_MANAGER;
import static dev.fulcrum.dma.SharedConstants.MOD_ID;

public class ModMenuApiImpl implements ModMenuApiCompat {
    @Override
    public ConfigScreenFactoryCompat<?> getConfigScreenFactoryCompat() {
        return (screen) -> {
            var configGui = new MagicConfigGui(MOD_ID, CONFIG_MANAGER, "test");
            configGui.setParent(screen);
            return configGui;
        };
    }

    @Override
    public String getModIdCompat() {
        return MOD_ID;
    }
}
