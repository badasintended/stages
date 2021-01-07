package badasintended.blockstages;

import badasintended.stages.api.init.ClientStagesInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import static badasintended.stages.api.StagesUtil.registerS2C;

@Environment(EnvType.CLIENT)
public class BlockStagesClient implements ClientStagesInit {

    @Override
    public void onStagesClientInit() {
        registerS2C(BlockStages.INITIALIZE, (client, handler, buf, responseSender) ->
            client.execute(() -> BlockStages.init(client.player))
        );

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            BlockStagesHolder player = (BlockStagesHolder) client.player;
            if (player != null && player.stages$shouldReload()) {
                client.worldRenderer.reload();
                player.stages$setReload(false);
            }
        });
    }

}
