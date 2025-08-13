package jaypasha.funpay.modules.impl.combat.auraModule.controllers;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.TargetFinderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Контроллер поиска целей.
 */
public class TargetFinderController implements TargetFinderLayer, Api {

    private Stream<? extends Entity> streamEntities(TargetFinderConfiguration tfc) {
        if (mc.world == null || mc.player == null) return Stream.empty();

        Stream<? extends Entity> players = mc.world.getPlayers().stream();
        Stream<? extends Entity> mobs = mc.world.getEntitiesByClass(
                MobEntity.class,
                mc.player.getBoundingBox().expand(tfc.distance()),
                e -> true
        ).stream();

        if (tfc.players() && tfc.mobs()) return Stream.concat(players, mobs);
        if (tfc.players()) return players;
        if (tfc.mobs()) return mobs;
        return Stream.empty();
    }

    @Override
    public Optional<? extends Entity> each(TargetFinderConfiguration tfc, Predicate<Entity> extraPredicate) {
        return streamEntities(tfc)
                .filter(e -> e != null && !e.equals(mc.player))
                .filter(tfc::isValid)
                .filter(extraPredicate)
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)));
    }

    /**
     * Ищет цель с минимальным здоровьем среди LivingEntity.
     */
    public Optional<? extends Entity> minByHealth(TargetFinderConfiguration tfc, Predicate<Entity> extraPredicate) {
        return streamEntities(tfc)
                .filter(e -> e instanceof LivingEntity && !e.equals(mc.player))
                .filter(tfc::isValid)
                .filter(extraPredicate)
                .map(e -> (LivingEntity) e)
                .min(Comparator.comparingDouble(LivingEntity::getHealth))
                .map(e -> (Entity) e);
    }

    /**
     * Если в прицеле (crosshair) EntityHitResult — вернёт её (при прохождении фильтров),
     * иначе вернёт результат each(...) (ближайшая подходящая).
     */
    public Optional<? extends Entity> lookingAt(TargetFinderConfiguration tfc, Predicate<Entity> extraPredicate) {
        if (mc.crosshairTarget instanceof EntityHitResult ehr) {
            Entity hit = ehr.getEntity();
            if (hit != null && tfc.isValid(hit) && extraPredicate.test(hit)) {
                return Optional.of(hit);
            }
        }
        return each(tfc, extraPredicate);
    }
}
