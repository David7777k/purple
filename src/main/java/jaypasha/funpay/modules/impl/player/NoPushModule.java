package jaypasha.funpay.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.api.events.impl.CollisionEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import net.minecraft.text.Text;

public class NoPushModule extends ModuleLayer {

    ModeListSetting collisions = new ModeListSetting(Text.of("Отменять от"), null, () -> true)
            .set("Игроков", "Блоков")
            .register(this);

    public NoPushModule() {
        super(Text.of("No Push"), null, Category.Player);
    }

    @Subscribe
    public void playersCollisionEvent(CollisionEvent.PlayerCollisionEvent playerCollisionEvent) {
        if (!getEnabled()) return;

        if (collisions.get("Игроков").getEnabled())
            playerCollisionEvent.cancel();
    }

    @Subscribe
    public void blocksCollisionEvent(CollisionEvent.BlocksCollisionEvent blocksCollisionEvent) {
        if (!getEnabled()) return;

        if (collisions.get("Блоков").getEnabled())
            blocksCollisionEvent.cancel();
    }
}
