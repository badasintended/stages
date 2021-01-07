package badasintended.blockstages.mixin;

import java.util.Map;

import badasintended.blockstages.BlockStagesHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements BlockStagesHolder {

    @Unique
    private final Map<Block, Block> lockedBlocks = new Object2ObjectOpenHashMap<>();

    @Unique
    private boolean reload = false;

    @Override
    public Map<Block, Block> stages$getLockedBlocks() {
        return lockedBlocks;
    }

    @Override
    public boolean stages$shouldReload() {
        return reload;
    }

    @Override
    public void stages$setReload(boolean reload) {
        this.reload = reload;
    }

}
