package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnPlayerInteractEntityEvent implements Listener {

    // --> coupling tool
    static MinecartTrains plugin = MinecartTrains.getInstance();

    Map<String, Integer> selectedCarts = new HashMap<String, Integer>();
    Map<String, UUID> firstCarts = new HashMap<String, UUID>();
    Map<String, UUID> secondCarts = new HashMap<String, UUID>();
    Map<String, Long> lastCouplings = new HashMap<String, Long>();

    NamespacedKey key = new NamespacedKey(plugin, "coupler");

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        String playerId = player.getUniqueId() + "";
        Material material = player.getInventory().getItemInMainHand().getType();
        Entity target = event.getRightClicked();
        Minecart minecart;

        if(material != Material.CHAIN) {
            return;
        }

        if(!(target instanceof Minecart)) {
            return;
        }
        minecart = (Minecart) target;

        event.setCancelled(true);

        if(selectedCarts.get(playerId) == null) {
            selectedCarts.put(playerId, 1);
            lastCouplings.put(playerId, System.currentTimeMillis());
        } else if(lastCouplings.get(playerId) < System.currentTimeMillis() - 10000) {
            selectedCarts.put(playerId, 1);
            lastCouplings.put(playerId, System.currentTimeMillis());
        }

        int selectedCart = selectedCarts.get(playerId);

        if(selectedCart == 1) {
            firstCarts.put(playerId, minecart.getUniqueId());
            selectedCarts.put(player.getUniqueId() + "", 2);
            player.sendMessage("Coupling (1/2) activated for " + minecart.getName());
        } else if(selectedCart == 2) {
            secondCarts.put(playerId, minecart.getUniqueId());
            selectedCarts.put(player.getUniqueId() + "", 1);
            player.sendMessage("Coupling (2/2) activated for " + minecart.getName());

            Entity firstCart = Bukkit.getEntity(firstCarts.get(playerId));
            Entity secondCart = Bukkit.getEntity(secondCarts.get(playerId));

            if(firstCart.getLocation().distance(secondCart.getLocation()) > 3) {
                player.sendMessage("Coupling failed! - The minecarts are to far apart!");
                return;
            }

            if(firstCart == secondCart) {
                player.sendMessage("Coupling failed! - Can't be coupled with itself!");
                return;
            }

            PersistentDataContainer data = secondCart.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, firstCarts.get(playerId) + "");

            player.sendMessage("Coupling successful!");
        }
    }
}
