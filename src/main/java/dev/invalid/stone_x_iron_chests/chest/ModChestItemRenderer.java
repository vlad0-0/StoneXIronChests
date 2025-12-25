// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ModChestItemRenderer implements SpecialModelRenderer<NewStoneChestBlock> {
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public ModChestItemRenderer(BlockEntityRenderDispatcher dispatcher) {
        this.blockEntityRenderDispatcher = dispatcher;
    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture)
                ).apply(instance, Unbaked::new)
        );

        @Override
        public @NotNull SpecialModelRenderer<?> bake(@NotNull EntityModelSet entityModelSet) {
            return new ModChestItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher());
        }

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    @Override
    @Nullable
    public NewStoneChestBlock extractArgument(ItemStack itemStack) {
        Block block = Block.byItem(itemStack.getItem());
        if (block instanceof NewStoneChestBlock stoneChestBlock) {
            return stoneChestBlock;
        }
        return null;
    }

    @Override
    public void render(@Nullable NewStoneChestBlock stoneChestBlock, @NotNull ItemDisplayContext displayContext,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay, boolean hasFoil) {
        if (stoneChestBlock == null) return;

        ModChestBlockEntity tile = stoneChestBlock.clientRenderData.getOrCreateTile();

        stoneChestBlock.clientRenderData.updateLevel();

        if (tile.getLevel() == null) return;

        BlockEntityRenderer<ModChestBlockEntity> renderer = this.blockEntityRenderDispatcher.getRenderer(tile);

        if (renderer != null) {
            poseStack.pushPose();
            renderer.render(tile, 0.0f, poseStack, buffer, combinedLight, combinedOverlay);
            poseStack.popPose();
        }
    }
}