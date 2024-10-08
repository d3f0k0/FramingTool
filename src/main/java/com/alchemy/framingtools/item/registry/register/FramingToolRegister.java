package com.alchemy.framingtools.item.registry.register;

import static com.alchemy.framingtools.item.registry.FramingToolsItem.*;

import com.alchemy.framingtools.util.Values;
import com.alchemy.framingtools.item.ItemHandFramingTool;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class FramingToolRegister {
    public static void initFramingTool() {
        if (Loader.isModLoaded(Values.STORAGE_DRAWERS_MODID)) {
            HAND_FRAMING_TOOL = createItem(new ItemHandFramingTool(new ResourceLocation(Values.FRAMING_TOOLS_MODID, "hand_framing_tool"), CreativeTabs.TOOLS));
        }
    }
}
