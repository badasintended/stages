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

public class StagesImpl implements Stages {

    private static final Int2ObjectOpenHashMap<Identifier> I2O = new Int2ObjectOpenHashMap<>();
    private static final Object2IntOpenHashMap<Identifier> O2I = new Object2IntOpenHashMap<>();

    private static final String TAG_NAME = "Stages";

    private static int lastIntKey = 0;

    private final ObjectSet<Identifier> stages = new ObjectOpenHashSet<>();
    private final ObjectSet<Identifier> immutableStages = ObjectSets.unmodifiable(stages);

    private final PlayerEntity player;
    private final boolean isClient;

    private boolean skipEvents = false;

    public StagesImpl(PlayerEntity player) {
        this.player = player;
        this.isClient = player instanceof ClientPlayerEntity;
    }

    public static void register(Identifier... stages) {
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

    public static void syncRegistry(ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(I2O.size());
        I2O.forEach((i, id) -> buf.writeIdentifier(id));
        ServerPlayNetworking.send(player, StagesMod.SYNC_REGISTRY, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void syncRegistry(Identifier... stages) {
        I2O.clear();
        O2I.clear();
        lastIntKey = 0;
        register(stages);
    }

    public void toggleEvents() {
        this.skipEvents = !this.skipEvents;
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
        if (O2I.containsKey(stage)) {
            if (skipEvents) {
                stages.add(stage);
            } else if (StageEvents.ADD.invoker().onAdd(this, stage)) {
                stages.add(stage);
                StageEvents.ADDED.invoker().onAdded(this, stage);
            }
        } else {
            StagesMod.LOGGER.error("[stages] Attempting to add unregistered stage id {}", stage);
        }
    }

    @Override
    public void remove(Identifier stage) {
        if (skipEvents) {
            stages.remove(stage);
        } else if (StageEvents.REMOVE.invoker().onRemove(this, stage)) {
            stages.remove(stage);
            StageEvents.REMOVED.invoker().onRemoved(this, stage);
        }
    }

    @Override
    public void clear() {
        stages.clear();
        if (!skipEvents) {
            StageEvents.CLEARED.invoker().onCleared(this);
        }
    }

    @Override
    public void sync() {
        ((StageHolder) player).stages$scheduleSync();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        toggleEvents();
        clear();
        ListTag list = tag.getList(TAG_NAME, NbtType.STRING);
        list.forEach(s -> add(new Identifier(s.asString())));
        toggleEvents();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag list = new ListTag();
        stages.forEach(s -> list.add(StringTag.of(s.toString())));
        tag.put(TAG_NAME, list);
        return tag;
    }


}
