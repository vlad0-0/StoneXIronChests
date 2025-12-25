// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static dev.invalid.stone_x_iron_chests.StoneXIronChests.CHEST_PREFIX;
import static dev.invalid.stone_x_iron_chests.StoneXIronChests.MODID;

public class ModItemTagsProvider extends ItemTagsProvider {

    public static final TagKey<Item> STONE_CHESTS = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(MODID, "stone_chest")
    );

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // Добавляем все каменные сундуки в тег через цикл по Enum
        for (EnumStoneChest chestType : EnumStoneChest.VALUES) {
            String chestId = CHEST_PREFIX + "_" + chestType.name().toLowerCase();
            this.tag(STONE_CHESTS).addOptional(
                    ResourceLocation.fromNamespaceAndPath(MODID, chestId)
            );
        }
    }
}
