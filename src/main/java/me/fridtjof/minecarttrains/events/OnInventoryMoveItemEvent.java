package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class OnInventoryMoveItemEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    private final String FUEL_CART_NAME = plugin.configManager.mainConfig.getConfig().getString("trains.fuel_cart_name");

    //stop hoppers from unloading fuel carts
    @EventHandler
    public void onMoveItemOutOffFuelCartEvent(InventoryMoveItemEvent event)
    {
        if(!(event.getSource().getHolder() instanceof StorageMinecart))
        {
            return;
        }

        if(!(event.getDestination().getHolder() instanceof Hopper))
        {
            return;
        }

        if(isFuelCart((StorageMinecart) event.getSource().getHolder()))
        {
            event.setCancelled(true);
        }
    }

    //allow hoppers to fill just coal in carts
    //TODO if a slot is occupied by a non coal item, the slots after that wont be unloaded
    @EventHandler
    public void onMoveItemIntoFuelCartEvent(InventoryMoveItemEvent event)
    {
        if(!(event.getDestination().getHolder() instanceof StorageMinecart))
        {
            return;
        }

        if((event.getItem().getType() == Material.COAL) || (event.getItem().getType() == Material.CHARCOAL))
        {
            return;
        }

        if(isFuelCart((StorageMinecart) event.getDestination().getHolder()))
        {
            event.setCancelled(true);
        }
    }

    public boolean isFuelCart(StorageMinecart cart)
    {
        if(cart.getCustomName() == null)
        {
            return false;
        }

        if(cart.getCustomName().equalsIgnoreCase(FUEL_CART_NAME))
        {
            return true;
        }

        return false;
    }
}
