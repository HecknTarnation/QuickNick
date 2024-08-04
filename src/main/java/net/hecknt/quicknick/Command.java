package net.hecknt.quicknick;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal(QuickNick.MODID).requires(p -> p.hasPermission(Config.permissionNeeded))
                        .then(Commands.argument("target", EntityArgument.player())
                            .then(Commands.argument("nickname", ComponentArgument.textComponent())
                                .executes(commandContext -> setNick(commandContext.getSource().source, EntityArgument.getPlayer(commandContext, "target"), ComponentArgument.getComponent(commandContext, "nickname"))))
                                .then(Commands.literal("clear").executes(commandContext -> clearNick(commandContext.getSource().source, EntityArgument.getPlayer(commandContext, "target"))))));
    }

    private static int clearNick(CommandSource source, ServerPlayer target) {
        target.getPersistentData().putString(QuickNick.MODID + ":nickname", "");
        source.sendSystemMessage(Component.literal("Cleared nickname of " + target.getName().plainCopy().getString()));
        QuickNickPacketHandler.sendPacket("", target);

        target.setCustomName(target.getName());
        target.refreshDisplayName();
        target.refreshTabListName();
        return 0;
    }

    private static int setNick(CommandSource source, ServerPlayer player, Component name){
        String json = "";
        try{
            //name = SanitizeUtil.sanitize(name);
            json = Component.Serializer.toJson(name);
        }catch(Exception e){
            QuickNick.LOGGER.warn(e.getMessage());
            json = "";
        }
        source.sendSystemMessage(Component.literal(player.getName().getString() + "'s nickname set to ").append(name));
        player.getPersistentData().putString(QuickNick.MODID + ":nickname", json);
        QuickNickPacketHandler.sendPacket(json, player);

        Component c = SanitizeUtil.sanitize(Component.Serializer.fromJsonLenient(json));
        player.setCustomName(c);
        player.refreshDisplayName();
        player.refreshTabListName();
        return 0;
    }

}
