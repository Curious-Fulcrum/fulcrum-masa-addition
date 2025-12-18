package dev.fulcrum.fma.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.fulcrum.fma.config.GuiConfigs;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            var gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        };
    }
}
