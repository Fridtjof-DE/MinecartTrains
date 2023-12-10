package me.fridtjof.minecarttrains;

import me.fridtjof.puddingapi.bukkit.utils.Logger;
import me.fridtjof.puddingapi.bukkit.utils.YamlConfig;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    JavaPlugin plugin;

    private Logger logger;
    public YamlConfig mainConfig, lobbyConfig, dataFile, messagesFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        logger = new Logger(plugin);
        reloadConfigs();
    }

    public void reloadConfigs() {
        loadMainConfig();
        loadMessagesFile();
    }


    public void loadMainConfig() {
        mainConfig = new YamlConfig(plugin.getDataFolder(), "config");

        //TODO change some _ to . in v2.0.0

        mainConfig.getConfig().options().header("This is the main configuration file");

        mainConfig.getConfig().addDefault("config_version", 1);

        mainConfig.getConfig().addDefault("trains.run_over_entities", true);
        mainConfig.getConfig().addDefault("trains.run_over_entities_min_velocity", 1.8D);

        mainConfig.getConfig().addDefault("trains.coupling_tool", Material.CHAIN.toString());

        mainConfig.getConfig().addDefault("trains.can_get_hit_by_arrows", false);

        mainConfig.getConfig().addDefault("trains.fuel_consumption_per_tick", 1);
        mainConfig.getConfig().addDefault("trains.fuel_refill_only_from_carts_named_tender", true);

        mainConfig.getConfig().options().copyDefaults(true);
        mainConfig.save();
        logger.info("Successfully (re)loaded config.yml");
    }

    public void loadMessagesFile() {
        messagesFile = new YamlConfig(plugin.getDataFolder(), "messages");
        messagesFile.getConfig().options().header("This is the localization file.");

        messagesFile.getConfig().addDefault("trains.coupling_successful", "Coupling successful!");
        messagesFile.getConfig().addDefault("trains.coupling_failed_distance", "Coupling failed! - The minecarts are too far apart!");
        messagesFile.getConfig().addDefault("trains.coupling_failed_self", "Coupling failed! - Can't be coupled with itself!");

        messagesFile.getConfig().addDefault("trains.coupling_1-2", "Coupling (1/2) activated for ");
        messagesFile.getConfig().addDefault("trains.coupling_2-2", "Coupling (2/2) activated for ");

        messagesFile.getConfig().options().copyDefaults(true);
        messagesFile.save();
        logger.info("Successfully (re)loaded messages.yml");
    }
}
