package com.hbm.blocks.generic;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.IItemHazard;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.hbm.modules.ItemHazardModule;
import com.hbm.saveddata.RadiationSavedData;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHazard extends Block implements IItemHazard {
	
	ItemHazardModule module;
	
	private float radIn = 0.0F;
	private float radMax = 0.0F;
	private ExtDisplayEffect extEffect = null;
	
	private boolean beaconable = false;

	public BlockHazard(String s) {
		this(Material.IRON, s);
	}
	
	public BlockHazard(SoundType type, String s) {
		this(Material.IRON, s);
		setSoundType(type);
	}

	public BlockHazard(Material mat, String s) {
		super(mat);
		this.setUnlocalizedName(s);
		this.setRegistryName(s);
		this.module = new ItemHazardModule();
		
		ModBlocks.ALL_BLOCKS.add(this);
	}
	
	public BlockHazard setDisplayEffect(ExtDisplayEffect extEffect) {
		this.extEffect = extEffect;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand){
		super.randomDisplayTick(stateIn, worldIn, pos, rand);
		if(extEffect == null)
			return;
		
		switch(extEffect) {
		case RADFOG:
		case SCHRAB:
		case FLAMES:
			sPart(worldIn, pos.getX(), pos.getY(), pos.getZ(), rand);
			break;
			
		case SPARKS:
			break;
			
		case LAVAPOP:
			worldIn.spawnParticle(EnumParticleTypes.LAVA, pos.getX() + rand.nextFloat(), pos.getY() + 1.1F, pos.getZ() + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			break;
			
		default: break;
		}
	}
	
	private void sPart(World world, int x, int y, int z, Random rand) {

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

			if(dir == ForgeDirection.DOWN && this.extEffect == ExtDisplayEffect.FLAMES)
				continue;

			if(world.getBlockState(new BlockPos(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)).getMaterial() == Material.AIR) {

				double ix = x + 0.5F + dir.offsetX + rand.nextDouble() * 3 - 1.5D;
				double iy = y + 0.5F + dir.offsetY + rand.nextDouble() * 3 - 1.5D;
				double iz = z + 0.5F + dir.offsetZ + rand.nextDouble() * 3 - 1.5D;

				if(dir.offsetX != 0)
					ix = x + 0.5F + dir.offsetX * 0.5 + rand.nextDouble() * dir.offsetX;
				if(dir.offsetY != 0)
					iy = y + 0.5F + dir.offsetY * 0.5 + rand.nextDouble() * dir.offsetY;
				if(dir.offsetZ != 0)
					iz = z + 0.5F + dir.offsetZ * 0.5 + rand.nextDouble() * dir.offsetZ;

				if(this.extEffect == ExtDisplayEffect.RADFOG) {
					world.spawnParticle(EnumParticleTypes.TOWN_AURA, ix, iy, iz, 0.0, 0.0, 0.0);
				}
				if(this.extEffect == ExtDisplayEffect.SCHRAB) {
					NBTTagCompound data = new NBTTagCompound();
					data.setString("type", "schrabfog");
					data.setDouble("posX", ix);
					data.setDouble("posY", iy);
					data.setDouble("posZ", iz);
					MainRegistry.proxy.effectNT(data);
				}
				if(this.extEffect == ExtDisplayEffect.FLAMES) {
					world.spawnParticle(EnumParticleTypes.FLAME, ix, iy, iz, 0.0, 0.0, 0.0);
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ix, iy, iz, 0.0, 0.0, 0.0);
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ix, iy, iz, 0.0, 0.1, 0.0);
				}
			}
		}
	}

	@Override
	public ItemHazardModule getModule() {
		return module;
	}

	@Override
	public IItemHazard addRadiation(float radiation) {
		this.getModule().addRadiation(radiation);
		this.radIn = radiation * 0.1F;
		this.radMax = radiation;
		return this;
	}

	public BlockHazard makeBeaconable() {
		this.beaconable = true;
		return this;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
		return beaconable;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		if(this.radIn > 0) {
			RadiationSavedData.incrementRad(worldIn, pos, radIn, Float.MAX_VALUE);
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
		}
	}
	
	@Override
	public int tickRate(World world) {
		if(this.radIn > 0)
			return 20;
		return super.tickRate(world);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state){
		super.onBlockAdded(worldIn, pos, state);
		if(this.radIn > 0)
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}
	
	public static enum ExtDisplayEffect {
		RADFOG,
		SPARKS,
		SCHRAB,
		FLAMES,
		LAVAPOP
	}
}