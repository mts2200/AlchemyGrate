package com.technicianlp.chestgrate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;

public class BlockChestGrate extends Block implements ITileEntityProvider {
	protected BlockChestGrate() {
		super(Material.iron);
		this.setHardness(3.0F);
		this.setResistance(17.0F);
		this.setStepSound(Block.soundTypeMetal);
		this.setCreativeTab(Thaumcraft.tabTC);
		this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setBlockName("alchgrate");
		this.setBlockTextureName("alchgrate:alchgrate");
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return super.renderAsNormalBlock();
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean func_149730_j() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
		return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
		return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		float f = (1 - 0.8125f) / 2;
		this.setBlockBounds(0.0F, 0.5F - f, 0.0F, 1.0F, 0.5F + f, 1.0F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (!world.isRemote) {
			player.openGui(Main.instance, GuiHandler.CONTAINER, world, x, y, z);
		}
		return true;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileChestGrate();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return createTileEntity(worldIn, meta);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!world.isRemote && tile instanceof TileChestGrate) {
			TileChestGrate chest = (TileChestGrate) tile;
			for (int i = 0, size = chest.getSizeInventory(); i < size; ++i) {
				ItemStack itemstack = chest.getStackInSlot(i);
				if (itemstack != null) {
					float xOff = world.rand.nextFloat() * 0.8F + 0.1F;
					float yOff = world.rand.nextFloat() * 0.8F + 0.1F;
					float zOff = world.rand.nextFloat() * 0.8F + 0.1F;

					EntityItem entityitem = new EntityItem(world, x + xOff, y + yOff, z + zOff, itemstack);
					entityitem.motionX = (float) world.rand.nextGaussian() * 0.05f;
					entityitem.motionY = (float) world.rand.nextGaussian() * 0.05f + 0.2F;
					entityitem.motionZ = (float) world.rand.nextGaussian() * 0.05f;

					if (world.spawnEntityInWorld(entityitem)) chest.setInventorySlotContents(i, null);
				}
			}
		}
		super.breakBlock(world, x, y, z, blockBroken, meta);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tile = worldIn.getTileEntity(x, y, z);
			if (tile instanceof TileChestGrate) {
				((TileChestGrate) tile).customName = stack.getDisplayName();
			}
		}
		super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
	}

	private boolean validPosition(World world, int x, int y, int z) {
		return world.getTileEntity(x, y - 1, z) instanceof TileAlchemyFurnaceAdvanced;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.validPosition(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (!this.validPosition(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}
}
