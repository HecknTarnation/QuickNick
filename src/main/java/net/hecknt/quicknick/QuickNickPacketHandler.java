package net.hecknt.quicknick;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class QuickNickPacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    static int ChangeNamePacketID = 0;
    public static void setup(final FMLCommonSetupEvent event){
        INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(QuickNick.MODID, "main")).networkProtocolVersion(() -> PROTOCOL_VERSION).clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();
        event.enqueueWork(() -> {
            INSTANCE.registerMessage(ChangeNamePacketID++, ChangeNamePacket.class, ChangeNamePacket::toBytes, ChangeNamePacket::new, ChangeNamePacket::handlePacket);
        });
    }

    public static void sendPacket(String newName, Player plr){
        INSTANCE.send(PacketDistributor.ALL.noArg(), new ChangeNamePacket(newName, plr.getId()));
    }

}
