package jaypasha.funpay.modules.impl.combat.auraModule.rotation;

import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.randomization.Randomization;
import jaypasha.funpay.utility.math.MathVector;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SmoothRotationLayer implements RotationLayer {

    public SmoothRotationLayer() {
        Pasxalka.getInstance().getEventBus().register(this);
    }

    @Override
    public Vector applyRotation(RotationConfiguration configuration, Vector from, Vec3d to) {
        Vec3d positionDelta = MathVector.calculatePositionDelta(mc.player.getPos(), to);
        Vector calculatedRotation = MathVector.calculateRotation(positionDelta);
        Vector rotationDelta = MathVector.calculateRotationDelta(from, calculatedRotation);

        float yawDelta = rotationDelta.getYaw();
        float pitchDelta = rotationDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
        if (rotationDifference == .0f) return from;

        float straightLineYaw = Math.abs(yawDelta / rotationDifference) * 35;
        float straightLinePitch = Math.abs(pitchDelta / rotationDifference) * 35;

        float moveYaw = Math.min(Math.max(yawDelta, -straightLineYaw), straightLineYaw);
        float movePitch = Math.min(Math.max(pitchDelta, -straightLinePitch), straightLinePitch);

        return new Vector(
            MathHelper.lerp(.8f, from.getYaw(), from.getYaw() + moveYaw),
            MathHelper.lerp(.8f, from.getPitch(), from.getPitch() + movePitch)
        );
    }

    @Override
    public Randomization applyRandomizationValue(Randomization.RandomizationType randomizationType) {
        return new Randomization(new Vec3d(0.13234f, 0.0515, 0.04156), jaypasha.funpay.utility.math.Math.random(40, 100), randomizationType);
    }
}
