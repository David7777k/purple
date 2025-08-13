package jaypasha.funpay.modules.impl.combat.auraModule.configs;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public record TargetFinderConfiguration(
        float distance,
        float fov,
        boolean players,
        boolean mobs,
        boolean invis
) implements Api {

    public boolean isValidDistance(Entity entity) {
        return mc.player != null && mc.player.distanceTo(entity) <= distance;
    }

    public boolean isValidTarget(Entity entity) {
        if (mc.player == null || entity == null || !entity.isAlive() || entity.isRemoved()) return false;

        boolean isPlayer = entity instanceof PlayerEntity;
        boolean isMob = entity instanceof MobEntity;
        if (isPlayer && !players) return false;
        if (isMob && !mobs) return false;

        if (entity.isInvisible() && !invis) return false;

        return true;
    }

    public boolean isInFov(Entity entity) {
        if (mc.player == null || entity == null) return false;

        Vec3d eye = mc.player.getEyePos();
        Vec3d to;
        if (entity instanceof net.minecraft.entity.LivingEntity le) {
            // целимся в глаза живых существ
            to = new Vec3d(entity.getX(), entity.getY() + le.getStandingEyeHeight(), entity.getZ()).subtract(eye).normalize();
        } else {
            to = entity.getPos().subtract(eye).normalize();
        }

        Vec3d look = RotationService.getInstance().getCurrentVector().normalize();
        double dot = look.dotProduct(to);
        dot = Math.max(-1.0, Math.min(1.0, dot));
        double angleDeg = Math.toDegrees(Math.acos(dot));
        return angleDeg <= (fov / 2.0);
    }

    public boolean isValid(Entity entity) {
        return entity != null
                && entity.isAlive()
                && !entity.isRemoved()
                && isValidDistance(entity)
                && isValidTarget(entity)
                && isInFov(entity);
    }
}
