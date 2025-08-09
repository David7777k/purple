package jaypasha.funpay.modules.impl.combat.autoTotem;

import jaypasha.funpay.Api;

import java.util.function.Supplier;

public final class HealthCalculator implements Api {

    public static boolean isValid(Supplier<Float> health) {
        if (mc.player.getHealth() <= health.get()) return false;

        return true;
    }

}
