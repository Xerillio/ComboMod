package com.xerillio.combo.proxy;

import com.xerillio.combo.ComboMod;
import com.xerillio.combo.client.GuiHandler;
import com.xerillio.combo.init.ComboBlocks;
import com.xerillio.combo.init.ComboItems;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		ComboBlocks.createBlocks();
		ComboBlocks.register();
		ComboItems.createItems();
		ComboItems.register();
	}
	
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ComboMod.instance, new GuiHandler());
	}
	
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
}
