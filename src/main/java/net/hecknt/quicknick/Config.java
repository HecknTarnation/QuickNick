package net.hecknt.quicknick;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = QuickNick.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue PERMISSION_NEEDED = BUILDER
            .comment("Permission level needed to run the command.")
            .defineInRange("PERMISSION_NEEDED", 1, 0, 2);

    private static final ForgeConfigSpec.BooleanValue REMOVE_CLICK_EVENTS = BUILDER
            .comment("Removes click events from nicknames.").define("removeClickEvent", false);
    private static final ForgeConfigSpec.BooleanValue REMOVE_INSERTION = BUILDER
            .comment("Removes insertion events from nicknames.").define("removeInsertions", false);
    private static final ForgeConfigSpec.BooleanValue REMOVE_HOVER_EVENT = BUILDER
            .comment("Removes hover events from nicknames.").define("removeHoverEvent", false);
    /*private static final ForgeConfigSpec.BooleanValue REMOVE_SCORE = BUILDER
            .comment("Removes scores from nicknames.").define("removeScore", false);
    private static final ForgeConfigSpec.BooleanValue REMOVE_SELECTOR = BUILDER
            .comment("Removes selectors from nicknames.").define("removeSelector", false);
    private static final ForgeConfigSpec.BooleanValue REMOVE_NBT = BUILDER
            .comment("Removes NBT from nicknames.").define("removeNBT", false);
    private static final ForgeConfigSpec.BooleanValue REMOVE_KEYBIND = BUILDER
            .comment("Removes keybinds from nicknames.").define("removeKeybind", false);*/

    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int permissionNeeded;
    public static boolean removeInsertions;
    public static boolean removeClickEvent;
    public static boolean removeHoverEvent;
    public static boolean removeScore = false;
    public static boolean removeSelector = false;
    public static boolean removeKeybind = false;
    public static boolean removeNbt = false;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        permissionNeeded = PERMISSION_NEEDED.get();
        removeInsertions = REMOVE_INSERTION.get();
        removeClickEvent = REMOVE_CLICK_EVENTS.get();
        removeHoverEvent = REMOVE_HOVER_EVENT.get();
        /*removeScore = REMOVE_SCORE.get();
        removeSelector = REMOVE_SELECTOR.get();
        removeKeybind = REMOVE_KEYBIND.get();
        removeNbt = REMOVE_NBT.get();*/
    }
}
