package jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.window;

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.components.settings.modeListSetting.ModeListSettingHelper;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.windows.WindowLayer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModeListSettingWindowComponent extends WindowLayer {

    ModeListSetting modeListSetting;
    List<Component> components = new ArrayList<>();

    public ModeListSettingWindowComponent(ModeListSetting modeListSetting) {
        this.modeListSetting = modeListSetting;
        components.addAll(ModeListSettingHelper.values(modeListSetting));
    }

    @Override
    public void init() {
        size(
                modeListSetting.asStringList().stream().map(e -> Api.inter().getWidth(e, 8) + 25).reduce(0f, Float::max),
                modeListSetting.asStringList().size() * 15f
        );
    }

    @Override
    public ModeListSettingWindowComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Blur + фон
        Api.blur()
                .radius(new QuadRadiusState(3))
                .size(new SizeState(getWidth(), getHeight()))
                .blurRadius(10)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .radius(new QuadRadiusState(3))
                .size(new SizeState(getWidth(), getHeight()))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 70)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Элементы
        float offset = 0f;
        for (Component e : components) {
            boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY() + offset, getWidth(), 15f);

            if (hovered) {
                Api.rectangle()
                        .size(new SizeState(getWidth(), 15f))
                        .radius(new QuadRadiusState(2))
                        .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 20)))
                        .build()
                        .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + offset);
            }

            e.position(getX(), getY() + offset).size(getWidth(), 15f).render(context, mouseX, mouseY, delta);
            offset += 15f;
        }
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            components.forEach(e -> e.mouseClicked(mouseX, mouseY, button));
            return true;
        } else {
            if (getAnimation().getDirection().equals(Direction.BACKWARDS)) return false;
            Pasxalka.getInstance().getClickGuiScreen().getWindowRepository().pop(this);
            return true;
        }
    }
}
