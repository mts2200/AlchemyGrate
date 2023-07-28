package com.technicianlp.chestgrate;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

@Mod(modid = "alchgrate", name = "Alchemical Grate", version = "1.0", dependencies = "required-after:Thaumcraft")
public class Main {

	@Mod.Instance
	public static Main instance;

	public BlockChestGrate block;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.block = new BlockChestGrate();

		GameRegistry.registerBlock(this.block, "alchgrate");
		GameRegistry.registerTileEntity(TileChestGrate.class, "alchgrate.alchgrate");

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("ALCHGRATE", new ItemStack(this.block, 1),
				new AspectList().add(Aspect.ORDER, 50).add(Aspect.ENTROPY, 50),
				"VGV", "VSV", "VCV",
				'V', new ItemStack(ConfigItems.itemResource, 1, 16),
				'G', new ItemStack(ConfigBlocks.blockMetalDevice, 1, 5),
				'S', new ItemStack(ConfigItems.itemShard, 1, 6),
				'C', new ItemStack(ConfigBlocks.blockChestHungry));

		AspectList aspects = new AspectList().add(Aspect.VOID, 3).add(Aspect.AURA, 3).add(Aspect.MAGIC, 3).add(Aspect.ORDER, 3).add(Aspect.ENTROPY, 3);
		ResearchItem research = new ResearchItem("ALCHGRATE", "ARTIFICE", aspects, 3, -2, 0, new ItemStack(this.block, 1));
		ResearchPage[] pages = new ResearchPage[]{new ResearchPage("tc.research_page.ALCHGRATE.1"), new ResearchPage(recipe)};
		research.setParents("ADVALCHEMYFURNACE", "HUNGRYCHEST", "GRATE").setPages(pages).setConcealed().setRound().registerResearchItem();

	}

}
