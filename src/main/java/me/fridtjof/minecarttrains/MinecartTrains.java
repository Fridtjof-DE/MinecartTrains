package me.fridtjof.minecarttrains;

import me.fridtjof.minecarttrains.events.EventManager;
import me.fridtjof.puddingapi.bukkit.utils.*;
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

    public static final String TENDER_NAME = "Tender";

    @Override
    public void onEnable() {

        new PuddingAPIVersionChecker(this, logger, 2307021844L);

        new RecipeManager();

        configManager = new ConfigManager(this);
        new EventManager(this);
        new ModrinthUpdateChecker(this,"plRff0I9", "spigot");
        new Metrics(this, 18918);

    }

    @Override
    public void onDisable() {

    }
}
