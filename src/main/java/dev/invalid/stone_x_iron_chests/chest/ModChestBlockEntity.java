// Copyright (C) 2025 vlad0-0. License: GPL-3.0

package dev.invalid.stone_x_iron_chests.chest;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ModChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private static final int EVENT_SET_OPEN_COUNT = 1;
    private NonNullList<ItemStack> items;
    private final ContainerOpenersCounter openersCounter;
    private final ChestLidController chestLidController;
    private static final int CONTAINER_SIZE = 36;

    protected ModChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                ModChestBlockEntity.playSound(level, blockPos, SoundEvents.CHEST_OPEN);
            }

            protected void onClose(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
                ModChestBlockEntity.playSound(level, blockPos, SoundEvents.CHEST_CLOSE);
            }

            protected void openerCountChanged(@NotNull Level level, @NotNull BlockPos blockPos,
                                              @NotNull BlockState blockState, int oldCount, int newCount) {
                ModChestBlockEntity.this.signalOpenCount(level, blockPos, blockState, newCount);
            }

            protected boolean isOwnContainer(@NotNull Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                }
                Container container = ((ChestMenu)player.containerMenu).getContainer();
                return container == ModChestBlockEntity.this;
            }
        };
        this.chestLidController = new ChestLidController();
    }

    public ModChestBlockEntity(BlockPos pos, BlockState blockState) {
        this(ModRegistry.STONE_CHEST_ENTITY.get(), pos, blockState);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.stone_chest");
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    public static void lidAnimateTick(Level level, ModChestBlockEntity blockEntity) {
        if (level != null && blockEntity != null) {
            blockEntity.chestLidController.tickLid();
        }
    }

    static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS,
                0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == EVENT_SET_OPEN_COUNT) {
            this.chestLidController.shouldBeOpen(type > 0);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @Override
    public void startOpen(@NotNull Player player) {
        if (!this.remove && !player.isSpectator() && this.getLevel() != null) {
            this.openersCounter.incrementOpeners(player, this.getLevel(),
                    this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(@NotNull Player player) {
        if (!this.remove && !player.isSpectator() && this.getLevel() != null) {
            this.openersCounter.decrementOpeners(player, this.getLevel(),
                    this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new ChestMenu(MenuType.GENERIC_9x4, id, player, this, 4);
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int containerId,
                                                     @NotNull Inventory playerInventory,
                                                     @NotNull Player player) {
        return this.createMenu(containerId, playerInventory);
    }

    public void recheckOpen() {
        if (!this.remove && this.getLevel() != null) {
            this.openersCounter.recheckOpeners(this.getLevel(),
                    this.getBlockPos(), this.getBlockState());
        }
    }

    public void updateLidState(int openCount) {
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            this.chestLidController.shouldBeOpen(openCount > 0);
        }
    }

    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int openCount) {
        if (level != null) {
            level.blockEvent(pos, state.getBlock(), EVENT_SET_OPEN_COUNT, openCount);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.getDefaultName();
    }
}
