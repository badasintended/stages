package badasintended.stages.impl;

import badasintended.stages.api.config.SyncedConfig;
import badasintended.stages.api.data.StageRegistry;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.init.ClientStagesInit;
import badasintended.stages.impl.config.ConfigHolderImpl;
import badasintended.stages.impl.data.StageRegistryImpl;
import badasintended.stages.impl.data.StagesImpl;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import static badasintended.stages.api.StagesUtil.MOD_ID;
import static badasintended.stages.api.StagesUtil.registerS2C;
import static badasintended.stages.impl.StagesMod.BEGIN_SYNC_REGISTRY;
import static badasintended.stages.impl.StagesMod.END_SYNC_REGISTRY;
import static badasintended.stages.impl.StagesMod.LOGGER;
import static badasintended.stages.impl.StagesMod.SYNC_CONFIG;
import static badasintended.stages.impl.StagesMod.SYNC_REGISTRY;
import static badasintended.stages.impl.StagesMod.SYNC_STAGE;

@Environment(EnvType.CLIENT)
public class StagesModClient implements ClientModInitializer {

    private final Int2BooleanOpenHashMap syncMap = new Int2BooleanOpenHashMap();

    @Override
    public void onInitializeClient() {
        registerS2C(SYNC_CONFIG, (client, handler, buf, sender) -> {
            String name = buf.readString();
            try {
                SyncedConfig config = (SyncedConfig) Class.forName(buf.readString()).getDeclaredConstructor().newInstance();
                config.fromBuf(buf);
                client.execute(() -> {
                    ConfigHolderImpl.CONFIGS.get(name).set(config);
                    LOGGER.info("[stages] Synced config \"{}\"", name);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        registerS2C(SYNC_STAGE, (client, handler, buf, sender) -> {
            syncMap.clear();
            while (buf.isReadable()) {
                syncMap.put(buf.readVarInt(), buf.readBoolean());
            }

            client.execute(() ->
                ((StagesImpl) Stages.get(client.player)).sync(syncMap)
            );
        });

        LOGGER.info("[stages] Loading ClientStagesInit");
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID + ":client", ClientStagesInit.class).forEach(container -> {
            ClientStagesInit init = container.getEntrypoint();
            init.onStagesClientInit();
            LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
        LOGGER.info("[stages] done");
    }

}
