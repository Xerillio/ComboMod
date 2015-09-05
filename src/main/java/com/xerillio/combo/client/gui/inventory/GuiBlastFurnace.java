package com.xerillio.combo.client.gui.inventory;

import com.xerillio.combo.Reference;
import com.xerillio.combo.inventory.ContainerBlastFurnace;
import com.xerillio.combo.tileentity.TileEntityBlastFurnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiBlastFurnace extends GuiContainer
{
	private static final ResourceLocation blastFurnaceGuiTextures =
			new ResourceLocation(Reference.MOD_ID + ":textures/gui/container/blast_furnace.png");
	private final InventoryPlayer invPlayer;
	private final IInventory tileFurnace;

	public GuiBlastFurnace(InventoryPlayer invPlayer, IInventory invFurnace)
	{
		super(new ContainerBlastFurnace(invPlayer, invFurnace));
		this.invPlayer = invPlayer;
		this.tileFurnace = invFurnace;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileFurnace.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.invPlayer.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(blastFurnaceGuiTextures);
        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
        int size;

        if (TileEntityBlastFurnace.isBurning(this.tileFurnace))
        {
            size = this.calculateBurnProgressBarHeight(13);
            this.drawTexturedModalRect(left + 56, top + 48 - size, 176, 12 - size, 14, size + 1);
        }

        size = this.calculateCookProgressBarWidth(24);
        this.drawTexturedModalRect(left + 79, top + 34, 176, 14, size + 1, 16);
    }
	
	private int calculateCookProgressBarWidth(int progressBarMaxWidth)
    {
        int cookTime = this.tileFurnace.getField(2);
        int totalCookTime = this.tileFurnace.getField(3);
        return totalCookTime != 0 && cookTime != 0 ? cookTime * progressBarMaxWidth / totalCookTime : 0;
    }
	
	private int calculateBurnProgressBarHeight(int progressBarMaxHeight)
    {
        int itemBurnTime = this.tileFurnace.getField(1);

        if (itemBurnTime == 0)
        {
            itemBurnTime = 200;
        }

        return this.tileFurnace.getField(0) * progressBarMaxHeight / itemBurnTime;
    }
	
}
