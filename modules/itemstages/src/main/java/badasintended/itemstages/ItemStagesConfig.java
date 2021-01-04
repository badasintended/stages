package badasintended.itemstages;


import java.io.IOException;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStagesConfig {

    public final Settings settings = new Settings();
    public final Map<Identifier, Entry> entries = new Object2ObjectOpenHashMap<>();

    public static class Settings {

        // @formatter:off
        public final boolean
            dropWhenOnHand     = true,
            dropWhenOnCursor   = true,
            changeModel        = true,
            hideTooltip        = true,
            preventToInventory = true,
            hideFromRei        = true;
        // @formatter:on

    }

    public static class Entry {

        public final Item item;
        public final Tag.Identified<Item> tag;
        public final CompoundTag nbt;
        public final String name;

        public Entry(Item item, Tag.Identified<Item> tag, CompoundTag nbt, String name) {
            this.item = item;
            this.tag = tag;
            this.nbt = nbt;
            this.name = name;
        }

        public static class Adapter extends TypeAdapter<Entry> {

            @Override
            public Entry read(JsonReader in) throws IOException {
                Item item = Items.AIR;
                Tag.Identified<Item> tag = null;
                CompoundTag nbt = ItemStages.EMPTY_TAG;
                String name = "";

                String id = "";
                if (in.peek() == JsonToken.STRING) {
                    id = in.nextString();
                } else {
                    in.beginObject();
                    while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
                        String propName = in.nextName();
                        switch (propName) {
                            case "id":
                                id = in.nextString();
                                break;
                            case "nbt":
                                try {
                                    nbt = StringNbtReader.parse(in.nextString());
                                } catch (CommandSyntaxException e) {
                                    throw new IOException(e);
                                }
                                break;
                            case "name":
                                name = in.nextString();
                                break;
                        }
                    }
                    in.endObject();
                }

                if (id.isEmpty()) {
                    throw new IOException("Needs item/tag id");
                }

                if (id.startsWith("#")) {
                    tag = (Tag.Identified<Item>) TagRegistry.item(new Identifier(id.substring(1)));
                } else {
                    item = Registry.ITEM.get(new Identifier(id));
                }

                return new Entry(item, tag, nbt, name);
            }

            @Override
            public void write(JsonWriter out, Entry value) throws IOException {
                if (value.name.isEmpty() && value.nbt.isEmpty()) {
                    out.value(value.tag == null ? Registry.ITEM.getId(value.item).toString() : "#" + value.tag.getId().toString());
                } else {
                    out.beginObject();
                    out.name("id");
                    if (value.tag != null) {
                        out.value("#" + value.tag.getId().toString());
                    } else {
                        out.value(Registry.ITEM.getId(value.item).toString());
                    }
                    if (!value.nbt.isEmpty()) {
                        out.name("nbt");
                        out.value(value.nbt.toString());
                    }
                    if (!value.name.isEmpty()) {
                        out.name("name");
                        out.value(value.name);
                    }
                    out.endObject();
                }
            }

        }

    }

    public static class IdentifierAdapter extends TypeAdapter<Identifier> {

        @Override
        public Identifier read(JsonReader in) throws IOException {
            return new Identifier("item", in.nextString());
        }

        @Override
        public void write(JsonWriter out, Identifier value) throws IOException {
            out.value(value.getPath());
        }

    }

}
