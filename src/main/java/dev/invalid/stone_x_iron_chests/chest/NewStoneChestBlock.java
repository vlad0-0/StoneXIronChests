// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.client.ClientRenderData;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class NewStoneChestBlock extends ModChestBlock {
    @OnlyIn(Dist.CLIENT)
    public ClientRenderData clientRenderData;

    EnumStoneChest chestType;

    public NewStoneChestBlock(EnumStoneChest chestType, ResourceKey<Block> key) {
        super(Properties.of()
                        .strength(3.5F, 5.0F)
                        .sound(SoundType.STONE)
                        .setId(key)
                        .requiresCorrectToolForDrops(),
                () -> ModRegistry.STONE_CHEST_ENTITY.get());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
        this.chestType = chestType;
    }
}