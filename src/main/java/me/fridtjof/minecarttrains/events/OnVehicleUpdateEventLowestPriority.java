package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.managers.LinkageManager;
import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class OnVehicleUpdateEventLowestPriority implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();
    LinkageManager linkageManager = new LinkageManager();

    private final int FUEL_PER_TICK = plugin.configManager.mainConfig.getConfig().getInt("trains.fuel.consumption_per_tick");
    private final boolean REFILL_ONLY_FROM_TENDER = plugin.configManager.mainConfig.getConfig().getBoolean("trains.fuel.refill_only_from_fuel_cart");
    private final String FUEL_CART_NAME = plugin.configManager.mainConfig.getConfig().getString("trains.fuel.cart_name");

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnFuelEmptyEvent(VehicleUpdateEvent event) {
        Vehicle vehicle = event.getVehicle();
        String uniqueId;
        Entity parent;

        if (!(vehicle instanceof PoweredMinecart)) {
            return;
        }

        PoweredMinecart cart = (PoweredMinecart) vehicle;

        //TODO
        //maybe adding for particles
        /*if(cart.getFuel() != 0)
        {
            cart.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, cart.getLocation().add(new Vector(0, 2, 0)), 1);
        }*/

        if((cart.getFuel() < 2) || (cart.getFuel() > (FUEL_PER_TICK + 1)))
        {
            return;
        }

        List<Entity> cartsInRange = vehicle.getNearbyEntities(3D, 3D, 3D);

        for(Entity entity : cartsInRange)
        {
            if(entity instanceof StorageMinecart)
            {
                if(REFILL_ONLY_FROM_TENDER)
                {
                    if(!isCartTender((StorageMinecart) entity))
                    {
                        continue;
                    }
                }

                Inventory inventory = ((StorageMinecart) entity).getInventory();

                if(inventory.contains(Material.COAL))
                {
                    inventory.removeItem(new ItemStack(Material.COAL, 1));
                    cart.setFuel(cart.getFuel() + 3600);
                    return;
                }

                if(inventory.contains(Material.CHARCOAL))
                {
                    inventory.removeItem(new ItemStack(Material.CHARCOAL, 1));
                    cart.setFuel(cart.getFuel() + 3600);
                    return;
                }
            }
        }
    }

    private boolean isCartTender(StorageMinecart cart) {
        if (cart.getCustomName() == null) {
            return false;
        }

        if (cart.getCustomName().equalsIgnoreCase(FUEL_CART_NAME)) {
            return true;
        }
        return false;
    }
}
