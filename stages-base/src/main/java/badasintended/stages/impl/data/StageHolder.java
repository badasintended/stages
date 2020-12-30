package badasintended.stages.impl.data;

import badasintended.stages.api.data.Stages;

public interface StageHolder {

    Stages stages$getStages();

    void stages$scheduleSync();

}
