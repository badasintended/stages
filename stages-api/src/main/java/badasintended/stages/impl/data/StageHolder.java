package badasintended.stages.impl.data;

import badasintended.stages.api.data.Stages;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface StageHolder {

    Stages stages$getStages();

    void stages$scheduleSync();

}
