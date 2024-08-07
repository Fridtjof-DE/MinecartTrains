package me.fridtjof.minecarttrains.managers;

import me.fridtjof.puddingapi.bukkit.utils.Logger;
import me.fridtjof.puddingapi.bukkit.utils.YamlConfig;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class ConfigManager
{

    JavaPlugin plugin;

    private Logger logger;
    public YamlConfig mainConfig, physicsConfig, messagesFile;

    public ConfigManager(JavaPlugin plugin)
    {
        this.plugin = plugin;
        logger = new Logger(plugin);
        reloadConfigs();
    }

    public void reloadConfigs()
    {
        loadMainConfig();
        loadPhysicsConfig();
        loadMessagesFile();
    }

    public void loadMainConfig()
    {
        mainConfig = new YamlConfig(plugin.getDataFolder(), "config");

        mainConfig.getConfig().options().setHeader(Collections.singletonList("This is the main configuration file"));

        //TODO improve naming - maybe in 2.0.0?

        mainConfig.getConfig().addDefault("config_version", 1);

        mainConfig.getConfig().addDefault("trains.run_over_entities", true);
        mainConfig.getConfig().addDefault("trains.run_over_entities_min_velocity", 1.8D);

        mainConfig.getConfig().addDefault("trains.coupling_tool", Material.CHAIN.toString());

        mainConfig.getConfig().addDefault("trains.can_get_hit_by_arrows", false);

        mainConfig.getConfig().addDefault("trains.fuel_consumption_per_tick", 1);
        mainConfig.getConfig().addDefault("trains.fuel_refill_only_from_fuel_cart", true);
        mainConfig.getConfig().addDefault("trains.fuel_cart_name", "Coal Cart");
        mainConfig.getConfig().addDefault("trains.fuel_hopper_logic", true);

        mainConfig.getConfig().options().copyDefaults(true);
        mainConfig.save();
        logger.info("Successfully (re)loaded config.yml");
    }

    public void loadPhysicsConfig()
    {
        physicsConfig = new YamlConfig(plugin.getDataFolder(), "physics");

        physicsConfig.getConfig().options().setHeader(Collections.singletonList("This is the physics configuration file - Handle with care! - Default values by @Wallaceman105"));

        physicsConfig.getConfig().addDefault("config_version", 1);

        physicsConfig.getConfig().addDefault("coupling.max_distance", 3.5);
        physicsConfig.getConfig().addDefault("link.speed.pull", 4.25);
        physicsConfig.getConfig().addDefault("link.speed.push", 8.5);
        physicsConfig.getConfig().addDefault("link.distance.max", 9.5);
        physicsConfig.getConfig().addDefault("link.distance.min", 0.7);
        physicsConfig.getConfig().addDefault("link.distance.pull-push_aimed", 1.6);
        physicsConfig.getConfig().addDefault("link.distance.push_apart_min", 1.599);
        physicsConfig.getConfig().addDefault("link.distance.pull_together_min", 1.601);

        physicsConfig.getConfig().options().copyDefaults(true);
        physicsConfig.save();
        logger.info("Successfully (re)loaded physics.yml");
    }

    public void loadMessagesFile()
    {
        messagesFile = new YamlConfig(plugin.getDataFolder(), "messages");
        messagesFile.getConfig().options().header("This is the localization file.");

        messagesFile.getConfig().addDefault("trains.coupling_successful", "Coupling successful!");
        messagesFile.getConfig().addDefault("trains.coupling_failed_distance", "Coupling failed! - The minecarts are too far apart!");
        messagesFile.getConfig().addDefault("trains.coupling_failed_self", "Coupling failed! - Can't be coupled with itself!");
        messagesFile.getConfig().addDefault("trains.coupling_already_coupled_with_itself", "Minecart is coupled with itself! - this shouldn't be possible!");

        messagesFile.getConfig().addDefault("trains.coupling_1-2", "Coupling (1/2) activated for ");
        messagesFile.getConfig().addDefault("trains.coupling_2-2", "Coupling (2/2) activated for ");

        messagesFile.getConfig().options().copyDefaults(true);
        messagesFile.save();
        logger.info("Successfully (re)loaded messages.yml");
    }
}
