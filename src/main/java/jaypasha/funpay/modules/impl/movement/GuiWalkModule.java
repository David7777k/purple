package jaypasha.funpay.modules.impl.movement;

import com.google.common.base.Suppliers; import com.google.common.collect.Lists; import com.google.common.eventbus.Subscribe; import jaypasha.funpay.api.events.impl.TickEvent; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import net.minecraft.client.gui.screen.ChatScreen; import net.minecraft.client.option.KeyBinding; import net.minecraft.client.util.InputUtil; import net.minecraft.text.Text;

import java.util.List; import java.util.function.Supplier;

public class GuiWalkModule extends ModuleLayer {

    // Кэшируем список KeyBinding'ов, которые хотим обрабатывать
    private final Supplier<List<KeyBinding>> movementKeys = Suppliers.memoize(() ->
            Lists.newArrayList(
                    mc.options.forwardKey,
                    mc.options.leftKey,
                    mc.options.rightKey,
                    mc.options.backKey,
                    mc.options.jumpKey
            )
    );

    public GuiWalkModule() {
        super(Text.of("GuiWalk"), null, Category.Movement);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!getEnabled() || mc.player == null || mc.world == null) return;
        if (mc.currentScreen instanceof ChatScreen) return;

        updateKeys();
    }

    private void updateKeys() {
        long windowHandle = mc.getWindow().getHandle();
        for (KeyBinding key : movementKeys.get()) {
            // Используем именно текущий код, если игрок переназначил клавишу
            int keyCode = key.getDefaultKey().getCode();
            key.setPressed(InputUtil.isKeyPressed(windowHandle, keyCode));
        }
    }

}