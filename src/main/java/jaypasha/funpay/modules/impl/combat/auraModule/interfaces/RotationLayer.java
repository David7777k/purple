package jaypasha.funpay.modules.impl.combat.auraModule.interfaces;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.randomization.Randomization;
import net.minecraft.util.math.Vec3d;

public interface RotationLayer extends Api {

    Vector applyRotation(RotationConfiguration configuration, Vector from, Vec3d to);

    Randomization applyRandomizationValue(Randomization.RandomizationType randomizationType);

}
