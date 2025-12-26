// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import com.progwml6.ironchest.common.block.IronChestsBlocks;
import dev.invalid.stone_x_iron_chests.ModRegistry;
import ftblag.stonechest.SCRegistry;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static dev.invalid.stone_x_iron_chests.StoneXIronChests.MODID;

public class ModRecipeProvider extends RecipeProvider {
    private static ResourceLocation location(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        //new stone chests recipes
        for (EnumStoneChest chestType : EnumStoneChest.VALUES) {
            String chestTypeString = chestType.name().toLowerCase();
            ItemLike originalChestItem = SCRegistry.chests[chestType.ordinal()].get().asItem();
            ItemLike chestItem = ModRegistry.stoneChests[chestType.ordinal()].get().asItem();

            String materialName = getMaterialName(chestTypeString);
            ItemLike material = getItemFromRegistry("minecraft", materialName);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, chestItem)
                    .requires(originalChestItem)
                    .unlockedBy("has_ingredient", has(originalChestItem))
                    .save(output, location("stone_chests/conversion/chest_" + chestTypeString));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chestItem)
                    .pattern("###")
                    .pattern("# #")
                    .pattern("###")
                    .define('#', Ingredient.of(material))
                    .unlockedBy("has_ingredient", has(material))
                    .save(output, location("stone_chests/fast/chest_" + chestTypeString));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chestItem)
                    .pattern("MPM")
                    .pattern("PCP")
                    .pattern("MPM")
                    .define('M', Ingredient.of(material))
                    .define('P', Ingredient.of(ItemTags.PLANKS))
                    .define('C', Ingredient.of(Tags.Items.CHESTS_WOODEN))
                    .unlockedBy("has_ingredient", has(material))
                    .save(output, location("stone_chests/planks/chest_" + chestTypeString));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chestItem)
                    .pattern("MGM")
                    .pattern("GCG")
                    .pattern("MGM")
                    .define('M', Ingredient.of(material))
                    .define('G', Ingredient.of(Tags.Items.GLASS_BLOCKS))
                    .define('C', Ingredient.of(Tags.Items.CHESTS_WOODEN))
                    .unlockedBy("has_ingredient", has(material))
                    .save(output, location("stone_chests/glass/chest_" + chestTypeString));
        }


        //new copper chest recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, IronChestsBlocks.COPPER_CHEST)
                .pattern("MPM")
                .pattern("PCP")
                .pattern("MPM")
                .define('M', Ingredient.of(Tags.Items.INGOTS_COPPER))
                .define('P', Ingredient.of(ItemTags.PLANKS))
                .define('C', Ingredient.of(ModItemTagsProvider.STONE_CHESTS))
                .unlockedBy("has_ingredient", has(Tags.Items.INGOTS_COPPER))
                .save(output, location("iron_chests/copper_chest_planks"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, IronChestsBlocks.COPPER_CHEST)
                .pattern("MGM")
                .pattern("GCG")
                .pattern("MGM")
                .define('M', Ingredient.of(Tags.Items.INGOTS_COPPER))
                .define('G', Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .define('C', Ingredient.of(ModItemTagsProvider.STONE_CHESTS))
                .unlockedBy("has_ingredient", has(Tags.Items.INGOTS_COPPER))
                .save(output, location("iron_chests/copper_chest_glass"));

        //new other chests recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, IronChestsBlocks.IRON_CHEST)
                .pattern("MPM")
                .pattern("PCP")
                .pattern("MPM")
                .define('M', Tags.Items.INGOTS_IRON)
                .define('P', ItemTags.PLANKS)
                .define('C', IronChestsBlocks.COPPER_CHEST)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output, location("iron_chests/iron_chest_planks"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, IronChestsBlocks.DIAMOND_CHEST)
                .pattern("PPP")
                .pattern("MCM")
                .pattern("PPP")
                .define('M', Tags.Items.GEMS_DIAMOND)
                .define('P', ItemTags.PLANKS)
                .define('C', IronChestsBlocks.GOLD_CHEST)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output, location("iron_chests/diamond_chest_planks"));

        //dirt chest disassembly
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.DIRT, 8)
                .requires(IronChestsBlocks.DIRT_CHEST)
                .unlockedBy("has_ingredient", has(IronChestsBlocks.DIRT_CHEST))
                .save(output, location("iron_chests/dirt_chest_disassembly"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.DIRT, 8)
                .requires(IronChestsBlocks.TRAPPED_DIRT_CHEST)
                .unlockedBy("has_ingredient", has(IronChestsBlocks.TRAPPED_DIRT_CHEST))
                .save(output, location("iron_chests/trapped_dirt_chest_disassembly"));
    }

    private String getMaterialName(String chestTypeString) {
        return switch (chestTypeString) {
            case "dripstone" -> "dripstone_block";
            default -> chestTypeString;
        };
    }

    private ItemLike getItemFromRegistry(String namespace, String itemId) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, itemId);
        return BuiltInRegistries.ITEM.get(resourceLocation);
    }
}
