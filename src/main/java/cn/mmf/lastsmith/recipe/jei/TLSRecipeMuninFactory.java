package cn.mmf.lastsmith.recipe.jei;

import cn.mmf.lastsmith.recipe.RecipeMunin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class TLSRecipeMuninFactory implements IRecipeWrapperFactory<RecipeMunin> {
	@Override
	public IRecipeWrapper getRecipeWrapper(RecipeMunin recipe) {
		return new RecipeMuninWrapper(JEIPlugin.jeiHelpers, recipe);
	}
	
}
