package me.TreeOfSelf.PandaDeathBan.mixin;

import com.mojang.authlib.GameProfile;
import me.TreeOfSelf.PandaDeathBan.BanMessageUtil;
import me.TreeOfSelf.PandaDeathBan.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public abstract class KickOnJoinMixin {
    @Shadow public abstract MinecraftServer getServer();

    @Inject(at = @At("TAIL"), method = "checkCanJoin", cancellable = true)
    public void checkCanJoin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        StateSaverAndLoader.PlayerDeathBanData playerData = StateSaverAndLoader.getPlayerState(profile.getId(), this.getServer());
        long currentTimeMillis = System.currentTimeMillis() / 1000L;
        if (playerData.deathUnbanTime > currentTimeMillis) {
            cir.setReturnValue(BanMessageUtil.createBanMessage(playerData.deathUnbanTime));
        }
    }
}
