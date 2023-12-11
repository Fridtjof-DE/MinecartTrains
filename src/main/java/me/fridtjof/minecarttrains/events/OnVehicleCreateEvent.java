package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class OnVehicleCreateEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    @EventHandler
    public void onVehicleCreateEvent(VehicleCreateEvent event)
    {
        if(!(event.getVehicle() instanceof StorageMinecart))
        {
            return;
        }

        StorageMinecart cart = (StorageMinecart) event.getVehicle();

        if(cart.getCustomName() == null)
        {
            return;
        }

        if(!cart.getCustomName().equalsIgnoreCase("Tender"))
        {
            return;
        }

        cart.setDisplayBlockData(Material.COAL_BLOCK.createBlockData());
    }
}
