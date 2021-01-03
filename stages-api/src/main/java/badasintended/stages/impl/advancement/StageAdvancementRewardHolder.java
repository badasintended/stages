package badasintended.stages.impl.advancement;

import java.util.Set;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface StageAdvancementRewardHolder {

    Set<Identifier> stages$getReward();

}
