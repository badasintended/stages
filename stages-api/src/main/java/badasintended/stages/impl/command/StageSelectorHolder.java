package badasintended.stages.impl.command;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface StageSelectorHolder {

    boolean stages$selectsStages();

    void stages$setSelectsStages(boolean selectsStages);

}
