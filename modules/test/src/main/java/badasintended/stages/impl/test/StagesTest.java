package badasintended.stages.impl.test;

import badasintended.stages.api.StagesUtil;
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
        return new Identifier(StagesUtil.MOD_ID, "test/" + path);
    }

    @Override
    public void onStagesInit() {

        StageEvents.REGISTRY.register(registry ->
            registry.register(A, B, C, D, E)
        );

        StageEvents.CHANGED.register(stages ->
            LOGGER.info("changed {}", stages.getPlayer().getDisplayName().getString())
        );

        StageEvents.ADD.register((stages, stage) -> {
            LOGGER.info("add    : {}, {}", stages.getPlayer().getDisplayName().getString(), stage);
            return true;
        });

        StageEvents.REMOVE.register((stages, stage) -> {
            LOGGER.info("remove : {}, {}", stages.getPlayer().getDisplayName().getString(), stage);
            return true;
        });
    }

}
