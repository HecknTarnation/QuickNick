package net.hecknt.quicknick;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.mojang.brigadier.Message;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(QuickNick.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QuickNick {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "quicknick";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public QuickNick() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void playerNameEvent(PlayerEvent.NameFormat event){
        Player p = event.getEntity();
        String nick = p.getPersistentData().getString(MODID + ":nickname");
        if(!nick.isEmpty()) {
            Component c = SanitizeUtil.sanitize(Component.Serializer.fromJsonLenient(nick));
            event.setDisplayname(c);
            p.setCustomName(c);
        }else{
            event.setDisplayname(p.getName());
            p.setCustomName(p.getName());
        }
        if(p instanceof ServerPlayer){
            ((ServerPlayer)p).refreshTabListName();
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event){
        Player pNew = event.getEntity();
        Player pOrg = event.getOriginal();
        if(!pNew.level().isClientSide){
            pNew.getPersistentData().putString(QuickNick.MODID + ":nickname", pOrg.getPersistentData().getString(QuickNick.MODID + ":nickname"));
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event)
    {
        Player player = event.getEntity();
        if (!player.level().isClientSide)
        {
            if (player.getPersistentData().contains(QuickNick.MODID + ":nickname"))
                QuickNickPacketHandler.sendPacket(player.getPersistentData().getString(QuickNick.MODID + ":nickname"), player);

            for(Player other : player.getServer().getPlayerList().getPlayers())
            {
                if (other.getPersistentData().contains(QuickNick.MODID + ":nickname"))
                    QuickNickPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ChangeNamePacket(other.getPersistentData().getString(QuickNick.MODID + ":nickname"), other.getId()));
            }
            if(player instanceof ServerPlayer){
                ((ServerPlayer)player).refreshTabListName();
            }
        }
    }

    @SubscribeEvent
    public static void onTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof Player)
        {
            Player targetPlayer = (Player) event.getTarget();
            if (targetPlayer.getPersistentData() != null && targetPlayer.getPersistentData().contains(QuickNick.MODID + ":nickname"))
            {
                ServerPlayer toRecieve = (ServerPlayer) event.getEntity();
                QuickNickPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> toRecieve), new ChangeNamePacket(targetPlayer.getPersistentData().getString(QuickNick.MODID + ":nickname"), targetPlayer.getId()));
                toRecieve.refreshTabListName();
                targetPlayer.setCustomName(Component.Serializer.fromJsonLenient(targetPlayer.getPersistentData().getString(QuickNick.MODID + ":nickname")));
                ((ServerPlayer) targetPlayer).refreshTabListName();
            }
        }
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event){
        Command.register(event.getDispatcher());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        QuickNickPacketHandler.setup(event);

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code

        }
    }
}
