package dev.fulcrum.fma;

import fi.dy.masa.malilib.util.StringUtils;
import net.fabricmc.loader.api.FabricLoader;

public interface SharedConstants {
    String MOD_ID = "fma";
    String MOD_NAME = "Fulcrum's Masa Addition";
    String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);

    // compat
    boolean HAS_GCA = FabricLoader.getInstance().isModLoaded("gca");
}
