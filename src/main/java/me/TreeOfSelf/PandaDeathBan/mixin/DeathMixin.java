package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.BanMessageUtil;
import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayerEntity.class, priority = 10000)
public class DeathMixin {
	@Inject(at = @At("HEAD"), method = "onSpawn")
	private void onSpawn(CallbackInfo info) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)(Object)this;
		if(serverPlayerEntity.isSpectator()) serverPlayerEntity.changeGameMode(GameMode.SURVIVAL);
	}

	@Inject(at = @At("HEAD"), method = "onDisconnect")
	private void onDisconnect(CallbackInfo info) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)(Object)this;
		if(serverPlayerEntity.isSpectator()) serverPlayerEntity.changeGameMode(GameMode.SURVIVAL);
	}

	@Inject(at = @At("HEAD"), method = "onDeath")
	private void onDeath(CallbackInfo info) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)(Object)this;
		StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(serverPlayerEntity);

		long currentTimeMillis = System.currentTimeMillis();
		long oneWeekMillis = 7 * 24 * 60 * 60 * 1000L;

        playerData.deathUnbanTime = (currentTimeMillis / 1000L) + (oneWeekMillis / 1000L);
		StateSaverAndLoader.getServerState(serverPlayerEntity.getServer()).markDirty();

		serverPlayerEntity.networkHandler.disconnect(BanMessageUtil.createBanMessage(playerData.deathUnbanTime));
	}
}