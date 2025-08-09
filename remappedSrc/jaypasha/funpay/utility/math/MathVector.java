package jaypasha.funpay.utility.math;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.Api;
import net.minecraft.entity.Entity;

import java.lang.Math;

public class MathVector implements Api {

    public static float getRotations(Entity entity) {
        if (mc.player == null || entity == null) return 0;

        double x = interpolate(entity.lastX, entity.getPos().x) - interpolate(mc.player.lastX, mc.player.getPos().x);
        double z = interpolate(entity.lastZ, entity.getPos().z) - interpolate(mc.player.lastZ, mc.player.getPos().z);
        return (float) -(java.lang.Math.atan2(x, z) * (180 / Math.PI));
    }

    public static double interpolate(double d, double d2) {
        return d + (d2 - d) * (double) mc.getRenderTickCounter().getTickProgress(true);
    }

}
