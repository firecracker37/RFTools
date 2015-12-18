package mcjty.rftools.blocks.crafter;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCrafter implements IMessage, IMessageHandler<PacketCrafter, IMessage> {
    private BlockPos pos;

    private int recipeIndex;
    private ItemStack items[];
    private boolean keepOne;
    private boolean craftInternal;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        keepOne = buf.readBoolean();
        craftInternal = buf.readBoolean();

        recipeIndex = buf.readByte();
        int l = buf.readByte();
        if (l == 0) {
            items = null;
        } else {
            items = new ItemStack[l];
            for (int i = 0 ; i < l ; i++) {
                boolean b = buf.readBoolean();
                if (b) {
                    items[i] = NetworkTools.readItemStack(buf);
                } else {
                    items[i] = null;
                }
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeBoolean(keepOne);
        buf.writeBoolean(craftInternal);

        buf.writeByte(recipeIndex);
        if (items != null) {
            buf.writeByte(items.length);
            for (ItemStack item : items) {
                if (item == null) {
                    buf.writeBoolean(false);
                } else {
                    buf.writeBoolean(true);
                    NetworkTools.writeItemStack(buf, item);
                }
            }
        } else {
            buf.writeByte(0);
        }
    }

    public PacketCrafter() {
    }

    public PacketCrafter(BlockPos pos, int recipeIndex, InventoryCrafting inv, ItemStack result, boolean keepOne, boolean craftInternal) {
        this.pos = pos;
        this.recipeIndex = recipeIndex;
        this.items = new ItemStack[10];
        if (inv != null) {
            for (int i = 0 ; i < 9 ; i++) {
                items[i] = inv.getStackInSlot(i);
            }
        }
        items[9] = result;
        this.keepOne = keepOne;
        this.craftInternal = craftInternal;
    }

    @Override
    public IMessage onMessage(PacketCrafter message, MessageContext ctx) {
        TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
        if(!(te instanceof CrafterBaseTE)) {
            // @Todo better logging
            System.out.println("createPowerMonitotPacket: TileEntity is not a CrafterBlockTileEntity!");
            return null;
        }
        CrafterBaseTE crafterBlockTileEntity = (CrafterBaseTE) te;
        if (message.recipeIndex != -1) {
            CraftingRecipe recipe = crafterBlockTileEntity.getRecipe(message.recipeIndex);
            recipe.setRecipe(message.items, message.items[9]);
            recipe.setKeepOne(message.keepOne);
            recipe.setCraftInternal(message.craftInternal);
            crafterBlockTileEntity.markDirtyClient();

        }
        return null;
    }

}