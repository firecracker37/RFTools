package mcjty.rftools.items.envmodules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.rftools.blocks.environmental.EnvModuleProvider;
import mcjty.rftools.blocks.environmental.EnvironmentalConfiguration;
import mcjty.rftools.blocks.environmental.modules.EnvironmentModule;
import mcjty.rftools.blocks.environmental.modules.WeaknessEModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class WeaknessEModuleItem extends Item implements EnvModuleProvider {

    public WeaknessEModuleItem() {
        setMaxStackSize(16);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add("This module gives weakness when");
        list.add("used in the environmental controller.");
        list.add(EnumChatFormatting.GREEN + "Uses " + EnvironmentalConfiguration.WEAKNESS_RFPERTICK + " RF/tick (per cubic block)");
        if (!EnvironmentalConfiguration.weaknessAvailable) {
            list.add(EnumChatFormatting.RED + "This module is disabled in config!");
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public Class<? extends EnvironmentModule> getServerEnvironmentModule() {
        return WeaknessEModule.class;
    }

    @Override
    public String getName() {
        return "Weakness";
    }
}