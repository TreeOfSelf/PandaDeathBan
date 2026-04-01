package me.TreeOfSelf.PandaDeathBan;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;

import java.util.Optional;
import java.util.UUID;

public class DeathBanResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("deathbanreset")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.argument("username", StringArgumentType.string())
                        .executes(context -> reset(context, StringArgumentType.getString(context, "username")))));
    }

    private static int reset(CommandContext<CommandSourceStack> context, String username) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();

        Optional<NameAndId> playerEntry = server.services().nameToIdCache().get(username);

        if (playerEntry.isEmpty()) {
            source.sendFailure(Component.literal("Player not found! They may have never joined the server."));
            return 0;
        }

        UUID playerUUID = playerEntry.get().id();

        StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(playerUUID, server);
        playerData.deathUnbanTime = 0L;
        StateSaverAndLoader.getServerState(server).setDirty();

        source.sendSuccess(() -> Component.literal("Death ban reset for player: " + username), true);
        return 1;
    }
}
