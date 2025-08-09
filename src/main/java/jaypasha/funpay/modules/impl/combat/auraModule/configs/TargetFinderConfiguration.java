package jaypasha.funpay.modules.impl.combat.auraModule.configs;

import jaypasha.funpay.Api;
import net.minecraft.entity.Entity;

import java.util.List;

public record TargetFinderConfiguration(
    float distance,
    List<Class<? extends Entity>> targetsClazz
) implements Api {

    public boolean isValidDistance(Entity entity) {
        return mc.player.distanceTo(entity) <= distance;
    }

    public boolean isValidTarget(Entity entity) {
        return targetsClazz().contains(entity.getClass());
    }

    public boolean isValid(Entity entity) {
        return this.isValidDistance(entity)
                && isValidTarget(entity)
                && entity.isAlive()
                && !entity.isRemoved();
    }

}
