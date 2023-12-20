package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import me.fridtjof.puddingapi.bukkit.items.HopperUtils;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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

    //prevent hoppers from filling non coal items in carts
    @EventHandler
    public void onMoveItemIntoFuelCartEvent(InventoryMoveItemEvent event)
    {
        if(!(event.getDestination().getHolder() instanceof StorageMinecart))
        {
            return;
        }

        if(!(event.getSource().getHolder() instanceof Hopper))
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

            //rearrange to possibly let other itemstacks get through
            if(!HopperUtils.rearrangeHopper(event.getSource(), Material.COAL))
            {
                if(!HopperUtils.rearrangeHopper(event.getSource(), Material.CHARCOAL))
                {
                    //rearranging was not successful
                    return;
                }
            }
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
