package dev.invalid.stone_x_iron_chests.client;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.chest.ModChestBlock;
import dev.invalid.stone_x_iron_chests.chest.ModChestBlockEntity;
import dev.invalid.stone_x_iron_chests.chest.NewStoneChestBlock;
import ftblag.stonechest.StoneChest;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class ClientRenderData {
    private final NewStoneChestBlock chestBlock;
    private final Material cachedMaterial;
    private ModChestBlockEntity tile = null;

    public ClientRenderData(EnumStoneChest chestType) {
        this.cachedMaterial = getMaterialForChest(chestType);
        this.chestBlock = ModRegistry.stoneChests[chestType.ordinal()].get();
    }

    public void updateLevel() {
        if (tile != null) {
            ClientLevel current = Minecraft.getInstance().level;
            if (tile.getLevel() != current && current != null) {
                tile.setLevel(current);
            }
        }
    }

    public void setTile() {
        tile = new ModChestBlockEntity(BlockPos.ZERO,
                this.chestBlock.defaultBlockState().setValue(ModChestBlock.FACING, Direction.SOUTH));
        if (Minecraft.getInstance().level != null) {
            tile.setLevel(Minecraft.getInstance().level);
        }
    }

    public ModChestBlockEntity getOrCreateTile() {
        if (tile == null) {
            setTile();
        }
        return tile;
    }

    private static Material getMaterialForChest(EnumStoneChest chestType) {
        String basePath = "entity/chest/" + chestType.name().toLowerCase(Locale.ENGLISH);
        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(StoneChest.MODID, basePath);
        return new Material(Sheets.CHEST_SHEET, textureLocation);
    }

    public Material getMaterial() {
        return this.cachedMaterial;
    }
}
