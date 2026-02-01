package me.fridtjof.minecarttrains;

import me.fridtjof.minecarttrains.managers.ConfigManager;
import me.fridtjof.minecarttrains.managers.EventManager;
import me.fridtjof.minecarttrains.managers.LinkageManager;
import me.fridtjof.minecarttrains.managers.RecipeManager;
import me.fridtjof.puddingapi.bukkit.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecartTrains extends JavaPlugin
{

    private static MinecartTrains instance;

    public MinecartTrains()
    {
        instance = this;
    }

    public static MinecartTrains getInstance()
    {
        return instance;
    }

    Logger logger = new Logger(this);

    public ConfigManager configManager;
    public LinkageManager linkageManager;

    @Override
    public void onEnable()
    {
        new PuddingAPIVersionChecker(this, 2602011954L);

        configManager = new ConfigManager(this);
        linkageManager = new LinkageManager();

        new RecipeManager();
        new EventManager(this);
        new ModrinthUpdateChecker(this,"plRff0I9", "spigot");
        new Metrics(this, 18918);

    }

    @Override
    public void onDisable()
    {

    }
}
