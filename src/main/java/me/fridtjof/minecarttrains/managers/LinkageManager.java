package me.fridtjof.minecarttrains.managers;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Minecart;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class LinkageManager
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    NamespacedKey key = new NamespacedKey(plugin, "coupler");

    public LinkageManager()
    {

    }

    public void removeLink(Minecart cart)
    {
        cart.getPersistentDataContainer().remove(key);
    }

    public String getLinkString(Minecart cart)
    {
        return cart.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public UUID getLinkUniqueId(Minecart cart)
    {
        return UUID.fromString(getLinkString(cart));
    }

    public void setLink(Minecart cart, String linkedCartUniqueId)
    {
        cart.getPersistentDataContainer().set(key, PersistentDataType.STRING, linkedCartUniqueId);
    }

    public boolean hasLink(Minecart cart)
    {
        return cart.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
}
