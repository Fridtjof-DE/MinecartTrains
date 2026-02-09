package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class MinecartChunkLoader implements Listener {

    private JavaPlugin plugin;
    private FileConfiguration mainConfig;

    private int chunkRadius;
    private boolean stationaryTimeoutEnabled;
    private long stationaryTimeoutMs;
    //TODO
    private boolean stationaryTimeoutExemptPoweredCarts;
    private boolean mobileTimeoutEnabled;
    private long mobileTimeoutMs;
    private boolean chunkHistoryEnabled;
    private int chunkHistorySize = 7;
    private boolean requiredMovementEnabled;
    private double requiredMovementDistance = 8.0;


    private final long SAVE_INTERVAL_MS = 5_000; // save active UUIDs every 5 seconds

    // Tracking per Minecart
    public final Map<Minecart, Set<Chunk>> activeChunks = new HashMap<>();
    private final Map<Minecart, Deque<Chunk>> recentChunks = new HashMap<>();
    private final Map<Minecart, Location> lastProgressLocation = new HashMap<>();
    private final Map<Minecart, Long> lastMovementTimestamp = new HashMap<>();
    private final Map<Minecart, Map<Chunk, Long>> chunkLoadTimestamps = new HashMap<>();
    private final Map<Minecart, Chunk> lastChunk = new HashMap<>(); // track last chunk for chunk-change-only updates

    public MinecartChunkLoader(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.mainConfig = configManager.mainConfig.getConfig();

        if(!mainConfig.getBoolean("trains.chunk_loading.enable"))
        {
            return;
        }

        this.chunkRadius = mainConfig.getInt("trains.chunk_loading.radius");
        this.stationaryTimeoutEnabled = mainConfig.getBoolean("trains.chunk_loading.abuse_protection.timeout.stationary.enable");
        this.stationaryTimeoutMs = mainConfig.getLong("trains.chunk_loading.abuse_protection.timeout.stationary.duration");
        this.stationaryTimeoutExemptPoweredCarts = mainConfig.getBoolean("trains.chunk_loading.abuse_protection.timeout.stationary.exempt_furnace_minecarts");
        this.mobileTimeoutEnabled = mainConfig.getBoolean("trains.chunk_loading.abuse_protection.timeout.mobile.enable");
        this.mobileTimeoutMs = mainConfig.getLong("trains.chunk_loading.abuse_protection.timeout.mobile.duration");
        this.chunkHistoryEnabled = mainConfig.getBoolean("trains.chunk_loading.abuse_protection.history.enable");
        this.chunkHistorySize = mainConfig.getInt("trains.chunk_loading.abuse_protection.history.size");
        this.requiredMovementEnabled = mainConfig.getBoolean("trains.chunk_loading.abuse_protection.movement_required.enable");
        this.requiredMovementDistance = mainConfig.getDouble("trains.chunk_loading.abuse_protection.movement_required.distance");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Schedule periodic save of active minecart UUIDs
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveActiveMinecartUUIDs, SAVE_INTERVAL_MS / 50, SAVE_INTERVAL_MS / 50);
    }

    @EventHandler
    public void onMinecartMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart minecart)) return;

        World world = minecart.getWorld();
        Location currentLocation = minecart.getLocation();
        long now = System.currentTimeMillis();

        // Step 0: check if chunk changed
        plugin.getLogger().info("Step 0");
        Chunk currentChunk = currentLocation.getChunk();
        Chunk previousChunk = lastChunk.get(minecart);
        if (previousChunk != null && previousChunk.equals(currentChunk)) {
            return; // skip processing if still in the same chunk
        }

        lastChunk.put(minecart, currentChunk);

        // Step 1: check stationary timeout
        if(stationaryTimeoutEnabled)
        {
            plugin.getLogger().info("Step 1");
            lastMovementTimestamp.putIfAbsent(minecart, now);
            lastProgressLocation.putIfAbsent(minecart, currentLocation);

            if (now - lastMovementTimestamp.get(minecart) > stationaryTimeoutMs) {
                removeAllTickets(minecart);
                lastProgressLocation.put(minecart, currentLocation);
                lastMovementTimestamp.put(minecart, now);
                return; // minecart considered stationary
            }
        }

        // Step 2: check for real movement
        if(requiredMovementEnabled)
        {
            plugin.getLogger().info("Step 2");
            Location lastLocation = lastProgressLocation.get(minecart);
            if (lastLocation.distanceSquared(currentLocation) < requiredMovementDistance * requiredMovementDistance) {
                return; // not enough movement
            }
        }

        // Step 3: check chunk rotation (anti-circle)
        if(chunkHistoryEnabled)
        {
            plugin.getLogger().info("Step 3");
            Deque<Chunk> history = recentChunks.computeIfAbsent(minecart, k -> new ArrayDeque<>());
            if (history.contains(currentChunk)) {
                lastProgressLocation.put(minecart, currentLocation);
                lastMovementTimestamp.put(minecart, now);
                return; // circle detected
            }

            // Step 4: update chunk history
            plugin.getLogger().info("Step 4");
            history.addLast(currentChunk);
            if (history.size() > chunkHistorySize) {
                history.removeFirst();
            }
        }

        // Step 5: calculate chunks to load
        plugin.getLogger().info("Step 5");
        Set<Chunk> chunksToLoad = new HashSet<>();
        int cx = currentChunk.getX();
        int cz = currentChunk.getZ();

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                chunksToLoad.add(world.getChunkAt(cx + dx, cz + dz));
            }
        }

        // Step 6: add tickets for new chunks
        plugin.getLogger().info("Step 6");
        Set<Chunk> currentlyActive = activeChunks.computeIfAbsent(minecart, k -> new HashSet<>());
        Map<Chunk, Long> loadTimes = chunkLoadTimestamps.computeIfAbsent(minecart, k -> new HashMap<>());

        for (Chunk chunk : chunksToLoad) {
            if (!currentlyActive.contains(chunk)) {
                world.addPluginChunkTicket(chunk.getX(), chunk.getZ(), plugin); // add ticket for chunk
                currentlyActive.add(chunk);
                loadTimes.put(chunk, now);
            } else {
                loadTimes.put(chunk, now); // update timestamp
            }
        }

        // Step 7: remove old tickets
        plugin.getLogger().info("Step 7");
        Iterator<Chunk> iter = currentlyActive.iterator();
        while (iter.hasNext()) {
            Chunk chunk = iter.next();
            if (!chunksToLoad.contains(chunk) || now - loadTimes.get(chunk) > mobileTimeoutMs) {
                world.removePluginChunkTicket(chunk.getX(), chunk.getZ(), plugin); //  remove ticket
                iter.remove();
                loadTimes.remove(chunk);
            }
        }

        // Step 8: save last progress location
        plugin.getLogger().info("Step 8");
        lastProgressLocation.put(minecart, currentLocation);
        lastMovementTimestamp.put(minecart, now);
    }

    public void removeAllTickets(Minecart minecart) {
        Set<Chunk> currentlyActive = activeChunks.get(minecart);
        if (currentlyActive == null) return;

        for (Chunk chunk : currentlyActive) {
            chunk.getWorld().removePluginChunkTicket(chunk.getX(), chunk.getZ(), plugin);; // remove ticket
        }

        currentlyActive.clear();
        Map<Chunk, Long> loadTimes = chunkLoadTimestamps.get(minecart);
        if (loadTimes != null) loadTimes.clear();
    }

    // Save active minecart UUIDs periodically for recovery
    private void saveActiveMinecartUUIDs() {
        Set<UUID> activeUUIDs = activeChunks.keySet().stream()
                .filter(Minecart::isValid) // make sure the entity still exists
                .map(Minecart::getUniqueId)
                .collect(Collectors.toSet());

        //TODO
        // dataConfig.set("activeMinecarts", new ArrayList<>(activeUUIDs));
        // dataConfig.saveConfig();
    }
}
