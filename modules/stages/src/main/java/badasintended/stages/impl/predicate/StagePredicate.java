package badasintended.stages.impl.predicate;

import java.util.Set;

import badasintended.stages.api.data.Stages;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class StagePredicate {

    public static final StagePredicate EMPTY = new StagePredicate(ObjectSets.emptySet(), ObjectSets.emptySet());

    private final Set<Identifier> must;
    private final Set<Identifier> mustNot;

    private StagePredicate(Set<Identifier> must, Set<Identifier> mustNot) {
        this.must = must;
        this.mustNot = mustNot;
    }

    public boolean test(PlayerEntity player) {
        Stages stages = Stages.get(player);
        return stages.containsAll(must) && !stages.containsAny(mustNot);
    }

    public static StagePredicate fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            Set<Identifier> must = new ObjectOpenHashSet<>();
            Set<Identifier> mustNot = new ObjectOpenHashSet<>();

            JsonObject jsonStages = JsonHelper.asObject(json, "stages");
            jsonStages.entrySet().forEach(entry -> {
                Identifier stage = new Identifier(entry.getKey());
                if (JsonHelper.asBoolean(entry.getValue(), "stage")) {
                    must.add(stage);
                } else {
                    mustNot.add(stage);
                }
            });
            return new StagePredicate(must, mustNot);
        } else
            return EMPTY;
    }

    public JsonElement toJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject object = new JsonObject();
            must.forEach(it -> object.addProperty(it.toString(), true));
            mustNot.forEach(it -> object.addProperty(it.toString(), false));
            return object;
        }
    }

}
