package cn.mmf.lastsmith.recipe.jei.cauldron;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Constants;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CauldronRecipeCategory  implements IRecipeCategory<CauldronRecipeWrapper> {
    private static final int brewPotionSlot1 = 0;
    private static final int brewPotionSlot2 = 1;
    private static final int brewPotionSlot3 = 2;
    private static final int brewIngredientSlot = 3;
    private static final int outputSlot = 4;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final String localizedName;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;
    private final IDrawableStatic blazeHeat;

    public CauldronRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = Constants.RECIPE_GUI_VANILLA;
        this.background = guiHelper.drawableBuilder(location, 0, 0, 64, 60).addPadding(1, 0, 0, 50).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Items.BREWING_STAND));
        this.localizedName = Translator.translateToLocal("gui.jei.category.brewing");
        this.arrow = guiHelper.drawableBuilder(location, 64, 0, 9, 28).buildAnimated(400, IDrawableAnimated.StartDirection.TOP, false);
        ITickTimer bubblesTickTimer = new CauldronRecipeCategory.BrewingBubblesTickTimer(guiHelper);
        this.bubbles = guiHelper.drawableBuilder(location, 73, 0, 12, 29).buildAnimated(bubblesTickTimer, IDrawableAnimated.StartDirection.BOTTOM);
        this.blazeHeat = guiHelper.createDrawable(location, 64, 29, 18, 4);
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    public String getUid() {
        return "minecraft.brewing";
    }

    public String getTitle() {
        return this.localizedName;
    }

    public String getModName() {
        return "Minecraft";
    }

    public IDrawable getBackground() {
        return this.background;
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public void drawExtras(Minecraft minecraft) {
        this.blazeHeat.draw(minecraft, 5, 30);
        this.bubbles.draw(minecraft, 8, 0);
        this.arrow.draw(minecraft, 42, 2);
    }

    public void setRecipe(IRecipeLayout recipeLayout, CauldronRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 0, 36);
        itemStacks.init(1, true, 23, 43);
        itemStacks.init(2, true, 46, 36);
        itemStacks.init(3, true, 23, 2);
        itemStacks.init(4, false, 80, 2);
        itemStacks.setBackground(4, this.slotDrawable);
        itemStacks.set(ingredients);
    }

    private static class BrewingBubblesTickTimer implements ITickTimer {
        private static final int[] BUBBLE_LENGTHS = new int[]{29, 23, 18, 13, 9, 5, 0};
        private final ITickTimer internalTimer;

        public BrewingBubblesTickTimer(IGuiHelper guiHelper) {
            this.internalTimer = guiHelper.createTickTimer(14, BUBBLE_LENGTHS.length - 1, false);
        }

        public int getValue() {
            int timerValue = this.internalTimer.getValue();
            return BUBBLE_LENGTHS[timerValue];
        }

        public int getMaxValue() {
            return BUBBLE_LENGTHS[0];
        }
    }
}
