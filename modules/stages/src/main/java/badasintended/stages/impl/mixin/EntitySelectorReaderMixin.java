package badasintended.stages.impl.mixin;

import badasintended.stages.impl.command.StageSelectorHolder;
import net.minecraft.command.EntitySelectorReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntitySelectorReader.class)
public abstract class EntitySelectorReaderMixin implements StageSelectorHolder {

    @Unique
    private boolean selectsStages = false;

    @Override
    public boolean stages$selectsStages() {
        return selectsStages;
    }

    @Override
    public void stages$setSelectsStages(boolean selectsStages) {
        this.selectsStages = selectsStages;
    }

}
