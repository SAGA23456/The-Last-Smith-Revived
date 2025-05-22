package cn.mmf.lastsmith;

import cn.mmf.lastsmith.blades.BladeLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class CreativeTabsTLS extends CreativeTabs {

	public CreativeTabsTLS() {
		super(TLSMain.MODID);
	}

	@Override
	@Nonnull
	public ItemStack getTabIconItem() {
		return new ItemStack(BladeLoader.blade);
	}

}
