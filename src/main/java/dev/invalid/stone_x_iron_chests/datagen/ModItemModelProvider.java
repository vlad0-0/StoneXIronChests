// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static dev.invalid.stone_x_iron_chests.StoneXIronChests.CHEST_PREFIX;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, StoneXIronChests.MODID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        for (EnumStoneChest type : EnumStoneChest.VALUES) {
            String chestName = CHEST_PREFIX + "_" + type.name().toLowerCase();

            withExistingParent(chestName, mcLoc("item/chest"));
        }
    }
}