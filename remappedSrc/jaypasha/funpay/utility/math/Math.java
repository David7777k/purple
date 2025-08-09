package jaypasha.funpay.utility.math;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import net.minecraft.client.util.math.MatrixStack;

public class Math extends org.joml.Math {

    // Если value ближе или равно threshold к nearest, то возращает nearest
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

}
