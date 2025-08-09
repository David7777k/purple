package jaypasha.funpay.modules.impl.combat.auraModule.controllers;

import jaypasha.funpay.modules.impl.combat.auraModule.randomization.Randomization;
import net.minecraft.util.math.Vec3d;

public class RandomizationController {

    Vec3d previous = Vec3d.ZERO;
    Vec3d current = Vec3d.ZERO;

    int ticks = 0;

    public void refreshRandomizationValue(Randomization randomization) {
        ticks++;
        if (ticks % randomization.getTicksUpdate() != 0) return;

        Vec3d newValue = randomization.getRandomValue();

        previous = current;
        current = newValue;
    }

    public Vec3d getCurrent(Randomization randomization) {
        refreshRandomizationValue(randomization);

        Vec3d value = randomization.getRandomizationType().getCalculator().calculate(previous, current);
        previous = value;

        return value;
    }
}
