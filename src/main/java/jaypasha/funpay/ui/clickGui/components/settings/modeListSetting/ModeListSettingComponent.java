package jaypasha.funpay.ui.clickGui.components.settings.modeListSetting;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.window.ModeListSettingWindowComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import jaypasha.funpay.utility.windows.WindowLayer;
import jaypasha.funpay.utility.windows.WindowRepository;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Supplier;

public class ModeListSettingComponent extends SettingComponent {

    private final Supplier<ModeListSetting> modeListSetting = Suppliers.memoize(() -> (ModeListSetting) getSettingLayer());
    private final WindowLayer windowLayer;

    public ModeListSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);
        this.windowLayer = new ModeListSettingWindowComponent(modeListSetting.get());
    }

    @Override
    public void init() {
        windowLayer.init();
        float contentWidth = 240f / 2 - 10;
        float nameH = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);

        float descriptionWrap = Math.max(0f, contentWidth - windowLayer.getWidth() - 10f);
        String descriptionText = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, descriptionWrap);
        float descH = Api.inter().getHeight(descriptionText, 6);

        size(contentWidth, nameH + 5 + descH);
    }

    @Override
    public ModeListSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        windowLayer.position(getX() + getWidth() - windowLayer.getWidth(), getY() + getHeight() / 2f);

        // Название
        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        // Описание
        float dynamicWrap = Math.max(0f, getWidth() - windowLayer.getWidth() - 10f);
        String descriptionText = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, dynamicWrap);
        if (!descriptionText.isEmpty()) {
            Api.text()
                    .size(6)
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                    .text(descriptionText)
                    .font(Api.inter())
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX(),
                            getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4);
        }

        // Value box (чужой стиль)
        String valueText = modeListSetting.get().empty() || modeListSetting.get().emptySelected()
                ? "N/A"
                : modeListSetting.get().getSelected().getFirst();
        float valueWidth = Api.inter().getWidth(valueText, 6) + 10;

        boolean hovered = jaypasha.funpay.utility.math.Math.isHover(mouseX, mouseY,
                getX() + getWidth() - valueWidth, getY(), valueWidth, 9);

        Api.blur()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(valueWidth, 9))
                .blurRadius(8)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth, getY());

        Api.rectangle()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(valueWidth, 9))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, hovered ? 80 : 60)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth, getY());

        Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 100))
                .text(valueText)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + getWidth() - valueWidth + 5, getY() + 0.5f);

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        WindowRepository windowRepository = Pasxalka.getInstance().getClickGuiScreen().getWindowRepository();
        if (jaypasha.funpay.utility.math.Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                && !windowRepository.contains(windowLayer)) {
            windowLayer.position(getX() + getWidth() - windowLayer.getWidth(), getY() + getHeight() / 2f);
            windowRepository.push(windowLayer);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
