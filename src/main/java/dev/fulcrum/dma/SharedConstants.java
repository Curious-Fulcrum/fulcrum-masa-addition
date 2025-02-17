package dev.fulcrum.dma;

import fi.dy.masa.malilib.util.StringUtils;
import net.fabricmc.loader.api.FabricLoader;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.GlobalConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigHandler;

public interface SharedConstants {
    String MOD_ID = "fma";
    String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    MagicConfigManager CONFIG_MANAGER = GlobalConfigManager.getConfigManager(MOD_ID);
    MagicConfigHandler CONFIG_HANDLER = new MagicConfigHandler(CONFIG_MANAGER,1);

    // compat
    boolean HAS_GCA = FabricLoader.getInstance().isModLoaded("gca");
    boolean HAS_TWEAKEROO = FabricLoader.getInstance().isModLoaded("tweakeroo");
}
