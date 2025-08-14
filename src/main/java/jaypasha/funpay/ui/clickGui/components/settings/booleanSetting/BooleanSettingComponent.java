package jaypasha.funpay.ui.clickGui.components.settings.booleanSetting;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.impl.BooleanSetting;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.MsdfUtil;
import net.minecraft.client.gui.DrawContext;

public class BooleanSettingComponent extends SettingComponent {

    private String descriptionText = "";

    public BooleanSettingComponent(SettingLayer settingLayer) {
        super(settingLayer);
    }

    @Override
    public void init() {
        // фикс: рассчитываем wrap по известной ширине компонента, а не по getWidth()=0 до size(...)
        float baseWidth = 240f / 2f - 10f;
        float wrap = baseWidth - 35f; // учёт зоны переключателя/отступов
        descriptionText = MsdfUtil.cutString(getSettingLayer().getDescription().getString(), 6, wrap);

        float nameHeight = Api.inter().getHeight(getSettingLayer().getName().getString(), 7);
        float descHeight = Api.inter().getHeight(descriptionText, 6);
        size(baseWidth, nameHeight + 5 + descHeight);
    }

    @Override
    public BooleanSettingComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        float anim = getSettingLayer().getAnimation().getOutput().floatValue();
        boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());

        // Фон при hover
        if (hovered) {
            Api.rectangle()
                    .size(new SizeState(getWidth(), getHeight()))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 8)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());
        }

        // Имя
        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, 95))
                .text(getSettingLayer().getName().getString())
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() - 1);

        // Описание
        if (!descriptionText.isEmpty()) {
            Api.text()
                    .size(6)
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                    .text(descriptionText)
                    .font(Api.inter())
                    .build()
                    .render(
                            context.getMatrices().peek().getPositionMatrix(),
                            getX(),
                            getY() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7) + 4
                    );
        }

        // Тумблер
        float toggleWidth = 16f;
        float toggleHeight = 8f;
        float toggleX = getX() + getWidth() - toggleWidth;
        float toggleY = getY() + (getHeight() - toggleHeight) / 2;

        // Фон тумблера
        Api.rectangle()
                .size(new SizeState(toggleWidth, toggleHeight))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 20)))
                .radius(4)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), toggleX, toggleY);

        // Кружок
        float knobSize = hovered ? 6.5f : 6f;
        float knobX = toggleX + 1 + anim * (toggleWidth - knobSize - 2);
        Api.rectangle()
                .size(new SizeState(knobSize, knobSize))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 100)))
                .radius(knobSize / 2f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), knobX, toggleY + (toggleHeight - knobSize) / 2f);

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            BooleanSetting setting = (BooleanSetting) getSettingLayer();
            setting.set(!setting.getEnabled());
            return true;
        }
        return false;
    }
}
