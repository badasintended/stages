package badasintended.stages.impl.test;

import badasintended.stages.api.StageConstants;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StagesTest implements StagesInit {

    public static final String MOD_ID = "stages-test";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final Identifier
        A = id("a"),
        B = id("b"),
        C = id("c"),
        D = id("d"),
        E = id("e");

    public static Identifier id(String path) {
        return new Identifier(StageConstants.MOD_ID, "test/" + path);
    }

    @Override
    public void onStagesInit() {
        Stages.register(A, B, C, D, E);

        StageEvents.ADD.register((stages, stage) -> {
            LOGGER.info("add    : {}, {}", stages.getPlayer().getDisplayName().getString(), stage);
            return true;
        });
        StageEvents.ADDED.register((stages, stage) -> {
            LOGGER.info("added  : {}, {}", stages.getPlayer().getDisplayName().getString(), stage);
        });
        StageEvents.REMOVE.register((stages, stage) -> {
            LOGGER.info("remove : {}, {}", stages.getPlayer().getDisplayName().getString(), stage);
            return true;
        });
        StageEvents.REMOVED.register((stages, stage) -> {
            LOGGER.info("removed: {}, {}", stages.getPlayer().getDisplayName().getString(), stage);
        });
        StageEvents.CLEARED.register((stages) -> {
            LOGGER.info("cleared: {}", stages.getPlayer().getDisplayName().getString());
        });
    }

}
