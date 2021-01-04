package badasintended.stages.impl.advancement.criterion;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.data.Stages;
import badasintended.stages.impl.predicate.StagePredicate;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class StagesChangedCriterion extends AbstractCriterion<StagesChangedCriterion.Conditions> {

    private static final Identifier ID = StagesUtil.id("changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(
        JsonObject obj, EntityPredicate.Extended player, AdvancementEntityPredicateDeserializer predicateDeserializer
    ) {
        return new Conditions(player, StagePredicate.fromJson(obj.get("stages")));
    }

    public void trigger(Stages stages) {
        if (!stages.isClient()) {
            test((ServerPlayerEntity) stages.getPlayer(), conditions -> conditions.test(stages.getPlayer()));
        }
    }

    public static class Conditions extends AbstractCriterionConditions {

        private final StagePredicate predicate;

        public Conditions(EntityPredicate.Extended player, StagePredicate predicate) {
            super(ID, player);
            this.predicate = predicate;
        }

        public boolean test(PlayerEntity player) {
            return predicate.test(player);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("stages", predicate.toJson());
            return jsonObject;
        }

    }

}
