package jaypasha.funpay.modules.impl.combat.auraModule.services;

import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.controllers.TargetFinderController;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.TargetFinderLayer;
import net.minecraft.entity.Entity;

import java.util.Optional;
import java.util.function.Predicate;

public class TargetFinderService implements TargetFinderLayer {
    private final TargetFinderController controller = new TargetFinderController();

    @Override
    public Optional<? extends Entity> each(TargetFinderConfiguration tfc, Predicate<Entity> predicate) {
        return controller.each(tfc, predicate);
    }

    public Optional<? extends Entity> minByHealth(TargetFinderConfiguration tfc, Predicate<Entity> predicate) {
        return controller.minByHealth(tfc, predicate);
    }

    public Optional<? extends Entity> lookingAt(TargetFinderConfiguration tfc, Predicate<Entity> predicate) {
        return controller.lookingAt(tfc, predicate);
    }
}
