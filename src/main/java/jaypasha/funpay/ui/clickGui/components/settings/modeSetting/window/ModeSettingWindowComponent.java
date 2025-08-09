package jaypasha.funpay.ui.clickGui.components.settings.modeSetting.window;

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.animations.Direction;
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
import java.util.concurrent.atomic.AtomicReference;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeSettingWindowComponent extends WindowLayer {

    ModeSetting modeSetting;
    List<Component> components = new ArrayList<>();

    public ModeSettingWindowComponent(ModeSetting modeSetting) {
        this.modeSetting = modeSetting;
        components.addAll(ModeSettingHelper.values(modeSetting));
    }

    @Override
    public void init() {
        size(
            modeSetting.getValues().stream().map(e -> Api.inter().getWidth(e, 8) + 25).reduce(0f, Float::max),
        modeSetting.getValues().size() * 15f
        );
    }

    @Override
    public ModeSettingWindowComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        Api.border()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(getWidth(), getHeight()))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 25)))
                .thickness(-.5f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.blur()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(getWidth(), getHeight()))
                .blurRadius(8)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .radius(new QuadRadiusState(2))
                .size(new SizeState(getWidth(), getHeight()))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 65)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        components.forEach(e -> {
            e.position(getX(), getY() + offset.get()).size(getWidth(), 15f).render(context, mouseX, mouseY, delta);
            offset.set(offset.get() + 15f);
        });

        return null;
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
