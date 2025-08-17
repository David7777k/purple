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

import java.util.function.Supplier;

public class BindSettingComponent extends SettingComponent {

    private final Supplier<BindSetting> bindSetting =
            Suppliers.memoize(() -> (BindSetting) getSettingLayer());

    private String getBindText() {
        String translated = KeyBoardUtil.translate(bindSetting.get().getKey());
        return translated == null || translated.isEmpty() ? "N/A" : translated;
    }

    private float getValueWidth() {
        return Api.inter().getWidth(getBindText(), 6) + 10;
    }

    private final Supplier<String> descriptionText =
            Suppliers.memoize(() -> MsdfUtil.cutString(
                    getSettingLayer().getDescription().getString(),
                    6, 240f / 2 - 10
            ));

    public BindSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);
    }

    @Override
    public void init() {
        float nameH = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);
        float descH = Api.inter().getHeight(descriptionText.get(), 6);
        size(240f / 2 - 10, nameH + 3 + descH);
    }

    @Override
    public BindSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
        boolean waitingBind = bindSetting.get().getSelected();

        if (hovered) {
            Api.rectangle()
                    .size(new SizeState(getWidth(), getHeight()))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 8)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());
        }

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        if (!descriptionText.get().isEmpty()) {
            Api.text()
                    .size(6)
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                    .text(descriptionText.get())
                    .font(Api.inter())
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX(),
                            getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4);
        }

        float valW = getValueWidth();
        float boxX = getX() + getWidth() - valW;
        float boxY = getY();

        Api.blur()
                .size(new SizeState(valW, 9))
                .radius(new QuadRadiusState(2))
                .blurRadius(6f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), boxX, boxY);

        Api.rectangle()
                .size(new SizeState(valW, 9))
                .radius(new QuadRadiusState(2))
                .color(new QuadColorState(
                        ColorUtility.applyOpacity(0xFFFFFFFF,
                                waitingBind
                                        ? (int) (20 + (Math.sin(System.currentTimeMillis() / 150.0) + 1) * 20)
                                        : (hovered ? 18 : 10)
                        )))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), boxX, boxY);

        Api.text()
                .size(6)
                .color(0xFFFFFFFF)
                .text(getBindText())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        boxX + 5, boxY + 1);

        return this;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!bindSetting.get().getSelected()) return false;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            bindSetting.get().setSelected(false); // отмена бинда
        } else {
            // use -1 as "no key"
            bindSetting.get().set(keyCode == GLFW.GLFW_KEY_DELETE ? -1 : keyCode);
            bindSetting.get().setSelected(false);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            if (button == 0) {
                bindSetting.get().setSelected(!bindSetting.get().getSelected());
            } else if (bindSetting.get().getSelected()) {
                // mouse buttons supported as binds (optional)
                bindSetting.get().set(button);
                bindSetting.get().setSelected(false);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
