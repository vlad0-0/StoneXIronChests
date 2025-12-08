// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.function.Supplier;

public class NewStoneChestBlock extends ModChestBlock {
    private final EnumStoneChest chestType;

    public NewStoneChestBlock(EnumStoneChest chestType, Supplier<BlockEntityType<? extends ModChestBlockEntity>> blockEntityType) {
        super(Properties.of()
                        .strength(3.5F, 5.0F)
                        .sound(SoundType.STONE)
                        .requiresCorrectToolForDrops(),
                blockEntityType);
        this.chestType = chestType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    public EnumStoneChest getChestType() {
        return this.chestType;
    }
}