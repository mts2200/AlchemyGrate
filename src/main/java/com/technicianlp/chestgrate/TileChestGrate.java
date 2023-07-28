package com.technicianlp.chestgrate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;

public class TileChestGrate extends TileThaumcraft implements IInventory {
	public static final String TAG_ITEMS = "Items";
	public static final String TAG_SLOT = "Slot";
	public static final String TAG_CUSTOM_NAME = "CustomName";

	private final ItemStack[] contents = new ItemStack[5];
	public String customName = null;

	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote)
			return;
		TileEntity below = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
		if (below instanceof TileAlchemyFurnaceAdvanced) {
			TileAlchemyFurnaceAdvanced furnace = (TileAlchemyFurnaceAdvanced) below;
			for (int i = 0; i < this.contents.length; i++) {
				if (this.contents[i] == null)
					continue;
				if (furnace.process(this.contents[i])) {
					this.decrStackSize(i, 1);
					this.worldObj.playSoundEffect(this.xCoord, this.yCoord - 1, this.zCoord, "thaumcraft:bubble", 0.2F, 1.0F + this.worldObj.rand.nextFloat() * 0.4F);
					break;
				}
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index < 0 || index >= this.getSizeInventory())
			return null;
		return this.contents[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.contents[index] != null) {
			ItemStack itemstack;
			if (this.contents[index].stackSize <= count) {
				itemstack = this.contents[index];
				this.contents[index] = null;
			} else {
				itemstack = this.contents[index].splitStack(count);
				if (this.contents[index].stackSize <= 0) {
					this.contents[index] = null;
				}
			}
			this.markDirty();
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack stack = this.getStackInSlot(index);
		this.setInventorySlotContents(index, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index < 0 || index >= this.getSizeInventory())
			return;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
			stack.stackSize = this.getInventoryStackLimit();

		if (stack != null && stack.stackSize <= 0)
			stack = null;

		this.contents[index] = stack;
		this.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (stack == null) {
			return false;
		}

		AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
		al = ThaumcraftCraftingManager.getBonusTags(stack, al);
		return al.size() != 0;
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container.alchgrate";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.isEntityAlive() && player.worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(this.xCoord, this.yCoord, this.zCoord) <= 64;
	}

	@Override
	public void openInventory() {
		// NO-OP
	}

	@Override
	public void closeInventory() {
		// NO-OP
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		super.writeCustomNBT(nbt);

		NBTTagList list = new NBTTagList();
		for (int i = 0, size = getSizeInventory(); i < size; ++i) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte(TAG_SLOT, (byte) i);
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}
		nbt.setTag(TAG_ITEMS, list);

		if (this.hasCustomInventoryName()) {
			nbt.setString(TAG_CUSTOM_NAME, this.customName);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		super.readCustomNBT(nbt);

		NBTTagList list = nbt.getTagList(TAG_ITEMS, Constants.NBT.TAG_COMPOUND);
		for (int i = 0, count = list.tagCount(); i < count; ++i) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte(TAG_SLOT) & 255;
			this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}

		if (nbt.hasKey(TAG_CUSTOM_NAME, Constants.NBT.TAG_STRING)) {
			this.customName = nbt.getString(TAG_CUSTOM_NAME);
		}
	}
}
