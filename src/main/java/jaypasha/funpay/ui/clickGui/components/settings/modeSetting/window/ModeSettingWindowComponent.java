package jaypasha.funpay.ui.clickGui.components.settings.modeSetting.window;

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.settings.impl.ModeSetting;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.components.settings.modeSetting.ModeSettingHelper;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.windows.WindowLayer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeSettingWindowComponent extends WindowLayer {

    ModeSetting modeSetting;
    List<Component> components = new ArrayList<>();

    public ModeSettingWindowComponent(ModeSetting modeSetting) {
        this.modeSetting = modeSetting;
        components.addAll(ModeSettingHelper.values(modeSetting));
    }

    public void init() {
        float maxWidth = modeSetting.getValues().stream()
                .map(e -> Api.inter().getWidth(e, 8) + 25)
                .reduce(0f, Float::max);
        float height = modeSetting.getValues().size() * 15f;

        // гарантируем минимум
        maxWidth = Math.max(40f, maxWidth);
        height   = Math.max(15f, height);

        size(maxWidth, height);
    }


    @Override
    public ModeSettingWindowComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (getWidth() <= 1f || getHeight() <= 1f) return this; // guard
        // Blur
        Api.blur()
                .radius(new QuadRadiusState(3))
                .size(new SizeState(getWidth(), getHeight()))
                .blurRadius(10)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Фон
        Api.rectangle()
                .radius(new QuadRadiusState(3))
                .size(new SizeState(getWidth(), getHeight()))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 70)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Подсветка при hover
        float offsetY = 0f;
        for (Component c : components) {
            boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY() + offsetY, getWidth(), 15f);

            if (hovered) {
                Api.rectangle()
                        .size(new SizeState(getWidth(), 15f))
                        .radius(new QuadRadiusState(2))
                        .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 20)))
                        .build()
                        .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + offsetY);
            }

            c.position(getX(), getY() + offsetY)
                    .size(getWidth(), 15f)
                    .render(context, mouseX, mouseY, delta);

            offsetY += 15f;
        }
        return this;
    }

        @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            for (Component e : components) {
                if (e.mouseClicked(mouseX, mouseY, button)) return true;
            }
            return true;

        }
        Pasxalka.getInstance().getClickGuiScreen().getWindowRepository().pop(this);
        return true;
    }
}
