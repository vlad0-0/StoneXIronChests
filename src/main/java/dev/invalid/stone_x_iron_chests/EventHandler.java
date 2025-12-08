// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests;

import dev.invalid.stone_x_iron_chests.chest.ModChestItemRenderer;
import dev.invalid.stone_x_iron_chests.chest.ModChestRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@EventBusSubscriber(
        modid = StoneXIronChests.MODID,
        value = {Dist.CLIENT}
)
public class EventHandler {
    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(ModRegistry.STONE_CHEST_ENTITY.get(), ModChestRenderer::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions extensions = new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ModChestItemRenderer.INSTANCE;
            }
        };

        Item[] chestItems = Arrays.stream(ModRegistry.stoneChests)
                .map(DeferredHolder::get)
                .map(Block::asItem)
                .toArray(Item[]::new);

        event.registerItem(extensions, chestItems);
    }
}