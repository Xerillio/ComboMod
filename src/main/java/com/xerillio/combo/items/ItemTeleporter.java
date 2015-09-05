package com.xerillio.combo.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTeleporter extends Item
{

	public ItemTeleporter()
	{
		setUnlocalizedName("item_teleporter");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!playerIn.isSneaking())
		{
			if (!stack.hasTagCompound())
			{
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("posX", pos.getX());
			nbt.setInteger("posY", pos.getY());
			nbt.setInteger("posZ", pos.getZ());
			stack.getTagCompound().setTag("coords", nbt);
			stack.setStackDisplayName(EnumChatFormatting.AQUA + stack.getDisplayName());
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn)
	{
		if (stack.hasTagCompound())
		{
			if (playerIn.isSneaking())
			{
				stack.getTagCompound().removeTag("coords");
				stack.clearCustomName();
			}
			else if (stack.getTagCompound().hasKey("coords") && GuiScreen.isCtrlKeyDown() && !worldIn.isRemote && playerIn instanceof EntityPlayerMP)
			{
				EntityPlayerMP entityplayermp = (EntityPlayerMP) playerIn;
				if (entityplayermp.playerNetServerHandler.getNetworkManager().isChannelOpen() && !entityplayermp.isPlayerSleeping())
				{
					if (playerIn.isRiding())
					{
						playerIn.mountEntity((Entity) null);
					}
					
					NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("coords");
					BlockPos pos = new BlockPos(nbt.getInteger("posX"), nbt.getInteger("posY"), nbt.getInteger("posZ"));
					
					// Find first block below or this, that we can stand on
					while (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
					{
						pos = pos.down();
					}
					
					Block block = worldIn.getBlockState(pos).getBlock();
					
					playerIn.setPositionAndUpdate(pos.getX() + 0.5f, pos.getY() + worldIn.getBlockState(pos).getBlock().getBlockBoundsMaxY(), pos.getZ() + 0.5f); // Center on block
					playerIn.fallDistance = 0.0F;
				}
			}
		}
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("coords");
			if (nbt != null)
			{
				int posX = nbt.getInteger("posX");
				int posY = nbt.getInteger("posY");
				int posZ = nbt.getInteger("posZ");
				tooltip.add("X: " + posX);
				tooltip.add("Y: " + posY);
				tooltip.add("Z: " + posZ);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			return stack.getTagCompound().hasKey("coords");
		}
		return false;
	}

}
