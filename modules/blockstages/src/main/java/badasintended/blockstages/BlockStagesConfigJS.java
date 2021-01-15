package badasintended.blockstages;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockStagesConfigJS extends EventJS {

    public static final String EVENT = "blockstages";

    public static void fire(BlockStagesConfig config) {
        new BlockStagesConfigJS(config).post(ScriptType.SERVER, EVENT);
    }

    private final BlockStagesConfig config;

    public BlockStagesConfigJS(BlockStagesConfig config) {
        this.config = config;
    }

    public void add(String id, String target, String as) {
        BlockStagesConfig.Entry entry = new BlockStagesConfig.Entry();
        if (target.startsWith("#")) {
            entry.tag = (Tag.Identified<Block>) TagRegistry.block(new Identifier(target.substring(1)));
        } else {
            entry.block = Registry.BLOCK.get(new Identifier(target));
        }
        entry.as = Registry.BLOCK.get(new Identifier(as));
        config.entries.put(BlockStages.id(id), entry);
    }

    public void remove(String id) {
        config.entries.remove(BlockStages.id(id));
    }

}
