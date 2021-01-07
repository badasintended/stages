package badasintended.blockstages;

import java.util.Map;

import net.minecraft.block.Block;

public interface BlockStagesHolder {

    Map<Block, Block> stages$getLockedBlocks();

    boolean stages$shouldReload();

    void stages$setReload(boolean reload);

}
