package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.managers.LinkageManager;
import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class OnVehicleUpdateEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();
    LinkageManager linkageManager = new LinkageManager();

    // config vars
    int fuelPerTick = plugin.configManager.mainConfig.getConfig().getInt("trains.fuel.consumption_per_tick");
    double couplingPullSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.speed.pull");
    double couplingPushSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.speed.push");
    double maxDistance = plugin.configManager.physicsConfig.getConfig().getDouble("link.distance.max");
    double minDistance = plugin.configManager.physicsConfig.getConfig().getDouble("link.distance.min");
    double aimedDistance = plugin.configManager.physicsConfig.getConfig().getDouble("link.distance.aimed");
    double aimedDistanceTolerance = plugin.configManager.physicsConfig.getConfig().getDouble("link.distance.aimed_tolerance");

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event)
    {
        Vehicle vehicle = event.getVehicle();
        Minecart minecart;
        String uniqueId;
        Entity parent;

        if(!(vehicle instanceof Minecart))
        {
            return;
        }
        minecart = (Minecart) vehicle;


        // ensures the below function only works on trains, not on single minecarts
        if(!linkageManager.hasLink((minecart)))
        {

            // causes a full stop for Furnace Minecart on un-powered rail, and pauses Fuel Consumption.
            if(!(minecart instanceof PoweredMinecart))
            {
                return;
            }

            PoweredMinecart poweredCart = (PoweredMinecart) minecart;

            int fuelAfterConsumption = poweredCart.getFuel() - fuelPerTick + 1;

            if(fuelAfterConsumption < 0)
            {
                fuelAfterConsumption = 0;
            }
            poweredCart.setFuel(fuelAfterConsumption);

            Block block = poweredCart.getLocation().getBlock();
            if(block.getType() != Material.POWERED_RAIL) {
                return;
            }
            RedstoneRail redstoneRail = (RedstoneRail) block.getBlockData();
            if(redstoneRail.isPowered()) {
                return;
            }
            poweredCart.setVelocity(new Vector(0 ,0 ,0));

            //to stop fuel consumption just re-add the consumed
            poweredCart.setFuel(((PoweredMinecart) poweredCart).getFuel() + fuelPerTick);

            return;
        }

        parent = Bukkit.getEntity(linkageManager.getLinkUniqueId(minecart));

        // breaks link if parent cart is broken
        if(parent == null)
        {
            linkageManager.removeLink(minecart);
            return;
        }

        // breaks link if the parent and child are the same cart
        if(parent == minecart)
        {
            plugin.getLogger().warning(plugin.configManager.mainConfig.getConfig().getString("trains.coupling_already_coupled_with_itself"));
            linkageManager.removeLink(minecart);
            return;
        }

        // defines the distance value as (parent's location)-(child's location)
        double distance = minecart.getLocation().distance(parent.getLocation());

        /* Sets the speed at which carts move towards their dependant. At HSRail speeds, this value must be high
        to avoid breaking trains during acceleration.
        Values in excess of 5 require more precision for the 'else if' statements, or else carts will overshoot
        and collide, disrupting momentum and causing the 'jiggle' bug.*/
        double pullSpeed = couplingPullSpeed;
        double pushSpeed = couplingPushSpeed;

        /* Sets max/min distance for links. Shorter distances are more reliable along corners, however, until the
        linkageManager can be reworked, longer maximums are needed to handle acceleration.
        If distance is out of bonds the links get removed and the cart stopped to prevent runaway trains. */
        if((distance > maxDistance) || distance < minDistance)
        {
            linkageManager.removeLink(minecart);
            minecart.setVelocity(new Vector(0, 0, 0));
            parent.setVelocity(new Vector(0, 0, 0));
        }

        /* Handles gap between parent and dependant. Values set too low result in collisions between carts
        while train is sitting still, especially on declines, causing 'jiggling' that kills momentum
        More precise parameters helps to eliminate the 'jiggling' behavior that interferes with momentum
        Slower 'away' speeds compared to 'toward' speeds also help to eliminate 'jiggling' */
        // acceleration if distance to small
        else if(distance < (aimedDistance - aimedDistanceTolerance))
        {
            moveToward(minecart, parent, pushSpeed, (distance - aimedDistance));
        }
        // acceleration if distance to large
        else if(distance > (aimedDistance + aimedDistanceTolerance))
        {
            moveToward(minecart, parent, pullSpeed, (distance - aimedDistance));
        }
    }

    // defines moveToward, which handles the elastic movement of dependant carts. Uses the speed and direction set earlier
    public void moveToward(Entity child, Entity parent, double speed, double distance)
    {
        Location childLoc = child.getLocation();
        Location parentLoc = parent.getLocation();

        double x = childLoc.getX() - parentLoc.getX();
        double y = childLoc.getY() - parentLoc.getY();
        double z = childLoc.getZ() - parentLoc.getZ();
        Vector velocity = new Vector(x, y, z).normalize().multiply(-speed);
        child.setVelocity(velocity.multiply((distance * distance * distance)));
    }
}
