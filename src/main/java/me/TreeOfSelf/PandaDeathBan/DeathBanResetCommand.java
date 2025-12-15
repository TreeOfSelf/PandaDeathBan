package me.TreeOfSelf.PandaDeathBan;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;

import java.util.Optional;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeathBanResetCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("deathbanreset")
                .requires(source -> source.getPermissions().hasPermission(new Permission.Level(PermissionLevel.ADMINS)))
                .then(argument("username", StringArgumentType.string())
                        .executes(context -> reset(context, StringArgumentType.getString(context, "username")))));
    }

    private static int reset(CommandContext<ServerCommandSource> context, String username) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        Optional<PlayerConfigEntry> playerEntry = server.getApiServices().nameToIdCache().findByName(username);

        if (playerEntry.isEmpty()) {
            source.sendError(Text.literal("Player not found! They may have never joined the server."));
            return 0;
        }

        UUID playerUUID = playerEntry.get().id();

        StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(playerUUID, server);
        playerData.deathUnbanTime = 0L;
        StateSaverAndLoader.getServerState(server).markDirty();

        source.sendFeedback(() -> Text.literal("Death ban reset for player: " + username), true);
        return 1;
    }
}