package badasintended.stages.impl.item;

import badasintended.stages.api.init.ClientStagesInit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public class ItemStagesClient implements ClientStagesInit {

    @Override
    public void onStagesClientInit() {
        ClientPlayNetworking.registerGlobalReceiver(ItemStages.SYNC, (client, handler, buf, responseSender) -> {
            Item item = Registry.ITEM.get(buf.readVarInt());
            CompoundTag nbt = buf.readCompoundTag();
            boolean unlock = buf.readBoolean();

            client.execute(() -> {
                ItemStages.editLockedItem(client.player, item, nbt, unlock);
            });
        });
    }

}
