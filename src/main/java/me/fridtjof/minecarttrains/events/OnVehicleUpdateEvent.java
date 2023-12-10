package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.logging.Level;

public class OnVehicleUpdateEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();
    LinkageManager linkageManager = new LinkageManager();

    int fuelPerTick = plugin.configManager.mainConfig.getConfig().getInt("trains.fuel_consumption_per_tick");

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        Vehicle vehicle = event.getVehicle();
        Minecart minecart;
        String uniqueId;
        Entity parent;

        if(!(vehicle instanceof Minecart)) {
            return;
        }
        minecart = (Minecart) vehicle;


        //this if only in the current one way linking system
        if(!linkageManager.hasLink((minecart))) {

            //stopping furnace minecart on not powered power rail
            if(!(minecart instanceof PoweredMinecart)) {
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
            int fuelToReAdd = plugin.configManager.mainConfig.getConfig().getInt("trains.fuel_consumption_per_tick");
            ((PoweredMinecart) poweredCart).setFuel(((PoweredMinecart) poweredCart).getFuel() + fuelPerTick);


            return;
        }

        parent = Bukkit.getEntity(linkageManager.getLinkUniqueId(minecart));

        if(parent == null) {
            linkageManager.removeLink(minecart);
            return;
        }

        if(parent == minecart) {
            plugin.getLogger().warning("Minecart is coupled with itself! - this shouldn't be possible!");
            linkageManager.removeLink(minecart);
            return;
        }

        double distance = minecart.getLocation().distance(parent.getLocation());

        double speed = 1.0;

        if((distance > 3) || distance < 0.7) {
            linkageManager.removeLink(minecart);
            minecart.setVelocity(new Vector(0, 0, 0));
            parent.setVelocity(new Vector(0, 0, 0));
        } else if(distance < 1.1) {
            moveToward(minecart, parent, speed, (distance - 1.2));
        } else if(distance > 1.3) {
            moveToward(minecart, parent, speed, (distance - 1.2));
        }
    }

    public void moveToward(Entity child, Entity parent, double speed, double distance){
        Location childLoc = child.getLocation();
        Location parentLoc = parent.getLocation();

        double x = childLoc.getX() - parentLoc.getX();
        double y = childLoc.getY() - parentLoc.getY();
        double z = childLoc.getZ() - parentLoc.getZ();
        Vector velocity = new Vector(x, y, z).normalize().multiply(-speed);
        child.setVelocity(velocity.multiply((distance * distance * distance)));
    }
}
