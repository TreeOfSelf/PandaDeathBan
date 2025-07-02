package me.TreeOfSelf.PandaDeathBan;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
	}
}