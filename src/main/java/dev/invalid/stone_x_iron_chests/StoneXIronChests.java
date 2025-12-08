// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests;

import com.mojang.logging.LogUtils;
import com.progwml6.ironchest.common.creativetabs.IronChestsCreativeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(StoneXIronChests.MODID)
public class StoneXIronChests {
    public static final String MODID = "stone_x_iron_chests";
    public static final String CHEST_PREFIX = "stone_chest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public StoneXIronChests(IEventBus modEventBus) {
        ModRegistry.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(IronChestsCreativeTabs.IRON_CHEST_TAP.getKey())) {
            ModRegistry.ITEMS.getEntries().forEach(entry -> {
                event.accept(entry.get());
            });
        }
    }
}
