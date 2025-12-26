// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import ftblag.stonechest.StoneChest;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ModChestRenderer<T extends ModChestBlockEntity> implements BlockEntityRenderer<T> {
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public ModChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
        this.lid = modelpart.getChild("lid");
        this.bottom = modelpart.getChild("bottom");
        this.lock = modelpart.getChild("lock");
    }

    @Override
    public void render(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        BlockState blockState = blockEntity.getBlockState();
        Block block = blockState.getBlock();

        if (!(block instanceof NewStoneChestBlock stoneChest)) {
            StoneXIronChests.LOGGER.error("Block is not NewStoneChestBlock!");
            return;
        }

        Direction direction = blockState.getValue(ModChestBlock.FACING);
        Material material = stoneChest.clientRenderData.getMaterial();

        poseStack.pushPose();
        float rotation = direction.toYRot();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float lidAngle = blockEntity.getOpenNess(partialTick);
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;

        VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutout);

        this.lid.xRot = -(lidAngle * ((float)Math.PI / 2F));
        this.lock.xRot = this.lid.xRot;

        this.lid.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        this.lock.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        this.bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    public @NotNull AABB getRenderBoundingBox(T blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
    }
}
