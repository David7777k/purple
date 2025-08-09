package jaypasha.funpay.ui.clickGui.components.settings.bindSetting;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.BindSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.keyboard.KeyBoardUtil;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Supplier;

public class BindSettingComponent extends SettingComponent {

    Supplier<BindSetting> bindSetting = Suppliers.memoize(() -> (BindSetting) getSettingLayer());
    Supplier<Float> valueWidth = () -> Api.inter().getWidth(KeyBoardUtil.translate(bindSetting.get().getKey()), 6) + 10;
    Supplier<String> descriptionText = Suppliers.memoize(() -> MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, 240f / 2 - 10));

    public BindSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);
    }

    @Override
    public void init() {
        float moduleNameHeight = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);
        float descriptionHeight = Api.inter().getHeight(descriptionText.get(), 6);

        size(240f / 2 - 10, moduleNameHeight + 5 + descriptionHeight);
    }

    @Override
    public BindSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
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

        Api.border()
                .size(new SizeState(valueWidth.get(), 9))
                .radius(new QuadRadiusState(2))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (25 + (25 * animation)))))
                .thickness(-1f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth.get(), getY());

        Api.text()
                .size(6)
                .color(0xFFFFFFFF)
                .text(KeyBoardUtil.translate(bindSetting.get().getKey()))
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - valueWidth.get() + 5, getY() + 1);

        return null;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!bindSetting.get().getSelected()) return false;

        bindSetting.get().set(keyCode == GLFW.GLFW_KEY_DELETE ? 0 : keyCode);

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            if (button == 0)
                bindSetting.get().setSelected(!bindSetting.get().getSelected());
            else if (bindSetting.get().getSelected()) {
                bindSetting.get().set(button);
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
