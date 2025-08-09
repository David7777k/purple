package jaypasha.funpay.modules.impl.combat.auraModule.services;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.controllers.RandomizationController;
import jaypasha.funpay.modules.impl.combat.auraModule.controllers.RotationController;
import jaypasha.funpay.modules.impl.combat.auraModule.randomization.Randomization;
import jaypasha.funpay.modules.impl.combat.auraModule.rotation.SmoothRotationLayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.util.math.Vec3d;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RotationService implements Api {

    @Getter
    private static RotationService instance;

    public RotationService() {
        instance = this;
    }

    @Getter
    @NonFinal
    Vector currentVector = new Vector(0,0);

    @Getter
    @NonFinal
    Vector previousVector = new Vector(0,0);

    RotationController controller = new RotationController();
    RandomizationController randomizationController = new RandomizationController();

    SmoothRotationLayer rotationLayer = new SmoothRotationLayer();

    public void applyRotation(RotationConfiguration rotationConfiguration) {
        Vec3d targetPosition = rotationConfiguration.entity().getPos().add(
                randomizationController.getCurrent(
                        rotationLayer.applyRandomizationValue(
                                rotationConfiguration.smoothRandomization()
                                        ? Randomization.RandomizationType.Smooth
                                        : Randomization.RandomizationType.Snap
                        )
                )
        );

        Vector calculatedRotation = controller.applyRotation(rotationConfiguration, rotationLayer, currentVector, targetPosition);
        previousVector = currentVector;
        currentVector = calculatedRotation.smoothInterpolate(currentVector, calculatedRotation, 1.0f);
    }

    public void resetRotation() {
        this.previousVector.set(mc.player.getYaw(), mc.player.getPitch());
        this.currentVector.set(mc.player.getYaw(), mc.player.getPitch());
    }

}
