package com.alchemy.framingtools.integration.jei;

import com.alchemy.framingtools.util.Values;
import com.alchemy.framingtools.util.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleRecipeMaker {
    static List<ItemStack> items = Arrays.asList(
            ItemStackHelper.fromName(Values.STORAGE_DRAWERS_MODID, "customdrawers", 0),
            ItemStackHelper.fromName(Values.STORAGE_DRAWERS_MODID, "customdrawers", 1),
            ItemStackHelper.fromName(Values.STORAGE_DRAWERS_MODID, "customdrawers", 2),
            ItemStackHelper.fromName(Values.STORAGE_DRAWERS_MODID, "customdrawers", 3),
            ItemStackHelper.fromName(Values.STORAGE_DRAWERS_MODID, "customdrawers", 4),
            ItemStackHelper.fromName(Values.STORAGE_DRAWERS_MODID, "customtrim", 0),
            ItemStackHelper.fromName(Values.FRAMING_TOOLS_MODID, "hand_framing_tool", 0)
    );

    static List<ItemStack> optionalItems = new ArrayList<>();

    public static List<ExampleRecipeWrapper> getExampleRecipe() {
        List<ExampleRecipeWrapper> recipes = new ArrayList<>();
        for (ItemStack stack : items) {
            for (boolean trim : Arrays.asList(true, false)){
                for (boolean front: Arrays.asList(true, false)){
                    ExampleRecipeWrapper recipe = new ExampleRecipeWrapper(stack, trim, front);
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }

    // This is just make frame compact drawer optional
    public static List<ExampleRecipeWrapper> getOptionalExampleRecipe() {
        List<ExampleRecipeWrapper> recipes = new ArrayList<>();
        if (Loader.isModLoaded(Values.FRAMED_COMPACT_MODID)) {
            optionalItems.add(ItemStackHelper.fromName(Values.FRAMED_COMPACT_MODID, "framed_compact_drawer", 0));
            optionalItems.add(ItemStackHelper.fromName(Values.FRAMED_COMPACT_MODID, "framed_slave", 0));
            optionalItems.add(ItemStackHelper.fromName(Values.FRAMED_COMPACT_MODID, "framed_drawer_controller", 0));
            for (ItemStack stack : optionalItems) {
                for (boolean trim : Arrays.asList(true, false)){
                    for (boolean front: Arrays.asList(true, false)){
                        ExampleRecipeWrapper recipe = new ExampleRecipeWrapper(stack, trim, front);
                        recipes.add(recipe);
                    }
                }
            }
        }
        return recipes;
    }

    private ExampleRecipeMaker() {

    }
}
