package me.fridtjof.minecarttrains;

import me.fridtjof.minecarttrains.events.EventManager;
import me.fridtjof.minecarttrains.events.ServerTickEvent;
import me.fridtjof.puddingapi.bukkit.utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.invoke.SerializedLambda;

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

        new PuddingAPIVersionChecker(this, logger, 2307021844L);

        configManager = new ConfigManager(this);
        new EventManager(this);
        new ModrinthUpdateChecker(this, "plRff0I9", "spigot");
        new Metrics(this, 18918);


        //minecart coupling stuff
        ServerTickEvent serverTickEvent = new ServerTickEvent();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                serverTickEvent.tick();
            }
        }, 0L, 1L);
    }

    @Override
    public void onDisable() {

    }
}
