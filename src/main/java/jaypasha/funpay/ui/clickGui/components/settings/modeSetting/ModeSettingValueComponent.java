package jaypasha.funpay.ui.clickGui.components.settings.modeSetting;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.impl.ModeSetting;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeSettingValueComponent extends Component {

    ModeSetting setting;
    String value;

    @Override
    public ModeSettingValueComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        float animationValue = setting.getAnimation().getOutput().floatValue();
        boolean selected = value.equals(setting.getValue());

        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, selected ? (int) (60 * animationValue) : 0)))
                .radius(new QuadRadiusState(2))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.text()
                .font(Api.inter())
                .size(8)
                .text(value)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (selected ? 50 + (50 * animationValue) : 50)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 5, getY() + Api.inter().getHeight(value, 8) / 4);

        Api.text()
                .text("B")
                .size(7)
                .font(Api.icons())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, selected ? (int) (100 * animationValue) : 0))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - 7 - 5, getY() - .5f + (getHeight() - 7) / 2);

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            setting.set(value);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
