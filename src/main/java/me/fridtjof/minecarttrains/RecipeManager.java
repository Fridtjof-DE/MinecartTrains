package me.fridtjof.minecarttrains;

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

    public RecipeManager()
    {
        //Tender recipe
        ItemStack tenderItemStack = new ItemStack(Material.CHEST_MINECART);
        ItemMeta tenderItemMeta = tenderItemStack.getItemMeta();
        tenderItemMeta.setDisplayName(MinecartTrains.TENDER_NAME);
        tenderItemStack.setItemMeta(tenderItemMeta);

        NamespacedKey tenderKey = new NamespacedKey(plugin, MinecartTrains.TENDER_NAME.toLowerCase());

        ShapelessRecipe tenderRecipe = new ShapelessRecipe(tenderKey, tenderItemStack);

        tenderRecipe.setCategory(CraftingBookCategory.REDSTONE);

        //tenderRecipe.shape("#B#", "#M#");

        tenderRecipe.addIngredient(Material.MINECART);
        tenderRecipe.addIngredient(Material.COAL_BLOCK);

        Bukkit.addRecipe(tenderRecipe);
    }
}
