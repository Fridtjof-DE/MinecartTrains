package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class OnVehicleCreateEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    private final String FUEL_CART_NAME = plugin.configManager.mainConfig.getConfig().getString("trains.fuel.cart_name");

    //create fuel cart
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

        if(!cart.getCustomName().equalsIgnoreCase(FUEL_CART_NAME))
        {
            return;
        }

        cart.setDisplayBlockData(Material.COAL_BLOCK.createBlockData());
    }
}
