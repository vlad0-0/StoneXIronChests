package dev.invalid.stone_x_iron_chests.datagen;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import dev.invalid.stone_x_iron_chests.chest.ModChestItemRenderer;
import ftblag.stonechest.SCRegistry;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput packOutput) {
        super(packOutput, StoneXIronChests.MODID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        for (EnumStoneChest type : EnumStoneChest.VALUES) {
            Block chestBlock = ModRegistry.stoneChests[type.ordinal()].get();
            Block originalChestBlock = SCRegistry.chests[type.ordinal()].get();
            ResourceLocation textureName = BuiltInRegistries.BLOCK.getKey(originalChestBlock);
            Block particleBlock = getParticleBlock(type);
            createChest(blockModels, chestBlock, particleBlock, textureName);
        }
    }

    public void createChest(BlockModelGenerators blockModels, Block chestBlock, Block particleBlock, ResourceLocation texture) {
        blockModels.createParticleOnlyBlock(chestBlock, particleBlock);
        Item item = chestBlock.asItem();
        ResourceLocation resourcelocation = ModelTemplates.CHEST_INVENTORY.create(item, TextureMapping.particle(particleBlock), blockModels.modelOutput);
        ItemModel.Unbaked itemModel = ItemModelUtils.specialModel(resourcelocation, new ModChestItemRenderer.Unbaked(texture));
        blockModels.itemModelOutput.accept(item, itemModel);
    }

    private Block getParticleBlock(EnumStoneChest type) {
        String chestName = type.name().toLowerCase(Locale.ENGLISH);
        String particleBlockName = switch (chestName) {
            case "dripstone" -> "dripstone_block";
            default -> chestName;
        };

        ResourceLocation particleKey = ResourceLocation.fromNamespaceAndPath("minecraft", particleBlockName);
        return BuiltInRegistries.BLOCK.get(particleKey)
                .map(Holder.Reference::value)
                .orElse(Blocks.AIR);
    }
}