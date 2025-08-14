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

    private final Supplier<ModeSetting> modeSetting = Suppliers.memoize(() -> (ModeSetting) getSettingLayer());
    private final WindowLayer windowLayer;

    public ModeSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);
        this.windowLayer = new ModeSettingWindowComponent(modeSetting.get());
    }

    @Override
    public void init() {
        windowLayer.init();

        // В init ещё нет «живой» ширины, поэтому используем базовую константу
        float contentWidth = 240f / 2 - 10;
        float nameH = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);

        float wrap = Math.max(0f, contentWidth - windowLayer.getWidth() - 10f);
        String descriptionText = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, wrap);
        float descH = Api.inter().getHeight(descriptionText, 6);

        size(contentWidth, nameH + 5 + descH);
    }

    @Override
    public ModeSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Актуализируем позицию окна каждый кадр
        windowLayer.position(getX() + getWidth() - windowLayer.getWidth(), getY() + getHeight() / 2f);

        // Name
        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        // Description (c «живой» шириной)
        float wrap = Math.max(0f, getWidth() - windowLayer.getWidth() - 10f);
        String description = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, wrap);
        if (!description.isEmpty()) {
            Api.text()
                    .size(6)
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                    .text(description)
                    .font(Api.inter())
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX(),
                            getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4);
        }

        // Value box with hover effect
        String valueText = Objects.requireNonNullElse(modeSetting.get().getValue(), "N/A");
        float valueWidth = Api.inter().getWidth(valueText, 6) + 10;
        boolean hovered = Math.isHover(mouseX, mouseY, getX() + getWidth() - valueWidth, getY(), valueWidth, 9);

        Api.rectangle()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(valueWidth, 9))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, hovered ? 20 : 10)))
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
        WindowRepository repo = Pasxalka.getInstance().getClickGuiScreen().getWindowRepository();

        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            if (!repo.contains(windowLayer)) {
                // Подстрахуемся положением
                windowLayer.position(getX() + getWidth() - windowLayer.getWidth(), getY() + getHeight() / 2f);
                repo.push(windowLayer);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
