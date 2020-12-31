package badasintended.stages.impl;

import badasintended.stages.api.StageConstants;
import badasintended.stages.api.data.Stages;
import badasintended.stages.api.event.StageSyncEvents;
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
        ClientPlayNetworking.registerGlobalReceiver(StagesMod.SYNC_REGISTRY, (client, handler, buf, sender) -> {
            int size = buf.readVarInt();
            Identifier[] stages = new Identifier[size];
            for (int i = 0; i < size; i++) {
                stages[i] = buf.readIdentifier();
            }

            client.execute(() -> StagesImpl.syncRegistry(stages));
        });

        ClientPlayNetworking.registerGlobalReceiver(StagesMod.SYNC_STAGES, (client, handler, buf, sender) -> {
            int size = buf.readVarInt();
            int[] stages = new int[size];
            for (int i = 0; i < size; i++) {
                stages[i] = buf.readVarInt();
            }

            client.execute(() -> {
                PlayerEntity player = client.player;
                StagesImpl data = (StagesImpl) Stages.get(player);
                data.toggleEvents();
                data.clear();
                for (int stage : stages) {
                    data.add(Stages.getStage(stage));
                }
                data.toggleEvents();
                StageSyncEvents.SYNC.invoker().onSync(data);
            });
        });

        StagesMod.LOGGER.info("[stages] Loading ClientStagesInit");
        FabricLoader.getInstance().getEntrypointContainers(StageConstants.MOD_ID + ":client", ClientStagesInit.class).forEach(container -> {
            ClientStagesInit init = container.getEntrypoint();
            init.onStagesClientInit();
            StagesMod.LOGGER.info("[stages] |=> loaded {} from {}", init.getClass().getName(), container.getProvider().getMetadata().getId());
        });
    }

}
