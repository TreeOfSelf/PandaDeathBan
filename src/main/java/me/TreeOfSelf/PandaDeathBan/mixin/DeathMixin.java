package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.BanMessageUtil;
import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerEntity.class, priority = 10000)
public class DeathMixin {
	@Inject(at = @At("HEAD"), method = "onDeath")
	private void onDeath(CallbackInfo info) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)(Object)this;
		StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(serverPlayerEntity);

		long currentTimeMillis = System.currentTimeMillis();
		long oneWeekMillis = 7 * 24 * 60 * 60 * 1000L;

        playerData.deathUnbanTime = (currentTimeMillis / 1000L) + (oneWeekMillis / 1000L);
		playerData.pendingDisconnect = java.util.Optional.of(true);
		StateSaverAndLoader.getServerState(serverPlayerEntity.getServer()).markDirty();

		serverPlayerEntity.getServer().execute(() -> {
			try {
				Thread.sleep(5000);
				if (serverPlayerEntity.networkHandler != null) {
					long newCurrentTimeMillis = System.currentTimeMillis();
					playerData.deathUnbanTime = (newCurrentTimeMillis / 1000L) + (oneWeekMillis / 1000L);
					playerData.pendingDisconnect = java.util.Optional.of(false);
					StateSaverAndLoader.getServerState(serverPlayerEntity.getServer()).markDirty();
					serverPlayerEntity.networkHandler.disconnect(BanMessageUtil.createBanMessage(playerData.deathUnbanTime));
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
	}
}