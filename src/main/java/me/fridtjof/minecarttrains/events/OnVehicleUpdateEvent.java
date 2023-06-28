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

public class OnVehicleUpdateEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();

    NamespacedKey key = new NamespacedKey(plugin, "coupler");

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

        PersistentDataContainer data = minecart.getPersistentDataContainer();

        if(!data.has(key, PersistentDataType.STRING)) {

            //stopping furnace minecart on not powered power rail
            if(!(minecart instanceof PoweredMinecart)) {
                return;
            }
            Block block = minecart.getLocation().getBlock();
            if(block.getType() != Material.POWERED_RAIL) {
                return;
            }
            RedstoneRail redstoneRail = (RedstoneRail) block.getBlockData();
            if(redstoneRail.isPowered()) {
                return;
            }
            minecart.setVelocity(new Vector(0 ,0 ,0));
            return;
        }
        //uniqueId = minecart.getMetadata("coupler").get(0).value().toString();

        uniqueId = data.get(key, PersistentDataType.STRING);
        //System.out.println(uniqueId + "----");

        parent = Bukkit.getEntity(UUID.fromString(uniqueId));
        if(parent == null) {
            System.out.println("[ERROR] Minecart parent not found!");
            //minecart.removeMetadata("coupler", plugin);
            data.remove(key);
            return;
        }
        //todo sollte sowieso verhindert werden
        if(parent == minecart) {
            System.out.println("[ERROR] Minecart is coupled with itself!");
            return;
        }

        double distance = minecart.getLocation().distance(parent.getLocation());

        double speed = 1.0;

        if((distance > 3) || distance < 0.7) {
            data.remove(key);
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
