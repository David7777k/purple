package jaypasha.funpay.api.animations.implement;

import jaypasha.funpay.api.animations.Animation;

public class InElasticAnimation extends Animation {

    @Override
    public double calculation(double value) {
        double x = value / ms;
        double c4 = (2 * Math.PI) / 3;

        return x == 0
                ? 0
                : x == 1
                ? 1
                : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4);
    }
}
