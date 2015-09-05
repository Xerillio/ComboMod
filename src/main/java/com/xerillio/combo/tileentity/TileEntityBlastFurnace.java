package com.xerillio.combo.tileentity;

import com.xerillio.combo.inventory.ContainerBlastFurnace;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBlastFurnace extends TileEntityLockable implements IUpdatePlayerListBox, ISidedInventory
{
	public enum slotEnum
	{
		COOKING_SLOT, BURNING_SLOT, OUTPUT_SLOT
	}
	
	private static final int[] slotsTop = new int[] {slotEnum.COOKING_SLOT.ordinal()};
    private static final int[] slotsBottom = new int[] {slotEnum.OUTPUT_SLOT.ordinal(), slotEnum.BURNING_SLOT.ordinal()};
    private static final int[] slotsSides = new int[] {slotEnum.BURNING_SLOT.ordinal()};
    /** The ItemStacks that hold the items currently being used in the furnace */
    private ItemStack[] furnaceItemStacks = new ItemStack[3];
    /** The number of ticks that the furnace will keep burning */
    private int furnaceBurnTime;
    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for */
    private int currentItemBurnTime;
    /** The number of ticks that the furnace has been cooking the item */
    private int cookTime;
    /** The number of ticks that a fresh copy of the currently-cooking item would would need to be cooked */
    private int totalCookTime;
    private String furnaceCustomName;
    
    @Override
    public int getSizeInventory()
    {
        return this.furnaceItemStacks.length;
    }
    
    @Override
    public ItemStack getStackInSlot(int index)
    {
        return this.furnaceItemStacks[index];
    }
    
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.furnaceItemStacks[index] != null)
        {
            ItemStack itemstack;

            if (this.furnaceItemStacks[index].stackSize <= count)
            {
                itemstack = this.furnaceItemStacks[index];
                this.furnaceItemStacks[index] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.furnaceItemStacks[index].splitStack(count);

                if (this.furnaceItemStacks[index].stackSize == 0)
                {
                    this.furnaceItemStacks[index] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (this.furnaceItemStacks[index] != null)
        {
            ItemStack itemstack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        boolean flag = stack != null && stack.isItemEqual(this.furnaceItemStacks[index]) && ItemStack.areItemStackTagsEqual(stack, this.furnaceItemStacks[index]);
        this.furnaceItemStacks[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == 0 && !flag)
        {
            this.totalCookTime = this.getItemCookTime(stack);
            this.cookTime = 0;
            this.markDirty();
        }
    }
    
    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.furnaceCustomName : "container.blast_furnace";
    }
    
    @Override
    public boolean hasCustomName()
    {
        return this.furnaceCustomName != null && this.furnaceCustomName.length() > 0;
    }
    
    public void setCustomInventoryName(String name)
    {
        this.furnaceCustomName = name;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.furnaceItemStacks.length)
            {
                this.furnaceItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.furnaceBurnTime = compound.getShort("BurnTime");
        this.cookTime = compound.getShort("CookTime");
        this.totalCookTime = compound.getShort("CookTimeTotal");
        this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[slotEnum.BURNING_SLOT.ordinal()]);

        if (compound.hasKey("CustomName", 8))
        {
            this.furnaceCustomName = compound.getString("CustomName");
        }
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setShort("BurnTime", (short)this.furnaceBurnTime);
        compound.setShort("CookTime", (short)this.cookTime);
        compound.setShort("CookTimeTotal", (short)this.totalCookTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.furnaceItemStacks.length; ++i)
        {
            if (this.furnaceItemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.furnaceCustomName);
        }
    }
    
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }
    
    public boolean isBurning()
    {
        return this.furnaceBurnTime > 0;
    }
    
    @SideOnly(Side.CLIENT)
    public static boolean isBurning(IInventory tileBlastFurnace)
    {
        return tileBlastFurnace.getField(0) > 0;
    }
    
    @Override
    public void update()
    {
        boolean wasBurning = this.isBurning();
        boolean stateHasChanged = false;
        final int burning_slot = slotEnum.BURNING_SLOT.ordinal();
        final int cooking_slot = slotEnum.COOKING_SLOT.ordinal();

        if (this.isBurning())
        {
            --this.furnaceBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (!this.isBurning() && (this.furnaceItemStacks[burning_slot] == null || this.furnaceItemStacks[cooking_slot] == null))
            {
                if (!this.isBurning() && this.cookTime > 0)
                {
                    this.cookTime = MathHelper.clamp_int(this.cookTime - 2, 0, this.totalCookTime);
                }
            }
            else
            {
                if (!this.isBurning() && this.canSmelt())
                {
                    this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[burning_slot]);

                    if (this.isBurning())
                    {
                        stateHasChanged = true;

                        if (this.furnaceItemStacks[burning_slot] != null)
                        {
                            --this.furnaceItemStacks[burning_slot].stackSize;

                            if (this.furnaceItemStacks[burning_slot].stackSize == 0)
                            {
                                this.furnaceItemStacks[burning_slot] = furnaceItemStacks[burning_slot].getItem().getContainerItem(furnaceItemStacks[burning_slot]);
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt())
                {
                    ++this.cookTime;

                    if (this.cookTime == this.totalCookTime)
                    {
                        this.cookTime = 0;
                        this.totalCookTime = this.getItemCookTime(this.furnaceItemStacks[cooking_slot]);
                        this.smeltItem();
                        stateHasChanged = true;
                    }
                }
                else
                {
                    this.cookTime = 0;
                }
            }

            if (wasBurning != this.isBurning())
            {
                stateHasChanged = true;
                BlockFurnace.setState(this.isBurning(), this.worldObj, this.pos);
            }
        }

        if (stateHasChanged)
        {
            this.markDirty();
        }
    }
    
    public int getItemCookTime(ItemStack stack)
    {
        return 200;
    }
    
    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt()
    {
        if (this.furnaceItemStacks[0] == null)
        {
            return false;
        }
        else
        {
            ItemStack resultStack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[slotEnum.COOKING_SLOT.ordinal()]);
            if (resultStack == null) return false;
            if (this.furnaceItemStacks[slotEnum.OUTPUT_SLOT.ordinal()] == null) return true;
            if (!this.furnaceItemStacks[slotEnum.OUTPUT_SLOT.ordinal()].isItemEqual(resultStack)) return false;
            int result = furnaceItemStacks[slotEnum.OUTPUT_SLOT.ordinal()].stackSize + resultStack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.furnaceItemStacks[slotEnum.OUTPUT_SLOT.ordinal()].getMaxStackSize();
        }
    }
    
    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
        	final int cooking_slot = slotEnum.COOKING_SLOT.ordinal();
        	final int burning_slot = slotEnum.BURNING_SLOT.ordinal();
        	final int output_slot = slotEnum.OUTPUT_SLOT.ordinal();
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[cooking_slot]);

            if (this.furnaceItemStacks[output_slot] == null)
            {
                this.furnaceItemStacks[output_slot] = itemstack.copy();
            }
            else if (this.furnaceItemStacks[output_slot].getItem() == itemstack.getItem())
            {
                this.furnaceItemStacks[output_slot].stackSize += itemstack.stackSize;
            }

            if (this.furnaceItemStacks[cooking_slot].getItem() == Item.getItemFromBlock(Blocks.sponge) && this.furnaceItemStacks[cooking_slot].getMetadata() == 1 && this.furnaceItemStacks[burning_slot] != null && this.furnaceItemStacks[burning_slot].getItem() == Items.bucket)
            {
                this.furnaceItemStacks[burning_slot] = new ItemStack(Items.water_bucket);
            }

            --this.furnaceItemStacks[cooking_slot].stackSize;

            if (this.furnaceItemStacks[cooking_slot].stackSize <= 0)
            {
                this.furnaceItemStacks[cooking_slot] = null;
            }
        }
    }
    
    /**
     * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't
     * fuel
     */
    public static int getItemBurnTime(ItemStack stack)
    {
        if (stack == null)
        {
            return 0;
        }
        else
        {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air)
            {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab)
                {
                    return 150;
                }

                if (block.getMaterial() == Material.wood)
                {
                    return 300;
                }

                if (block == Blocks.coal_block)
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(stack);
        }
    }
    
    public static boolean isItemFuel(ItemStack stack)
    {
        return getItemBurnTime(stack) > 0;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }
    
    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}
    
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot == slotEnum.OUTPUT_SLOT.ordinal() ? false
        		: (slot != slotEnum.BURNING_SLOT.ordinal() ? true : isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
    }
    
    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing direction)
    {
        return this.isItemValidForSlot(slot, itemStackIn);
    }
    
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction)
    {
        if (direction == EnumFacing.DOWN && slot == slotEnum.BURNING_SLOT.ordinal())
        {
            Item item = stack.getItem();

            if (item != Items.water_bucket && item != Items.bucket)
            {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String getGuiID()
    {
        return "cb:blast_furnace";
    }
    
    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerBlastFurnace(playerInventory, this);
    }
    
    @Override
    public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.furnaceBurnTime;
            case 1:
                return this.currentItemBurnTime;
            case 2:
                return this.cookTime;
            case 3:
                return this.totalCookTime;
            default:
                return 0;
        }
    }
    
    @Override
    public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.furnaceBurnTime = value;
                break;
            case 1:
                this.currentItemBurnTime = value;
                break;
            case 2:
                this.cookTime = value;
                break;
            case 3:
                this.totalCookTime = value;
        }
    }
    
    @Override
    public int getFieldCount()
    {
        return 4;
    }
    
    @Override
    public void clear()
    {
        for (int i = 0; i < this.furnaceItemStacks.length; ++i)
        {
            this.furnaceItemStacks[i] = null;
        }
    }

}
