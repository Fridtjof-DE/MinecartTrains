package me.fridtjof.minecarttrains;

import me.fridtjof.minecarttrains.events.EventManager;
import me.fridtjof.puddingapi.bukkit.utils.DependencyChecker;
import me.fridtjof.puddingapi.bukkit.utils.Logger;
import me.fridtjof.puddingapi.bukkit.utils.Metrics;
import me.fridtjof.puddingapi.bukkit.utils.PuddingAPIVersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecartTrains extends JavaPlugin {

    private static MinecartTrains instance;

    public MinecartTrains() {
        instance = this;
    }

    public static MinecartTrains getInstance() {
        return instance;
    }

    Logger logger = new Logger(this);

    public ConfigManager configManager;

    @Override
    public void onEnable() {

        new PuddingAPIVersionChecker(this, logger, 2301202350L);

        configManager = new ConfigManager(this);
        new EventManager(this);
        //TODO Modrinth version checker
        new Metrics(this, 18918);

    }

    @Override
    public void onDisable() {

    }
}
