package cn.mmf.lastsmith.recipe.jei.cauldron;

import com.google.common.base.Preconditions;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.gui.GuiHelper;
import mezz.jei.plugins.vanilla.anvil.AnvilRecipeCategory;
import mezz.jei.plugins.vanilla.anvil.AnvilRecipeMaker;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeCategory;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeMaker;
import mezz.jei.plugins.vanilla.brewing.PotionSubtypeInterpreter;
import mezz.jei.plugins.vanilla.furnace.FuelRecipeMaker;
import mezz.jei.plugins.vanilla.furnace.FurnaceFuelCategory;
import mezz.jei.plugins.vanilla.furnace.FurnaceSmeltingCategory;
import mezz.jei.plugins.vanilla.furnace.SmeltingRecipeMaker;
import mezz.jei.plugins.vanilla.ingredients.enchant.EnchantDataHelper;
import mezz.jei.plugins.vanilla.ingredients.enchant.EnchantDataListFactory;
import mezz.jei.plugins.vanilla.ingredients.enchant.EnchantDataRenderer;
import mezz.jei.plugins.vanilla.ingredients.enchant.EnchantedBookCache;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackHelper;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackListFactory;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackRenderer;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackHelper;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackListFactory;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import mezz.jei.runtime.JeiHelpers;
import mezz.jei.startup.StackHelper;
import mezz.jei.transfer.PlayerRecipeTransferHandler;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.plugins.vanilla.crafting.CraftingRecipeCategory;
import mezz.jei.plugins.vanilla.crafting.CraftingRecipeChecker;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapedRecipesWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.TippedArrowRecipeMaker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class CauldronJEIPlugin implements IModPlugin{
	@Nullable
	private ISubtypeRegistry subtypeRegistry;

	public CauldronJEIPlugin() {
	}

	public void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
		this.subtypeRegistry = subtypeRegistry;
		subtypeRegistry.registerSubtypeInterpreter(Items.TIPPED_ARROW, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerSubtypeInterpreter(Items.POTIONITEM, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerSubtypeInterpreter(Items.SPLASH_POTION, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerSubtypeInterpreter(Items.LINGERING_POTION, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerSubtypeInterpreter(Items.BANNER, (itemStack) -> {
			EnumDyeColor baseColor = ItemBanner.getBaseColor(itemStack);
			return baseColor.toString();
		});
		subtypeRegistry.registerSubtypeInterpreter(Items.SPAWN_EGG, (itemStack) -> {
			ResourceLocation resourceLocation = ItemMonsterPlacer.getNamedIdFrom(itemStack);
			return resourceLocation == null ? "" : resourceLocation.toString();
		});
		subtypeRegistry.registerSubtypeInterpreter(Items.ENCHANTED_BOOK, (itemStack) -> {
			List<String> enchantmentNames = new ArrayList();

			for(NBTBase nbt : ItemEnchantedBook.getEnchantments(itemStack)) {
				if (nbt instanceof NBTTagCompound) {
					NBTTagCompound nbttagcompound = (NBTTagCompound)nbt;
					int j = nbttagcompound.getShort("id");
					Enchantment enchantment = Enchantment.getEnchantmentByID(j);
					if (enchantment != null) {
						String enchantmentUid = enchantment.getName() + ".lvl" + nbttagcompound.getShort("lvl");
						enchantmentNames.add(enchantmentUid);
					}
				}
			}

			enchantmentNames.sort((Comparator)null);
			return enchantmentNames.toString();
		});
	}

	public void registerIngredients(IModIngredientRegistration ingredientRegistration) {
		Preconditions.checkState(this.subtypeRegistry != null);
		StackHelper stackHelper = Internal.getStackHelper();
		ItemStackListFactory itemStackListFactory = new ItemStackListFactory(this.subtypeRegistry);
		List<ItemStack> itemStacks = itemStackListFactory.create(stackHelper);
		ItemStackHelper itemStackHelper = new ItemStackHelper(stackHelper);
		ItemStackRenderer itemStackRenderer = new ItemStackRenderer();
		ingredientRegistration.register(VanillaTypes.ITEM, itemStacks, itemStackHelper, itemStackRenderer);
		List<FluidStack> fluidStacks = FluidStackListFactory.create();
		FluidStackHelper fluidStackHelper = new FluidStackHelper();
		FluidStackRenderer fluidStackRenderer = new FluidStackRenderer();
		ingredientRegistration.register(VanillaTypes.FLUID, fluidStacks, fluidStackHelper, fluidStackRenderer);
		List<EnchantmentData> enchantments = EnchantDataListFactory.create();
		EnchantedBookCache enchantedBookCache = new EnchantedBookCache();
		MinecraftForge.EVENT_BUS.register(enchantedBookCache);
		EnchantDataHelper enchantmentHelper = new EnchantDataHelper(enchantedBookCache, itemStackHelper);
		EnchantDataRenderer enchantmentRenderer = new EnchantDataRenderer(itemStackRenderer, enchantedBookCache);
		ingredientRegistration.register(VanillaTypes.ENCHANT, enchantments, enchantmentHelper, enchantmentRenderer);
	}

	public void registerCategories(IRecipeCategoryRegistration registry) {
		JeiHelpers jeiHelpers = Internal.getHelpers();
		GuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registry.addRecipeCategories(new IRecipeCategory[]{new CraftingRecipeCategory(guiHelper), new FurnaceFuelCategory(guiHelper), new FurnaceSmeltingCategory(guiHelper), new BrewingRecipeCategory(guiHelper), new AnvilRecipeCategory(guiHelper)});
	}

	public void register(IModRegistry registry) {
		IIngredientRegistry ingredientRegistry = registry.getIngredientRegistry();
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
		registry.addRecipes(CraftingRecipeChecker.getValidRecipes(jeiHelpers), "minecraft.crafting");
		registry.addRecipes(SmeltingRecipeMaker.getFurnaceRecipes(jeiHelpers), "minecraft.smelting");
		registry.addRecipes(FuelRecipeMaker.getFuelRecipes(ingredientRegistry, jeiHelpers), "minecraft.fuel");
		registry.addRecipes(BrewingRecipeMaker.getBrewingRecipes(ingredientRegistry), "minecraft.brewing");
		registry.addRecipes(AnvilRecipeMaker.getAnvilRecipes(vanillaRecipeFactory, ingredientRegistry), "minecraft.anvil");
		if (CraftingRecipeChecker.hasTippedArrowRecipe) {
			registry.addRecipes(TippedArrowRecipeMaker.getTippedArrowRecipes(), "minecraft.crafting");
		}

		registry.handleRecipes(ShapedOreRecipe.class, (recipe) -> new ShapedOreRecipeWrapper(jeiHelpers, recipe), "minecraft.crafting");
		registry.handleRecipes(ShapedRecipes.class, (recipe) -> new ShapedRecipesWrapper(jeiHelpers, recipe), "minecraft.crafting");
		registry.handleRecipes(ShapelessOreRecipe.class, (recipe) -> new ShapelessRecipeWrapper(jeiHelpers, recipe), "minecraft.crafting");
		registry.handleRecipes(ShapelessRecipes.class, (recipe) -> new ShapelessRecipeWrapper(jeiHelpers, recipe), "minecraft.crafting");
		registry.addRecipeClickArea(GuiCrafting.class, 88, 32, 28, 23, new String[]{"minecraft.crafting"});
		registry.addRecipeClickArea(GuiInventory.class, 137, 29, 10, 13, new String[]{"minecraft.crafting"});
		registry.addRecipeClickArea(GuiBrewingStand.class, 97, 16, 14, 30, new String[]{"minecraft.brewing"});
		registry.addRecipeClickArea(GuiFurnace.class, 78, 32, 28, 23, new String[]{"minecraft.smelting", "minecraft.fuel"});
		registry.addRecipeClickArea(GuiRepair.class, 102, 48, 22, 15, new String[]{"minecraft.anvil"});
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		recipeTransferRegistry.addRecipeTransferHandler(ContainerWorkbench.class, "minecraft.crafting", 1, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(new PlayerRecipeTransferHandler(jeiHelpers.recipeTransferHandlerHelper()), "minecraft.crafting");
		recipeTransferRegistry.addRecipeTransferHandler(ContainerFurnace.class, "minecraft.smelting", 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerFurnace.class, "minecraft.fuel", 1, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerBrewingStand.class, "minecraft.brewing", 0, 4, 5, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerRepair.class, "minecraft.anvil", 0, 2, 3, 36);
		registry.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), new String[]{"minecraft.crafting"});
		registry.addRecipeCatalyst(new ItemStack(Blocks.FURNACE), new String[]{"minecraft.smelting", "minecraft.fuel"});
		registry.addRecipeCatalyst(new ItemStack(Items.BREWING_STAND), new String[]{"minecraft.brewing"});
		registry.addRecipeCatalyst(new ItemStack(Blocks.ANVIL), new String[]{"minecraft.anvil"});
		IIngredientBlacklist ingredientBlacklist = registry.getJeiHelpers().getIngredientBlacklist();
		ingredientBlacklist.addIngredientToBlacklist(new ItemStack(Items.SKULL, 1, 3));
		ingredientBlacklist.addIngredientToBlacklist(new ItemStack(Items.ENCHANTED_BOOK, 1, 32767));
	}
}
