package com.technicianlp.chestgrate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerChestGrate extends Container {

	private final TileChestGrate tile;

	public ContainerChestGrate(InventoryPlayer player, TileChestGrate tile) {
		this.tile = tile;

		for (int i = 0; i < tile.getSizeInventory(); ++i) {
			this.addSlotToContainer(new Slot(tile, i, 44 + i * 18, 20));
		}

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, y * 18 + 51));
			}
		}

		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 58 + 51));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tile.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack current = slot.getStack();
			previous = current.copy();

			if (index < this.tile.getSizeInventory()) {
				if (!this.mergeItemStack(current, this.tile.getSizeInventory(), this.inventorySlots.size(), true)) {
					return null;
				}
			} else {
				if (!this.mergeItemStack(current, 0, this.tile.getSizeInventory(), false)) {
					return null;
				}
			}

			if (current.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (current.stackSize == previous.stackSize)
				return null;
			slot.onPickupFromSlot(player, current);
		}

		return previous;
	}
}
