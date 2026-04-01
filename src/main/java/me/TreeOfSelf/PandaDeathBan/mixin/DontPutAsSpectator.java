package me.TreeOfSelf.PandaDeathBan.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class DontPutAsSpectator {

	@Redirect(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isHardcore()Z"))
	private boolean redirectIsHardcore(MinecraftServer server) {
		return false;
	}
}
