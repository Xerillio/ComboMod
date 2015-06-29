package com.xerillio.combo.proxy;

import com.xerillio.combo.init.ComboItems;

public class ClientProxy extends CommonProxy{
	
	@Override
	public void registerRenders()
	{
		ComboItems.registerRenders();
	}

}
