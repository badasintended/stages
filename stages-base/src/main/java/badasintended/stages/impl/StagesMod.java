package badasintended.stages.impl;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.init.StagesInit;
import badasintended.stages.impl.command.StagesCommand;
import badasintended.stages.impl.data.StagesImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StagesMod implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(StagesUtil.MOD_ID);

    // @formatter:off
    public static final Identifier
        SYNC_REGISTRY = StagesUtil.id("sync_registry"),
        SYNC_STAGES   = StagesUtil.id("sync_stages"),
        REQUEST_SYNC  = StagesUtil.id("request_sync_stages");
    // @formatter:on

    @Override
    public void onInitialize() {
        StagesCommand.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            StagesImpl.syncRegistry(handler.player);
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_SYNC, (server, player, handler, buf, sender) -> {
            server.execute(() -> {
                Stages.get(player).sync();
            });
        });

        LOGGER.info("[stages] Loading StagesInit");
        FabricLoader.getInstance().getEntrypointContainers(StagesUtil.MOD_ID + ":main", StagesInit.class).forEach(container -> {
            StagesInit init = container.getEntrypoint();
            init.onStagesInit();
            LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
    }

}
