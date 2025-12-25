package dev.invalid.stone_x_iron_chests.client;

import dev.invalid.stone_x_iron_chests.ModRegistry;
import dev.invalid.stone_x_iron_chests.StoneXIronChests;
import dev.invalid.stone_x_iron_chests.chest.ModChestItemRenderer;
import dev.invalid.stone_x_iron_chests.chest.ModChestRenderer;
import dev.invalid.stone_x_iron_chests.chest.NewStoneChestBlock;
import ftblag.stonechest.blocks.EnumStoneChest;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(modid = StoneXIronChests.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(StoneXIronChests.MODID, "stone_chest_renderer"),
                ModChestItemRenderer.Unbaked.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(ModRegistry.STONE_CHEST_ENTITY.get(), ModChestRenderer::new);
        event.enqueueWork(() -> {
            for (EnumStoneChest type : EnumStoneChest.VALUES) {
                Block block = ModRegistry.stoneChests[type.ordinal()].get();
                if (block instanceof NewStoneChestBlock chestBlock) {
                    chestBlock.clientRenderData = new ClientRenderData(type);
                }
            }
        });
    }
}