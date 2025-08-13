package jaypasha.funpay.modules.impl.combat.auraModule.configs;

import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import net.minecraft.entity.Entity;

public record RotationConfiguration(
        Entity entity,
        float distance,
        RotationLayer rotationLayer,
        boolean smoothRandomization
) { }
