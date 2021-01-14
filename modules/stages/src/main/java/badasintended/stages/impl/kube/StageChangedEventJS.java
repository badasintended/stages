package badasintended.stages.impl.kube;

import badasintended.stages.api.data.Stages;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ListJS;

public class StageChangedEventJS extends EventJS {

    private final Stages stages;
    private final ServerPlayerJS serverPlayerJS;

    public StageChangedEventJS(Stages stages) {
        this.stages = stages;

        this.serverPlayerJS = ServerJS.instance.getPlayer(stages.getPlayer());
    }

    public ServerPlayerJS getPlayer() {
        return serverPlayerJS;
    }

    public ListJS getStages() {
        ListJS list = new ListJS();
        list.addAll(stages.values());
        return list;
    }

}
