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

    Supplier<String> descriptionText = Suppliers.memoize(() -> MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, 240f / 2 - 10));

    public SliderSettingComponent(SliderSetting sliderSetting) {
        super(sliderSetting);
    }

    @Override
    public void init() {
        float moduleNameHeight = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);
        float descriptionHeight = Api.inter().getHeight(descriptionText.get(), 6);

        size(240f / 2 - 10, moduleNameHeight + 5 + descriptionHeight + 7.5f);
    }

    @Override
    public SliderSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        SliderSetting sliderSetting = (SliderSetting) getSettingLayer();

        if (sliderSetting.getDragging())
            update(mouseX);

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        if (Objects.nonNull(sliderSetting.getValue())) {
            String valueString = String.format("%.1f", sliderSetting.getValue());
            float valueWidth = 10 + Api.inter().getWidth(valueString, 6);

            Api.border()
                    .size(new SizeState(valueWidth, 9))
                    .radius(new QuadRadiusState(2))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 25)))
                    .thickness(-1f)
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth, getY());

            Api.text()
                    .font(Api.inter())
                    .text(valueString)
                    .color(0xFFFFFFFF)
                    .size(6)
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth + 5, getY() + .5f);
        }

        if (!descriptionText.get().isEmpty())
            Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                .text(descriptionText.get())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4);

        Api.rectangle()
                .size(new SizeState(getWidth(), 5))
                .radius(new QuadRadiusState(1))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 25)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + getHeight() - 5);

        if (Objects.nonNull(sliderSetting.getValue())) {
            float sliderWidth = getWidth() * ((sliderSetting.getValue() - sliderSetting.getMin()) / (sliderSetting.getMax() - sliderSetting.getMin()));

            Api.rectangle()
                    .size(new SizeState(sliderWidth, 5))
                    .radius(new QuadRadiusState(1))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 100)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + getHeight() - 5);
        }

        return null;
    }

    void update(double mouseX) {
        SliderSetting sliderSetting = (SliderSetting) getSettingLayer();

        float clampedMouseX = (float) MathHelper.clamp(mouseX, getX(), getX() + getWidth());

        float newValue = sliderSetting.getMin() + ((clampedMouseX - getX()) / (getX() + getWidth() - getX())) * (sliderSetting.getMax() - sliderSetting.getMin());

        newValue = Math.round(newValue / sliderSetting.getIncrements()) * sliderSetting.getIncrements();

        newValue = Math.max(sliderSetting.getMin(), Math.min(sliderSetting.getMax(), newValue));

        sliderSetting.set(newValue);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (jaypasha.funpay.utility.math.Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            if (jaypasha.funpay.utility.math.Math.isHover(mouseX, mouseY, getX(), getY() + getHeight() - 5, getWidth(), 5)) {
                SliderSetting sliderSetting = (SliderSetting) getSettingLayer();
                sliderSetting.setDragging(true);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        SliderSetting sliderSetting = (SliderSetting) getSettingLayer();
        sliderSetting.setDragging(false);

        return false;
    }
}
