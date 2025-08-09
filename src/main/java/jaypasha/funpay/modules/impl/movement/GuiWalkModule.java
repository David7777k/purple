package jaypasha.funpay.modules.impl.movement;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.api.events.impl.TickEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class GuiWalkModule extends ModuleLayer {

    Supplier<List<KeyBinding>> list = Suppliers.memoize(() -> Lists.newArrayList(mc.options.forwardKey, mc.options.leftKey, mc.options.rightKey, mc.options.backKey, mc.options.jumpKey));

    public GuiWalkModule() {
        super(Text.of("GuiWalk"), null, Category.Movement);
    }

    @Subscribe
    public void tickEvent(TickEvent tickEvent) {
        if (!getEnabled() || mc.currentScreen instanceof ChatScreen) return;

        list.get().forEach(e -> e.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), e.getDefaultKey().getCode())));
    }
}
