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

    Supplier<String> descriptionText = Suppliers.memoize(() -> MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, 240f / 2 - 35));

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
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        if (!descriptionText.get().isEmpty())
            Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                .text(descriptionText.get())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4);

        Api.text()
                .text("B")
                .size(8)
                .font(Api.icons())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * animation)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - 8 - (5 * animation),
                        getY() + (getHeight() - 8) / 2);


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
