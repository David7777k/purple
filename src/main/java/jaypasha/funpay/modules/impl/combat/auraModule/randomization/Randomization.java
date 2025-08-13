package jaypasha.funpay.modules.impl.combat.auraModule.randomization;

import jaypasha.funpay.modules.impl.combat.auraModule.randomization.type.SmoothRandomizationCalculator;
import jaypasha.funpay.modules.impl.combat.auraModule.randomization.type.SnapRandomizationCalculator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.math.Vec3d;

@Getter
@RequiredArgsConstructor
public class Randomization {

    @NonNull
    Vec3d value;

    @NonNull
    Integer ticksUpdate;

    @NonNull
    RandomizationType randomizationType;

    public Vec3d getRandomValue() {
        // правка: диапазон [-value, +value]
        return new Vec3d(
                ((Math.random() * 2.0) - 1.0) * value.getX(),
                ((Math.random() * 2.0) - 1.0) * value.getY(),
                ((Math.random() * 2.0) - 1.0) * value.getZ()
        );
    }

    @Getter
    @AllArgsConstructor
    public enum RandomizationType {
        Smooth(new SmoothRandomizationCalculator()),
        Snap(new SnapRandomizationCalculator());

        final jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RandomizationCalculator calculator;
    }
}
