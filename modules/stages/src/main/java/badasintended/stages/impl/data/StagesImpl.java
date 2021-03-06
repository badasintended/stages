package badasintended.stages.impl.data;

import java.util.Collection;

import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.impl.StagesMod;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;

import static badasintended.stages.api.StagesUtil.s2c;

public class StagesImpl implements Stages, Tickable {

    private static final String TAG_NAME = "Stages";

    private final ObjectSet<Identifier> stages = new ObjectOpenHashSet<>();
    private final ObjectSet<Identifier> immutableStages = ObjectSets.unmodifiable(stages);

    private final ObjectSet<Identifier> cache = new ObjectOpenHashSet<>();

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
        if (!StageRegistry.isRegistered(stage)) {
            StagesMod.LOGGER.error("[stages] Attempting to add unregistered stage id {}", stage);
        } else if (StageEvents.ADD.invoker().onAdd(this, stage)) {
            stages.add(stage);
            changed = true;
        }
    }

    @Override
    public void remove(Identifier stage) {
        assertServerSide();
        if (!StageRegistry.isRegistered(stage)) {
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
        if (changed && !isClient) {
            changed = false;
            StageEvents.CHANGED.invoker().onChanged(this);
            s2c(player, StagesMod.SYNC_STAGE, buf -> {
                for (Identifier stage : stages) {
                    if (!cache.contains(stage)) {
                        buf.writeVarInt(StageRegistry.getRawId(stage));
                        buf.writeBoolean(true);
                    }
                }
                for (Identifier stage : cache) {
                    if (!stages.contains(stage)) {
                        buf.writeVarInt(StageRegistry.getRawId(stage));
                        buf.writeBoolean(false);
                    }
                }
            });
            cache.clear();
            cache.addAll(stages);
        }
    }

    @Environment(EnvType.CLIENT)
    public void sync(Int2BooleanOpenHashMap map) {
        map.forEach((id, add) -> {
            Identifier stage = StageRegistry.getStage(id);
            if (add) {
                stages.add(stage);
                StageEvents.ADDED.invoker().onAdded(this, stage);
            } else {
                stages.remove(stage);
                StageEvents.REMOVED.invoker().onRemoved(this, stage);
            }
        });
        StageEvents.CHANGED.invoker().onChanged(this);
    }

}
