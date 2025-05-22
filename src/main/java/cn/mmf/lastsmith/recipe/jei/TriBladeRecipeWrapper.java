package cn.mmf.lastsmith.recipe.jei;

import cn.mmf.lastsmith.recipe.RecipeTriBladeTLS;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class TriBladeRecipeWrapper extends ShapedOreRecipeWrapper {
	private RecipeTriBladeTLS recipe;
	public TriBladeRecipeWrapper(IJeiHelpers jeiHelpers, RecipeTriBladeTLS recipe) {
		super(jeiHelpers, recipe);
		this.recipe=recipe;
	}
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
        String localizedInfo = I18n.format("jei.tls.tip.stage.template.text") + I18n.format("achievement.lastsmith." + recipe.getAdvancementName() + ".title");
        minecraft.fontRenderer.drawString(
                localizedInfo,
                -2,
                -12,
                0x777777,
                true
        );
    }
}
