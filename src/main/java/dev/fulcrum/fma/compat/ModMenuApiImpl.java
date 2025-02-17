package dev.fulcrum.fma.compat;

import dev.fulcrum.fma.SharedConstants;
import top.hendrixshen.magiclib.api.compat.modmenu.ModMenuApiCompat;

import static dev.fulcrum.fma.SharedConstants.MOD_ID;

public class ModMenuApiImpl implements ModMenuApiCompat {
    @Override
    public ConfigScreenFactoryCompat<?> getConfigScreenFactoryCompat() {
        return (screen) -> {
            var configGui = SharedConstants.getConfigGui();
            configGui.setParent(screen);
            return configGui;
        };
    }

    @Override
    public String getModIdCompat() {
        return MOD_ID;
    }
}
