package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static dev.invalid.stone_x_iron_chests.StoneXIronChests.CHEST_PREFIX;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, StoneXIronChests.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        String chestsTier = "Stone Tier Chest";
        String chestsSharedKey = "block." + StoneXIronChests.MODID + "." + CHEST_PREFIX + "_";
        String chestTranslatable;
        add("container.stone_chest", chestsTier);
        for (EnumStoneChest chestType : EnumStoneChest.VALUES) {
            chestTranslatable = chestsSharedKey + chestType.name().toLowerCase();
            add(chestTranslatable, chestsTier + ": " + startsWithCapitals(chestType.name()));
        }
    }

    private String startsWithCapitals(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        String[] parts = name.split("_");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            String capitalized = part.substring(0, 1).toUpperCase() +
                    part.substring(1).toLowerCase();

            if (!result.isEmpty()) {
                result.append(" ");
            }
            result.append(capitalized);
        }

        return result.toString();
    }
}
