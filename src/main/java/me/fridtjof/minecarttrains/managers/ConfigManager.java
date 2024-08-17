package me.fridtjof.minecarttrains.managers;

import me.fridtjof.puddingapi.bukkit.utils.Logger;
import me.fridtjof.puddingapi.bukkit.utils.YamlConfig;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;

public class ConfigManager
{

    JavaPlugin plugin;

    private Logger logger;
    public YamlConfig mainConfig, physicsConfig, messagesFile;

    public static int mainConfigVersion = 2;
    public static int physicsConfigVersion = 1;
    public static String versionStringName = "config_version";

    public ConfigManager(JavaPlugin plugin)
    {
        this.plugin = plugin;
        logger = new Logger(plugin);
        setupConfigs();
        checkConfigVersions();
        //2nd time to overwrite renamed ones
        setupConfigs();
        reloadConfigs();
    }

    private void setupConfigs()
    {
        mainConfig = new YamlConfig(plugin.getDataFolder(), "config");
        physicsConfig = new YamlConfig(plugin.getDataFolder(), "physics");
        messagesFile = new YamlConfig(plugin.getDataFolder(), "messages");
    }

    private void checkConfigVersions()
    {
        checkConfigVersion(mainConfig, versionStringName, mainConfigVersion);
        checkConfigVersion(physicsConfig, versionStringName, physicsConfigVersion);
    }

    //TODO move to PuddingAPI
    private void checkConfigVersion(YamlConfig config, String versionStringName, int wantedVersion)
    {
        if(!config.getConfig().contains(versionStringName))
        {
            return;
        }
        if(config.getConfig().getInt(versionStringName) == wantedVersion)
        {
            return;
        }


        String filePath = config.getFile().getAbsolutePath();

        File renamedFile = new File(filePath + "_backup");

        int backupCount = 0;

        while(renamedFile.exists())
        {
            backupCount++;
            renamedFile = new File(filePath + "_backup_" + backupCount);
        }

        config.getFile().renameTo(renamedFile);

        logger.warn("The old " + config.getName() + " was replaced by a new version of the default file due to an update");
    }

    private void reloadConfigs()
    {
        loadMainConfig();
        loadPhysicsConfig();
        loadMessagesFile();
    }

    private void loadMainConfig()
    {
        mainConfig.getConfig().options().setHeader(Collections.singletonList("This is the main configuration file"));

        mainConfig.getConfig().addDefault(versionStringName, mainConfigVersion);

        mainConfig.getConfig().addDefault("trains.run_over.entities", true);
        mainConfig.getConfig().addDefault("trains.run_over.min_velocity", 1.8D);
        mainConfig.getConfig().addDefault("trains.run_over.damage", 10000);

        mainConfig.getConfig().addDefault("trains.coupling.tool", Material.CHAIN.toString());

        mainConfig.getConfig().addDefault("trains.can_get_hit_by_arrows", false);

        mainConfig.getConfig().addDefault("trains.fuel.consumption_per_tick", 1);
        mainConfig.getConfig().addDefault("trains.fuel.refill_only_from_fuel_cart", true);
        mainConfig.getConfig().addDefault("trains.fuel.cart_name", "Coal Cart");
        mainConfig.getConfig().addDefault("trains.fuel.do_hopper_logic", true);

        mainConfig.getConfig().options().copyDefaults(true);
        mainConfig.save();
        logger.info("Successfully (re)loaded config.yml");
    }

    private void loadPhysicsConfig()
    {
        physicsConfig.getConfig().options().setHeader(Collections.singletonList("This is the physics configuration file - Handle with care! - Default values by @Wallaceman105"));

        physicsConfig.getConfig().addDefault(versionStringName, physicsConfigVersion);

        physicsConfig.getConfig().addDefault("coupling.max_distance", 3.5);
        physicsConfig.getConfig().addDefault("link.speed.pull", 1);
        physicsConfig.getConfig().addDefault("link.speed.push", 32);
        physicsConfig.getConfig().addDefault("link.distance.max", 9.5);
        physicsConfig.getConfig().addDefault("link.distance.min", 0.7);
        physicsConfig.getConfig().addDefault("link.distance.pull-push_aimed", 1.6);
        physicsConfig.getConfig().addDefault("link.distance.push_apart_min", 1.599);
        physicsConfig.getConfig().addDefault("link.distance.pull_together_min", 1.601);

        physicsConfig.getConfig().options().copyDefaults(true);
        physicsConfig.save();
        logger.info("Successfully (re)loaded physics.yml");
    }

    private void loadMessagesFile()
    {
        messagesFile.getConfig().options().header("This is the localization file.");;

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
