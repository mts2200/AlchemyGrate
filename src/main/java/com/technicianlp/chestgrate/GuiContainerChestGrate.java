package com.technicianlp.chestgrate;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiContainerChestGrate extends GuiContainer {

	private static final ResourceLocation background = new ResourceLocation("textures/gui/container/hopper.png");
	private final InventoryPlayer player;
	private final TileChestGrate tile;

	public GuiContainerChestGrate(InventoryPlayer player, TileChestGrate tile) {
		super(new ContainerChestGrate(player, tile));
		this.player = player;
		this.tile = tile;
		this.ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(this.tile.hasCustomInventoryName() ? this.tile.getInventoryName() : I18n.format(this.tile.getInventoryName()), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.player.hasCustomInventoryName() ? this.player.getInventoryName() : I18n.format(this.player.getInventoryName()), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}
