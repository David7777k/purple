package jaypasha.funpay.modules.impl.combat.auraModule.controllers;

import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.TargetFinderLayer;
import net.minecraft.entity.Entity;

import java.util.*;
import java.util.function.Predicate;
public class TargetFinderController implements TargetFinderLayer {

    @Override
    public Optional<? extends Entity> each(TargetFinderConfiguration tfc, Predicate<Entity> predicate) {
        List<? extends Entity> entities = mc.world.getPlayers();

        return entities.stream()
                .filter(e -> !e.equals(mc.player))
                .filter(tfc::isValidTarget)
                .filter(tfc::isValidDistance)
                .filter(predicate)
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)));
    }
}
