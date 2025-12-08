// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests;

import dev.invalid.stone_x_iron_chests.chest.ModChestBlockEntity;
import dev.invalid.stone_x_iron_chests.chest.NewStoneChestBlock;
import ftblag.stonechest.blocks.EnumStoneChest;

import java.util.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.invalid.stone_x_iron_chests.StoneXIronChests.CHEST_PREFIX;

@SuppressWarnings("unchecked")
public class ModRegistry {
    public static final DeferredRegister<Block> BLOCKS;
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES;
    public static final DeferredRegister<Item> ITEMS;
    public static DeferredHolder<Block, NewStoneChestBlock>[] stoneChests;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ModChestBlockEntity>> STONE_CHEST_ENTITY;

    public static void register(IEventBus modEventBus) {

        for(EnumStoneChest type : EnumStoneChest.VALUES) {
            String name = CHEST_PREFIX + "_" + type.name().toLowerCase(Locale.ENGLISH);
            DeferredHolder<Block, NewStoneChestBlock> chestObject = BLOCKS.register(name, () ->
                    new NewStoneChestBlock(type, () -> STONE_CHEST_ENTITY.get()));
            stoneChests[type.ordinal()] = chestObject;
            ITEMS.register(name, () -> new BlockItem(chestObject.get(), new Item.Properties()));
        }

        // Entity type
        STONE_CHEST_ENTITY = TILE_ENTITIES.register("stone_chest", () ->
                BlockEntityType.Builder.of(ModChestBlockEntity::new,
                                Arrays.stream(stoneChests).map(DeferredHolder::get).toArray(Block[]::new))
                        .build(null));

        BLOCKS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    static {
        BLOCKS = DeferredRegister.create(Registries.BLOCK, StoneXIronChests.MODID);
        TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, StoneXIronChests.MODID);
        ITEMS = DeferredRegister.create(Registries.ITEM, StoneXIronChests.MODID);
        stoneChests = new DeferredHolder[EnumStoneChest.VALUES.length];
    }
}