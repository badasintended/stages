package badasintended.stages.api.event;

import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
     * Called on the next tick after {@link Stages} is changed (addition, removal).<br>
     * Called both on server and client side.
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
    public interface Add {

        boolean onAdd(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Remove {

        boolean onRemove(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Changed {

        void onChanged(Stages stages);

    }

}
