package jaypasha.funpay.ui.clickGui.components.settings.modeListSetting;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeListSettingValueComponent extends Component {

    ModeListSetting setting;
    String value;

    public ModeListSettingValueComponent(ModeListSetting setting, String value) {
        this.setting = setting;
        this.value = value;
    }

    @Override
    public ModeListSettingValueComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean enabled = setting.get(value).getEnabled();
        float anim = setting.get(value).getAnimation().getOutput().floatValue();

        // Blur фон
        Api.blur()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(getWidth(), getHeight()))
                .blurRadius(6)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Базовый прямоугольник
        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, enabled ? 70 : 50)))
                .radius(new QuadRadiusState(2))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Hover-подсветка
        boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
        if (hovered) {
            Api.rectangle()
                    .size(new SizeState(getWidth(), getHeight()))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 15)))
                    .radius(new QuadRadiusState(2))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());
        }

        // Текст значения
        Api.text()
                .font(Api.inter())
                .size(7)
                .text(value)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (50 + 50 * anim)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + 5,
                        getY() + (getHeight() / 2f - Api.inter().getHeight(value, 7) / 2f));

        // Галочка, если включено
        if (enabled) {
            Api.text()
                    .text("✔")
                    .size(7)
                    .font(Api.inter())
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (80 + 20 * anim)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + getWidth() - 10,
                            getY() + (getHeight() / 2f - 3));
        }

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            var bs = setting.get(value);
            if (bs != null) {
                bs.set(!bs.getEnabled()); // toggle, без сброса остальных
            }
            return true; // окно не закрываем
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
