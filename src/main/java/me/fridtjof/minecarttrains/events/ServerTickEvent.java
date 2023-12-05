package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

import java.util.logging.Logger;

public class ServerTickEvent {

    static MinecartTrains plugin = MinecartTrains.getInstance();
    Logger logger = plugin.getLogger();

    LinkageHandler linkageHandler = new LinkageHandler();

    public ServerTickEvent() {

    }

    public void tick() {
        /*for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(entity instanceof Minecart) {
                    linkageHandler.handlerLinking((Minecart) entity);
                }
            }
        }*/
    }
}
