package badasintended.itemstages;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

class UnknownItem extends Item {

    public static final Text TOOLTIP = new TranslatableText("item.itemstages.unknown.tooltip").formatted(Formatting.GRAY);

    public UnknownItem() {
        super(new Settings());
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(TOOLTIP);
    }

}
