package jaypasha.funpay.modules.impl.combat.auraModule.configs;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import jaypasha.funpay.utility.math.MathVector;
import net.minecraft.entity.Entity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.function.BooleanSupplier;

public record AttackConfiguration(
        Entity entity,
        float attackDistance,
        boolean onlyCriticals,
        boolean smartCriticals,
        boolean rayCasting,
        boolean attackThroughWall,
        boolean dontAttackIfUsing,
        boolean onlyWithWeapon,
        BooleanSupplier isFalling
) implements Api {

    public boolean isValid() {
        return entity != null
                && mc.player != null
                && mc.interactionManager != null
                && mc.player.distanceTo(entity) <= attackDistance
                && canAttackNow();
    }

    boolean canAttackNow() {
        if (!isSwingCooled()) return false;

        if (dontAttackIfUsing && mc.player.isUsingItem()) return false;

        if (onlyWithWeapon) {
            var stack = mc.player.getMainHandStack();
            var item = stack.getItem();
            boolean isWeapon = item instanceof SwordItem || item instanceof AxeItem;
            if (!isWeapon) return false;
        }

        if (rayCasting) {
            var rotationService = RotationService.getInstance();
            Vec3d dir = rotationService.getCurrentVector().normalize();

            var blockHit = MathVector.raycast(attackDistance, dir, false);
            var entityHit = MathVector.raycastEntity(attackDistance, dir, e -> e.isAlive() && e == entity);

            if (!attackThroughWall && blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
                if (entityHit == null) return false;
                double blockDist = blockHit.getPos().distanceTo(mc.player.getEyePos());
                double entityDist = entityHit.getPos().distanceTo(mc.player.getEyePos());
                if (blockDist < entityDist) return false;
            }

            if (entityHit == null || entityHit.getEntity() != entity) return false;
        }

        if (onlyCriticals) {
            if (smartCriticals) {
                return mc.options.jumpKey.isPressed() && isCritical();
            }
            return isCritical();
        }

        return true;
    }

    boolean isSwingCooled() {
        return mc.player.getAttackCooldownProgress(1.0f) >= 1.0f;
    }

    boolean isCritical() {
        return mc.player != null
                && !mc.player.isOnGround()
                && isFalling != null
                && isFalling.getAsBoolean();
    }
}
