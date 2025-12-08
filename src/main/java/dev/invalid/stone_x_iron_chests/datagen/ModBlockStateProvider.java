// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import ftblag.stonechest.StoneChest;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Locale;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, StoneXIronChests.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (EnumStoneChest type : EnumStoneChest.VALUES) {
            String originalChestName = "chest_" + type.name().toLowerCase(Locale.ENGLISH);

            ModelFile modelFile = new ModelFile.UncheckedModelFile(
                    ResourceLocation.fromNamespaceAndPath(StoneChest.MODID, "block/" + originalChestName)
            );

            getVariantBuilder(ModRegistry.stoneChests[type.ordinal()].get())
                    .partialState()
                    .setModels(new ConfiguredModel(modelFile));
        }
    }
}