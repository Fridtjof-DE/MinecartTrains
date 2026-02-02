package me.fridtjof.minecarttrains.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FurnaceCartSmokeTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final long intervalTicks;

    public FurnaceCartSmokeTask(JavaPlugin plugin, long intervalTicks) {
        this.plugin = plugin;
        this.intervalTicks = intervalTicks;
    }

    public void start() {
        if(!plugin.getConfig().getBoolean("trains.visuals.steam_particles")) {
            return;
        }
        this.runTaskTimer(plugin, 0L, intervalTicks);
    }

    public void stop() {
        this.cancel();
    }

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (PoweredMinecart cart : world.getEntitiesByClass(PoweredMinecart.class)) {

                if(cart.getFuel() == 0) continue;

                var loc1 = cart.getLocation().add(0, -0.2, 0);
                //var loc2 = cart.getLocation().add(0, 1.1, 0);

                world.spawnParticle(
                        Particle.CAMPFIRE_COSY_SMOKE,
                        loc1,
                        2,
                        0.15,
                        0.15,
                        0.15,
                        0.01
                );

                /*world.spawnParticle(
                        Particle.CAMPFIRE_SIGNAL_SMOKE,
                        loc2,
                        1,
                        0.05,
                        0.0,
                        0.05,
                        0.17
                );*/
            }
        }
    }
}