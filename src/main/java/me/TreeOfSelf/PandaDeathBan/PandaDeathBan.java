package me.TreeOfSelf.PandaDeathBan;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PandaDeathBan implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("panda-death-ban");
	public static final String MOD_ID = "panda-death-ban";

	@Override
	public void onInitialize() {
		LOGGER.info("PandaDeathBan Started!");

		ConfigManager.loadConfig();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			DeathBanResetCommand.register(dispatcher);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			long currentTick = server.getTicks();
			List<ServerPlayerEntity> playersToDisconnect = new ArrayList<>();

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(player);
				if (playerData.disconnectAtTick != -1 && currentTick >= playerData.disconnectAtTick) {
					playersToDisconnect.add(player);
				}
			}

			for (ServerPlayerEntity player : playersToDisconnect) {
				StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(player);
				long currentTimeMillis = System.currentTimeMillis();
				playerData.deathUnbanTime = (currentTimeMillis / 1000L) + ConfigManager.getConfig().banDurationSeconds;
				playerData.disconnectAtTick = -1;
				StateSaverAndLoader.getServerState(server).markDirty();

				if (player.networkHandler != null) {
					player.networkHandler.disconnect(BanMessageUtil.createBanMessage(playerData.deathUnbanTime));
				}
			}
		});
	}
}