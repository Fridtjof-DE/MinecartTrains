package me.fridtjof.minecarttrains.events;

import me.fridtjof.minecarttrains.MinecartTrains;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Minecart;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.logging.Logger;

public class LinkageHandler
{

    static MinecartTrains plugin = MinecartTrains.getInstance();
    Logger logger = plugin.getLogger();

    NamespacedKey key1 = new NamespacedKey(plugin, "coupler_1");
    NamespacedKey key2 = new NamespacedKey(plugin, "coupler_2");

    private static final float MAX_DISTANCE = 3F;
    private static final float MIN_DISTANCE = 0.7F;
    private static final float AIMED_DISTANCE = 1.2F;
    private static final double SPRING_CONSTANT = 0.95;
    private static final double DAMPING = -0.4;

    public LinkageHandler() {
    }

    public void handleLinking(Minecart cartToBeHandled)
    {
        if(!hasAnyLink(cartToBeHandled))
        {
            return;
        }

        Minecart linkedCartOne = null;
        Minecart linkedCartTwo = null;

        if(hasLink(cartToBeHandled, key1))
        {
            linkedCartOne = getCart(getLinkUniqueId(cartToBeHandled, key1));
        }
        if(hasLink(cartToBeHandled, key2))
        {
            linkedCartTwo = getCart(getLinkUniqueId(cartToBeHandled, key2));
        }

        if(!canCartBeAdjusted(cartToBeHandled, linkedCartOne))
        {
            removeLink(cartToBeHandled, key1);
            linkedCartOne = null;
        }
        if(!canCartBeAdjusted(cartToBeHandled, linkedCartTwo))
        {
            removeLink(cartToBeHandled, key2);
            linkedCartTwo = null;
        }

        adjustCart(cartToBeHandled, linkedCartOne, linkedCartTwo);
    }

    private boolean canCartBeAdjusted(Minecart cartToBeAdjusted, Minecart cartFromLink)
    {
        if(cartFromLink == null)
        {
            //logger.info("Tried to adjust to cart that is null");
            return false;
        }
        if(cartToBeAdjusted.getLocation().distance(cartFromLink.getLocation()) > MAX_DISTANCE)
        {
            return false;
        }
        if(cartToBeAdjusted == cartFromLink)
        {
            logger.info("Tried to adjust cart with itself");
            return false;
        }
        if(cartToBeAdjusted.getWorld() != cartFromLink.getWorld())
        {
            logger.info("Tried to adjust carts from different worlds");
            return false;
        }
        if(cartToBeAdjusted.getLocation().distance(cartFromLink.getLocation()) > MAX_DISTANCE)
        {
            logger.info("Link broke because of too far distance");
            return false;
        }

        UUID uniqueIdCartLinkedToLinkedCartOne = getLinkUniqueId(cartFromLink, key1);
        Minecart cartLinkedToLinkedCartOne = null;
        if(uniqueIdCartLinkedToLinkedCartOne != null)
        {
            cartLinkedToLinkedCartOne = getCart(getLinkUniqueId(cartFromLink, key1));
        }

        UUID uniqueIdCartLinkedToLinkedCartTwo = getLinkUniqueId(cartFromLink, key2);
        Minecart cartLinkedToLinkedCartTwo = null;
        if(uniqueIdCartLinkedToLinkedCartTwo != null)
        {
            cartLinkedToLinkedCartTwo = getCart(getLinkUniqueId(cartFromLink, key2));
        }

        if((cartToBeAdjusted != cartLinkedToLinkedCartOne) && (cartToBeAdjusted != cartLinkedToLinkedCartTwo))
        {
            logger.info("Link broke because the cart that this cart was linked to was not linked to this cart");
            return false;
        }
        return true;
    }

    private void adjustCart(Minecart cartToBeAdjusted, Minecart linkedCartOne, Minecart linkedCartTwo)
    {
        Vector connectingVectorToAimedPointToCartOne = new Vector().zero();
        Vector connectingVectorToAimedPointToCartTwo = new Vector().zero();
        if(linkedCartOne != null) {
            connectingVectorToAimedPointToCartOne = getConnectingVectorToCart(cartToBeAdjusted, linkedCartOne);
        }
        if(linkedCartTwo != null) {
            connectingVectorToAimedPointToCartTwo = getConnectingVectorToCart(cartToBeAdjusted, linkedCartTwo);
        }

        Vector adjustmentVector = connectingVectorToAimedPointToCartOne.subtract(connectingVectorToAimedPointToCartTwo);
        logger.info("---" + adjustmentVector.toString());
        adjustmentVector = adjustmentVector.multiply(SPRING_CONSTANT);
        logger.info("------" + adjustmentVector.toString());

        Vector dampingVector = cartToBeAdjusted.getVelocity().multiply(DAMPING);

        cartToBeAdjusted.setVelocity(adjustmentVector.multiply(dampingVector));
    }

    private Vector getConnectingVectorToCart(Minecart cartToBeAdjusted, Minecart linkedCart) {
        Vector cartToBeAdjustedPositon = cartToBeAdjusted.getLocation().toVector();
        Vector linkedCartPosition = linkedCart.getLocation().toVector();
        Vector connectingVectorToLinkedCart = cartToBeAdjustedPositon.subtract(linkedCartPosition);
        double distanceToLinkedCart = cartToBeAdjustedPositon.distance(linkedCartPosition);
        double multiplier = ((distanceToLinkedCart / 2) - (AIMED_DISTANCE / 2)) / distanceToLinkedCart;
        Vector aimedPointToLinkedCart = cartToBeAdjustedPositon.add(connectingVectorToLinkedCart.multiply(multiplier));
        logger.info(aimedPointToLinkedCart.toString());
        //logger.info("ConnectingVec" + cartToBeAdjustedPositon.subtract(aimedPointToLinkedCart).toString());

        //return cartToBeAdjustedPositon.subtract(aimedPointToLinkedCart);
        return aimedPointToLinkedCart;
    }


    private void removeLink(Minecart cart, NamespacedKey key)
    {
        cart.getPersistentDataContainer().remove(key);
    }

    private String getLinkString(Minecart cart, NamespacedKey key)
    {
        return cart.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
    private UUID getLinkUniqueId(Minecart cart, NamespacedKey key)
    {
        String uniqueId = getLinkString(cart, key);
        if(uniqueId == null)
        {
            return null;
        }
        return UUID.fromString(uniqueId);
    }
    private Minecart getCart(UUID uuid)
    {
        return (Minecart) Bukkit.getEntity(uuid);
    }


    private void setLink(Minecart cartToHaveLinkSet, Minecart cartToBeLinkedTo, NamespacedKey key) {
        cartToHaveLinkSet.getPersistentDataContainer().set(key, PersistentDataType.STRING, cartToBeLinkedTo.getUniqueId().toString());
    }

    public void setLinkOne(Minecart cartToHaveLinkSet, Minecart cartToBeLinkedTo) {
        setLink(cartToHaveLinkSet, cartToBeLinkedTo, key1);
    }

    public void setLinkTwo(Minecart cartToHaveLinkSet, Minecart cartToBeLinkedTo) {
        setLink(cartToHaveLinkSet, cartToBeLinkedTo, key2);
    }

    //Check for Linking
    private boolean hasAnyLink(Minecart cart)
    {
        if(cart.getPersistentDataContainer().has(key1, PersistentDataType.STRING))
        {
            return true;
        }
        if(cart.getPersistentDataContainer().has(key2, PersistentDataType.STRING))
        {
            return true;
        }
        return false;
    }

    private boolean hasLink(Minecart cart, NamespacedKey key)
    {
        if(cart.getPersistentDataContainer().has(key, PersistentDataType.STRING))
        {
            return true;
        }
        return false;
    }

    public boolean hasLinkOne(Minecart cart)
    {
        if(cart.getPersistentDataContainer().has(key1, PersistentDataType.STRING))
        {
            return true;
        }
        return false;
    }

    public boolean hasLinkTwo(Minecart cart)
    {
        if(cart.getPersistentDataContainer().has(key2, PersistentDataType.STRING))
        {
            return true;
        }
        return false;
    }

    public boolean hasBothLinks(Minecart cart)
    {
        if(!cart.getPersistentDataContainer().has(key1, PersistentDataType.STRING))
        {
            return false;
        }
        if(!cart.getPersistentDataContainer().has(key2, PersistentDataType.STRING))
        {
            return false;
        }
        return true;
    }
}
