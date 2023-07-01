package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class OnVehicleEntityCollisionEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();

    // killing entities when run over
    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
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

        if(vehicle.getVelocity().length() < 1.8) {
            return;
        }

        Damageable damageable = (Damageable) entity;
        damageable.setHealth(0);
        //System.out.println("" + vehicle.getVelocity().length());
    }
}
