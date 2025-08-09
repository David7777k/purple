package jaypasha.funpay.api.animations;

public interface AnimationCalculation {
    default double calculation(double value){
        return 0;
    }
}
