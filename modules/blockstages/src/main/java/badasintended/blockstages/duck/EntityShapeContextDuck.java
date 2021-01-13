package badasintended.blockstages.duck;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;

public interface EntityShapeContextDuck {

    static PlayerEntity getPlayer(ShapeContext context) {
        if (context instanceof EntityShapeContext) {
            return ((EntityShapeContextDuck) context).stages$getPlayer();
        }
        return null;
    }

    PlayerEntity stages$getPlayer();

}
