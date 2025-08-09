package jaypasha.funpay.ui.clickGui.components.module;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.ModuleEvent;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.render.utility.TextureUtil;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.ComponentBuilder;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleLayerComponent extends Component {

    ModuleLayer moduleLayer;

    List<SettingComponent> components = new ArrayList<>();

    public ModuleLayerComponent(ModuleLayer moduleLayer) {
        this.moduleLayer = moduleLayer;
        this.components.addAll(Helper.settingComponents(moduleLayer));
    }

    @Override
    public ComponentBuilder render(DrawContext context, int mouseX, int mouseY, float delta) {
        float animation = moduleLayer.getAnimation().getOutput().floatValue();

        Api.border()
                .size(new SizeState(getWidth(),getHeight()))
                .radius(new QuadRadiusState(2.5f))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 20)))
                .thickness(-1f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(2.5f))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, (int) (10 + (25 * animation)))))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.text()
                .size(17f / 2)
                .font(Api.inter())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (50 + (50 * animation))))
                .text(moduleLayer.getModuleName().getString())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 5 + (5 * animation), getY() - 1 + (40f / 2 - Api.inter().getHeight(moduleLayer.getModuleName().getString(), 17f / 2)) / 2);

        Api.texture()
                .size(new SizeState(10, 10))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * animation))))
                .texture(0f, 0f, 1f, 1f, TextureUtil.of("images/check.png"))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - 10 - (5 * animation), getY() + (40f / 2 - 10) / 2);

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        components.stream()
            .filter(e -> e.getSettingLayer().getVisible().get())
            .forEach(e -> {
                    e.position(getX() + 5f, getY() + 20f + offset.get()).render(context, mouseX, mouseY, delta);
                    offset.set(offset.get() + e.getHeight());
        });

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight()) && components.stream().noneMatch(e -> e.mouseClicked(mouseX, mouseY, button))) {
            if (button == 0)
                EventManager.call(new ModuleEvent.ToggleEvent(moduleLayer));
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        components.forEach(e -> e.mouseReleased(mouseX, mouseY, button));

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
