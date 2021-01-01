package badasintended.itemstages;

import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

public interface ItemStageHolder {

    Map<Item, Set<CompoundTag>> stages$getLockedItems();

}
