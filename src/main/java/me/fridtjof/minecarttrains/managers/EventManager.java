package me.fridtjof.minecarttrains.managers;

import me.fridtjof.minecarttrains.events.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class EventManager implements Listener
{

    public EventManager(JavaPlugin plugin)
    {
        registerListeners(plugin);
    }

    private void registerListeners(JavaPlugin plugin)
    {
        getServer().getPluginManager().registerEvents((Listener) new OnPlayerInteractEntityEvent(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnVehicleUpdateEvent(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnVehicleUpdateEventLowestPriority(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnVehicleEntityCollisionEvent(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnProjectileHitEvent(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnVehicleCreateEvent(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnInventoryMoveItemEvent(), plugin);
        getServer().getPluginManager().registerEvents((Listener) new OnVehicleMoveEvent(), plugin);
    }

}
