package com.hbm.items.tool;

import java.util.List;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Level;

import com.hbm.config.GeneralConfig;
import com.hbm.interfaces.IBomb;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemLaserDetonator extends Item {

	public ItemLaserDetonator(String s) {
		this.setRegistryName(s);
		this.setUnlocalizedName(s);
		this.setCreativeTab(MainRegistry.controlTab);
		
		ModItems.ALL_ITEMS.add(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add("Aim & click to detonate!");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		RayTraceResult ray = Library.rayTrace(player, 500, 1);
		BlockPos pos = ray.getBlockPos();
		if(!world.isRemote)
		{
	    	if(world.getBlockState(pos).getBlock() instanceof IBomb) {
				if(pos.getX() > 1500 && pos.getY() > 1500 || pos.getX() < -1500 && pos.getZ() < 1500) {
					((IBomb) world.getBlockState(pos).getBlock()).explode(world, pos);

					if (GeneralConfig.enableExtendedLogging)
						MainRegistry.logger.log(Level.INFO, "[DET] Tried to detonate block at " + pos.getX() + " / " + pos.getY() + " / " + pos.getZ() + " by " + player.getDisplayName() + "!");

					player.sendMessage(new TextComponentTranslation("§2[Detonated]§r"));
					world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.techBleep, SoundCategory.AMBIENT, 1.0F, 1.0F);
				} else {
					if(world.isRemote) {
						player.sendMessage(new TextComponentString(TextFormatting.RED + "Coordinates in detonator can must be at least 1500 blocks from the center of the map."));
					}
				}
	    	} else {
	    		player.sendMessage(new TextComponentTranslation("§cTarget can not be detonated.§r"));
	        	world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.techBleep, SoundCategory.AMBIENT, 1.0F, 1.0F);
	    	}
		}
		return super.onItemRightClick(world, player, hand);
	}
}
