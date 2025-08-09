package jaypasha.funpay.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.api.events.impl.TickEvent;
import jaypasha.funpay.modules.impl.combat.autoTotem.HealthCalculator;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.SliderSetting;
import jaypasha.funpay.utility.inventory.InventoryUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

@Setter
@Getter
public class AutoTotemModule extends ModuleLayer {

    SliderSetting minHealth = new SliderSetting(Text.of("Health"), null, () -> true)
            .set(1f, 19f, .5f)
            .register(this);

    ItemStack lastItem;

    public AutoTotemModule() {
        super(Text.of("Auto Totem"), null, Category.Combat);
    }

    @Subscribe
    public void tickEvent(TickEvent tickEvent) {
        if (!HealthCalculator.isValid(minHealth::getValue)) {
            if (!mc.player.getOffHandStack().getItem().equals((Items.TOTEM_OF_UNDYING))) {
                lastItem = mc.player.getOffHandStack();
                InventoryUtils.quickMoveFromTo(mc.player.getInventory().getSlotWithStack(InventoryUtils.byItem(Items.TOTEM_OF_UNDYING)), 45);
                print("Swapped to Totem");
            }
        } else {
            if (mc.player.getOffHandStack().getItem().equals(Items.TOTEM_OF_UNDYING) && lastItem != null) {
                InventoryUtils.quickMoveFromTo(mc.player.getInventory().getSlotWithStack(lastItem), 45);
                print("Swapped back");
                lastItem = null;
            }
        }
    }
}
