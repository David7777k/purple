package jaypasha.funpay.utility.math;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import net.minecraft.client.util.math.MatrixStack;

import java.util.concurrent.ThreadLocalRandom;

public class Math extends org.joml.Math {

    public static float stick(float value, float nearest, float threshold) {
        return Math.abs(value - nearest) <= threshold ? nearest : value;
    }

    public static boolean isHover(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public static void scale(MatrixStack stack, float x, float y, float scale, Runnable data) {
        stack.push();
        stack.translate(x, y, 0);
        stack.scale(scale, scale, 1);
        stack.translate(-x, -y, 0);
        data.run();
        stack.pop();
    }

    public static String lerp(float delta, String from, String to) {
        int step = (int) floor(delta * (from.length() + to.length()));

        return step < from.length()
                ? from.substring(0, max(0, from.length() - step))
                : to.substring(0, min(step - from.length(), to.length()));
    }

    public static Integer random(Integer from, Integer to) {
        return ThreadLocalRandom.current().nextInt(from, to);
    }

    public static Float random(Float from, Float to) {
        return ThreadLocalRandom.current().nextFloat(from, to);
    }

}
