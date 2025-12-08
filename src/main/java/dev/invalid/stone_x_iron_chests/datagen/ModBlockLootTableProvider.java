// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        for (EnumStoneChest type : EnumStoneChest.VALUES) {
            dropSelf(ModRegistry.stoneChests[type.ordinal()].get());
        }
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModRegistry.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}