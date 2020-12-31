package badasintended.stages.impl.item;

import badasintended.stages.api.init.ClientStagesInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ItemStagesClient implements ClientStagesInit {

    @Override
    public void onStagesClientInit() {
        ClientPlayNetworking.registerGlobalReceiver(ItemStages.SYNC_LOCKED_ITEM, (client, handler, buf, responseSender) -> {
            Item item = Registry.ITEM.get(buf.readVarInt());
            CompoundTag nbt = buf.readCompoundTag();
            boolean unlock = buf.readBoolean();

            client.execute(() -> {
                ItemStages.editLockedItem(client.player, item, nbt, unlock);
            });
        });
    }

}
