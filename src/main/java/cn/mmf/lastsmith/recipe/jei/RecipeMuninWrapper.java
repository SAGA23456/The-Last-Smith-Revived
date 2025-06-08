package cn.mmf.lastsmith.recipe.jei;

import cn.mmf.lastsmith.recipe.RecipeMunin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import static cn.mmf.lastsmith.TLSConfig.simplified_recipe_advancement_info;

public class RecipeMuninWrapper extends ShapedOreRecipeWrapper {
	private RecipeMunin recipe;
	public RecipeMuninWrapper(IJeiHelpers jeiHelpers, RecipeMunin recipe) {
		super(jeiHelpers, recipe);
		this.recipe = recipe;
	}
	
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
        String localizedInfo = simplified_recipe_advancement_info ?
                I18n.format("jei.tls.tip.stage.all.text") :
                I18n.format("jei.tls.tip.stage.final_blade");
        // But format is different
        int color = simplified_recipe_advancement_info ? 0x777777 : 0xBF0000;
        minecraft.fontRenderer.drawString(
                localizedInfo,
                -2,
                -12,
                color,
                simplified_recipe_advancement_info
        );
    }
    
}
