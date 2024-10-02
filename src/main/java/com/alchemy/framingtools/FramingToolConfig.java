package com.alchemy.framingtools;

import com.alchemy.framingtools.util.Values;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Values.FRAMING_TOOLS_MODID)
public class FramingToolConfig {

    @Config.Comment("Set Side material for example recipe. Format: modid:blockname:metadata")
    public static String sideMaterial = "minecraft:concrete:3";

    @Config.Comment("Set Trim material for example recipe. Format: modid:blockname:metadata")
    public static String trimMaterial = "minecraft:concrete:2";

    @Config.Comment("Set Front material for example recipe. Format: modid:blockname:metadata")
    public static String frontMaterial = "minecraft:concrete:1";

    @Mod.EventBusSubscriber(modid = Values.FRAMING_TOOLS_MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Values.FRAMING_TOOLS_MODID)) {
                ConfigManager.sync(Values.FRAMING_TOOLS_MODID, Config.Type.INSTANCE);
            }
        }
    }

}
