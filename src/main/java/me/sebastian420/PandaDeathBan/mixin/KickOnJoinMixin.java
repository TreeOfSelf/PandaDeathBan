package me.sebastian420.PandaDeathBan.mixin;

import me.sebastian420.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.sebastian420.PandaDeathBan.BanMessageUtil.createBanMessage;

@Mixin(PlayerManager.class)
public class KickOnJoinMixin {
    @Inject(at = @At("HEAD"), method = "onPlayerConnect", cancellable = true)
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(player);

        long currentTimeMillis = System.currentTimeMillis() / 1000L;

        if (playerData.deathUnbanTime > currentTimeMillis) {
            connection.disconnect(createBanMessage(playerData.deathUnbanTime));
        }

        ci.cancel();
    }
}
