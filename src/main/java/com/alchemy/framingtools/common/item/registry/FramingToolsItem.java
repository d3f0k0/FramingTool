package com.alchemy.framingtools.common.item.registry;
import java.util.*;


import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import com.alchemy.framingtools.common.item.*;
import com.alchemy.framingtools.common.item.registry.register.FramingToolRegister;


@SuppressWarnings("unused")
public class FramingToolsItem {

    private static final String nullTranslationKey = "item.null";

    private static final List<Item> ITEMS = new ArrayList<>();

    public static ItemHandFramingTool HAND_FRAMING_TOOL;

    public static void preInit() {

        /* Mod Specific Items */
        FramingToolRegister.initFramingTool();
    }

    /* HELPER FUNCTIONS */
    public static void register(IForgeRegistry<Item> registry) {
        for (Item item : ITEMS) {
            registerItem(item, registry);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (Item item : ITEMS) {
            registerModel(item);
        }
    }
    public static <T extends Item> T createItem(T item) {
        ITEMS.add(item);
        return item;
    }


    private static void registerItem(Item item, IForgeRegistry<Item> registry) {
        registry.register(item);
        if (item.getTranslationKey().equals(nullTranslationKey)) {
            ResourceLocation rl = item.getRegistryName();
            assert rl != null;
            item.setTranslationKey(rl.getNamespace() + "." + rl.getPath());
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Item item) {
        ResourceLocation rl = item.getRegistryName();
        assert rl != null;
        ModelBakery.registerItemVariants(item, rl);
        ModelResourceLocation mrl;

        mrl = new ModelResourceLocation(rl, "inventory");
        assert item.getCreativeTab() != null;

        // Item does not have subtypes
        if (!item.getHasSubtypes()) {
            ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
            return;
        }

        // Register each sub item's model
        NonNullList<ItemStack> subItems = NonNullList.create();
        item.getSubItems(item.getCreativeTab(), subItems);

        for (ItemStack stack : subItems)
            ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getMetadata(), mrl);
    }
}
