package dev.fulcrum.fma.config;

import fi.dy.masa.malilib.gui.GuiConfigsBase;

import java.util.List;

import static dev.fulcrum.fma.SharedConstants.MOD_ID;
import static dev.fulcrum.fma.SharedConstants.MOD_VERSION;

public class GuiConfigs extends GuiConfigsBase {
    public GuiConfigs() {
        super(10, 25, MOD_ID, null, "fma.gui.title.configs", MOD_VERSION);
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(Configs.ALL);
    }
}
