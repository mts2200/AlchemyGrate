package com.technicianlp.chestgrate;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	public static final int CONTAINER = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == CONTAINER)
			return new ContainerChestGrate(player.inventory, (TileChestGrate) world.getTileEntity(x, y, z));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == CONTAINER)
			return new GuiContainerChestGrate(player.inventory, (TileChestGrate) world.getTileEntity(x, y, z));
		return null;
	}
}
