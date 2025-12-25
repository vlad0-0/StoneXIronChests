// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import com.mojang.serialization.MapCodec;
import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ModChestBlock extends ModAbstractSingleChestBlock<ModChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final MapCodec<ModChestBlock> CODEC = simpleCodec((properties) ->
            new ModChestBlock(properties, () -> ModRegistry.STONE_CHEST_ENTITY.get()));
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape AABB;

    public @NotNull MapCodec<ModChestBlock> codec() {
        return CODEC;
    }

    public ModChestBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<? extends ModChestBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState blockState1,
                                              @NotNull LevelReader level,
                                              @NotNull ScheduledTickAccess tickAccess,
                                              @NotNull BlockPos blockPos1,
                                              @NotNull Direction direction,
                                              @NotNull BlockPos blockPos2,
                                              @NotNull BlockState blockState2,
                                              @NotNull RandomSource randomSource) {
        if (blockState1.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(blockPos1, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(blockState1, level, tickAccess, blockPos1,
                direction, blockPos2, blockState2, randomSource);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        return this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level,
                            @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (isChestBlockedAt(level, pos)) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
            if (menuprovider != null) {
                player.openMenu(menuprovider);
                player.awardStat(this.getOpenChestStat());
                if (level instanceof ServerLevel serverLevel) {
                    PiglinAi.angerNearbyPiglins(serverLevel, player, true);
                }
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                int id, int param) {
        if (id == 1) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof ModChestBlockEntity chestEntity) {
                chestEntity.updateLidState(param);
                return true;
            }
        }
        return super.triggerEvent(state, level, pos, id, param);
    }

    protected Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    @Nullable
    public static Container getContainer(Level level, BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ModChestBlockEntity) {
            return (Container) blockentity;
        }
        return null;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ModChestBlockEntity) {
            return (MenuProvider) blockentity;
        }
        StoneXIronChests.LOGGER.error("No MenuProvider found");
        return null;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ModChestBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> blockEntityType) {
        BlockEntityType<? extends ModChestBlockEntity> chestType = this.blockEntityType.get();

        if (chestType == blockEntityType && level.isClientSide()) {
            return createChestTicker();
        }
        return null;
    }

    private <E extends BlockEntity> BlockEntityTicker<E> createChestTicker() {
        return (level, pos, state, blockEntity) -> {
            if (level.isClientSide() && blockEntity instanceof ModChestBlockEntity chestEntity) {
                ModChestBlockEntity.lidAnimateTick(level, chestEntity);
            }

            if (!level.isClientSide() && blockEntity instanceof ModChestBlockEntity chestEntity) {
                chestEntity.recheckOpen();
            }
        };
    }

    public static boolean isChestBlockedAt(LevelAccessor level, BlockPos pos) {
        return isBlockedChestByBlock(level, pos) || isCatSittingOnChest(level, pos);
    }

    private static boolean isBlockedChestByBlock(BlockGetter level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        return level.getBlockState(blockpos).isRedstoneConductor(level, blockpos);
    }

    private static boolean isCatSittingOnChest(LevelAccessor level, BlockPos pos) {
        List<Cat> list = level.getEntitiesOfClass(Cat.class,
                new AABB(pos.getX(), pos.getY() + 1, pos.getZ(),
                        pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
        if (!list.isEmpty()) {
            for(Cat cat : list) {
                if (cat.isInSittingPose()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos pos) {
        Container container = getContainer(level, pos);
        if (container != null) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
        }
        return 0;
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ModChestBlockEntity) {
            ((ModChestBlockEntity) blockentity).recheckOpen();
        }
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        AABB = Block.box(1.0F, 0.0F, 1.0F, 15.0F, 14.0F, 15.0F);
    }
}