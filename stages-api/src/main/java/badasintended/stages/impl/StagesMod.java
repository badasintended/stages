package badasintended.stages.impl;

import java.nio.charset.StandardCharsets;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.config.Config;
import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.StagesInit;
import badasintended.stages.impl.advancement.criterion.StagesChangedCriterion;
import badasintended.stages.impl.command.StageCommands;
import badasintended.stages.impl.data.StagesImpl;
import badasintended.stages.impl.mixin.CriteriaAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StagesMod implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(StagesUtil.MOD_ID);
    public static final StagesChangedCriterion CRITERION = new StagesChangedCriterion();

    // @formatter:off
    public static final Identifier
        BEGIN_SYNC_REGISTRY = StagesUtil.id("begin_sync_registry"),
        SYNC_REGISTRY       = StagesUtil.id("sync_registry"),
        END_SYNC_REGISTRY   = StagesUtil.id("end_sync_registry"),
        SYNC_STAGES         = StagesUtil.id("sync_stages"),
        SYNC_CONFIG         = StagesUtil.id("sync_config");
    // @formatter:on

    @Override
    public void onInitialize() {
        StageCommands.register();
        CriteriaAccessor.register(CRITERION);

        StageEvents.CHANGED.register(CRITERION::trigger);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> Config.CONFIGS.values().forEach(Config::destroy));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> StagesImpl.lockRegistry());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            StagesImpl.syncRegistry(handler.player);
            Config.CONFIGS.forEach((name, config) -> {
                if (config.isSynced()) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeString(name);
                    buf.writeByteArray(config.toJson().getBytes(StandardCharsets.UTF_8));
                    sender.sendPacket(SYNC_CONFIG, buf);
                }
            });
        });

        LOGGER.info("[stages] Loading StagesInit");
        FabricLoader.getInstance().getEntrypointContainers(StagesUtil.MOD_ID + ":main", StagesInit.class).forEach(container -> {
            StagesInit init = container.getEntrypoint();
            init.onStagesInit();
            LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
        LOGGER.info("[stages] done");
    }

}
