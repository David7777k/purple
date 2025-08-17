package jaypasha.funpay.ui.clickGui.components.settings.modeListSetting;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.window.ModeListSettingWindowComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import jaypasha.funpay.utility.windows.WindowLayer;
import jaypasha.funpay.utility.windows.WindowRepository;
import net.minecraft.client.gui.DrawContext;

import java.util.List;
import java.util.function.Supplier;

public class ModeListSettingComponent extends SettingComponent {

    private final Supplier<ModeListSetting> modeListSetting = Suppliers.memoize(() -> (ModeListSetting) getSettingLayer());
    private final WindowLayer windowLayer;

    // единый лимит ширины превью
    private static final float MAX_BOX_WIDTH = 100f;

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
        // якорим окно справа, по центру по Y от этого компонента
        windowLayer.position(getX() + getWidth() - windowLayer.getWidth(), getY() + getHeight() / 2f);

        // Имя
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

        // Превью выбранных значений (текст + ширина после обрезки)
        Preview pv = buildPreview(modeListSetting.get().getSelected(), MAX_BOX_WIDTH);
        float valueWidth = pv.width(); // уже учтён padding + обрезка
        float boxX = getX() + getWidth() - valueWidth;
        float boxY = getY();
        boolean hovered = Math.isHover(mouseX, mouseY, boxX, boxY, valueWidth, 9);

        // Blur под бокс
        Api.blur()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(valueWidth, 9))
                .blurRadius(8)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), boxX, boxY);

        // Фон бокса
        Api.rectangle()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(valueWidth, 9))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, hovered ? 80 : 60)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), boxX, boxY);

        // Текст превью
        Api.text()
                .size(6)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 100))
                .text(pv.text())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), boxX + 5, boxY + 0.5f);

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        WindowRepository repo = Pasxalka.getInstance().getClickGuiScreen().getWindowRepository();

        // Тот же расчёт превью, что и в render — чтобы хит-тест совпадал
        Preview pv = buildPreview(modeListSetting.get().getSelected(), MAX_BOX_WIDTH);
        float valueWidth = pv.width();
        float boxX = getX() + getWidth() - valueWidth;
        float boxY = getY();

        if (Math.isHover(mouseX, mouseY, boxX, boxY, valueWidth, 9)) {
            if (!repo.contains(windowLayer) && !modeListSetting.get().asStringList().isEmpty()) {
                windowLayer.position(boxX, boxY + 9f);
                repo.push(windowLayer);
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // --- helpers ---

    private record Preview(String text, float width) {}

    private Preview buildPreview(List<String> selected, float maxBoxWidth) {
        String valueText;
        if (selected == null || selected.isEmpty()) {
            valueText = "N/A";
        } else if (selected.size() <= 2) {
            valueText = String.join(", ", selected);
        } else {
            valueText = selected.get(0) + ", " + selected.get(1) + " +" + (selected.size() - 2);
        }

        // обрезаем под maxBoxWidth (с учётом padding = 10)
        String clipped = valueText;
        float pad = 10f;
        while (Api.inter().getWidth(clipped, 6) + pad > maxBoxWidth && clipped.length() > 4) {
            clipped = clipped.substring(0, clipped.length() - 2) + "…";
        }

        float finalWidth = Math.max(20f, Math.min(maxBoxWidth, Api.inter().getWidth(clipped, 6) + pad));
        return new Preview(clipped, finalWidth);
    }
}
