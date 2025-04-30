package me.TreeOfSelf.PandaDeathBan;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

import static me.TreeOfSelf.PandaDeathBan.PandaDeathBan.MOD_ID;

public class StateSaverAndLoader extends PersistentState {

    public static Codec<StateSaverAndLoader> codec(ServerWorld world) {
        return Codec.of(new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(StateSaverAndLoader stateSaverAndLoader, DynamicOps<T> dynamicOps, T t) {
                NbtCompound nbtCompound = new NbtCompound();
                stateSaverAndLoader.writeNbt(nbtCompound);
                return DataResult.success((T) nbtCompound);
            }
        }, new Decoder<>() {
            @Override
            public <T> DataResult<Pair<StateSaverAndLoader, T>> decode(DynamicOps<T> ops, T input) {
                NbtCompound nbtCompound = (NbtCompound) ops.convertTo(NbtOps.INSTANCE, input);
                StateSaverAndLoader partnerState = createFromNbt(nbtCompound, world.getRegistryManager());
                return DataResult.success(Pair.of(partnerState, ops.empty()));
            }
        });
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players").get();
        playersNbt.getKeys().forEach(key -> {
            PlayerDeathBanData playerData = new PlayerDeathBanData();

            playerData.deathUnbanTime = playersNbt.getCompound(key).get().getLong("deathUnbanTime").get();

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public HashMap<UUID, PlayerDeathBanData> players = new HashMap<>();

    public static StateSaverAndLoader readNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players").get();
        playersNbt.getKeys().forEach(key -> {
            PlayerDeathBanData playerData = new PlayerDeathBanData();

            playerData.deathUnbanTime = playersNbt.getCompound(key).get().getLong("deathUnbanTime").get();

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }




    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
                MOD_ID,
                StateSaverAndLoader::new,
                codec(server.getWorld(World.OVERWORLD)),
                null
        );

        StateSaverAndLoader state = persistentStateManager.getOrCreate(type);
        state.markDirty();
        return state;
    }

    public static PlayerDeathBanData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerDeathBanData());
    }

    public static PlayerDeathBanData getPlayerState(UUID playerUUID, MinecraftServer server) {
        StateSaverAndLoader serverState = getServerState(server);
        return serverState.players.computeIfAbsent(playerUUID, uuid -> new PlayerDeathBanData());
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putLong("deathUnbanTime", playerData.deathUnbanTime);

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static class PlayerDeathBanData {
        public Long deathUnbanTime = 0L;
    }
}