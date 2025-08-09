package jaypasha.funpay.ui.clickGui.components.settings.modeSetting;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.ModeSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeSetting.window.ModeSettingWindowComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import jaypasha.funpay.utility.windows.WindowLayer;
import jaypasha.funpay.utility.windows.WindowRepository;
import net.minecraft.client.gui.DrawContext;

import java.util.Objects;
import java.util.function.Supplier;

public class ModeSettingComponent extends SettingComponent {

    Supplier<ModeSetting> modeSetting = Suppliers.memoize(() -> (ModeSetting) getSettingLayer());

    WindowLayer windowLayer;

    public ModeSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);

        windowLayer = new ModeSettingWindowComponent(modeSetting.get());
    }

    @Override
    public void init() {
        String descriptionText = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, 240f / 2 - 10 - windowLayer.getWidth() - 10);

        windowLayer.init();
        windowLayer.position(getX() + getWidth() - windowLayer.getWidth(), getY() + getHeight() / 2);

        float moduleNameHeight = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);
        float descriptionHeight = Api.inter().getHeight(descriptionText, 6);

        size(240f / 2 - 10, moduleNameHeight + 5 + descriptionHeight);
    }

    @Override
    public ModeSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        String descriptionText = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, 240f / 2 - 10 - windowLayer.getWidth() - 10);

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        if (!descriptionText.isEmpty())
            Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                .text(descriptionText)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4);

        String valueText = modeSetting.get().getValue() == null ? "N/A" : modeSetting.get().getValue();
        float valueWidth = Api.inter().getWidth(valueText, 6) + 10;

        Api.rectangle()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(valueWidth, 9))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 10)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth, getY());

        Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 100))
                .text(valueText)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth + 5, getY() + .5);

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        WindowRepository windowRepository = Pasxalka.getInstance().getClickGuiScreen().getWindowRepository();

        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight()) && !windowRepository.contains(windowLayer)) {
            windowRepository.push(windowLayer);

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
