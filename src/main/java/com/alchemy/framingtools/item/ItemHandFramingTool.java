package com.alchemy.framingtools.item;

import com.alchemy.framingtools.FramingToolConfig;
import com.alchemy.framingtools.FramingTools;
import com.alchemy.framingtools.item.registry.FramingToolsItem;
import com.alchemy.framingtools.util.Values;
import com.alchemy.framingtools.util.helper.ItemNBTHelper;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IFrameable;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import eutros.framedcompactdrawers.block.tile.TileControllerCustom;
import eutros.framedcompactdrawers.block.tile.TileSlaveCustom;
import eutros.framedcompactdrawers.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.alchemy.framingtools.util.Translate.translate;

@Optional.Interface(iface = "com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IFrameable",
        modid = Values.STORAGE_DRAWERS_MODID)
public class ItemHandFramingTool extends Item implements IFrameable {

    public static final String MAT_SIDE_TAG = "MatS";
    public static final String MAT_TRIM_TAG = "MatT";
    public static final String MAT_FRONT_TAG = "MatF";

    //Hard mode
    public static final String STICK = "Stick";
    public static final String MAT_SIDE_AMOUNT = "AmountS";
    public static final String MAT_TRIM_AMOUNT = "AmountT";
    public static final String MAT_FRONT_AMOUNT  = "AmountF";

    public ItemHandFramingTool(ResourceLocation rl, CreativeTabs tab) {
        setMaxStackSize(1);
        setCreativeTab(tab);
        setRegistryName(rl);

        // Add Description
        //addDescription();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip,
                               @NotNull ITooltipFlag flagIn) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        // An abomination for checking not set
        if (notSetCondition(stack)) {
            tooltip.add(translate("tooltip.framingtools.hand_framing_tool.not_set"));
            return;
        }

        if (FramingToolConfig.isHardMode) {
            if (tagCompound.getInteger("Stick") > 0) {
                tooltip.add(translate("tooltip.framingtools.hand_framing_tool.sticks_hard", tagCompound.getInteger(STICK)));
            } else {
                addTooltipItemHard(tooltip, "tooltip.framingtools.hand_framing_tool.side_hard",
                        getItemStackFromKey(tagCompound, MAT_SIDE_TAG), tagCompound.getInteger(MAT_SIDE_AMOUNT));
                addTooltipItemHard(tooltip, "tooltip.framingtools.hand_framing_tool.trim_hard",
                        getItemStackFromKey(tagCompound, MAT_TRIM_TAG), tagCompound.getInteger(MAT_TRIM_AMOUNT));
                addTooltipItemHard(tooltip, "tooltip.framingtools.hand_framing_tool.front_hard",
                        getItemStackFromKey(tagCompound, MAT_FRONT_TAG), tagCompound.getInteger(MAT_FRONT_AMOUNT));

            }
        } else {
            addTooltipItem(tooltip, "tooltip.framingtools.hand_framing_tool.side",
                    getItemStackFromKey(tagCompound, MAT_SIDE_TAG));
            addTooltipItem(tooltip, "tooltip.framingtools.hand_framing_tool.trim",
                    getItemStackFromKey(tagCompound, MAT_TRIM_TAG));
            addTooltipItem(tooltip, "tooltip.framingtools.hand_framing_tool.front",
                    getItemStackFromKey(tagCompound, MAT_FRONT_TAG));
        }
    }

    private boolean notSetCondition(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (FramingToolConfig.isHardMode) {
            return tagCompound == null // Null Tag
                    || ((getItemStackFromKey(tagCompound, MAT_SIDE_TAG).isEmpty() || tagCompound.getInteger(MAT_SIDE_AMOUNT) == 0) // empty side or 0 amount size
                    && (tagCompound.getInteger(STICK) == 0 )); // stick is 0
        } else {
            return tagCompound == null || getItemStackFromKey(tagCompound, MAT_SIDE_TAG).isEmpty();
        }
    }

    @SideOnly(Side.CLIENT)
    private void addTooltipItem(@NotNull List<String> tooltip, String translationKey, ItemStack stack) {
        tooltip.add(translate(translationKey, stack.isEmpty() ? "-" : stack.getDisplayName()));
    }
    @SideOnly(Side.CLIENT)
    private void addTooltipItemHard(@NotNull List<String> tooltip, String translationKey, ItemStack stack, int amount) {
        tooltip.add(translate(translationKey, (stack.isEmpty() || amount == 0) ? "- " : stack.getDisplayName(), amount));
    }

    @Override
    public @NotNull EnumActionResult onItemUse(@NotNull EntityPlayer player, World world, @NotNull BlockPos pos,
                                               @NotNull EnumHand hand, @NotNull EnumFacing facing,
                                               float hitX, float hitY, float hitZ) {
        // This is to return success if we framed it, but not decorated it
        EnumActionResult actionResult = EnumActionResult.PASS;

        if (world.isAirBlock(pos))
            return actionResult;

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!(block instanceof INetworked))
            return actionResult;

        // At this point, further returns should be fail
        actionResult = EnumActionResult.FAIL;

        ItemStack tool = player.getHeldItem(hand);
        NBTTagCompound tagCompound = tool.getTagCompound();

        if (FramingToolConfig.isHardMode) {
            // Hard mode logic
            if (tagCompound == null)
                return actionResult;

            ItemStack matS, matF, matT;
            int amountS, amountF, amountT;
            matS = getItemStackFromKey(tagCompound, MAT_SIDE_TAG);
            amountS = tagCompound.getInteger(MAT_SIDE_AMOUNT);
            if (matS.isEmpty() || amountS == 0) {
                // Framing ONLY IF side is empty
                if (!isDecorating(Objects.requireNonNull(block.getRegistryName())) && tagCompound.getInteger(STICK) > 0) {
                    ItemNBTHelper.setInt(tool, STICK, tagCompound.getInteger(STICK) - 1);
                    // Make it framed
                    var framedResult = makeFramedState(world, pos);
                    if (framedResult != null) return framedResult;
                    // This should be success, if we framed but not decorated
                    actionResult = EnumActionResult.SUCCESS;
                }
                return actionResult;
            } else {
                if (!isDecorating(Objects.requireNonNull(block.getRegistryName()))) {
                    var framedResult = makeFramedState(world, pos);
                    if (framedResult != null) return framedResult;
                }
            }
            matT = getItemStackFromKey(tagCompound, MAT_TRIM_TAG);
            amountT = tagCompound.getInteger(MAT_TRIM_AMOUNT);
            matF = getItemStackFromKey(tagCompound, MAT_FRONT_TAG);
            amountF = tagCompound.getInteger(MAT_FRONT_AMOUNT);
            // Decorate
            MaterialData materialData = getMaterialData(world, pos);
            if (materialData != null) {
                if (amountS > 0) {
                    materialData.setSide(matS.copy());
                    ItemNBTHelper.setInt(tool, MAT_SIDE_AMOUNT, amountS - 1);
                }
                if (amountT > 0) {
                    materialData.setTrim(matT.copy());
                    ItemNBTHelper.setInt(tool, MAT_TRIM_AMOUNT, amountT - 1);
                }
                if (amountF > 0) {
                    materialData.setFront(matF.copy());
                    ItemNBTHelper.setInt(tool, MAT_FRONT_AMOUNT, amountF - 1);
                }
            }
            // Reload Block
            world.markBlockRangeForRenderUpdate(pos, pos);

            return EnumActionResult.SUCCESS;

        } else {
            // Check if we should make this block a framed one
            if (!isDecorating(Objects.requireNonNull(block.getRegistryName()))) {
                // Make it framed
                var framedResult = makeFramedState(world, pos);
                if (framedResult != null) return framedResult;
                // This should be success, if we framed but not decorated
                actionResult = EnumActionResult.SUCCESS;
            }
            if (tagCompound == null)
                return actionResult;
            // Get Decorate Info
            ItemStack matS, matF, matT;
            matS = getItemStackFromKey(tagCompound, MAT_SIDE_TAG);
            if (matS.isEmpty()) {
                return actionResult;
            }
            matT = getItemStackFromKey(tagCompound, MAT_TRIM_TAG);
            matF = getItemStackFromKey(tagCompound, MAT_FRONT_TAG);
            // Decorate
            MaterialData materialData = getMaterialData(world, pos);
            if (materialData != null) {
                materialData.setSide(matS.copy());
                materialData.setTrim(matT.copy());
                materialData.setFront(matF.copy());
            }
            // Reload Block
            world.markBlockRangeForRenderUpdate(pos, pos);
            return EnumActionResult.SUCCESS;
        }


    }

    private boolean isDecorating(ResourceLocation registryName) {
        return registryName.getNamespace().equals(Values.FRAMED_COMPACT_MODID) ||
                registryName.equals(new ResourceLocation(Values.STORAGE_DRAWERS_MODID, "customdrawers")) ||
                registryName.equals(new ResourceLocation(Values.STORAGE_DRAWERS_MODID, "customtrim"));
    }

    /**
     * Returns null if everything is as planned, or an action state to return instead.
     */
    @Nullable
    private EnumActionResult makeFramedState(World world, BlockPos pos) {
        if (Loader.isModLoaded(Values.FRAMED_COMPACT_MODID) && makeFramedCompactState(world, pos)) return null;

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // If framed compact is not loaded (checked above), and we are trying to frame a controller, slave or compacting
        // drawer, exit.
        if (block instanceof BlockCompDrawers || block instanceof BlockController || block instanceof BlockSlave)
            return EnumActionResult.FAIL;

        // Special Case for drawers, to transfer items
        if (block instanceof BlockDrawers) {
            handleDrawerFraming(world, pos, com.jaquadro.minecraft.storagedrawers.core.ModBlocks.customDrawers
                    .getStateFromMeta(block.getMetaFromState(state)));
            return null;
        }

        // Only block that extends INetworked at this point is trims
        IBlockState newState = com.jaquadro.minecraft.storagedrawers.core.ModBlocks.customTrim.getDefaultState();
        world.setBlockState(pos, newState);
        return null;
    }

    /**
     * Returns true if succeeded, false if not
     */
    @Optional.Method(modid = Values.FRAMED_COMPACT_MODID)
    private boolean makeFramedCompactState(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockCompDrawers) {
            handleDrawerFraming(world, pos, ModBlocks.framedCompactDrawer.getDefaultState());
            return true;
        }
        IBlockState newState;
        // Meta for controllers are their direction, so read that (Custom Controller's meta is a bit different to normal
        // controller, so -2 to meta is needed)
        if (block instanceof BlockController)
            newState = ModBlocks.framedDrawerController.getStateFromMeta(block.getMetaFromState(state) - 2);
        else if (block instanceof BlockSlave) newState = ModBlocks.framedSlave.getDefaultState();
        else return false;

        world.setBlockState(pos, newState);
        return true;
    }

    private void handleDrawerFraming(World world, BlockPos pos, IBlockState state) {
        var tag = new NBTTagCompound();

        TileEntityDrawers tile = Objects.requireNonNull((TileEntityDrawers) world.getTileEntity(pos));

        // Get nbt (items stored, locked, etc.) + direction
        tile.writeToPortableNBT(tag);
        int direction = tile.getDirection();

        // Set new BlockState
        world.setBlockState(pos, state);

        // Reload tile, to the new block
        tile = Objects.requireNonNull((TileEntityDrawers) world.getTileEntity(pos));

        // Load back nbt + direction
        tile.readFromPortableNBT(tag);
        tile.setDirection(direction);
    }

    @Nullable
    private MaterialData getMaterialData(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (Loader.isModLoaded(Values.FRAMED_COMPACT_MODID)) {
            var data = getMaterialDataFramed(tile);
            if (data != null) return data;
        }

        // Framed Comp Drawers & Drawers
        if (tile instanceof TileEntityDrawers drawers) {
            return drawers.material();
        }

        // Framed Trim
        if (tile instanceof TileEntityTrim trim)
            return trim.material();

        // Tile was null, or didn't inherit from these, aka error
        FramingTools.LOGGER.fatal("[Hand Framing Tool] Failed to get the material data of tile entity at block pos {}.",
                pos);
        return null;
    }

    /**
     * Don't use this, this is seperated from main method to allow for just storage drawers, without framed compact
     */
    @Nullable
    @Optional.Method(modid = Values.FRAMED_COMPACT_MODID)
    private MaterialData getMaterialDataFramed(TileEntity tile) {
        // Framed Controller
        if (tile instanceof TileControllerCustom controller) {
            return controller.material();
        }

        // Framed Slave
        if (tile instanceof TileSlaveCustom slave) {
            return slave.material();
        }
        return null;
    }

    public static ItemStack getItemStackFromKey(NBTTagCompound tagCompound, String key) {
        if (!tagCompound.hasKey(key))
            return ItemStack.EMPTY;
        else
            return new ItemStack(tagCompound.getCompoundTag(key));
    }

    @Override
    public ItemStack decorate(ItemStack itemStack, ItemStack matSide, ItemStack matTrim, ItemStack matFront) {
        ItemStack stack = new ItemStack(FramingToolsItem.HAND_FRAMING_TOOL, 1);
        NBTTagCompound compound = new NBTTagCompound();

        if (!matSide.isEmpty()) {
            compound.setTag(MAT_SIDE_TAG, getMaterialTag(matSide));
            if (FramingToolConfig.isHardMode) {
                compound.setInteger(MAT_SIDE_AMOUNT, ItemNBTHelper.getInt(itemStack, MAT_SIDE_AMOUNT, 0) + 1);
            }
        }

        if (!matTrim.isEmpty()) {
            compound.setTag(MAT_TRIM_TAG, getMaterialTag(matTrim));
            if (FramingToolConfig.isHardMode) {
                compound.setInteger(MAT_TRIM_AMOUNT, ItemNBTHelper.getInt(itemStack, MAT_TRIM_AMOUNT, 0) + 1);
            }
        }

        if (!matFront.isEmpty()) {
            compound.setTag(MAT_FRONT_TAG, getMaterialTag(matFront));
            if (FramingToolConfig.isHardMode) {
                compound.setInteger(MAT_FRONT_AMOUNT, ItemNBTHelper.getInt(itemStack, MAT_FRONT_TAG, 0) + 1);
            }
        }

        stack.setTagCompound(compound);
        return stack;
    }

    private static NBTTagCompound getMaterialTag(@Nonnull ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        stack.writeToNBT(tag);
        return tag;
    }

//    public void addDescription() {
//        JEIPlugin.addDescription(new ItemStack(this),
//                translatable("item.nomilabs.hand_framing_tool.desc1"),
//                translatable("item.nomilabs.hand_framing_tool.desc2"),
//                translatable("item.nomilabs.hand_framing_tool.desc3"),
//                translatable("item.nomilabs.hand_framing_tool.desc4"),
//                translatable("item.nomilabs.hand_framing_tool.desc5"),
//                translatable("item.nomilabs.hand_framing_tool.desc6"));
//    }
}