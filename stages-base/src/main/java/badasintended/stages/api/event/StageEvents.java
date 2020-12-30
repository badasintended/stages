package badasintended.stages.api.event;

import badasintended.stages.api.data.Stages;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

public final class StageEvents {

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
     * Called on {@link Stages#add(Identifier)} <b>after</b> a stage is added to a player.<br>
     * The player has the stage when this even is called.
     */
    public static final Event<Added> ADDED = createArrayBacked(Added.class, callbacks -> (stages, stage) -> {
        for (Added callback : callbacks) {
            callback.onAdded(stages, stage);
        }
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
     * Called on {@link Stages#remove(Identifier)} <b>after</b> a stage is removed from a player.<br>
     * The stage is already removed from player when this even is called.
     */
    public static final Event<Removed> REMOVED = createArrayBacked(Removed.class, callbacks -> (stages, stage) -> {
        for (Removed callback : callbacks) {
            callback.onRemoved(stages, stage);
        }
    });

    /**
     * Called on {@link Stages#clear()} <b>after</b> all stages are removed from a player.<br>
     * The player does not have any stage when this event is called.
     */
    public static final Event<Cleared> CLEARED = createArrayBacked(Cleared.class, callbacks -> (stages) -> {
        for (Cleared callback : callbacks) {
            callback.onCleared(stages);
        }
    });

    @FunctionalInterface
    public interface Add {

        boolean onAdd(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Added {

        void onAdded(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Remove {

        boolean onRemove(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Removed {

        void onRemoved(Stages stages, Identifier stage);

    }

    @FunctionalInterface
    public interface Cleared {

        void onCleared(Stages stages);

    }

}
