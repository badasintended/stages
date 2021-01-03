package badasintended.stages.impl.data;

import java.util.Collection;

import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.impl.StagesMod;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;

public class StagesImpl implements Stages, Tickable {

    private static final Int2ObjectOpenHashMap<Identifier> I2O = new Int2ObjectOpenHashMap<>();
    private static final Object2IntOpenHashMap<Identifier> O2I = new Object2IntOpenHashMap<>();

    private static final String TAG_NAME = "Stages";

    private static int lastIntKey = 0;
    private static boolean registryLocked = false;

    public static void register(Identifier... stages) {
        if (registryLocked) {
            throw new UnsupportedOperationException("[stages] Attempting to register new stage at server runtime");
        }
        for (Identifier stage : stages) {
            if (O2I.containsKey(stage)) {
                I2O.remove(O2I.getInt(stage));
            }
            I2O.put(lastIntKey, stage);
            O2I.put(stage, lastIntKey);
            lastIntKey++;
        }
    }

    public static boolean isRegistered(Identifier stage) {
        return O2I.containsKey(stage);
    }

    public static int stage2int(Identifier stage) {
        return O2I.getInt(stage);
    }

    public static Identifier int2stage(int i) {
        return I2O.get(i);
    }

    public static Collection<Identifier> allStages() {
        return I2O.values();
    }

    public static void lockRegistry() {
        registryLocked = true;
    }

    public static void syncRegistry(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, StagesMod.BEGIN_SYNC_REGISTRY, new PacketByteBuf(Unpooled.buffer()));

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        int i = 0;
        for (Identifier value : I2O.values()) {
            if (i % 10 == 0) {
                ServerPlayNetworking.send(player, StagesMod.SYNC_REGISTRY, buf);
                buf = new PacketByteBuf(Unpooled.buffer());
            }
            buf.writeIdentifier(value);
            i++;
        }
        if (i % 10 != 0) {
            ServerPlayNetworking.send(player, StagesMod.SYNC_REGISTRY, buf);
        }

        ServerPlayNetworking.send(player, StagesMod.END_SYNC_REGISTRY, new PacketByteBuf(Unpooled.buffer()));
    }

    @Environment(EnvType.CLIENT)
    public static void beginSyncRegistry() {
        I2O.clear();
        O2I.clear();
        lastIntKey = 0;
        registryLocked = false;
    }

    @Environment(EnvType.CLIENT)
    public static void endSyncRegistry() {
        registryLocked = true;
    }


    // --------------------------------------------------------------------------------------------


    private final ObjectSet<Identifier> stages = new ObjectOpenHashSet<>();
    private final ObjectSet<Identifier> immutableStages = ObjectSets.unmodifiable(stages);

    private final PlayerEntity player;
    private final boolean isClient;

    private boolean changed = false;

    public StagesImpl(PlayerEntity player) {
        this.player = player;
        this.isClient = player instanceof ClientPlayerEntity;
    }

    private void assertServerSide() {
        if (isClient) {
            throw new UnsupportedOperationException("[stages] Attempting to modify stages on client side");
        }
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public boolean isClient() {
        return isClient;
    }

    @Override
    public Collection<Identifier> values() {
        return immutableStages;
    }

    @Override
    public boolean contains(Identifier stage) {
        return this.stages.contains(stage);
    }

    @Override
    public void add(Identifier stage) {
        assertServerSide();
        if (!O2I.containsKey(stage)) {
            StagesMod.LOGGER.error("[stages] Attempting to add unregistered stage id {}", stage);
        } else if (StageEvents.ADD.invoker().onAdd(this, stage)) {
            stages.add(stage);
            changed = true;
        }
    }

    @Override
    public void remove(Identifier stage) {
        assertServerSide();
        if (!O2I.containsKey(stage)) {
            StagesMod.LOGGER.error("[stages] Attempting to remove unregistered stage id {}", stage);
        } else if (StageEvents.REMOVE.invoker().onRemove(this, stage)) {
            stages.remove(stage);
            changed = true;
        }
    }

    @Override
    public void clear() {
        assertServerSide();
        stages.clear();
        changed = true;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        clear();
        ListTag list = tag.getList(TAG_NAME, NbtType.STRING);
        list.forEach(s -> stages.add(new Identifier(s.asString())));
        changed = true;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag list = new ListTag();
        stages.forEach(s -> list.add(StringTag.of(s.toString())));
        tag.put(TAG_NAME, list);
        return tag;
    }

    @Override
    public void tick() {
        if (changed) {
            StageEvents.CHANGED.invoker().onChanged(this);
            changed = false;
            if (!isClient) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeVarInt(stages.size());
                stages.forEach(s -> buf.writeVarInt(Stages.getRawId(s)));

                ServerPlayNetworking.send((ServerPlayerEntity) player, StagesMod.SYNC_STAGES, buf);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void sync(int[] stageIds) {
        stages.clear();
        for (int stageId : stageIds) {
            stages.add(int2stage(stageId));
        }
        changed = true;
    }

}
