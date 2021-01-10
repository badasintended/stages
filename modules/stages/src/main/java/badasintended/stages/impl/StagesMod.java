package badasintended.stages.impl;

import java.util.Set;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.config.SyncedConfig;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import badasintended.stages.impl.advancement.criterion.StagesChangedCriterion;
import badasintended.stages.impl.command.StageCommands;
import badasintended.stages.impl.config.ConfigHolderImpl;
import badasintended.stages.impl.data.StageRegistryImpl;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static badasintended.stages.api.StagesUtil.s2c;

public class StagesMod implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(StagesUtil.MOD_ID);
    public static final StagesChangedCriterion CRITERION = new StagesChangedCriterion();

    // @formatter:off
    public static final Identifier
        BEGIN_SYNC_REGISTRY = StagesUtil.id("begin_sync_registry"),
        SYNC_REGISTRY       = StagesUtil.id("sync_registry"),
        END_SYNC_REGISTRY   = StagesUtil.id("end_sync_registry"),
        SYNC_STAGE_ADDED    = StagesUtil.id("sync_stage_added"),
        SYNC_STAGE_REMOVED  = StagesUtil.id("sync_stage_removed"),
        SYNC_STAGE_CHANGED  = StagesUtil.id("sync_stage_changed"),
        SYNC_CONFIG         = StagesUtil.id("sync_config");
    // @formatter:on

    public static void sync(ServerPlayerEntity player) {
        StageRegistryImpl.syncRegistry(player);
        ConfigHolderImpl.CONFIGS.forEach((name, config) -> {
            if (config.isSynced()) {
                s2c(player, SYNC_CONFIG, buf -> {
                    buf.writeString(name);
                    buf.writeString(config.get().getClass().getName());
                    ((SyncedConfig) config.get()).toBuf(buf);
                });
            }
        });

        Stages stages = Stages.get(player);
        Set<Identifier> values = new ObjectOpenHashSet<>(stages.values());
        stages.clear();
        values.forEach(stages::add);
    }

    @Override
    public void onInitialize() {
        StageCommands.register();
        CriterionRegistry.register(CRITERION);

        StageEvents.CHANGED.register(CRITERION::trigger);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            sync(handler.player)
        );

        LOGGER.info("[stages] Loading StagesInit");
        FabricLoader.getInstance().getEntrypointContainers(StagesUtil.MOD_ID + ":main", StagesInit.class).forEach(container -> {
            StagesInit init = container.getEntrypoint();
            init.onStagesInit();
            LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
        LOGGER.info("[stages] done");
    }

}
