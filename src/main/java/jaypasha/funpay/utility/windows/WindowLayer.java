package jaypasha.funpay.utility.windows;

import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.ui.clickGui.ComponentBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class WindowLayer implements ComponentBuilder {

    float x, y, width, height;
    Animation animation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Animation getAnimation() { return animation; }

    @Override
    public WindowLayer position(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    @Override
    public WindowLayer size(float width, float height) {
        this.width = width;
        this.height = height;

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public void init() {

    }
}
