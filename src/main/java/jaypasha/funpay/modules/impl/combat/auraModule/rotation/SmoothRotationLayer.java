package jaypasha.funpay.modules.impl.combat.auraModule.rotation;

import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.interfaces.RotationLayer;
import net.minecraft.util.math.Vec3d;

public class SmoothRotationLayer implements RotationLayer {

    private final double maxStep; // чем меньше — тем плавнее
    private final double snapDot; // если почти совпало — щёлкнуть точно на цель

    public SmoothRotationLayer() {
        this(0.2, 0.995);
    }

    public SmoothRotationLayer(double maxStep, double snapDot) {
        this.maxStep = Math.max(0.01, Math.min(1.0, maxStep));
        this.snapDot = Math.max(0.9, Math.min(0.9999, snapDot));
    }

    @Override
    public Vec3d apply(Vec3d current, Vec3d desired) {
        Vec3d cur = current.normalize();
        Vec3d des = desired.normalize();

        double dot = cur.dotProduct(des);
        if (dot >= snapDot) {
            return des; // почти смотрим на цель — сразу фиксируемся
        }

        Vec3d stepped = cur.multiply(1.0 - maxStep).add(des.multiply(maxStep));
        return stepped.normalize();
    }

    @Override
    public Vector applyRotation(RotationConfiguration configuration, Vector from, Vec3d to) {
        // Конвертим углы -> вектор (текущее), применяем apply, затем вектор -> углы
        Vec3d currentDir = from.toVector().normalize();
        Vec3d nextDir = apply(currentDir, to.normalize());

        // Пересчитываем обратно в yaw/pitch
        double x = nextDir.x;
        double y = nextDir.y;
        double z = nextDir.z;
        double h = Math.sqrt(x * x + z * z);

        float yaw = (float) Math.toDegrees(Math.atan2(-x, z));
        float pitch = (float) Math.toDegrees(Math.atan2(-y, h));
        if (pitch > 90.0f) pitch = 90.0f;
        if (pitch < -90.0f) pitch = -90.0f;

        return new Vector(yaw, pitch);
    }
}
