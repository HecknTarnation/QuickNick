package net.hecknt.quicknick;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeNamePacket {

    public String newName;
    public boolean isChanging;
    public int entityID;

    public ChangeNamePacket(String newName, int entityID){
        this.newName = newName;
        this.entityID = entityID;
    }

    public ChangeNamePacket(){

    }

    public ChangeNamePacket(FriendlyByteBuf buf){
        this.newName = buf.readUtf();
        this.entityID = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeUtf(newName);
        buf.writeInt(entityID);
    }

    public static void handlePacket(ChangeNamePacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();

            Player plr = (Player) minecraft.level.getEntity(packet.entityID);

            if(plr != null) {
                ctx.get().setPacketHandled(true);

                if (!packet.newName.equals("")) {
                    minecraft.player.connection.getPlayerInfo(plr.getGameProfile().getId()).setTabListDisplayName(Component.Serializer.fromJsonLenient(packet.newName));
                    plr.setCustomName(Component.Serializer.fromJsonLenient(packet.newName));
                } else {
                    minecraft.player.connection.getPlayerInfo(plr.getGameProfile().getId()).setTabListDisplayName(Component.literal(plr.getGameProfile().getName()));
                    plr.setCustomName(Component.Serializer.fromJsonLenient(packet.newName));
                }

                plr.refreshDisplayName();
                if (plr instanceof ServerPlayer) {
                    ((ServerPlayer) plr).refreshTabListName();
                }
                plr.setCustomNameVisible(true);
            }

        });
    }

}
