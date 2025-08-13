package jaypasha.funpay.modules.impl.combat.auraModule.interfaces;

import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import net.minecraft.util.math.Vec3d;

public interface RotationLayer {
    /**
     * @param current текущее направление взгляда (нормализованный Vec3d)
     * @param desired желаемое направление на цель (нормализованный Vec3d)
     * @return следующее направление (нормализованный Vec3d)
     */
    Vec3d apply(Vec3d current, Vec3d desired);

    Vector applyRotation(RotationConfiguration configuration, Vector from, Vec3d to);
}
