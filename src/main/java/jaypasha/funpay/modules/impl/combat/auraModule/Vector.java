package jaypasha.funpay.modules.impl.combat.auraModule;

import jaypasha.funpay.Api;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class Vector implements Api {

    float yaw, pitch;

    public Vector set(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;

        return this;
    }

    public Vector set(Vector vector) {
        this.yaw = vector.getYaw();
        this.pitch = vector.getPitch();

        return this;
    }

    public Vector add(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;

        return this;
    }

    public Vector subtract(float yaw, float pitch) {
        this.yaw -= yaw;
        this.pitch -= pitch;

        return this;
    }

    public Vector subtract(Vector vector) {
        this.yaw -= vector.getYaw();
        this.pitch -= vector.getPitch();

        return this;
    }

    public Vector multiply(float yawScale, float pitchScale) {
        this.yaw *= yawScale;
        this.pitch *= pitchScale;

        return this;
    }

    public static float interpolateAngle(float from, float to, float factor) {
        float diff = MathHelper.wrapDegrees(to - from);
        return from + diff * factor;
    }

    public Vector smoothInterpolate(Vector from, Vector to, float factor) {
        float yaw = interpolateAngle(from.getYaw(), to.getYaw(), factor);
        float pitch = interpolateAngle(from.getPitch(), to.getPitch(), factor);
        return new Vector(yaw, pitch);
    }

    public Vector wrapDegrees() {
        this.yaw = MathHelper.wrapDegrees(yaw);
        this.pitch = MathHelper.wrapDegrees(pitch);

        return this;
    }

    public Vec3d toVector() {
        float yawRad = (float) Math.toRadians(this.yaw);
        float pitchRad = (float) Math.toRadians(this.pitch);

        double x = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double y = -MathHelper.sin(pitchRad);
        double z = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);

        return new Vec3d(x, y, z);
    }
}
