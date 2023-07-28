package com.technicianlp.chestgrate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
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
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class BlockChestGrate extends Block {

	private final Random random = new Random();

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
	public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
		IInventory tile = (IInventory) world.getTileEntity(x, y, z);
		if (tile != null) {
			for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1) {
				ItemStack itemstack = tile.getStackInSlot(i1);
				if (itemstack != null) {
					float xOff = this.random.nextFloat() * 0.8F + 0.1F;
					float yOff = this.random.nextFloat() * 0.8F + 0.1F;
					float zOff = this.random.nextFloat() * 0.8F + 0.1F;

					EntityItem entityitem = new EntityItem(world, (float) x + xOff, (float) y + yOff, (float) z + zOff, itemstack);

					entityitem.motionX = (float) this.random.nextGaussian() * 0.05f;
					entityitem.motionY = (float) this.random.nextGaussian() * 0.05f + 0.2F;
					entityitem.motionZ = (float) this.random.nextGaussian() * 0.05f;
					world.spawnEntityInWorld(entityitem);
				}
			}
		}
		super.breakBlock(world, x, y, z, blockBroken, meta);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			((TileChestGrate) worldIn.getTileEntity(x, y, z)).customName = stack.getDisplayName();
		}
		super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
	}

	private boolean validPosition(World world, int x, int y, int z) {
		Block below = world.getBlock(x, y - 1, z);
		int meta = world.getBlockMetadata(x, y - 1, z);
		return (below == ConfigBlocks.blockAlchemyFurnace && meta == 0);
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
