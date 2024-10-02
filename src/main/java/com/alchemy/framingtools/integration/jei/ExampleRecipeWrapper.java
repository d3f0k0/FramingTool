package com.alchemy.framingtools.integration.jei;

import com.alchemy.framingtools.FramingToolConfig;
import com.alchemy.framingtools.item.registry.FramingToolsItem;
import com.alchemy.framingtools.util.helper.ItemMeta;
import com.alchemy.framingtools.util.helper.ItemStackHelper;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IFrameable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.alchemy.framingtools.util.Translate.translate;


public class ExampleRecipeWrapper implements IShapedCraftingRecipeWrapper {
    protected ItemStack framer;
    protected boolean trim;
    protected boolean front;
    private static ItemStack tool = new ItemStack(FramingToolsItem.HAND_FRAMING_TOOL);

    private static final String[] sideMaterialParsed = FramingToolConfig.sideMaterial.split(":");
    private static final String[] trimMaterialParsed = FramingToolConfig.trimMaterial.split(":");
    private static final String[] frontMaterialParsed = FramingToolConfig.frontMaterial.split(":");

    private static final ItemStack sideMaterial = ItemStackHelper.fromName(sideMaterialParsed[0], sideMaterialParsed[1], Integer.parseInt(sideMaterialParsed[2]));
    private static final ItemStack trimMaterial = ItemStackHelper.fromName(trimMaterialParsed[0], trimMaterialParsed[1], Integer.parseInt(trimMaterialParsed[2]));
    private static final ItemStack frontMaterial = ItemStackHelper.fromName(frontMaterialParsed[0], frontMaterialParsed[1], Integer.parseInt(frontMaterialParsed[2]));

    public ExampleRecipeWrapper(ItemStack framer, boolean trim, boolean front) {
        this.framer = framer.copy();
        this.trim = trim;
        this.front = front;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        var sideStack = sideMaterial;
        var trimStack = trim ? trimMaterial : ItemStack.EMPTY;
        var frontStack = front ? frontMaterial : ItemStack.EMPTY;

        List<ItemStack> inputs = Arrays.asList(
                sideStack, trimStack, null,
                frontStack, framer
        );
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        try {
            ingredients.setOutput(VanillaTypes.ITEM, addNBT(framer, trim, front));
        } catch (NBTException e) {
            throw new RuntimeException(e);
        }

    }

    //Might delete this (this code is remenant from the copied quark elytra code)
//    @Override
//    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
//        minecraft.fontRenderer.drawString("Example", 60, 46, Color.gray.getRGB());
//    }

    public static ItemStack addNBT(ItemStack stack, boolean trim, boolean front) throws NBTException {
        var sideStack = sideMaterial;
        var trimStack = trim ? trimMaterial : ItemStack.EMPTY;
        var frontStack = front ? frontMaterial : ItemStack.EMPTY;

        var frameable = (IFrameable) stack.getItem();
        stack = frameable.decorate(stack.copy(), sideStack, trimStack, frontStack);

        // Add lore similar to Nomifactory original, might do it for now
        NBTTagCompound stackNBT = stack.getTagCompound();
        stackNBT.merge(writeLore(Arrays.asList(
                ItemMeta.compare(stack, tool) ?
                        translate("tooltip.framingtools.hand_framing_tool.recipe_tool") :
                        translate("tooltip.framingtools.hand_framing_tool.recipe_drawer"),
                translate("tooltip.framingtools.hand_framing_tool.recipe_top_left"),
                translate("tooltip.framingtools.hand_framing_tool.recipe_top_right"),
                translate("tooltip.framingtools.hand_framing_tool.recipe_bottom_left")
        )));
        stack.setTagCompound(stackNBT);

        return stack;
    }

    private static NBTTagCompound writeLore(List<String> lore) throws NBTException {
        StringBuilder loreNBT = new StringBuilder("{display:{Lore:[");
        for (String line : lore) {
            loreNBT.append("\"");
            loreNBT.append(line);
            loreNBT.append("\"");
            loreNBT.append(",");
        }
        loreNBT.append("]}}");
        NBTTagCompound result = JsonToNBT.getTagFromJson(loreNBT.toString());
        return result;
    }

    @Override
    public int getHeight(){
        return 3;
    }

    @Override
    public int getWidth(){
        return 3;
    }

}
