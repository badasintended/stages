package badasintended.itemstages;

import badasintended.stages.api.init.ClientStagesInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static badasintended.stages.api.StagesUtil.registerS2C;

@Environment(EnvType.CLIENT)
public class ItemStagesClient implements ClientStagesInit {

    @Override
    public void onStagesClientInit() {
        registerS2C(ItemStages.INITIALIZE, (client, handler, buf, responseSender) ->
            client.execute(() -> ItemStages.init(client.player))
        );
    }

}
