package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class OnVehicleEntityCollisionEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();

    // killing entities when run over
    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {

        if(!plugin.configManager.mainConfig.getConfig().getBoolean("trains.run_over_entities")) {
            return;
        }

        Vehicle vehicle = event.getVehicle();
        Entity  entity = event.getEntity();

        if(!(vehicle instanceof Minecart)) {
            return;
        }
        if(entity instanceof Minecart) {
            return;
        }
        if(!(entity instanceof Damageable)) {
            return;
        }

        if(vehicle instanceof RideableMinecart) {
            if(vehicle.getPassenger() == null) {
                return;
            }
        }

        if(vehicle.getVelocity().length() < plugin.configManager.mainConfig.getConfig().getDouble("trains.run_over_entities_min_velocity")) {
            return;
        }

        Damageable damageable = (Damageable) entity;
        damageable.damage(10000, vehicle);
    }
}
