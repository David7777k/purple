package jaypasha.funpay.modules.impl.combat.auraModule.randomization;

import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RandomizationCalculator;
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
        return new Vec3d(
            (Math.random() * value.getX()) - value.getX(),
            (Math.random() * value.getY()) - value.getY(),
            (Math.random() * value.getZ()) - value.getZ()
        );
    }

    @Getter
    @AllArgsConstructor
    public enum RandomizationType {
        Smooth(new SmoothRandomizationCalculator()),
        Snap(new SnapRandomizationCalculator());

        final RandomizationCalculator calculator;
    }
}
