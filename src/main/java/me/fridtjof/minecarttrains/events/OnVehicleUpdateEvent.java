package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.managers.LinkageManager;
import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class OnVehicleUpdateEvent implements Listener
{

    static MinecartTrains plugin = MinecartTrains.getInstance();
    LinkageManager linkageManager = plugin.linkageManager;

    // config vars
    int fuelPerTick = plugin.configManager.mainConfig.getConfig().getInt("trains.fuel.consumption_per_tick");

    double maxDistance = plugin.configManager.physicsConfig.getConfig().getDouble("link.distance.max");
    double minDistance = plugin.configManager.physicsConfig.getConfig().getDouble("link.distance.min");

    double couplingPullSpeedLowSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.low_speed.speed.pull");
    double couplingPushSpeedLowSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.low_speed.speed.push");
    double aimedDistanceLowSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.low_speed.distance.aimed");
    double aimedDistanceToleranceLowSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.low_speed.distance.aimed_tolerance");

    double couplingPullSpeedHighSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.high_speed.speed.pull");
    double couplingPushSpeedHighSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.high_speed.speed.push");
    double aimedDistanceHighSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.high_speed.distance.aimed");
    double aimedDistanceToleranceHighSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("link.high_speed.distance.aimed_tolerance");

    double highSpeedGate = plugin.configManager.physicsConfig.getConfig().getDouble("link.high_speed.gate_speed");


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

        //DEBUG
        if(!minecart.getPassengers().isEmpty())
        {
            Entity passenger = minecart.getPassengers().get(0);
            if((passenger instanceof Player))
            {
                double shownSpeed = minecart.getVelocity().length() * 72;
                passenger.sendMessage("Speed: " + (int)shownSpeed + " km/h");
            }
        }

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


        //check if parent is in same world
        World childWorld = minecart.getWorld();
        World parentWorld = parent.getWorld();
        if(childWorld != parentWorld)
        {
            linkageManager.removeLink(minecart);
            return;
        }


        // may cause problems
        //check if parent is derailed
        Location minecartLocation = minecart.getLocation();
        Material parentTrack = parentWorld.getBlockAt(parent.getLocation()).getType();
        Material parentTrackOnHill = parentWorld.getBlockAt(parent.getLocation().add(0, -1, 0)).getType();
        if(!isAnyKindOfTrack(parentTrack) && !isAnyKindOfTrack(parentTrackOnHill))
        {
            linkageManager.removeLink(minecart);
            return;
        }


        // defines the distance value as (parent's location)-(child's location)
        double distance = minecart.getLocation().distance(parent.getLocation());

        /* Sets the speed at which carts move towards their dependant. At HSRail speeds, this value must be high
        to avoid breaking trains during acceleration.
        Values in excess of 5 require more precision for the 'else if' statements, or else carts will overshoot
        and collide, disrupting momentum and causing the 'jiggle' bug.*/
        double pullSpeed;
        double pushSpeed;

        double aimedDistance;
        double aimedDistanceTolerance;

        // sets the parts pull vars based on the speed of the parent
        if(parent.getVelocity().length() <= highSpeedGate)
        {
            //low speed
            pullSpeed = couplingPullSpeedLowSpeed;
            aimedDistance = this.aimedDistanceLowSpeed;
            aimedDistanceTolerance = this.aimedDistanceToleranceLowSpeed;
        }
        else
        {
            //high speed
            pullSpeed = couplingPullSpeedHighSpeed;
            aimedDistance = this.aimedDistanceHighSpeed;
            aimedDistanceTolerance = this.aimedDistanceToleranceHighSpeed;
        }


        // sets the carts push speed based on its own speed
        if(minecart.getVelocity().length() <= highSpeedGate)
        {
            //low speed
            pushSpeed = couplingPushSpeedLowSpeed;
        }
        else
        {
            //high speed
            pushSpeed = couplingPushSpeedHighSpeed;
        }


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


    //TODO move to puddingapi
    public boolean isAnyKindOfTrack(Material material)
    {
        if(material == Material.POWERED_RAIL)
        {
            return true;
        }
        if(material == Material.RAIL)
        {
            return true;
        }
        if(material == Material.ACTIVATOR_RAIL)
        {
            return true;
        }
        if(material == Material.DETECTOR_RAIL)
        {
            return true;
        }
        return false;
    }
}
