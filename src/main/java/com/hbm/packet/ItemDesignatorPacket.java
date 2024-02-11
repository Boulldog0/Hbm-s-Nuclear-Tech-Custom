package com.hbm.packet;

import com.hbm.items.ModItems;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ItemDesignatorPacket implements IMessage {

	//0: Add
	//1: Subtract
	//2: Set
	int x;
	int z;

	public ItemDesignatorPacket(){
	}

	public ItemDesignatorPacket(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.z = buf.readInt();

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<ItemDesignatorPacket, IMessage> {
		
		@Override
		public IMessage onMessage(ItemDesignatorPacket m, MessageContext ctx) {
			ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
				EntityPlayer p = ctx.getServerHandler().player;
				
				ItemStack stack = p.getHeldItem(EnumHand.MAIN_HAND);
				
				if(stack == null || stack.getItem() != ModItems.designator_manual) {
					stack = p.getHeldItem(EnumHand.OFF_HAND);
					if(stack == null || stack.getItem() != ModItems.designator_manual)
						return;
				}
				if(!stack.hasTagCompound() && m.x > 1500 && m.z > 1500 || m.x < -1500 && m.z < -1500)
					stack.setTagCompound(new NBTTagCompound());
				if(m.x > 1500 && m.z > 1500 || m.x < -1500 && m.z < -1500) {
					stack.getTagCompound().setInteger("xCoord", m.x);
					stack.getTagCompound().setInteger("zCoord", m.z);
				} else {
					if(p.world.isRemote) {
						p.sendMessage(new TextComponentString(TextFormatting.RED + "The coordinates must be at least 1500 blocks from the center of the map."));
					}
				}
			});
			return null;
		}
	}
}

