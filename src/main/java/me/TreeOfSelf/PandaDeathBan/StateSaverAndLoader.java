package me.TreeOfSelf.PandaDeathBan;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.UUID;

public class StateSaverAndLoader extends SavedData {

    public static final Identifier DATA_ID = Identifier.withDefaultNamespace("panda-death-ban");

    private static final Codec<HashMap<UUID, PlayerDeathBanData>> PLAYERS_CODEC = Codec.unboundedMap(
            Codec.STRING.xmap(UUID::fromString, UUID::toString),
            PlayerDeathBanData.CODEC
    ).xmap(HashMap::new, map -> map);

    public static final Codec<StateSaverAndLoader> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PLAYERS_CODEC.optionalFieldOf("players", new HashMap<>()).forGetter(s -> s.players)
    ).apply(instance, StateSaverAndLoader::new));

    public static final SavedDataType<StateSaverAndLoader> TYPE = new SavedDataType<>(
            DATA_ID,
            StateSaverAndLoader::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    public HashMap<UUID, PlayerDeathBanData> players;

    public StateSaverAndLoader() {
        this.players = new HashMap<>();
    }

    public StateSaverAndLoader(HashMap<UUID, PlayerDeathBanData> players) {
        this.players = players;
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }

    public static PlayerDeathBanData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.level().getServer());
        return serverState.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerDeathBanData());
    }

    public static PlayerDeathBanData getPlayerState(UUID playerUUID, MinecraftServer server) {
        StateSaverAndLoader serverState = getServerState(server);
        return serverState.players.computeIfAbsent(playerUUID, uuid -> new PlayerDeathBanData());
    }

    public static class PlayerDeathBanData {
        public static final Codec<PlayerDeathBanData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.LONG.optionalFieldOf("deathUnbanTime", 0L).forGetter(d -> d.deathUnbanTime),
                Codec.LONG.optionalFieldOf("disconnectAtTick", -1L).forGetter(d -> d.disconnectAtTick)
        ).apply(i, PlayerDeathBanData::new));

        public long deathUnbanTime = 0L;
        public long disconnectAtTick = -1L;

        public PlayerDeathBanData() {
        }

        public PlayerDeathBanData(long deathUnbanTime, long disconnectAtTick) {
            this.deathUnbanTime = deathUnbanTime;
            this.disconnectAtTick = disconnectAtTick;
        }
    }
}
