package badasintended.itemstages;

import badasintended.stages.api.StagesUtil;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;

public class ItemStagesReiPlugin implements REIPluginV0 {

    private static final Identifier ID = StagesUtil.id("item/rei");

    @Override
    public Identifier getPluginIdentifier() {
        return ID;
    }

    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        entryRegistry.removeEntryIf(entryStack -> entryStack.getType() == EntryStack.Type.ITEM && entryStack.getItem() == ItemStages.UNKNOWN_ITEM);
    }

}
