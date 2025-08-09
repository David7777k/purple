package jaypasha.funpay.api.animations.implement;

import jaypasha.funpay.api.animations.Animation;

public class InOutBackAnimation extends Animation {

    @Override
    public double calculation(double value) {
        double x = value / ms;

        double c1 = 1.70158;
        double c2 = c1 * 1.525;

        return x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }
}
