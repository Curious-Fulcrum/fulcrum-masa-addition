package dev.fulcrum.fma.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Configs implements IConfigHandler {
    private static final String PREFIX = "fma.config";
    private final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("fma.json");

    public static final ConfigHotkey openConfigGui = new ConfigHotkey("openConfigGui", "F,C").apply(PREFIX);

    public static final ConfigHotkey sortInventory = new ConfigHotkey("sortInventory", "R", KeybindSettings.GUI).apply(PREFIX);

    public static final ConfigBoolean sortInventoryShulkerBoxLast = new ConfigBoolean("sortInventoryShulkerBoxLast", true).apply(PREFIX);

    public static final ConfigBoolean betterSneaking = new ConfigBoolean("betterSneaking", true).apply(PREFIX);

    public static final ConfigBoolean villagerRestockTime = new ConfigBoolean("villagerRestockTime", true).apply(PREFIX);

    static final List<ConfigHotkey> HOTKEYS = List.of(openConfigGui, sortInventory);
    static final List<IConfigBase> GENERIC = List.of(sortInventoryShulkerBoxLast, betterSneaking, villagerRestockTime);
    static final List<IConfigBase> ALL = List.of(openConfigGui, sortInventory, sortInventoryShulkerBoxLast, betterSneaking);

    public static void init() {
        var callbacks = new Callbacks();
        openConfigGui.getKeybind().setCallback(callbacks);
        sortInventory.getKeybind().setCallback(callbacks);
    }

    @Override
    public void load() {
        var configFile = configPath;
        if (Files.exists(configFile) && Files.isReadable(configFile)) {
            JsonElement element = JsonUtils.parseJsonFileAsPath(configFile);
            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Generic", GENERIC);
                ConfigUtils.readConfigBase(root, "Hotkeys", HOTKEYS);
            }
        }
    }

    @Override
    public void save() {
        JsonObject root = new JsonObject();
        ConfigUtils.writeConfigBase(root, "Generic", GENERIC);
        ConfigUtils.writeConfigBase(root, "Hotkeys", HOTKEYS);
        JsonUtils.writeJsonToFileAsPath(root, configPath);
    }
}

