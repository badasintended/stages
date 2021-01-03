package badasintended.stages.impl;

import java.nio.charset.StandardCharsets;

import badasintended.stages.api.StagesUtil;
import badasintended.stages.api.config.Config;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.init.ClientStagesInit;
import badasintended.stages.impl.data.StagesImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StagesModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(StagesMod.SYNC_CONFIG, (client, handler, buf, sender) -> {
            String name = buf.readString();
            String json = new String(buf.readByteArray(), StandardCharsets.UTF_8);

            client.execute(() -> Config.CONFIGS.get(name).fromJson(json));
        });

        ClientPlayNetworking.registerGlobalReceiver(StagesMod.BEGIN_SYNC_REGISTRY, (client, handler, buf, sender) ->
            client.execute(StagesImpl::beginSyncRegistry)
        );

        ClientPlayNetworking.registerGlobalReceiver(StagesMod.SYNC_REGISTRY, (client, handler, buf, sender) -> {
            int i = buf.readVarInt();
            Identifier stage = buf.readIdentifier();

            client.execute(() -> StagesImpl.syncRegistry(i, stage));
        });

        ClientPlayNetworking.registerGlobalReceiver(StagesMod.END_SYNC_REGISTRY, (client, handler, buf, sender) ->
            client.execute(StagesImpl::endSyncRegistry)
        );

        ClientPlayNetworking.registerGlobalReceiver(StagesMod.SYNC_STAGES, (client, handler, buf, sender) -> {
            int size = buf.readVarInt();
            int[] stages = new int[size];
            for (int i = 0; i < size; i++) {
                stages[i] = buf.readVarInt();
            }

            client.execute(() -> {
                PlayerEntity player = client.player;
                StagesImpl data = (StagesImpl) Stages.get(player);
                data.sync(stages);
            });
        });

        StagesMod.LOGGER.info("[stages] Loading ClientStagesInit");
        FabricLoader.getInstance().getEntrypointContainers(StagesUtil.MOD_ID + ":client", ClientStagesInit.class).forEach(container -> {
            ClientStagesInit init = container.getEntrypoint();
            init.onStagesClientInit();
            StagesMod.LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
        StagesMod.LOGGER.info("[stages] done");
    }

}
