package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class OnVehicleEntityCollisionEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();

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

        if(vehicle instanceof RideableMinecart) {
            if(vehicle.getPassenger() == null) {
                return;
            }
        }

        if(vehicle.getVelocity().length() < 1.8) {
            return;
        }

        //TODO kill entities
        entity.remove();
        //System.out.println("" + vehicle.getVelocity().length());
    }
}
