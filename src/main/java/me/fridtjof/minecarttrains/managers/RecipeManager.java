package me.fridtjof.minecarttrains.managers;

import me.fridtjof.minecarttrains.MinecartTrains;
import me.fridtjof.puddingapi.bukkit.utils.RecipeMaker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class RecipeManager
{

    static MinecartTrains plugin = MinecartTrains.getInstance();

    private final String FUEL_CART_NAME = plugin.configManager.mainConfig.getConfig().getString("trains.fuel_cart_name");

    public RecipeManager()
    {
        //Tender recipe
        ItemStack tenderItemStack = new ItemStack(Material.CHEST_MINECART);
        ItemMeta tenderItemMeta = tenderItemStack.getItemMeta();
        tenderItemMeta.setDisplayName(FUEL_CART_NAME);
        tenderItemStack.setItemMeta(tenderItemMeta);

        NamespacedKey tenderKey = new NamespacedKey(plugin, FUEL_CART_NAME.toLowerCase());

        ShapelessRecipe tenderRecipe = new ShapelessRecipe(tenderKey, tenderItemStack);

        tenderRecipe.setCategory(CraftingBookCategory.REDSTONE);

        //tenderRecipe.shape("#B#", "#M#");

        tenderRecipe.addIngredient(Material.MINECART);
        tenderRecipe.addIngredient(Material.COAL_BLOCK);

        Bukkit.addRecipe(tenderRecipe);
    }
}
