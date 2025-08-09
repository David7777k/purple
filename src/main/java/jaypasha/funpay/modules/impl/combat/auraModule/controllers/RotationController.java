package jaypasha.funpay.modules.impl.combat.auraModule.controllers;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import net.minecraft.util.math.Vec3d;

public class RotationController implements Api {

    public Vector applyRotation(RotationConfiguration configuration, RotationLayer rotationMode, Vector from, Vec3d to) {
        return rotationMode.applyRotation(configuration, from, to);
    }

}
