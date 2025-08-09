package jaypasha.funpay.api.animations.implement;


import jaypasha.funpay.api.animations.Animation;

public class DecelerateAnimation extends Animation {

    @Override
    public double calculation(double value) {
        double x = value / ms;
        return 1 - (x - 1) * (x - 1);
    }
}
