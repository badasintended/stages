package badasintended.stages.api.data;

import java.util.Arrays;
import java.util.Collection;

import badasintended.stages.api.event.StageSyncEvents;
import badasintended.stages.impl.data.StageHolder;
import badasintended.stages.impl.data.StagesImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * Contains all unlocked stages from a player
 * <p><ul>
 * <li>All stage ids must be registered first before it can be added to player</li>
 * <li>A {@link Stages} needs to be manually synced to client, therefore shouldn't be used unless you know what you do</li>
 * <li>Always call {@link Stages#sync()} after you done changing things</li>
 * </ul><p>
 *
 * @see Stages#register(Identifier...)
 */
public interface Stages {

    /**
     * Get a {@link Stages} from a player
     */
    static Stages get(PlayerEntity player) {
        return ((StageHolder) player).stages$getStages();
    }

    /**
     * Register stages
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
     * Get stage from raw id
     */
    static Identifier getStage(int i) {
        return StagesImpl.int2stage(i);
    }

    /**
     * Get raw id of a stage
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

    PlayerEntity getPlayer();

    boolean isClient();

    /**
     * Get an immutable collection of unlocked stages
     */
    Collection<Identifier> values();

    /**
     * @return whether player has the stage
     */
    boolean contains(Identifier stage);

    /**
     * Add new stage to player
     * <br>
     * All stages ids must be registered first before it can be added to player
     *
     * @see Stages#register(Identifier...)
     */
    void add(Identifier stage);


    /**
     * Remove a stage from player
     */
    void remove(Identifier stage);

    /**
     * Clear all stage to player
     */
    void clear();

    /**
     * Schedule an S2C sync on the next tick.
     * <p><ul>
     * <li>When called on client, it'll sends a packet to request sync from server</li>
     * <li>When called on server, it'll sends the stages to client</li>
     * </ul><p>
     * After the data is synced, it'll call {@link StageSyncEvents#SYNC} event on the client
     */
    void sync();

    void fromTag(CompoundTag tag);

    CompoundTag toTag(CompoundTag tag);

    /**
     * @return whether player has all the stages
     */
    default boolean containsAll(Identifier... stages) {
        return containsAll(Arrays.asList(stages));
    }

    /**
     * @return whether player has all the stages
     */
    default boolean containsAll(Collection<Identifier> stages) {
        for (Identifier id : stages) {
            if (!contains(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return whether player has one or more stages
     */
    default boolean containsAny(Identifier... stages) {
        return containsAny(Arrays.asList(stages));
    }

    /**
     * @return whether player has one or more stages
     */
    default boolean containsAny(Collection<Identifier> stages) {
        for (Identifier id : stages) {
            if (contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add new stages to player
     * <br>
     * All stages ids must be registered first before it can be added to player
     *
     * @see Stages#register(Identifier...)
     */
    default void addAll(Identifier... stages) {
        for (Identifier id : stages) {
            add(id);
        }
    }

    /**
     * Add new stages to player
     * <br>
     * All stages ids must be registered first before it can be added to player
     *
     * @see Stages#register(Identifier...)
     */
    default void addAll(Collection<Identifier> stages) {
        stages.forEach(this::add);
    }

    /**
     * Remove stages from player
     */
    default void removeAll(Identifier... ids) {
        for (Identifier id : ids) {
            remove(id);
        }
    }

    /**
     * Remove stages from player
     */
    default void removeAll(Collection<Identifier> stages) {
        stages.forEach(this::remove);
    }

}
