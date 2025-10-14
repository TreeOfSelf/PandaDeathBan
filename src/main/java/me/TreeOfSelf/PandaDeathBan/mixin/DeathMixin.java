package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.ConfigManager;
import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayerEntity.class, priority = 10000)
public class DeathMixin {
	@Inject(at = @At("HEAD"), method = "onDeath")
	private void onDeath(CallbackInfo info) {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)(Object)this;
		StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(serverPlayerEntity);

		long currentTimeMillis = System.currentTimeMillis();
		playerData.deathUnbanTime = (currentTimeMillis / 1000L) + ConfigManager.getConfig().banDurationSeconds;
		playerData.disconnectAtTick = serverPlayerEntity.getEntityWorld().getServer().getTicks() + 100;
		StateSaverAndLoader.getServerState(serverPlayerEntity.getEntityWorld().getServer()).markDirty();
	}
}