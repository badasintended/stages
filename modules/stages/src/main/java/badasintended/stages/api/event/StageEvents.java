package badasintended.stages.api.event;

import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

public final class StageEvents {

    /**
     * Called on {@link ServerLifecycleEvents#SERVER_STARTING}, where you should register stages.
     */
    public static final Event<Registry> REGISTRY = createArrayBacked(Registry.class, callbacks -> registry -> {
        for (Registry callback : callbacks) {
            callback.onRegister(registry);
        }
    });

    /**
     * Called after {@link StageRegistry} is reloaded via /reload command.<br>
     * Keep in mind that registry is loaded way before datapacks
     *
     * @see #REGISTRY
     */
    public static final Event<RegistryReloaded> REGISTRY_RELOADED = createArrayBacked(RegistryReloaded.class, callbacks -> server -> {
        for (RegistryReloaded callback : callbacks) {
            callback.onRegistryReloaded(server);
        }
    });

    /**
     * Called on {@link Stages#add(Identifier)} <b>before</b> a stage is added to a player.<br>
     * The player does <b>not</b> have the stage when this event is called.<br>
     * return {@code false} to cancel the addition
     */
    public static final Event<Add> ADD = createArrayBacked(Add.class, callbacks -> (stages, stage) -> {
        for (Add callback : callbacks) {
            if (!callback.onAdd(stages, stage)) {
                return false;
            }
        }
        return true;
    });

    /**
     * Called on {@link Stages#remove(Identifier)} <b>before</b> a stage is removed from a player.<br>
     * The player <b>still</b> has the stage when this event is called.<br>
     * return {@code false} to cancel the removal
     */
    public static final Event<Remove> REMOVE = createArrayBacked(Remove.class, callbacks -> (stages, stage) -> {
        for (Remove callback : callbacks) {
            if (!callback.onRemove(stages, stage)) {
                return false;
            }
        }
        return true;
    });

    /**
     * Called on {@link Stages#add(Identifier)} immediately <b>after</b> a stage is added to a player.<br>
     * The player has the stage when this event is called.<br>
     * Called on both server and client side.
     */
    public static final Event<Added> ADDED = createArrayBacked(Added.class, callbacks -> (stages, stage) -> {
        for (Added callback : callbacks) {
            callback.onAdded(stages, stage);
        }
    });

    /**
     * Called on {@link Stages#remove(Identifier)} immediately <b>after</b> a stage is removed from a player.<br>
     * The player does not have the stage when this event is called.<br>
     * Called on both server and client side.
     */
    public static final Event<Removed> REMOVED = createArrayBacked(Removed.class, callbacks -> (stages, stage) -> {
        for (Removed callback : callbacks) {
            callback.onRemoved(stages, stage);
        }
    });

    /**
     * Called on the next tick after {@link Stages} is changed (addition, removal).<br>
     * Called both on server and client side.<br>
     * Consider using {@link #ADDED} or {@link #REMOVED} instead.
     */
    public static final Event<Changed> CHANGED = createArrayBacked(Changed.class, callbacks -> stages -> {
        for (Changed callback : callbacks) {
            callback.onChanged(stages);
        }
    });

    @FunctionalInterface
    public interface Registry {

        void onRegister(StageRegistry registry);

    }

    @FunctionalInterface
    public interface RegistryReloaded {

        void onRegistryReloaded(MinecraftServer server);

    }

    @FunctionalInterface
    public interface Add {

        boolean onAdd(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Remove {

        boolean onRemove(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Added {

        void onAdded(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Removed {

        void onRemoved(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Changed {

        void onChanged(Stages stages);

    }

}
