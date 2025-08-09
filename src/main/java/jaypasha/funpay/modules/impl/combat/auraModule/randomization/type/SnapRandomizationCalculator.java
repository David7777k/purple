package jaypasha.funpay.modules.impl.combat.auraModule.randomization.type;

import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RandomizationCalculator;
import net.minecraft.util.math.Vec3d;

public class SnapRandomizationCalculator implements RandomizationCalculator {

    @Override
    public Vec3d calculate(Vec3d previousValue, Vec3d nextValue) {
        return nextValue;
    }
}
