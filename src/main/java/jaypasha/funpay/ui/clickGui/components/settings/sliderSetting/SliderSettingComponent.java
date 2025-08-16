package jaypasha.funpay.ui.clickGui.components.settings.sliderSetting;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.impl.SliderSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;
import java.util.function.Supplier;

public class SliderSettingComponent extends SettingComponent {

    private float smoothedProgress = 0f;
    private float lastWrapWidth = -1f;
    private String wrappedDescription = "";

    private final Supplier<String> descriptionSource = Suppliers.memoize(
            () -> getSettingLayer().getDescription().getString()
    );

    public SliderSettingComponent(SliderSetting sliderSetting) {
        super(sliderSetting);
    }

    @Override
    public void init() {
        float width = 240f / 2 - 10;
        float nameH = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);

        wrappedDescription = MsdfUtil.cutString(descriptionSource.get(), 6, width);
        float descH = Api.inter().getHeight(wrappedDescription, 6);

        size(width, nameH + 5 + descH + 7.5f);
        lastWrapWidth = width;
    }

    @Override
    public SliderSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        SliderSetting slider = (SliderSetting) getSettingLayer();

        if (getWidth() != lastWrapWidth) {
            wrappedDescription = MsdfUtil.cutString(descriptionSource.get(), 6, getWidth());
            lastWrapWidth = getWidth();
        }

        boolean overTrack = isOverTrack(mouseX, mouseY);
        if (Boolean.TRUE.equals(slider.getDragging())) {
            update(mouseX);
        }

        // Заголовок
        String rawName = getSettingLayer().getName().getString();
        String valueString = Objects.nonNull(slider.getValue())
                ? String.format(java.util.Locale.US, "%.1f", slider.getValue())
                : "";

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(rawName)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        // Значение справа с блёром и тёмным фоном
        if (!valueString.isEmpty()) {
            float valueBoxW = Api.inter().getWidth(valueString, 6) + 10;

            Api.blur()
                    .radius(new QuadRadiusState(2))
                    .size(new SizeState(valueBoxW, 9))
                    .blurRadius(8)
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + getWidth() - valueBoxW, getY());

            Api.rectangle()
                    .size(new SizeState(valueBoxW, 9))
                    .radius(new QuadRadiusState(2))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 70)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + getWidth() - valueBoxW, getY());

            Api.text()
                    .font(Api.inter())
                    .text(valueString)
                    .color(0xFFFFFFFF)
                    .size(6)
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + getWidth() - valueBoxW + 5, getY() + .5f);
        }

        // Описание
        if (!wrappedDescription.isEmpty()) {
            Api.text()
                    .size(6)
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                    .text(wrappedDescription)
                    .font(Api.inter())
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX(),
                            getY() + Api.inter().getHeight(rawName, 7) + 4);
        }

        // Трек
        float trackY = getY() + getHeight() - 5;
        Api.blur()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(getWidth(), 5))
                .blurRadius(6)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), trackY);

        Api.rectangle()
                .size(new SizeState(getWidth(), 5))
                .radius(new QuadRadiusState(2))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 50)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), trackY);

        // Прогресс
        float min = slider.getMin();
        float max = slider.getMax();
        float value = Objects.requireNonNullElse(slider.getValue(), min);
        float range = Math.max(0.0001f, max - min);
        float targetProgress = MathHelper.clamp((value - min) / range, 0f, 1f);

        smoothedProgress += (targetProgress - smoothedProgress) * MathHelper.clamp(0.18f + delta * 0.5f, 0.18f, 0.65f);

        float fillW = getWidth() * smoothedProgress;
        if (fillW > 0.5f) {
            Api.rectangle()
                    .size(new SizeState(fillW, 5))
                    .radius(new QuadRadiusState(2))
                    .color(new QuadColorState(0xFFFFFFFF))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), trackY);
        }

        // Хэндл
        float handleSize = (overTrack || Boolean.TRUE.equals(slider.getDragging())) ? 7f : 6f;
        float handleX = getX() + Math.max(0.5f, Math.min(getWidth() - handleSize - 0.5f, fillW - handleSize / 2f));
        float handleY = trackY + (5f - handleSize) / 2f;

        Api.shadow()
                .radius(new QuadRadiusState(handleSize / 2f + 1f))
                .softness(1)
                .shadow(8)
                .size(new SizeState(handleSize + 1f, handleSize + 1f))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 28)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), handleX - 0.5f, handleY - 0.5f);

        Api.rectangle()
                .size(new SizeState(handleSize, handleSize))
                .radius(new QuadRadiusState(handleSize / 2f))
                .color(new QuadColorState(0xFFFFFFFF))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), handleX, handleY);

        return this;
    }

    private boolean isOverTrack(double mouseX, double mouseY) {
        return jaypasha.funpay.utility.math.Math.isHover(mouseX, mouseY, getX(), getY() + getHeight() - 5, getWidth(), 5);
    }

    void update(double mouseX) {
        SliderSetting slider = (SliderSetting) getSettingLayer();

        float clampedMouseX = (float) MathHelper.clamp(mouseX, getX(), getX() + getWidth());
        float range = slider.getMax() - slider.getMin();

        if (Math.abs(range) < 1e-6f) {
            slider.set(slider.getMin());
            return;
        }

        float t = (clampedMouseX - getX()) / getWidth();
        float newValue = slider.getMin() + t * range;

        float step = slider.getIncrements();
        if (step > 0f) {
            newValue = Math.round(newValue / step) * step;
        }

        newValue = Math.max(slider.getMin(), Math.min(slider.getMax(), newValue));

        slider.set(newValue);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isOverTrack(mouseX, mouseY)) {
            ((SliderSetting) getSettingLayer()).setDragging(true);
            update(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ((SliderSetting) getSettingLayer()).setDragging(false);
        return false;
    }
}
