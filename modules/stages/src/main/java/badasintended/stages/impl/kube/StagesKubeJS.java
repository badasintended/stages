package badasintended.stages.impl.kube;

import badasintended.stages.api.event.StageEvents;
import dev.latvian.kubejs.script.ScriptType;

public class StagesKubeJS {

    public static final String CHANGED = "stages.changed";

    public static void init() {
        StageEvents.CHANGED.register(stages -> {
            if (!stages.isClient()) {
                new StageChangedEventJS(stages).post(ScriptType.SERVER, CHANGED);
            }
        });
    }

}
