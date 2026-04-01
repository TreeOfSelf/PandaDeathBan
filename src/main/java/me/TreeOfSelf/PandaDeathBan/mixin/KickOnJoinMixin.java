package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.BanMessageUtil;
import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class KickOnJoinMixin {

	@Inject(at = @At("RETURN"), method = "canPlayerLogin", cancellable = true)
	public void pandaDeathBan$canPlayerLogin(SocketAddress address, NameAndId nameAndId, CallbackInfoReturnable<Component> cir) {
		if (cir.getReturnValue() != null) {
			return;
		}
		PlayerList self = (PlayerList)(Object)this;
		StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(nameAndId.id(), self.getServer());
		long currentTimeMillis = System.currentTimeMillis() / 1000L;
		if (playerData.deathUnbanTime > currentTimeMillis) {
			cir.setReturnValue(BanMessageUtil.createBanMessage(self.getServer(), playerData.deathUnbanTime));
		} else {
			playerData.disconnectAtTick = -1;
			StateSaverAndLoader.getServerState(self.getServer()).setDirty();
		}
	}
}
