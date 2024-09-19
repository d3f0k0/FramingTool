package com.alchemy.framingtools.common.util;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class Side {

    public static boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public static boolean isDedicatedServer() {
        return FMLCommonHandler.instance().getSide().isServer();
    }
}
