package badasintended.stages.impl.predicate;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface StagePredicateHolder {

    void stages$setPredicate(StagePredicate predicate);

}
