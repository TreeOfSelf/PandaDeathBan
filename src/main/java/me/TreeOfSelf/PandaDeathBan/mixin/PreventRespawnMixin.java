package me.TreeOfSelf.PandaDeathBan.mixin;

import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class PreventRespawnMixin {
    
    @Shadow
    public ServerPlayerEntity player;
    
    @Inject(at = @At("HEAD"), method = "onClientStatus", cancellable = true)
    private void preventRespawnIfPending(ClientStatusC2SPacket packet, CallbackInfo ci) {
        if (packet.getMode() == ClientStatusC2SPacket.Mode.PERFORM_RESPAWN) {
            StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(player);
            if (playerData.disconnectAtTick != -1) {
                ci.cancel();
            }
        }
    }
} 