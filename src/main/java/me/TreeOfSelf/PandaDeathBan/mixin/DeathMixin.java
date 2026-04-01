package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.ConfigManager;
import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 10000)
public class DeathMixin {
	@Inject(at = @At("HEAD"), method = "die")
	private void onDeath(DamageSource source, CallbackInfo info) {
		ServerPlayer serverPlayer = (ServerPlayer)(Object)this;
		StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(serverPlayer);

		long currentTimeMillis = System.currentTimeMillis();
		playerData.deathUnbanTime = (currentTimeMillis / 1000L) + ConfigManager.getConfig().banDurationSeconds;
		playerData.disconnectAtTick = serverPlayer.level().getServer().getTickCount() + (ConfigManager.getConfig().disconnectTimerSeconds * 20);
		StateSaverAndLoader.getServerState(serverPlayer.level().getServer()).setDirty();
	}
}
