package dev.invalid.stone_x_iron_chests.mixin;

import com.progwml6.ironchest.IronChests;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class DirtChestRemainderMixin {

    @Inject(method = "getCraftingRemainingItem", at = @At("HEAD"), cancellable = true)
    private void injectGetCraftingRemainingItem(CallbackInfoReturnable<Item> cir) {
        Item self = (Item) (Object) this;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(self);

        if (!id.getNamespace().equals(IronChests.MODID)) {
            return;
        }

        switch (id.getPath()) {
            case "dirt_chest" ->
                    cir.setReturnValue(Items.CHEST);
            case "trapped_dirt_chest" ->
                    cir.setReturnValue(Items.TRAPPED_CHEST);
        }
    }

    @Inject(method = "hasCraftingRemainingItem", at = @At("HEAD"), cancellable = true)
    private void injectHasCraftingRemainingItem(CallbackInfoReturnable<Boolean> cir) {
        Item self = (Item) (Object) this;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(self);

        if (!id.getNamespace().equals(IronChests.MODID)) {
            return;
        }

        switch (id.getPath()) {
            case "dirt_chest", "trapped_dirt_chest" ->
                    cir.setReturnValue(true);
        }
    }
}