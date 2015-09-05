package com.xerillio.combo.init;

import com.xerillio.combo.Reference;
import com.xerillio.combo.items.ItemTeleporter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ComboItems {
	
	public static ItemTeleporter item_teleporter;
	
	public static void createItems()
	{
		item_teleporter = new ItemTeleporter();
	}
	
	public static void register()
	{
		GameRegistry.registerItem(item_teleporter, item_teleporter.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders()
	{
		registerRender(item_teleporter);
	}
	
	public static void registerRender(Item item)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

}
