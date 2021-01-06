package badasintended.stages.api.data;

import java.util.Collection;

import badasintended.stages.api.event.StageEvents;
import badasintended.stages.impl.data.StageRegistryImpl;
import net.minecraft.util.Identifier;

/**
 * @see StageEvents#REGISTRY
 */
public interface StageRegistry {

    /**
     * @return whether stage is registered
     */
    static boolean isRegistered(Identifier stage) {
        return StageRegistryImpl.isRegistered(stage);
    }

    /**
     * Get stage from raw id. Useful for s2c syncing
     */
    static Identifier getStage(int i) {
        return StageRegistryImpl.int2stage(i);
    }

    /**
     * Get raw id of a stage. Useful for s2c syncing
     */
    static int getRawId(Identifier stage) {
        return StageRegistryImpl.stage2int(stage);
    }

    /**
     * Get all registered stages
     */
    static Collection<Identifier> allStages() {
        return StageRegistryImpl.allStages();
    }

    /**
     * Register new stage.<br>
     */
    void register(Identifier stage);

    /**
     * Register new stages.<br>
     */
    default void register(Identifier... stages) {
        for (Identifier stage : stages) {
            register(stage);
        }
    }

    /**
     * Register new stages.<br>
     */
    default void register(Collection<Identifier> stages) {
        stages.forEach(this::register);
    }

}
