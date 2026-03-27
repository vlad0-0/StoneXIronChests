// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import com.progwml6.ironchest.common.block.IronChestsBlocks;
import dev.invalid.stone_x_iron_chests.ModRegistry;
import ftblag.stonechest.SCRegistry;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static dev.invalid.stone_x_iron_chests.StoneXIronChests.MODID;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    @Override
    protected void buildRecipes() {
        HolderGetter<Item> itemGetter = this.registries.lookupOrThrow(Registries.ITEM);

        //new stone chests recipes
        for (EnumStoneChest chestType : EnumStoneChest.VALUES) {
            String chestTypeString = chestType.name().toLowerCase();
            ItemLike originalChestItem = SCRegistry.chests[chestType.ordinal()].get().asItem();
            ItemLike chestItem = ModRegistry.stoneChests[chestType.ordinal()].get().asItem();

            String materialName = getMaterialName(chestTypeString);
            ItemLike material = getItemFromRegistry("minecraft", materialName);

            ShapelessRecipeBuilder.shapeless(itemGetter, RecipeCategory.MISC, chestItem)
                    .requires(originalChestItem)
                    .unlockedBy("has_ingredient", has(originalChestItem))
                    .save(output, recipeKey("stone_chests/conversion/chest_" + chestTypeString));

            ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, chestItem)
                    .pattern("MPM")
                    .pattern("PCP")
                    .pattern("MPM")
                    .define('M', material)
                    .define('P', ItemTags.PLANKS)
                    .define('C', Tags.Items.CHESTS_WOODEN)
                    .unlockedBy("has_ingredient", has(material))
                    .save(output, recipeKey("stone_chests/planks/chest_" + chestTypeString));

            ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, chestItem)
                    .pattern("MGM")
                    .pattern("GCG")
                    .pattern("MGM")
                    .define('M', material)
                    .define('G', Tags.Items.GLASS_BLOCKS)
                    .define('C', Tags.Items.CHESTS_WOODEN)
                    .unlockedBy("has_ingredient", has(material))
                    .save(output, recipeKey("stone_chests/glass/chest_" + chestTypeString));

            StonecuttingRecipe(output,
                    RecipeCategory.BUILDING_BLOCKS,
                    chestItem,
                    material,
                    4);

            if (material == Items.COBBLESTONE ||
                    material == Items.COBBLED_DEEPSLATE ||
                    material == Items.BLACKSTONE) { continue; }

            ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, chestItem)
                    .pattern("###")
                    .pattern("# #")
                    .pattern("###")
                    .define('#', material)
                    .unlockedBy("has_ingredient", has(material))
                    .save(output, recipeKey("stone_chests/fast/chest_" + chestTypeString));
        }

        //new copper chest recipes
        ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, IronChestsBlocks.COPPER_CHEST)
                .pattern("MPM")
                .pattern("PCP")
                .pattern("MPM")
                .define('M', Tags.Items.INGOTS_COPPER)
                .define('P', ItemTags.PLANKS)
                .define('C', ModItemTagsProvider.STONE_CHESTS)
                .unlockedBy("has_ingredient", has(Tags.Items.INGOTS_COPPER))
                .save(output, recipeKey("iron_chests/copper_chest_planks"));

        ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, IronChestsBlocks.COPPER_CHEST)
                .pattern("MGM")
                .pattern("GCG")
                .pattern("MGM")
                .define('M', Tags.Items.INGOTS_COPPER)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('C', ModItemTagsProvider.STONE_CHESTS)
                .unlockedBy("has_ingredient", has(Tags.Items.INGOTS_COPPER))
                .save(output, recipeKey("iron_chests/copper_chest_glass"));

        //new other chests recipes
        ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, IronChestsBlocks.IRON_CHEST)
                .pattern("MPM")
                .pattern("PCP")
                .pattern("MPM")
                .define('M', Tags.Items.INGOTS_IRON)
                .define('P', ItemTags.PLANKS)
                .define('C', IronChestsBlocks.COPPER_CHEST)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output, recipeKey("iron_chests/iron_chest_planks"));

        ShapedRecipeBuilder.shaped(itemGetter, RecipeCategory.MISC, IronChestsBlocks.DIAMOND_CHEST)
                .pattern("PPP")
                .pattern("MCM")
                .pattern("PPP")
                .define('M', Tags.Items.GEMS_DIAMOND)
                .define('P', ItemTags.PLANKS)
                .define('C', IronChestsBlocks.GOLD_CHEST)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output, recipeKey("iron_chests/diamond_chest_planks"));

        //dirt chest disassembly
        ShapelessRecipeBuilder.shapeless(itemGetter, RecipeCategory.MISC, Items.DIRT, 8)
                .requires(IronChestsBlocks.DIRT_CHEST)
                .unlockedBy("has_ingredient", has(IronChestsBlocks.DIRT_CHEST))
                .save(output, recipeKey("iron_chests/dirt_chest_disassembly"));

        ShapelessRecipeBuilder.shapeless(itemGetter, RecipeCategory.MISC, Items.DIRT, 8)
                .requires(IronChestsBlocks.TRAPPED_DIRT_CHEST)
                .unlockedBy("has_ingredient", has(IronChestsBlocks.TRAPPED_DIRT_CHEST))
                .save(output, recipeKey("iron_chests/trapped_dirt_chest_disassembly"));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries, @NotNull RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public @NotNull String getName() {
            return "Stone X Iron Chests Recipes";
        }
    }

    protected void StonecuttingRecipe(RecipeOutput output, RecipeCategory category,
                                      ItemLike ingredient, ItemLike result, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), category, result, count)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(output, getConversionItemToItemRecipeName(ingredient, result) + "_stonecutting");
    }

    protected String getConversionItemToItemRecipeName(ItemLike ingredient, ItemLike result) {
        String recipeName = getItemName(ingredient);
        return recipeName + "_to_" + getItemName(result);
    }

    private String getMaterialName(String chestTypeString) {
        return switch (chestTypeString) {
            case "dripstone" -> "dripstone_block";
            default -> chestTypeString;
        };
    }

    private Item getItemFromRegistry(String namespace, String itemId) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, itemId);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, location);
        return this.registries.lookupOrThrow(Registries.ITEM)
                .getOrThrow(key)
                .value();
    }

    private static ResourceKey<Recipe<?>> recipeKey(String name) {
        return ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}