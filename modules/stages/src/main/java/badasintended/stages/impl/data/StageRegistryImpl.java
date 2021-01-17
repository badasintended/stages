package badasintended.stages.impl.data;

import java.util.Collection;

import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.impl.StagesMod;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static badasintended.stages.api.StagesUtil.s2c;

public class StageRegistryImpl implements StageRegistry {

    private static StageRegistryImpl instance = null;

    public static StageRegistryImpl get() {
        if (instance == null) {
            instance = new StageRegistryImpl();
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    public static void lock() {
        get().locked = true;
    }

    public static boolean isRegistered(Identifier stage) {
        return get().o2i.containsKey(stage);
    }

    public static int stage2int(Identifier stage) {
        return get().o2i.getInt(stage);
    }

    public static Identifier int2stage(int i) {
        return get().i2o.get(i);
    }

    public static Collection<Identifier> allStages() {
        return get().o2i.keySet();
    }

    public static void syncRegistry(ServerPlayerEntity player) {
        s2c(player, StagesMod.BEGIN_SYNC_REGISTRY, buf -> {});
        get().o2i.forEach((stage, i) -> s2c(player, StagesMod.SYNC_REGISTRY, buf -> {
            buf.writeVarInt(i);
            buf.writeIdentifier(stage);
        }));
        s2c(player, StagesMod.END_SYNC_REGISTRY, buf -> {});
    }

    @Environment(EnvType.CLIENT)
    public static void syncRegistry(int i, Identifier stage) {
        get().i2o.put(i, stage);
        get().o2i.put(stage, i);
    }


    private final Int2ObjectOpenHashMap<Identifier> i2o = new Int2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Identifier> o2i = new Object2IntOpenHashMap<>();

    private int lastIntKey = 0;
    private boolean locked = false;

    @Override
    public void register(Identifier stage) {
        if (locked) {
            throw new IllegalStateException("Tried to touch registry impl and failed");
        }
        if (o2i.containsKey(stage)) {
            i2o.remove(o2i.getInt(stage));
        }
        i2o.put(lastIntKey, stage);
        o2i.put(stage, lastIntKey);
        lastIntKey++;
    }

}
