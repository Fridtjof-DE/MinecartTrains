package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

public class NewOnVehicleUpdateEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();
    LinkageHandler linkageManager = new LinkageHandler();

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        Vehicle vehicle = event.getVehicle();

        if (!(vehicle instanceof Minecart)) {
            return;
        }

        linkageManager.handleLinking((Minecart) vehicle);
    }
}
