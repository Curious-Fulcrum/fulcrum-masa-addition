package dev.fulcrum.fma;

import fi.dy.masa.malilib.util.StringUtils;
import net.fabricmc.loader.api.FabricLoader;
import top.hendrixshen.magiclib.api.i18n.I18n;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.GlobalConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigHandler;
import top.hendrixshen.magiclib.impl.malilib.config.gui.MagicConfigGui;

public interface SharedConstants {
    String MOD_ID = "fma";
    String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    MagicConfigManager CONFIG_MANAGER = GlobalConfigManager.getConfigManager(MOD_ID);
    MagicConfigHandler CONFIG_HANDLER = new MagicConfigHandler(CONFIG_MANAGER, 1);

    static MagicConfigGui getConfigGui() {
        return new MagicConfigGui(MOD_ID, CONFIG_MANAGER, I18n.tr("fma.gui.title.configs", MOD_VERSION));
    }

    // compat
    boolean HAS_GCA = FabricLoader.getInstance().isModLoaded("gca");
}
