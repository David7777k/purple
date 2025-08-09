package jaypasha.funpay.modules.impl.combat.auraModule.interfaces;

import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface RandomizationCalculator {

    Vec3d calculate(Vec3d previousValue, Vec3d nextValue);

}
