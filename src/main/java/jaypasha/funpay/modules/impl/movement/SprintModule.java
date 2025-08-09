package jaypasha.funpay.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.api.events.impl.TickEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import net.minecraft.text.Text;

import static net.minecraft.client.util.InputUtil.isKeyPressed;

public class SprintModule extends ModuleLayer {

    public SprintModule() {
        super(Text.of("Sprint"), null, Category.Movement);
    }

    @Subscribe
    public void tickEvent(TickEvent tickEvent) {
        if (!getEnabled()) return;

        mc.player.setSprinting(isKeyPressed(mc.getWindow().getHandle(), mc.options.forwardKey.getDefaultKey().getCode()));
    }
}
