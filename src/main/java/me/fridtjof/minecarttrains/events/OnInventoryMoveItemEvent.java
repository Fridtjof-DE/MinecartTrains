package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.block.Hopper;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class OnInventoryMoveItemEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event)
    {
        if(!(event.getSource().getHolder() instanceof StorageMinecart))
        {
            return;
        }

        if(!(event.getDestination().getHolder() instanceof Hopper))
        {
            return;
        }

        StorageMinecart cart = (StorageMinecart) event.getSource().getHolder();

        if(cart.getCustomName() == null)
        {
            return;
        }

        if(cart.getCustomName().equalsIgnoreCase(MinecartTrains.TENDER_NAME))
        {
            event.setCancelled(true);
        }
    }
}
