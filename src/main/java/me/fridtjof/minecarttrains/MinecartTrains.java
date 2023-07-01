package me.fridtjof.minecarttrains;

import me.fridtjof.minecarttrains.events.EventManager;
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        new EventManager(this);
        
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
