package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class OnVehicleEntityCollisionEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    // get config vars
    boolean runOver = plugin.configManager.mainConfig.getConfig().getBoolean("trains.run_over.entities");
    double damage = plugin.configManager.mainConfig.getConfig().getDouble("trains.run_over.damage");
    double minVelocity = plugin.configManager.mainConfig.getConfig().getDouble("trains.run_over.min_velocity");

    // killing entities when run over
    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event)
    {

        if(!runOver)
        {
            return;
        }

        Vehicle vehicle = event.getVehicle();
        Entity  entity = event.getEntity();

        if(!(vehicle instanceof Minecart))
        {
            return;
        }
        if(entity instanceof Minecart)
        {
            return;
        }
        if(!(entity instanceof Damageable))
        {
            return;
        }

        if(vehicle instanceof RideableMinecart)
        {
            if(vehicle.getPassenger() == null)
            {
                return;
            }
        }

        //fixing rare situations where a player riding the cart was killed
        if(entity.isInsideVehicle())
        {
            return;
        }

        if(vehicle.getVelocity().length() < minVelocity)
        {
            return;
        }

        Damageable damageable = (Damageable) entity;
        //damageable.setHealth(0); this also killed player in gamemode 1
        damageable.damage(damage, vehicle);
    }
}
