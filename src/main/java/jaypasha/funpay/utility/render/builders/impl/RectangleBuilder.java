package jaypasha.funpay.utility.render.builders.impl;

import jaypasha.funpay.utility.render.builders.AbstractBuilder;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.renderers.impl.BuiltRectangle;

public final class RectangleBuilder extends AbstractBuilder<BuiltRectangle> {

    private SizeState size = SizeState.NONE;
    private QuadRadiusState radius = QuadRadiusState.NO_ROUND;
    private QuadColorState color = QuadColorState.TRANSPARENT;
    private float smoothness = 1.0f;

    public RectangleBuilder size(SizeState size) {
        this.size = size;
        return this;
    }

    // удобная перегрузка по ширине/высоте
    public RectangleBuilder size(float width, float height) {
        this.size = new SizeState(width, height);
        return this;
    }

    public RectangleBuilder radius(QuadRadiusState radius) {
        this.radius = radius;
        return this;
    }

    // удобная перегрузка — передаём одно float (радиус)
    public RectangleBuilder radius(float r) {
        this.radius = new QuadRadiusState(r);
        return this;
    }

    public RectangleBuilder color(QuadColorState color) {
        this.color = color;
        return this;
    }

    // удобная перегрузка — если у тебя цвет как int (RGBA)
    public RectangleBuilder color(int rgba) {
        this.color = new QuadColorState(rgba);
        return this;
    }

    // нормальное название метода (маленькая буква)
    public RectangleBuilder smoothness(float smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    @Override
    protected BuiltRectangle _build() {
        return new BuiltRectangle(
                this.size,
                this.radius,
                this.color,
                this.smoothness
        );
    }

    @Override
    protected void reset() {
        this.size = SizeState.NONE;
        this.radius = QuadRadiusState.NO_ROUND;
        this.color = QuadColorState.TRANSPARENT;
        this.smoothness = 1.0f;
    }
}
