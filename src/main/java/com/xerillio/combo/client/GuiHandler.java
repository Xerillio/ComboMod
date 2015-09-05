package com.xerillio.combo.client;

import com.xerillio.combo.ComboMod;
import com.xerillio.combo.client.gui.inventory.GuiBlastFurnace;
import com.xerillio.combo.inventory.ContainerBlastFurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (tileEntity != null)
        {
            if (ID == ComboMod.GuiEnum.BLAST_FURNACE.ordinal())
            {
                return new ContainerBlastFurnace(player.inventory, (IInventory)tileEntity);
            }
        }
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (tileEntity != null)
        {
            if (ID == ComboMod.GuiEnum.BLAST_FURNACE.ordinal())
            {
                return new GuiBlastFurnace(player.inventory, (IInventory)tileEntity);
            }
        }
		return null;
	}
	
}
