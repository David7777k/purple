package jaypasha.funpay.modules.impl.combat.auraModule.interfaces;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import net.minecraft.entity.Entity;

import java.util.Optional;
import java.util.function.Predicate;

@FunctionalInterface
public interface TargetFinderLayer extends Api {

    Optional<? extends Entity> each(TargetFinderConfiguration tfc, Predicate<Entity> predicate);

}
