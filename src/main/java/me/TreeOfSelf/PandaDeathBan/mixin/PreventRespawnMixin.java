package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class PreventRespawnMixin {

	@Shadow
	public ServerPlayer player;

	@Inject(at = @At("HEAD"), method = "handleClientCommand", cancellable = true)
	private void pandaDeathBan$preventRespawnIfPending(ServerboundClientCommandPacket packet, CallbackInfo ci) {
		if (packet.getAction() == ServerboundClientCommandPacket.Action.PERFORM_RESPAWN) {
			StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(player);
			if (playerData.disconnectAtTick != -1) {
				ci.cancel();
			}
		}
	}
}
