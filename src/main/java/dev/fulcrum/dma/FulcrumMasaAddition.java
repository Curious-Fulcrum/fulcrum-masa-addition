package dev.fulcrum.dma;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;

import static dev.fulcrum.dma.SharedConstants.CONFIG_HANDLER;
import static dev.fulcrum.dma.SharedConstants.MOD_ID;

public class FulcrumMasaAddition implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(() ->
                ConfigManager.getInstance().registerConfigHandler(MOD_ID, CONFIG_HANDLER));
        Configs.init();
    }
}
