package jaypasha.funpay.utility.color;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import net.minecraft.util.math.ColorHelper;

public class ColorUtility extends ColorHelper {

    public static int applyOpacity(int hex, int percent) {
        return ColorHelper.withAlpha((255 / 100) * percent, hex);
    }

    public static int applyOpacity(int hex, float opacity) {
        return ColorHelper.getArgb((int) (ColorHelper.getAlpha(hex) * (opacity / 255)), ColorHelper.getRed(hex), ColorHelper.getGreen(hex), ColorHelper.getBlue(hex));
    }

    public static QuadColorState applyOpacity(QuadColorState colorState, int opacity) {
        return new QuadColorState(ColorHelper.withAlpha(opacity, colorState.color1()),
                ColorHelper.withAlpha(opacity, colorState.color2()),
                ColorHelper.withAlpha(opacity, colorState.color3()),
                ColorHelper.withAlpha(opacity, colorState.color4()));
    }

    public static int lerp(float value, int from, int to) {
        return ColorHelper.lerp(value, from, to);
    }


}
