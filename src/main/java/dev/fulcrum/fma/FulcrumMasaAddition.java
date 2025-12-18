package dev.fulcrum.fma;

import dev.fulcrum.fma.config.Configs;
import dev.fulcrum.fma.config.GuiConfigs;
import dev.fulcrum.fma.config.KeybindProvider;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.fabricmc.api.ClientModInitializer;

import static dev.fulcrum.fma.SharedConstants.MOD_ID;
import static dev.fulcrum.fma.SharedConstants.MOD_NAME;

public class FulcrumMasaAddition implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(() -> {
            ConfigManager.getInstance().registerConfigHandler(MOD_ID, new Configs());
            Registry.CONFIG_SCREEN.registerConfigScreenFactory(new ModInfo(MOD_ID, MOD_NAME, GuiConfigs::new));
            InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());
        });
        Configs.init();
    }

}
