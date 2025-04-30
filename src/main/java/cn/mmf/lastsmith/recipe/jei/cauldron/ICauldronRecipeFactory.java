package cn.mmf.lastsmith.recipe.jei.cauldron;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ICauldronRecipeFactory {
    IRecipeWrapper createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3);

    IRecipeWrapper createAnvilRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3);

    IRecipeWrapper createSmeltingRecipe(List<ItemStack> var1, ItemStack var2);

    IRecipeWrapper createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3);
}
