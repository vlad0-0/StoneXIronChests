// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.invalid.stone_x_iron_chests.ModRegistry;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ModChestItemRenderer INSTANCE = new ModChestItemRenderer();
    private final Map<EnumStoneChest, ModChestBlockEntity> tiles = new HashMap<>();

    public ModChestItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private ModChestBlockEntity getOrCreateTile(EnumStoneChest type) {
        return tiles.computeIfAbsent(type, k -> {
            BlockState defaultState = ModRegistry.stoneChests[type.ordinal()].get()
                    .defaultBlockState();
            ModChestBlockEntity tile = new ModChestBlockEntity(BlockPos.ZERO, defaultState);
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                tile.setLevel(level);
            }
            return tile;
        });
    }

    @Override
    public void renderByItem(ItemStack itemStack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Block block = Block.byItem(itemStack.getItem());
        if (block instanceof NewStoneChestBlock stoneChestBlock) {
            ModChestBlockEntity tile = getOrCreateTile(stoneChestBlock.getChestType());

            if (tile.getLevel() == null) {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    tile.setLevel(level);
                }
            }

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.translate(-0.5, -0.5, -0.5);

            Minecraft.getInstance().getBlockEntityRenderDispatcher()
                    .renderItem(tile, poseStack, buffer, combinedLight, combinedOverlay);
            poseStack.popPose();
        }
    }
}