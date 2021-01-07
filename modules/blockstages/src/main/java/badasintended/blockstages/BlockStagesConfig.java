package badasintended.blockstages;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockStagesConfig {

    public final Object2ObjectOpenHashMap<Identifier, Entry> entries = new Object2ObjectOpenHashMap<>();

    public static class Entry {

        public final Block block;
        public final Tag.Identified<Block> tag;
        public final Block as;

        public Entry(Block block, Tag.Identified<Block> tag, Block as) {
            this.block = block;
            this.tag = tag;
            this.as = as;
        }

        public static class Adapter extends TypeAdapter<Entry> {

            @Override
            public Entry read(JsonReader in) throws IOException {
                Block block = Blocks.AIR;
                Tag.Identified<Block> tag = null;
                Block as = Blocks.AIR;

                in.beginObject();
                while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
                    String name = in.nextName();
                    if (name.equals("target")) {
                        String id = in.nextString();
                        if (id.startsWith("#")) {
                            tag = (Tag.Identified<Block>) TagRegistry.block(new Identifier(id.substring(1)));
                        } else {
                            block = Registry.BLOCK.get(new Identifier(id));
                        }
                    } else if (name.equals("as")) {
                        as = Registry.BLOCK.get(new Identifier(in.nextString()));
                    }
                }
                in.endObject();
                return new Entry(block, tag, as);
            }

            @Override
            public void write(JsonWriter out, Entry value) throws IOException {
                out.beginObject();
                out.name("target");
                if (value.tag != null) {
                    out.value("#" + value.tag.getId());
                } else {
                    out.value(Registry.BLOCK.getId(value.block).toString());
                }
                out.name("as");
                out.value(Registry.BLOCK.getId(value.as).toString());
                out.endObject();
            }

        }

    }

    public static class IdentifierAdapter extends TypeAdapter<Identifier> {

        @Override
        public Identifier read(JsonReader in) throws IOException {
            return new Identifier("block", in.nextString());
        }

        @Override
        public void write(JsonWriter out, Identifier value) throws IOException {
            out.value(value.getPath());
        }

    }

}
