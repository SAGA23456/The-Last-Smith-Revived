package cn.mmf.lastsmith.event;

import cn.mmf.lastsmith.TLSMain;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import cn.mmf.lastsmith.item.ItemLoader;
import cn.mmf.lastsmith.util.BladeUtil;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class AnvilEvent {
	@SubscribeEvent
	public static void MuninOldRecipe(AnvilUpdateEvent event) {
        if (!(event.getLeft().getItem() instanceof ItemSlashBlade))
            return;
        if (!(event.getRight().getItem() == ItemLoader.SCROLL)||(event.getRight().getMetadata()!=17))
            return;
        event.setMaterialCost(1);
        ItemStack out = event.getLeft().copy();
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(out);
        if(!ItemSlashBladeNamed.CurrentItemName.get(tag).equalsIgnoreCase("flammpfeil.slashblade.named.smith.final"))
        	return;
		ItemSlashBlade.TextureName.set(tag, "named/smith/texture_final_old");
		ItemSlashBlade.ModelName.set(tag, "named/smith/model");
        out.setItemDamage(0);
        event.setCost(1);
        event.setOutput(out);
	}

    private static boolean isTLSMaterial(ItemStack stack) {
        ResourceLocation rl = stack.getItem().getRegistryName();
        return rl != null && rl.getResourceDomain().equals(TLSMain.MODID) && rl.getResourcePath().equals("materials") &&
                // meta 0, 1, 2 are not TLS refine materials,
                // see cn.mmf.lastsmith.item.ItemLoader.MATERIALS
                stack.getMetadata() > 2;
    }


	@SubscribeEvent
	public static void bewitchedBladeRecipe(AnvilUpdateEvent event) {
        if (!(event.getLeft().getItem() instanceof ItemSlashBlade))
            return;
        if (!isTLSMaterial(event.getRight()))
            return;

        ItemStack out = event.getLeft().copy();
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(out);

        // Fast Refine
        int refineCount = event.getRight().getCount();

        // Refine Limit if it is present
        int refineLimit = ItemSlashBlade.getItemTagCompound(event.getLeft()).hasKey("RefineLimit") ? ItemSlashBlade.getItemTagCompound(event.getLeft()).getInteger("RefineLimit") : Integer.MAX_VALUE;
        refineCount = Math.min(refineCount, (refineLimit - ItemSlashBlade.RepairCount.get(tag)));

        // If this is the BewitchedRecipe, do not apply fast refine
        boolean isBewitchedRecipe = false;

        int cost = event.getCost();
        float repairFactor;
        switch (event.getRight().getItemDamage()) {
        case 3:
            cost = Math.max(2, cost);
            repairFactor = 0.4f;
            ItemSlashBlade.ProudSoul.add(tag, 100 * refineCount);
            break;
        case 4:
            cost = Math.max(3, cost);
            repairFactor = 0.6f;
            ItemSlashBlade.ProudSoul.add(tag, 500 * refineCount);
            break;
        case 5:
            cost = Math.max(4, cost);
            repairFactor = 0.7f;
            ItemSlashBlade.ProudSoul.add(tag, 2500 * refineCount);
            break;
        case 6:
            cost = Math.max(1, cost);
            repairFactor = 1f;
            ItemSlashBlade.ProudSoul.add(tag, 10000);
    		ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);
    		BladeUtil.getInstance().IsBewitchedActived.set(tag, true);
            isBewitchedRecipe = true;
            break;
        default:
            return;  
        }

        if (!isBewitchedRecipe) cost *= refineCount;
        event.setCost(cost);

        ItemSlashBlade.RepairCount.add(tag, isBewitchedRecipe? 1 : refineCount);

        // TLS Materials repair SlashBlade to a fixed proportion of durability, so it should be regardless of refine times.
        int repair = Math.min(out.getItemDamage(), (int) (out.getMaxDamage() * repairFactor));
        out.setItemDamage(out.getItemDamage() - repair);

        // If fast refine, use entire stack of material
        event.setMaterialCost(isBewitchedRecipe? 1 : refineCount);

        if (StringUtils.isBlank(event.getName())){
            if (event.getLeft().hasDisplayName())
                out.clearCustomName();
        }else if (!event.getName().equals(event.getLeft().getDisplayName()))
            out.setStackDisplayName(event.getName());

        event.setOutput(out);
	}
}
