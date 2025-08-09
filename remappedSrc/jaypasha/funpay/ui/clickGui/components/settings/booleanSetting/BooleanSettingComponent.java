package jaypasha.funpay.ui.clickGui.components.settings.booleanSetting;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.BooleanSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import jaypasha.funpay.utility.render.utility.TextureUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.Objects;
import java.util.function.Supplier;

public class BooleanSettingComponent extends SettingComponent {

    Supplier<String> descriptionText = Suppliers.memoize(() -> Objects.isNull(getSettingLayer().getDescription()) ? "Описание отсутствует." : MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, 240f / 2 - 35));

    public BooleanSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);
    }

    @Override
    public void init() {
        float moduleNameHeight = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);
        float descriptionHeight = Api.inter().getHeight(descriptionText.get(), 6);

        size(240f / 2 - 10, moduleNameHeight + 5 + descriptionHeight);
    }

    @Override
    public BooleanSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        float animation = getSettingLayer().getAnimation().getOutput().floatValue();

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                .text(descriptionText.get())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 5);

        Api.texture()
                .size(new SizeState(10, 10))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * animation))))
                .texture(0f, 0f, 1f, 1f, TextureUtil.of("images/check.png"))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - 10 - (5 * animation),
                        getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + (Api.inter().getHeight(descriptionText.get(), 6) / 2));

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            BooleanSetting booleanSetting = (BooleanSetting) getSettingLayer();
            booleanSetting.set(!booleanSetting.getEnabled());

            return true;
        }

        return false;
    }
}
