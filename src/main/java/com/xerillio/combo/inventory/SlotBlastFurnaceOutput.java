package com.xerillio.combo.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotBlastFurnaceOutput extends Slot
{
	private EntityPlayer thePlayer;
	private int sizeOutput;

	public SlotBlastFurnaceOutput(EntityPlayer thePlayer, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
	{
		super(inventoryIn, slotIndex, xPosition, yPosition);
		this.thePlayer = thePlayer;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
        return false;
    }
	
	@Override
	public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.sizeOutput += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }
	
	@Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        this.onCrafting(stack);
        super.onPickupFromSlot(playerIn, stack);
    }
	
	@Override
	protected void onCrafting(ItemStack stack, int amount)
    {
        this.sizeOutput += amount;
        this.onCrafting(stack);
    }
	
	@Override
	protected void onCrafting(ItemStack stack)
    {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.sizeOutput);

        if (!this.thePlayer.worldObj.isRemote)
        {
            int expEarned = this.sizeOutput;
            float expMul = FurnaceRecipes.instance().getSmeltingExperience(stack);
            int j;

            if (expMul == 0.0F)
            {
                expEarned = 0;
            }
            else if (expMul < 1.0F)
            {
                j = MathHelper.floor_float((float)expEarned * expMul);

                if (j < MathHelper.ceiling_float_int((float)expEarned * expMul) && Math.random() < (double)((float)expEarned * expMul - (float)j))
                {
                    ++j;
                }

                expEarned = j;
            }

            while (expEarned > 0)
            {
                j = EntityXPOrb.getXPSplit(expEarned);
                expEarned -= j;
                this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, j));
            }
        }

        this.sizeOutput = 0;

        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerSmeltedEvent(thePlayer, stack);

        if (stack.getItem() == Items.iron_ingot)
        {
            this.thePlayer.triggerAchievement(AchievementList.acquireIron);
        }

        if (stack.getItem() == Items.cooked_fish)
        {
            this.thePlayer.triggerAchievement(AchievementList.cookFish);
        }
    }

}
