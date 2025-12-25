// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

// Must specify Bus.MOD to receive GatherDataEvent
@EventBusSubscriber(modid = StoneXIronChests.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        // Unified Registration: Register everything here to use runClientData for all tasks [1]

        // 1. Client Data
        event.createProvider(ModModelProvider::new); // Automatically provides PackOutput
        event.createProvider(output -> new ModLanguageProvider(output, "en_us"));

        // 2. Server Data (Recipes via Runner pattern)
        event.createProvider(ModRecipeProvider.Runner::new); // Standard runner helper

        // 3. Loot Tables
        event.createProvider(output -> new LootTableProvider(
                output,
                Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(
                        ModBlockLootTableProvider::new,
                        LootContextParamSets.BLOCK
                )),
                event.getLookupProvider() // Helper to get the registry future
        ));

        // 4. Tags (Using the specialized block and item tag helper)
        var blockTags = event.createProvider(output ->
                new ModBlockTagsProvider(output, event.getLookupProvider()));

        event.createProvider(output ->
                new ModItemTagsProvider(output, event.getLookupProvider(), blockTags.contentsGetter()));
    }
}