package jaypasha.funpay.api.animations.implement;

import jaypasha.funpay.api.animations.Animation;

public class InOutQuintAnimation extends Animation {

    @Override
    public double calculation(double value) {
        double x = value / ms;

        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }
}
