package me.TreeOfSelf.PandaDeathBan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private static final File CONFIG_FILE = new File("config/PandaDeathBan.json");

    private static Config config;

    public static class Config {
        public long banDurationSeconds = 604800;
        public List<String> banMessage = Arrays.asList(
                "<red>☠ You are Dead ☠</red>",
                "",
                "<white>You can join in: <yellow>%death_time_remaining%</yellow></white>",
                "",
                "<gray>Discord: discord.hardcoreanarchy.gay</gray>"
        );
    }

    public static void loadConfig() {
        try {
            File configDir = new File("config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            if (!CONFIG_FILE.exists()) {
                config = new Config();
                saveConfig();
                PandaDeathBan.LOGGER.info("Created default config file");
                return;
            }

            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, Config.class);

                if (config == null) {
                    config = new Config();
                }
                if (config.banMessage == null) {
                    config.banMessage = new Config().banMessage;
                }

                saveConfig();
                PandaDeathBan.LOGGER.info("Loaded config file");
            }
        } catch (IOException e) {
            PandaDeathBan.LOGGER.error("Failed to load config, using defaults", e);
            config = new Config();
        }
    }

    private static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            PandaDeathBan.LOGGER.error("Failed to save config", e);
        }
    }

    public static Config getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}