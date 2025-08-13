package jaypasha.funpay.modules.impl.combat.auraModule.services;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationService implements Api {
    private static final RotationService INSTANCE = new RotationService();

    private Vec3d currentVector = new Vec3d(0, 0, 1);

    public static RotationService getInstance() {
        return INSTANCE;
    }

    public void resetRotation() {
        if (mc.player == null) return;
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        currentVector = fromYawPitch(yaw, pitch);
    }

    // Возвращает Vec3d (юниты в 3D)
    public Vec3d getCurrentVector() {
        if (mc.player == null) return currentVector;
        // синхронизируемся с игроком
        return fromYawPitch(mc.player.getYaw(), mc.player.getPitch());
    }

    // Возвращает удобный wrapper Vector(yaw, pitch)
    public Vector getCurrentRotationVector() {
        Vec3d dir = getCurrentVector();
        if (dir == null) return null;
        float[] yp = toYawPitch(dir);
        return new Vector(yp[0], yp[1]);
    }

    public void aimAt(Entity target, RotationConfiguration cfg) {
        if (mc.player == null || target == null || cfg == null) return;

        Vec3d eye = mc.player.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();
        if (target instanceof net.minecraft.entity.LivingEntity le) {
            targetPos = new Vec3d(target.getX(), target.getY() + le.getStandingEyeHeight(), target.getZ());
        }

        Vec3d desired = targetPos.subtract(eye).normalize();
        RotationLayer layer = cfg.rotationLayer();

        Vec3d nextDir = layer.apply(getCurrentVector().normalize(), desired);
        float[] yp = toYawPitch(nextDir);

        mc.player.setYaw(yp[0]);
        mc.player.setPitch(yp[1]);
        currentVector = nextDir;
    }

    private static Vec3d fromYawPitch(float yaw, float pitch) {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);
        float x = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        float y = -MathHelper.sin(pitchRad);
        float z = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);
        return new Vec3d(x, y, z).normalize();
    }

    private static float[] toYawPitch(Vec3d dir) {
        double x = dir.x;
        double y = dir.y;
        double z = dir.z;
        double h = Math.sqrt(x * x + z * z);

        float yaw = (float) Math.toDegrees(Math.atan2(-x, z));
        float pitch = (float) Math.toDegrees(Math.atan2(-y, h));
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new float[]{yaw, pitch};
    }

    public void applyRotation(RotationConfiguration rotCfg) {
    }
}
