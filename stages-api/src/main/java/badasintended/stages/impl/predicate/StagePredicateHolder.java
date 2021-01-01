package badasintended.stages.impl.predicate;

import java.util.Set;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface StagePredicateHolder {

    Set<Identifier> stages$getMust();

    Set<Identifier> stages$getMustNot();

}
