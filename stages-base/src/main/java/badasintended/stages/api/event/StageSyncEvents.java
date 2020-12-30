package badasintended.stages.api.event;

import badasintended.stages.api.data.Stages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

@Environment(EnvType.CLIENT)
public final class StageSyncEvents {

    /**
     * Called after server {@link Stages} is synced to the client
     */
    public static final Event<Sync> SYNC = createArrayBacked(Sync.class, callbacks -> stages -> {
        for (Sync callback : callbacks) {
            callback.onSync(stages);
        }
    });

    @FunctionalInterface
    public interface Sync {

        void onSync(Stages stages);

    }

}
