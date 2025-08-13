package jaypasha.funpay.modules.impl.combat.auraModule.configs;

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import jaypasha.funpay.utility.math.MathVector;
import net.minecraft.entity.Entity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.function.BooleanSupplier;

public record AttackConfiguration (
        Entity entity,
        float attackDistance,
        boolean onlyCriticals,
        boolean smartCriticals,
        boolean rayCasting,
        boolean attackThrowWall,
        boolean dontAttackIfUsing,
        boolean onlyWithWeapon,
        BooleanSupplier isFalling    // <-- добавлено
) implements Api {

    // temporary debug reason (can remove later)
    private static String debugReason = "";
    public static String getDebugReason() { return debugReason; }

    public boolean isValid() {
        debugReason = "";

        if (entity == null) { debugReason = "entity == null"; return false; }
        if (mc.player == null) { debugReason = "mc.player == null"; return false; }
        if (mc.interactionManager == null) { debugReason = "interactionManager == null"; return false; }
        if (!entity.isAlive() || entity.isRemoved()) { debugReason = "entity dead/removed"; return false; }

        double dist = mc.player.distanceTo(entity);
        if (dist > attackDistance) { debugReason = "out of range: " + dist + " > " + attackDistance; return false; }

        if (!isCanAttack()) {
            if (debugReason.isEmpty()) debugReason = "isCanAttack == false";
            return false;
        }

        debugReason = "valid";
        return true;
    }

    boolean isCanAttack() {
        // cooldown: нормальная проверка
        if (!isSwingCooled()) {
            debugReason = "swing not cooled";
            return false;
        }

        if (dontAttackIfUsing && mc.player.isUsingItem()) {
            debugReason = "player using item";
            return false;
        }

        if (onlyWithWeapon) {
            var stack = mc.player.getMainHandStack();
            var item = stack.getItem();
            boolean isWeapon = item instanceof SwordItem || item instanceof AxeItem;
            if (!isWeapon) {
                debugReason = "not holding sword/axe";
                return false;
            }
        }

        if (rayCasting) {
            RotationService rotationService = ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getRotationService();

            BlockHitResult blockHitResult = null;
            EntityHitResult entityHitResult = null;
            try {
                blockHitResult = MathVector.raycast(attackDistance, rotationService.getCurrentVector(), false);
            } catch (Throwable ignored) {}

            try {
                entityHitResult = MathVector.raycastEntity(attackDistance, rotationService.getCurrentVector(), e -> e.isAlive());
            } catch (Throwable ignored) {}

            if (!attackThrowWall && blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
                // if block is closer than entity -> block
                if (entityHitResult == null) {
                    debugReason = "raycast: block before entity (no entityHit)";
                    return false;
                }
                double blockDist = blockHitResult.getPos().distanceTo(mc.player.getEyePos());
                double entityDist = entityHitResult.getPos().distanceTo(mc.player.getEyePos());
                if (blockDist < entityDist) {
                    debugReason = "raycast: block closer than entity";
                    return false;
                }
            }

            if (entityHitResult == null || entityHitResult.getEntity() == null) {
                debugReason = "raycast: entity not hit by ray";
                return false;
            }

            // finally ensure the hit entity is our target
            if (entityHitResult.getEntity() != entity) {
                debugReason = "raycast: hit different entity: " + entityHitResult.getEntity().getName().getString();
                return false;
            }
        }

        if (onlyCriticals) {
            if (smartCriticals) {
                if (!(mc.options.jumpKey.isPressed() && isCritical())) {
                    debugReason = "smart criticals fail";
                    return false;
                }
            } else {
                if (!isCritical()) {
                    debugReason = "not critical";
                    return false;
                }
            }
        }

        debugReason = "canAttackNow ok";
        return true;
    }

    boolean isSwingCooled() {
        // use full progress check
        return mc.player.getAttackCooldownProgress(1.0f) >= 1.0f;
    }

    boolean isCritical() {
        // используем переданный BooleanSupplier (если он есть)
        return mc.player != null
                && !mc.player.isOnGround()
                && isFalling != null
                && isFalling.getAsBoolean();
    }
}
