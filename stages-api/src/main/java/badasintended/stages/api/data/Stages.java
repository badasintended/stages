package badasintended.stages.api.data;

import java.util.Collection;

import badasintended.stages.impl.data.StageHolder;
import badasintended.stages.impl.data.StagesImpl;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * Contains all unlocked stages from a player
 * <ul>
 *     <li>All stage ids must be registered first before it can be added to player.</li>
 *     <li>A {@link Stages} can only be modified on the server.</li>
 *     <li>All changes will automatically be synced to client</li>
 * </ul>
 *
 * @see Stages#register(Identifier...)
 */
@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "unused"})
public interface Stages {

    /**
     * Get a {@link Stages} from a player
     */
    static Stages get(PlayerEntity player) {
        return ((StageHolder) player).stages$getStages();
    }

    /**
     * Register new stages.<br>
     * Registry will be locked after {@link ServerLifecycleEvents#SERVER_STARTED} is called
     * and further attempt will throw an {@link UnsupportedOperationException}.
     */
    static void register(Identifier... stages) {
        StagesImpl.register(stages);
    }

    /**
     * @return whether stage is registered
     */
    static boolean isRegistered(Identifier stage) {
        return StagesImpl.isRegistered(stage);
    }

    /**
     * Get stage from raw id. Useful for s2c syncing
     */
    static Identifier getStage(int i) {
        return StagesImpl.int2stage(i);
    }

    /**
     * Get raw id of a stage. Useful for s2c syncing
     */
    static int getRawId(Identifier stage) {
        return StagesImpl.stage2int(stage);
    }

    /**
     * Get all registered stages
     */
    static Collection<Identifier> allStages() {
        return StagesImpl.allStages();
    }


    /* -------------------------------------------------------------------------------------------------------------------------------------------- */


    /**
     * @return the holder of the stages
     */
    PlayerEntity getPlayer();

    /**
     * @return whether you are in client side
     */
    boolean isClient();

    /**
     * @return an <b>immutable</b> collection of unlocked stages
     */
    Collection<Identifier> values();

    /**
     * @return whether player has the stage
     */
    boolean contains(Identifier stage);

    /**
     * Add new stage to player
     * <ul>
     *     <li>All stages ids must be {@link #register(Identifier...) registered} first before it can be added to player</li>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    void add(Identifier stage);


    /**
     * Remove a stage from player
     * <ul>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    void remove(Identifier stage);

    /**
     * Clear all stage to player
     * <ul>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    void clear();

    void fromTag(CompoundTag tag);

    CompoundTag toTag(CompoundTag tag);


    /* -------------------------------------------------------------------------------------------------------------------------------------------- */


    /**
     * @return whether player has all the stages
     */
    default boolean containsAll(Identifier... stages) {
        for (Identifier stage : stages) {
            if (!contains(stage)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return whether player has all the stages
     */
    default boolean containsAll(Collection<Identifier> stages) {
        for (Identifier stage : stages) {
            if (!contains(stage)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return whether player has one or more stages
     */
    default boolean containsAny(Identifier... stages) {
        for (Identifier stage : stages) {
            if (contains(stage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return whether player has one or more stages
     */
    default boolean containsAny(Collection<Identifier> stages) {
        for (Identifier stage : stages) {
            if (contains(stage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add new stages to player
     * <ul>
     *     <li>All stages ids must be {@link #register(Identifier...) registered} first before it can be added to player</li>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    default void addAll(Identifier... stages) {
        for (Identifier id : stages) {
            add(id);
        }
    }

    /**
     * Add new stages to player
     * <ul>
     *     <li>All stages ids must be {@link #register(Identifier...) registered} first before it can be added to player</li>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    default void addAll(Collection<Identifier> stages) {
        stages.forEach(this::add);
    }

    /**
     * Remove stages from player
     * <ul>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    default void removeAll(Identifier... ids) {
        for (Identifier id : ids) {
            remove(id);
        }
    }

    /**
     * Remove stages from player
     * <ul>
     *     <li>Can only be performed on server</li>
     * </ul>
     */
    default void removeAll(Collection<Identifier> stages) {
        stages.forEach(this::remove);
    }

}
