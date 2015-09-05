package com.xerillio.combo.init;

import com.xerillio.combo.Reference;
import com.xerillio.combo.blocks.BlockBlastFurnace;
import com.xerillio.combo.blocks.BlockTest;
import com.xerillio.combo.tileentity.TileEntityBlastFurnace;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ComboBlocks
{
	
	public static Block test_block;
	public static Block blast_furnace;
	public static Block blast_furnace_lit;
	
	public static void createBlocks()
	{
		test_block = new BlockTest();
		blast_furnace = new BlockBlastFurnace(false);
		blast_furnace_lit = new BlockBlastFurnace(true);
	}
	
	public static void register()
	{
		GameRegistry.registerBlock(test_block, test_block.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(blast_furnace, blast_furnace.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(blast_furnace_lit, blast_furnace_lit.getUnlocalizedName().substring(5));
		
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, "tile_entity_blast_furnace");
	}
	
	public static void registerRenders()
	{
		registerRender(test_block);
		registerRender(blast_furnace);
		registerRender(blast_furnace_lit);
	}
	
	public static void registerRender(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

}
