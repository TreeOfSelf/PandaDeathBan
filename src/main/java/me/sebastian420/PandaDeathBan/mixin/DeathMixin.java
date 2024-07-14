package me.sebastian420.PandaDeathBan.mixin;

import me.sebastian420.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.sebastian420.PandaDeathBan.BanMessageUtil.createBanMessage;

@Mixin(value = ServerPlayerEntity.class, priority = 10000)
public class DeathMixin {
	@Inject(at = @At("HEAD"), method = "onDeath")
	private void onDeath(CallbackInfo info) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)(Object)this;
		StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(serverPlayerEntity);

		long currentTimeMillis = System.currentTimeMillis();
		long oneWeekMillis = 7 * 24 * 60 * 60 * 1000L;

        playerData.deathUnbanTime = (currentTimeMillis / 1000L) + (oneWeekMillis / 1000L);
		StateSaverAndLoader.getServerState(serverPlayerEntity.getServer()).markDirty();

		serverPlayerEntity.networkHandler.disconnect(createBanMessage(playerData.deathUnbanTime));
	}
}