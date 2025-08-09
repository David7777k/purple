package jaypasha.funpay.modules.impl.combat.auraModule.configs;

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import jaypasha.funpay.utility.math.MathVector;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public record AttackConfiguration (
    Entity entity,
    float attackDistance,
    boolean onlyCriticals,
    boolean smartCriticals,
    boolean rayCasting,
    boolean attackThrowWall,
    boolean dontAttackIfUsing
) implements Api {

    public boolean isValid() {
        return mc.player.distanceTo(entity) <= attackDistance && isCanAttack();
    }

    boolean isCanAttack() {
        if (!isSwingCooled()) return false;

        if (dontAttackIfUsing) {
            if (mc.player.isUsingItem())
                return false;
        }

        if (rayCasting) {
            RotationService rotationService = ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getRotationService();
            BlockHitResult blockHitResult = MathVector.raycast(attackDistance, rotationService.getCurrentVector(), false);
            EntityHitResult entityHitResult = MathVector.raycastEntity(attackDistance, rotationService.getCurrentVector(), Entity::isAlive);

            if (!attackThrowWall && blockHitResult.getType().equals(HitResult.Type.BLOCK)) return false;

            if (entityHitResult == null || entityHitResult.getEntity() == null) return false;
        }

        if (onlyCriticals) {
            if (smartCriticals) {
                if (mc.options.jumpKey.isPressed()) return isCritical();
                else return true;
            }

            return isCritical();
        }

        return true;
    }

    boolean isSwingCooled() {
        return mc.player.getAttackCooldownProgress(.5f) > .9f;
    }

    boolean isCritical() {
        return !mc.player.isOnGround() && ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getFallDetector().isFalling();
    }

}
