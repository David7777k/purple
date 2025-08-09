package jaypasha.funpay.utility.inventory;

import jaypasha.funpay.Api;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

public final class InventoryUtils implements Api {

    public static void moveTo(int syncId, ItemStack stack, int slot) {
        if (Objects.isNull(stack)) return;

        mc.interactionManager.clickSlot(syncId, mc.player.getInventory().getSlotWithStack(stack), 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    public static ItemStack byItem(Item item) {
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);

            if (itemStack.getItem().equals(item)) return itemStack;
        }

        return null;
    }

    public static boolean quickMoveFromTo(int from, int to) {
        if (from > 8) {
            mc.interactionManager.clickSlot(0, from, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, to, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, from, 0, SlotActionType.PICKUP, mc.player);
        } else {
            mc.interactionManager.clickSlot(0, from + 36, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, to, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, from + 36, 0, SlotActionType.PICKUP, mc.player);
        }

        return true;
    }

}
