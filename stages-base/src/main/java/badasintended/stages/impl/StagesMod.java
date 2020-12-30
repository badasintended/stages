package badasintended.stages.impl;

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

    public static final String MOD_ID = "stages";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // @formatter:off
    public static final Identifier
        SYNC_REGISTRY = id("sync_registry"),
        SYNC_STAGES   = id("sync_stages"),
        REQUEST_SYNC  = id("request_sync_stages");
    // @formatter:on

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

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
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID + ":main", StagesInit.class).forEach(container -> {
            StagesInit init = container.getEntrypoint();
            init.onStagesInit();
            LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
    }

}
