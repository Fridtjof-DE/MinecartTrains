package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import me.fridtjof.minecarttrains.managers.LinkageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class OnVehicleMoveEvent implements Listener {


    static MinecartTrains plugin = MinecartTrains.getInstance();
    LinkageManager linkageManager = plugin.linkageManager;

    double defaultSpeed = 0.4;
    Material highSpeedBlock = Material.getMaterial(plugin.configManager.mainConfig.getConfig().getString("tracks.high_speed.bed_block"));
    double maxHighSpeedSpeed = plugin.configManager.physicsConfig.getConfig().getDouble("track.high_speed.max_speed");
    double acceleration = plugin.configManager.physicsConfig.getConfig().getDouble("track.high_speed.acceleration");

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event)
    {
        // only continue if vehicle is a minecart
        if(!(event.getVehicle() instanceof Minecart))
        {
            return;
        }

        Minecart minecart = (Minecart) event.getVehicle();
        World world = minecart.getWorld();
        Location location = minecart.getLocation();

        if(linkageManager.hasLink(minecart))
        {
            Minecart parent = (Minecart) Bukkit.getEntity(linkageManager.getLinkUniqueId(minecart));

            minecart.setMaxSpeed(parent.getMaxSpeed());
            return;
        }

        Block track = world.getBlockAt(location);
        Block bed = world.getBlockAt(location.add(0, -1, 0));

        if(track.getType() != Material.POWERED_RAIL)
        {
            return;
        }
        if(bed.getType() != highSpeedBlock)
        {
            minecart.setMaxSpeed(defaultSpeed);
            return;
        }

        double currentMaxSpeed = minecart.getMaxSpeed();

        if(currentMaxSpeed < maxHighSpeedSpeed)
        {
            //minecart.setMaxSpeed(currentMaxSpeed * acceleration);
            minecart.setMaxSpeed(currentMaxSpeed + acceleration);
        }
        if(currentMaxSpeed > maxHighSpeedSpeed)
        {
            minecart.setMaxSpeed(maxHighSpeedSpeed);
        }

        //custom rails
        /*
        if(track.getType() != Material.RAIL)
        {
            minecart.setMaxSpeed(0.4);
            return;
        }
        minecart.setMaxSpeed(0.4 * 4);

        double currentSpeed = minecart.getVelocity().length();
        double minSpeed = 0.1;
        double copperSpeed = 2.5;
        double boost = 1.05;

        if(bed.getType() == Material.COPPER_BLOCK)
        {
            if((currentSpeed < minSpeed) || currentSpeed > copperSpeed)
            {
                return;
            }
            Vector acceleration = minecart.getVelocity().multiply(boost);

            minecart.setVelocity(acceleration);
        }*/
    }
}
