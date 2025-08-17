package jaypasha.funpay.utility.color;

import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class ColorUtility {

    /**
     * Создаёт ARGB цвет из компонентов (0-255)
     */
    public static int rgba(int r, int g, int b, int a) {
        r = clamp8(r);
        g = clamp8(g);
        b = clamp8(b);
        a = clamp8(a);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clamp8(int v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return v;
    }

    /**
     * Применяет прозрачность в процентах 0..100 к существующему ARGB цвету.
     * percent = 100 -> сохраняет исходный альфа.
     */
    public static int applyOpacity(int hex, int percent) {
        float p = Math.min(Math.max(percent, 0), 100) / 100f;
        int originalAlpha = (hex >> 24) & 0xFF;
        int newAlpha = Math.round(originalAlpha * p);
        int rgb = hex & 0x00FFFFFF;
        return (newAlpha << 24) | rgb;
    }

    /**
     * Применяет прозрачность, принимая opacity в 0..255 (как в исходниках).
     */
    public static int applyOpacity(int hex, float opacity255) {
        float p = Math.min(Math.max(opacity255, 0f), 255f) / 255f;
        int originalAlpha = (hex >> 24) & 0xFF;
        int newAlpha = Math.round(originalAlpha * p);
        int rgb = hex & 0x00FFFFFF;
        return (newAlpha << 24) | rgb;
    }

    public static QuadColorState applyOpacity(QuadColorState colorState, int opacity) {
        return new QuadColorState(
                applyOpacity(colorState.color1(), opacity),
                applyOpacity(colorState.color2(), opacity),
                applyOpacity(colorState.color3(), opacity),
                applyOpacity(colorState.color4(), opacity)
        );
    }

    /**
     * Линейная интерполяция между двумя цветами (ARGB).
     * progress 0..1
     */
    public static int interpolate(int colorA, int colorB, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));

        int aA = (colorA >> 24) & 0xFF;
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;

        int aB = (colorB >> 24) & 0xFF;
        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int a = Math.round(aA + (aB - aA) * progress);
        int r = Math.round(rA + (rB - rA) * progress);
        int g = Math.round(gA + (gB - gA) * progress);
        int b = Math.round(bA + (bB - bA) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Удобный alias для читабельности (использовался в проекте).
     */
    public static int lerp(float value, int from, int to) {
        return interpolate(from, to, value);
    }

    /**
     * Читает пиксель OpenGL (RGBA) и возвращает ARGB как int.
     */
    public static int pixelColor(int x, int y) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        int r = byteBuffer.get(0) & 0xFF;
        int g = byteBuffer.get(1) & 0xFF;
        int b = byteBuffer.get(2) & 0xFF;
        int a = byteBuffer.get(3) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
