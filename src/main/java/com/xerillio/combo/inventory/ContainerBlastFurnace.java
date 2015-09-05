package com.xerillio.combo.inventory;

import com.xerillio.combo.tileentity.TileEntityBlastFurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBlastFurnace extends Container
{
	
	private final IInventory tileBlastFurnace;
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int cookTime;
    private int totalCookTime;
	
	public ContainerBlastFurnace(InventoryPlayer invPlayer, IInventory invFurnace)
    {
        this.tileBlastFurnace = invFurnace;
        // TODO custom slots
        this.addSlotToContainer(new Slot(invFurnace, 0, 56, 17));
        this.addSlotToContainer(new SlotBlastFurnaceFuel(invFurnace, 1, 56, 53));
        this.addSlotToContainer(new SlotBlastFurnaceOutput(invPlayer.player, invFurnace, 2, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }
	
	@Override
	public void addCraftingToCrafters(ICrafting listener)
    {
        super.addCraftingToCrafters(listener);
        listener.func_175173_a(this, this.tileBlastFurnace);
    }
	
	@Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.cookTime != this.tileBlastFurnace.getField(2))
            {
                icrafting.sendProgressBarUpdate(this, 2, this.tileBlastFurnace.getField(2));
            }

            if (this.furnaceBurnTime != this.tileBlastFurnace.getField(0))
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileBlastFurnace.getField(0));
            }

            if (this.currentItemBurnTime != this.tileBlastFurnace.getField(1))
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileBlastFurnace.getField(1));
            }

            if (this.totalCookTime != this.tileBlastFurnace.getField(3))
            {
                icrafting.sendProgressBarUpdate(this, 3, this.tileBlastFurnace.getField(3));
            }
        }

        this.cookTime = this.tileBlastFurnace.getField(2);
        this.furnaceBurnTime = this.tileBlastFurnace.getField(0);
        this.currentItemBurnTime = this.tileBlastFurnace.getField(1);
        this.totalCookTime = this.tileBlastFurnace.getField(3);
    }

	@SideOnly(Side.CLIENT)
	@Override
    public void updateProgressBar(int id, int data)
    {
        this.tileBlastFurnace.setField(id, data);
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tileBlastFurnace.isUseableByPlayer(playerIn);
    }
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex)
    {
		final int cooking_slot = TileEntityBlastFurnace.slotEnum.COOKING_SLOT.ordinal();
		final int burning_slot = TileEntityBlastFurnace.slotEnum.BURNING_SLOT.ordinal();
		final int output_slot = TileEntityBlastFurnace.slotEnum.OUTPUT_SLOT.ordinal();
        final int blastFurnInvSize = this.tileBlastFurnace.getSizeInventory();
		
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == TileEntityBlastFurnace.slotEnum.OUTPUT_SLOT.ordinal())
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex != burning_slot && slotIndex != cooking_slot)
            {
                if (FurnaceRecipes.instance().getSmeltingResult(itemstack1) != null)
                {
                    if (!this.mergeItemStack(itemstack1, cooking_slot, cooking_slot + 1, false))
                    {
                        return null;
                    }
                }
                else if (TileEntityBlastFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, burning_slot, burning_slot + 1, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= blastFurnInvSize && slotIndex < blastFurnInvSize + 27)
                {
                    if (!this.mergeItemStack(itemstack1, blastFurnInvSize + 27, blastFurnInvSize + 36, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= blastFurnInvSize + 27 && slotIndex < blastFurnInvSize + 36
                		&& !this.mergeItemStack(itemstack1, blastFurnInvSize, blastFurnInvSize + 27, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, blastFurnInvSize, blastFurnInvSize + 36, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

}
