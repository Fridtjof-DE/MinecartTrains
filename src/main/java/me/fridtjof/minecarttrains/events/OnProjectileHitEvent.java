package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class OnProjectileHitEvent implements Listener {

    static MinecartTrains plugin = MinecartTrains.getInstance();
    boolean cancelBeHit = plugin.configManager.mainConfig.getConfig().getBoolean("trains.can_get_hit_by_arrows");

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event)
    {
        if(cancelBeHit)
        {
            return;
        }

        if(!(event.getEntity() instanceof Arrow))
        {
            return;
        }

        if(!(event.getHitEntity() instanceof Minecart))
        {
            return;
        }

        event.setCancelled(true);
    }
}
