package com.hbm.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.hbm.forgefluid.FFUtils;
import com.hbm.main.ResourceManager;
import com.hbm.render.RenderHelper;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.render.misc.BeamPronter;
import com.hbm.render.misc.BeamPronter.EnumBeamType;
import com.hbm.render.misc.BeamPronter.EnumWaveType;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCoreEmitter;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.hbm.tileentity.machine.TileEntityCoreStabilizer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderCoreComponent extends TileEntitySpecialRenderer<TileEntityMachineBase> {

	@Override
	public boolean isGlobalRenderer(TileEntityMachineBase te) {
		return true;
	}
	
	@Override
	public void render(TileEntityMachineBase tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        
        GL11.glRotatef(90, 0F, 1F, 0F);
        
		switch(tileEntity.getBlockMetadata()) {
		case 0:
	        GL11.glTranslated(0.0D, 0.5D, -0.5D);
			GL11.glRotatef(90, 1F, 0F, 0F); break;
		case 1:
	        GL11.glTranslated(0.0D, 0.5D, 0.5D);
			GL11.glRotatef(90, -1F, 0F, 0F); break;
		case 2:
			GL11.glRotatef(90, 0F, 1F, 0F); break;
		case 4:
			GL11.glRotatef(180, 0F, 1F, 0F); break;
		case 3:
			GL11.glRotatef(270, 0F, 1F, 0F); break;
		case 5:
			GL11.glRotatef(0, 0F, 1F, 0F); break;
		}
		
        GL11.glTranslated(0.0D, 0D, 0.0D);

        if(tileEntity instanceof TileEntityCoreEmitter) {
	        bindTexture(ResourceManager.dfc_emitter_tex);
	        ResourceManager.dfc_emitter.renderAll();
	        GL11.glTranslated(0, 0.5, 0);
	        int range = ((TileEntityCoreEmitter)tileEntity).beam;
	        
	        if(range > 0) {
		        BeamPronter.prontBeam(Vec3.createVectorHelper(0, 0, range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0x404000, 0x404000, 0, 1, 0F, 2, 0.0625F);
		        BeamPronter.prontBeam(Vec3.createVectorHelper(0, 0, range), EnumWaveType.RANDOM, EnumBeamType.SOLID, 0x401500, 0x401500, (int)tileEntity.getWorld().getTotalWorldTime() % 1000, range * 2, 0.125F, 4, 0.0625F);
		        BeamPronter.prontBeam(Vec3.createVectorHelper(0, 0, range), EnumWaveType.RANDOM, EnumBeamType.SOLID, 0x401500, 0x401500, (int)tileEntity.getWorld().getTotalWorldTime() % 1000 + 1, range * 2, 0.125F, 4, 0.0625F);
	        }
        }

        if(tileEntity instanceof TileEntityCoreReceiver) {
	        bindTexture(ResourceManager.dfc_receiver_tex);
	        ResourceManager.dfc_receiver.renderAll();
        }

        if(tileEntity instanceof TileEntityCoreInjector) {
	        bindTexture(ResourceManager.dfc_injector_tex);
	        ResourceManager.dfc_injector.renderAll();
	        
	        GL11.glTranslated(0, 0.5, 0);
	        TileEntityCoreInjector injector = (TileEntityCoreInjector)tileEntity;
	        int range = injector.beam;
	        
	        if(range > 0) {
	        	RenderHelper.bindBlockTexture();
	        	if(injector.tanks[0].getFluidAmount() > 0)
	        		BeamPronter.prontBeamWithIcon(Vec3.createVectorHelper(0, 0, range), EnumWaveType.RANDOM, EnumBeamType.LINE, FFUtils.getTextureFromFluid(injector.tanks[0].getFluid().getFluid()), 0x808080, (int)tileEntity.getWorld().getTotalWorldTime() % 1000, range, 0.0625F, 0, 0);
	        	if(injector.tanks[1].getFluidAmount() > 0)
	        		BeamPronter.prontBeamWithIcon(Vec3.createVectorHelper(0, 0, range), EnumWaveType.RANDOM, EnumBeamType.LINE, FFUtils.getTextureFromFluid(injector.tanks[1].getFluid().getFluid()), 0x808080, (int)tileEntity.getWorld().getTotalWorldTime() % 1000 + 1, range, 0.0625F, 0, 0);
	        }
        }

        if(tileEntity instanceof TileEntityCoreStabilizer) {
	        bindTexture(ResourceManager.dfc_stabilizer_tex);
	        ResourceManager.dfc_injector.renderAll();

	        
	        GL11.glTranslated(0, 0.5, 0);
	        TileEntityCoreStabilizer stabilizer = (TileEntityCoreStabilizer)tileEntity;
	        int range = stabilizer.beam;

	        if(range > 0) {
	    		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 0, range), EnumWaveType.SPIRAL, EnumBeamType.LINE, 0xffa200, 0xffd000, (int)tileEntity.getWorld().getTotalWorldTime() * -25 % 360, range * 3, 0.125F, 0, 0);
	    		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 0, range), EnumWaveType.SPIRAL, EnumBeamType.LINE, 0xffa200, 0xffd000, (int)tileEntity.getWorld().getTotalWorldTime() * -15 % 360 + 180, range * 3, 0.125F, 0, 0);
	    		BeamPronter.prontBeam(Vec3.createVectorHelper(0, 0, range), EnumWaveType.SPIRAL, EnumBeamType.LINE, 0xffa200, 0xffd000, (int)tileEntity.getWorld().getTotalWorldTime() * -5 % 360 + 180, range * 3, 0.125F, 0, 0);
	        }
        }
        
        GlStateManager.enableLighting();

        GL11.glPopMatrix();
	}
}
