// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, StoneXIronChests.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (EnumStoneChest type : EnumStoneChest.VALUES) {
            Block chestBlock = ModRegistry.stoneChests[type.ordinal()].get();
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(chestBlock);
            this.tag(BlockTags.GUARDED_BY_PIGLINS).add(chestBlock);
            this.tag(BlockTags.FEATURES_CANNOT_REPLACE).add(chestBlock);
            this.tag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE).add(chestBlock);
        }
    }
}
