package badasintended.stages.impl.test;

import badasintended.stages.api.event.StageEvents;
import badasintended.stages.api.init.ClientStagesInit;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class StagesTestClient implements ClientStagesInit {

    private boolean
        a = false,
        b = false,
        c = false,
        d = false,
        e = false;

    @Override
    public void onStagesClientInit() {
        StageEvents.CHANGED.register(stages -> {
            StagesTest.LOGGER.info("synced : {}", stages.getPlayer().getDisplayName().getString());

            a = stages.contains(StagesTest.A);
            b = stages.contains(StagesTest.B);
            c = stages.contains(StagesTest.C);
            d = stages.contains(StagesTest.D);
            e = stages.contains(StagesTest.E);
        });

        HudRenderCallback.EVENT.register((matrices, delta) -> {
            TextRenderer text = MinecraftClient.getInstance().textRenderer;
            text.draw(matrices, "a: " + a, 5, 5, 0xFFFFFF);
            text.draw(matrices, "b: " + b, 5, 15, 0xFFFFFF);
            text.draw(matrices, "c: " + c, 5, 25, 0xFFFFFF);
            text.draw(matrices, "d: " + d, 5, 35, 0xFFFFFF);
            text.draw(matrices, "e: " + e, 5, 45, 0xFFFFFF);
        });
    }

}
