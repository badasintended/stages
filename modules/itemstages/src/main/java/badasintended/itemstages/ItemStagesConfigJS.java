package badasintended.itemstages;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.MapJS;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStagesConfigJS extends EventJS {

    public static final String EVENT = "itemstages";

    public static void fire(ItemStagesConfig config) {
        new ItemStagesConfigJS(config).post(ScriptType.SERVER, EVENT);
    }

    private final ItemStagesConfig config;

    public ItemStagesConfigJS(ItemStagesConfig config) {
        this.config = config;
    }

    public void add(String id, String target) {
        add(id, target, null);
    }

    public void add(String id, String target, Object nbt) {
        ItemStagesConfig.Entry entry = new ItemStagesConfig.Entry();
        if (target.startsWith("#")) {
            entry.tag = (Tag.Identified<Item>) TagRegistry.item(new Identifier(target.substring(1)));
        } else {
            entry.item = Registry.ITEM.get(new Identifier(target));
        }
        entry.nbt = MapJS.nbt(nbt);
        if (entry.nbt == null) {
            entry.nbt = ItemStages.EMPTY_TAG;
        }
        config.entries.put(ItemStages.id(id), entry);
    }

    public void remove(String id) {
        config.entries.remove(new Identifier(id));
    }

    public void settings(Object obj) {
        MapJS map = MapJS.of(obj);
        if (map != null) {
            ItemStagesConfig.Settings settings = config.settings;
            settings.dropWhenOnHand = (boolean) map.getOrDefault("dropWhenOnHand", settings.dropWhenOnHand);
            settings.dropWhenOnCursor = (boolean) map.getOrDefault("dropWhenOnCursor", settings.dropWhenOnCursor);
            settings.changeModel = (boolean) map.getOrDefault("changeModel", settings.changeModel);
            settings.hideTooltip = (boolean) map.getOrDefault("hideTooltip", settings.hideTooltip);
            settings.preventToInventory = (boolean) map.getOrDefault("preventToInventory", settings.preventToInventory);
            settings.hideFromRei = (boolean) map.getOrDefault("hideFromRei", settings.hideFromRei);
        }
    }

}
