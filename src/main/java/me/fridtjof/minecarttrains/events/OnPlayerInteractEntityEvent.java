package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.LinkageManager;
import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

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

    Material couplingTool = Material.getMaterial(plugin.configManager.mainConfig.getConfig().getString("trains.coupling_tool"));

    LinkageManager linkageManager = new LinkageManager();

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

        if(material != couplingTool) {
            return;
        }

        if(!(target instanceof Minecart)) {
            return;
        }
        minecart = (Minecart) target;

        event.setCancelled(true);

        //forget first selection
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
            player.sendMessage(plugin.configManager.messagesFile.getConfig().getString("trains.coupling_1-2") + minecart.getName());
        } else if(selectedCart == 2) {
            secondCarts.put(playerId, minecart.getUniqueId());
            selectedCarts.put(player.getUniqueId() + "", 1);
            player.sendMessage(plugin.configManager.messagesFile.getConfig().getString("trains.coupling_2-2") + minecart.getName());

            Entity firstCart = Bukkit.getEntity(firstCarts.get(playerId));
            Entity secondCart = Bukkit.getEntity(secondCarts.get(playerId));

            if(firstCart.getLocation().distance(secondCart.getLocation()) > 3) {
                player.sendMessage(plugin.configManager.messagesFile.getConfig().getString("trains.coupling_failed_distance"));
                return;
            }

            if(firstCart == secondCart) {
                player.sendMessage(plugin.configManager.messagesFile.getConfig().getString("trains.coupling_failed_self"));
                return;
            }

            linkageManager.setLink((Minecart) secondCart, firstCarts.get(playerId) + "");

            player.sendMessage(plugin.configManager.messagesFile.getConfig().getString("trains.coupling_successful"));
        }
    }
}
