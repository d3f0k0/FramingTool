package com.alchemy.framingtools.util.helper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemStackHelper {
    public static ItemStack fromName(String modName, String registryName, int meta) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modName, registryName));
        return new ItemStack(item, 1, meta);
    }
}
