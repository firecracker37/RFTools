package mcjty.rftools.commands;

import mcjty.rftools.dimension.DimensionInformation;
import mcjty.rftools.dimension.RfToolsDimensionManager;
import mcjty.rftools.dimension.world.types.EffectType;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class CmdAddEffect extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "<effect>";
    }

    @Override
    public String getCommand() {
        return "effectadd";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Several parameters are missing!"));
            return;
        } else if (args.length > 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many parameters!"));
            return;
        }

        String effectName = fetchString(sender, args, 1, "");
        effectName = "EFFECT_" + effectName.toUpperCase();
        EffectType type = null;
        try {
            type = EffectType.valueOf(effectName);
        } catch (IllegalArgumentException e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Bad effect name!"));
            return;
        }

        World world = sender.getEntityWorld();
        int dim = world.provider.dimensionId;
        RfToolsDimensionManager dimensionManager = RfToolsDimensionManager.getDimensionManager(world);
        DimensionInformation information = dimensionManager.getDimensionInformation(dim);
        if (information == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Not an RFTools dimension!"));
            return;
        }

        information.getEffectTypes().add(type);
        dimensionManager.save(world);
    }
}
