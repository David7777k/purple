package jaypasha.funpay.modules.impl.combat;

import com.google.common.eventbus.Subscribe; import jaypasha.funpay.api.events.impl.TickEvent; import jaypasha.funpay.modules.impl.combat.autoTotem.HealthCalculator; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.modules.settings.impl.SliderSetting; import jaypasha.funpay.utility.inventory.InventoryUtils; import lombok.Getter; import lombok.Setter; import net.minecraft.item.ItemStack; import net.minecraft.item.Items; import net.minecraft.text.Text;

@Setter @Getter public class AutoTotemModule extends ModuleLayer {

    private static final int OFFHAND_SLOT = 45;

    private final SliderSetting minHealth = new SliderSetting(Text.of("Health"), null, () -> true)
            .set(1f, 19f, .5f)
            .register(this);

    private ItemStack lastItem;

    public AutoTotemModule() {
        super(Text.of("Auto Totem"), null, Category.Combat);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!getEnabled() || mc.player == null || mc.world == null) return;

        boolean needTotem = !HealthCalculator.isValid(minHealth::getValue);
        ItemStack offHandStack = mc.player.getOffHandStack();

        if (needTotem) {
            if (!offHandStack.isOf(Items.TOTEM_OF_UNDYING)) {
                // Запоминаем предыдущий предмет только если он не пустой и не тотем
                if (!offHandStack.isEmpty() && !offHandStack.isOf(Items.TOTEM_OF_UNDYING)) {
                    lastItem = offHandStack.copy();
                }

                int totemSlot = mc.player.getInventory().getSlotWithStack(InventoryUtils.byItem(Items.TOTEM_OF_UNDYING));
                if (totemSlot != -1) {
                    InventoryUtils.quickMoveFromTo(totemSlot, OFFHAND_SLOT);
                    print("Swapped to Totem");
                }
            }
        } else {
            // Возвращаем прошлый предмет, если есть
            if (offHandStack.isOf(Items.TOTEM_OF_UNDYING) && lastItem != null && !lastItem.isEmpty()) {
                int slot = mc.player.getInventory().getSlotWithStack(lastItem);
                if (slot != -1) {
                    InventoryUtils.quickMoveFromTo(slot, OFFHAND_SLOT);
                    print("Swapped back");
                }
                lastItem = null;
            }
        }
    }

}

