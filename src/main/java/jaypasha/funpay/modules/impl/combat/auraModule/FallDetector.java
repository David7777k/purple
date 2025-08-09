package jaypasha.funpay.modules.impl.combat.auraModule;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.impl.TickEvent;
import lombok.Getter;

@Getter
public class FallDetector implements Api {

    public FallDetector() {
        Pasxalka.getInstance().getEventBus().register(this);
    }

    private double prevY;
    private double currentY;
    private boolean falling;

    @Subscribe
    private void tickEvent(TickEvent event) {
        currentY = mc.player.getY();

        if (mc.player.isOnGround()) {
            falling = false;
        } else {
            falling = prevY > currentY + 0.01f;
        }

        prevY = currentY;
    }
}