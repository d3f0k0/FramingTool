package com.alchemy.framingtools.recipe;

import com.alchemy.framingtools.FramingToolConfig;
import com.alchemy.framingtools.FramingTools;
import com.alchemy.framingtools.item.ItemHandFramingTool;
import com.alchemy.framingtools.util.helper.ItemNBTHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import static com.alchemy.framingtools.item.ItemHandFramingTool.MAT_SIDE_TAG;

public class AddStickRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe{

    public AddStickRecipe(ResourceLocation resourceLocation) {
        setRegistryName(resourceLocation);
    }
    @Override
    public boolean matches(InventoryCrafting inv, @NotNull World worldIn) {
        boolean haveTool = false;
        boolean haveStick = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()){
                if (stack.getItem() instanceof ItemHandFramingTool) {
                    NBTTagCompound tagCompound = stack.getTagCompound();
                    if (tagCompound == null || ItemHandFramingTool.getItemStackFromKey(tagCompound, MAT_SIDE_TAG).isEmpty()) {
                        haveTool = true;
                    }
                }
                if (stack.getItem() == Items.STICK) {
                    haveStick = true;
                }
            }
        }
        return FramingToolConfig.isHardMode && haveTool && haveStick;
    }

    @Override
    @NotNull
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack toolStack = ItemStack.EMPTY;
        int amountOfSticks = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()){
                if (stack.getItem() instanceof ItemHandFramingTool) {
                    NBTTagCompound tagCompound = stack.getTagCompound();
                    if (tagCompound == null || ItemHandFramingTool.getItemStackFromKey(tagCompound, MAT_SIDE_TAG).isEmpty()) {
                        toolStack = stack.copy();
                    }
                }
                if (stack.getItem() == Items.STICK) {
                    amountOfSticks++;
                }
            }
        }
        FramingTools.LOGGER.info(amountOfSticks);
        if (toolStack.getItem() instanceof ItemHandFramingTool) {
            ItemStack returnStack = toolStack.copy();
            ItemNBTHelper.setInt(returnStack, "Stick", ItemNBTHelper.getInt(returnStack, "Stick", 0) + amountOfSticks);
            return returnStack;
        } else {
            return ItemStack.EMPTY;
        }

    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
