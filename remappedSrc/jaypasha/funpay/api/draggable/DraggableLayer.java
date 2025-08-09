package jaypasha.funpay.api.draggable;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class DraggableLayer implements jaypasha.funpay.api.draggable.interfaces.DraggableLayer, Api {

    @Setter
    @NonNull
    Float x, y, width, height;

    @Setter
    Float prevX, prevY;

    @Setter
    @NonNull
    Supplier<Boolean> visible;

    @Setter
    @NonFinal
    Boolean dragging = false;

    @NonFinal
    Boolean settingOpened = false;

    final List<SettingLayer> settings = new ArrayList<>();

    final Animation animation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    final Animation settingAnimation = new DecelerateAnimation()
            .setMs(300)
            .setValue(1);

    public void toggleSetting() {
        this.settingOpened = !this.settingOpened;
        this.settingAnimation.setDirection(this.settingOpened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public void setPosition(float x, float y) {
        this.prevX = this.x;
        this.prevY = this.y;
        this.x = x;
        this.y = y;
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
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public void tick() {}
}
