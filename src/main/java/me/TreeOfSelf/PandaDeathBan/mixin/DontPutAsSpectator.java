package me.TreeOfSelf.PandaDeathBan.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class DontPutAsSpectator {
    
    @Redirect(method = "onClientStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isHardcore()Z"))
    private boolean redirectIsHardcore(MinecraftServer server) {
        return false;
    }
}
