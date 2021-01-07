package badasintended.stages.impl;

import java.nio.charset.StandardCharsets;

import badasintended.stages.api.config.Config;
import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.init.ClientStagesInit;
import badasintended.stages.impl.data.StageRegistryImpl;
import badasintended.stages.impl.data.StagesImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static badasintended.stages.api.StagesUtil.MOD_ID;
import static badasintended.stages.api.StagesUtil.registerS2C;
import static badasintended.stages.impl.StagesMod.BEGIN_SYNC_REGISTRY;
import static badasintended.stages.impl.StagesMod.END_SYNC_REGISTRY;
import static badasintended.stages.impl.StagesMod.LOGGER;
import static badasintended.stages.impl.StagesMod.SYNC_CONFIG;
import static badasintended.stages.impl.StagesMod.SYNC_REGISTRY;
import static badasintended.stages.impl.StagesMod.SYNC_STAGE_ADDED;
import static badasintended.stages.impl.StagesMod.SYNC_STAGE_CHANGED;
import static badasintended.stages.impl.StagesMod.SYNC_STAGE_REMOVED;

@Environment(EnvType.CLIENT)
public class StagesModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerS2C(SYNC_CONFIG, (client, handler, buf, sender) -> {
            String name = buf.readString();
            String json = new String(buf.readByteArray(), StandardCharsets.UTF_8);

            client.execute(() -> {
                Config.CONFIGS.get(name).fromJson(json);
                LOGGER.info("[stages] Synced config \"{}\"", name);
            });
        });

        registerS2C(BEGIN_SYNC_REGISTRY, (client, handler, buf, sender) ->
            client.execute(StageRegistryImpl::destroy)
        );

        registerS2C(SYNC_REGISTRY, (client, handler, buf, sender) -> {
            int i = buf.readVarInt();
            Identifier stage = buf.readIdentifier();

            client.execute(() -> StageRegistryImpl.syncRegistry(i, stage));
        });

        registerS2C(END_SYNC_REGISTRY, (client, handler, buf, sender) ->
            client.execute(() -> LOGGER.info("[stages] Registry synced, total {} stages", StageRegistry.allStages().size()))
        );

        registerS2C(SYNC_STAGE_ADDED, (client, handler, buf, sender) -> {
            int id = buf.readVarInt();

            client.execute(() -> {
                PlayerEntity player = client.player;
                StagesImpl data = (StagesImpl) Stages.get(player);
                data.syncAdd(id);
            });
        });

        registerS2C(SYNC_STAGE_REMOVED, (client, handler, buf, sender) -> {
            int id = buf.readVarInt();

            client.execute(() -> {
                PlayerEntity player = client.player;
                StagesImpl data = (StagesImpl) Stages.get(player);
                data.syncRemove(id);
            });
        });

        registerS2C(SYNC_STAGE_CHANGED, (client, handler, buf, sender) ->
            client.execute(() -> ((StagesImpl) Stages.get(client.player)).syncChanged())
        );

        LOGGER.info("[stages] Loading ClientStagesInit");
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID + ":client", ClientStagesInit.class).forEach(container -> {
            ClientStagesInit init = container.getEntrypoint();
            init.onStagesClientInit();
            LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
        LOGGER.info("[stages] done");
    }

}
