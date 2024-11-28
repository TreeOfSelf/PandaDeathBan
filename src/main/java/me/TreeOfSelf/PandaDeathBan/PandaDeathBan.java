package me.TreeOfSelf.PandaDeathBan;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaDeathBan implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("panda-death-ban");
	public static final String MOD_ID = "panda-death-ban";

	@Override
	public void onInitialize() {
		LOGGER.info("PandaDeathBan Started!");
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			DeathBanResetCommand.register(dispatcher);
		});
		ServerPlayConnectionEvents.JOIN.register(this::onJoin);
		ServerPlayerEvents.AFTER_RESPAWN.register(this::onRespawn);
	}

	private void onRespawn(ServerPlayerEntity serverPlayerEntity, ServerPlayerEntity serverPlayerEntity1, boolean b) {
		if(serverPlayerEntity.interactionManager.getGameMode() == GameMode.SPECTATOR) serverPlayerEntity.changeGameMode(GameMode.SURVIVAL);
	}

	private void onJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
		ServerPlayerEntity serverPlayerEntity = serverPlayNetworkHandler.getPlayer();
		if(serverPlayerEntity.interactionManager.getGameMode() == GameMode.SPECTATOR) serverPlayerEntity.changeGameMode(GameMode.SURVIVAL);
	}
	

}