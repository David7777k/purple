package jaypasha.funpay.utility.math;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class MathVector implements Api {

    public static float rotationDifference(Entity entity) {
        if (mc.player == null || entity == null) return 0;

        double x = interpolate(entity.prevX, entity.getPos().x) - interpolate(mc.player.prevX, mc.player.getPos().x);
        double z = interpolate(entity.prevZ, entity.getPos().z) - interpolate(mc.player.prevZ, mc.player.getPos().z);
        // явно используем java.lang.Math
        return (float) -(java.lang.Math.atan2(x, z) * (180 / java.lang.Math.PI));
    }

    public static Vec3d lerpPosition(Entity entity) {
        float tickDelta = mc.getRenderTickCounter().getTickDelta(true);
        return new Vec3d(
                entity.prevX + (entity.getX() - entity.prevX) * tickDelta,
                entity.prevY + (entity.getY() - entity.prevY) * tickDelta,
                entity.prevZ + (entity.getZ() - entity.prevZ) * tickDelta
        );
    }

    public static double interpolate(double d, double d2) {
        return d + (d2 - d) * (double) mc.getRenderTickCounter().getTickDelta(true);
    }

    public static float distanceTo(Vec3d from, Vec3d to) {
        float f = (float) (from.getX() - to.getX());
        float g = (float) (from.getY() - to.getY());
        float h = (float) (from.getZ() - to.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public static Vector calculateRotationDelta(Vector from, Vector to) {
        return to.subtract(from).wrapDegrees();
    }

    public static Vector2f calculateRotationDelta(Vector2f from, Vector2f to) {
        return new Vector2f(MathHelper.wrapDegrees(to.getX() - from.getX()), MathHelper.wrapDegrees(to.getY() - from.getY()));
    }

    public static Vector calculateRotation(Vec3d vec3d) {
        // Здесь используем java.lang.Math явно, чтобы не было конфликтов с локальным Math
        return new Vector(
                (float) java.lang.Math.toDegrees(java.lang.Math.atan2(vec3d.z, vec3d.x)) - 90f,
                (float) java.lang.Math.toDegrees(-java.lang.Math.atan2(vec3d.y, java.lang.Math.hypot(vec3d.x, vec3d.z)))
        ).wrapDegrees();
    }

    public static Vec3d calculatePositionDelta(Vec3d from, Vec3d to) {
        return to.subtract(from);
    }

    // --- Перегрузка: принимаем Vec3d ---
    public static BlockHitResult raycast(double range, Vec3d dirVec, boolean includeFluids) {
        Entity entity = mc.cameraEntity;
        if (entity == null) return null;

        Vec3d start = entity.getCameraPosVec(1.0F);
        Vec3d rotationVec = dirVec.normalize();
        Vec3d end = start.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);

        World world = mc.world;
        if (world == null) return null;

        RaycastContext.FluidHandling fluidHandling = includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE;
        RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, fluidHandling, entity);

        return world.raycast(context);
    }

    // --- Перегрузка: принимаем наш Vector (yaw/pitch) ---
    public static BlockHitResult raycast(double range, Vector rotVector, boolean includeFluids) {
        return raycast(range, rotVector.toVector(), includeFluids);
    }

    public static EntityHitResult raycastEntity(double range, Vec3d dirVec, Predicate<Entity> filter) {
        Entity entity = mc.cameraEntity;
        if (entity == null) return null;

        Vec3d cameraVec = entity.getCameraPosVec(1.0F);
        Vec3d rotationVec = dirVec.normalize();
        Vec3d end = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = entity.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);

        return ProjectileUtil.raycast(
                entity,
                cameraVec,
                end,
                box,
                (e) -> !e.isSpectator() && filter.test(e),
                range * range
        );
    }

    public static EntityHitResult raycastEntity(double range, Vector rotVector, Predicate<Entity> filter) {
        return raycastEntity(range, rotVector.toVector(), filter);
    }
}
