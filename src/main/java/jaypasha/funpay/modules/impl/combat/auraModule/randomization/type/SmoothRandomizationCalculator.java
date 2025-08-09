package jaypasha.funpay.modules.impl.combat.auraModule.randomization.type;

import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RandomizationCalculator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SmoothRandomizationCalculator implements RandomizationCalculator {

    @Override
    public Vec3d calculate(Vec3d previousValue, Vec3d nextValue) {
        return new Vec3d(
            MathHelper.lerp(0.05f, previousValue.getX(), nextValue.getX()),
            MathHelper.lerp(0.05f, previousValue.getY(), nextValue.getY()),
            MathHelper.lerp(0.05f, previousValue.getZ(), nextValue.getZ())
        );
    }
}
