package jaypasha.funpay.ui.clickGui.components.panel;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.BackgroundComponent;
import jaypasha.funpay.ui.clickGui.components.module.ModuleLayerComponent;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.math.Math;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PanelComponent extends Component {

    List<ModuleLayerComponent> componentsList = new ArrayList<>();

    BackgroundComponent backgroundComponent;

    public PanelComponent(Category category) {
        backgroundComponent = new BackgroundComponent(category);
        componentsList.addAll(Helper.moduleLayers(category, (e) -> true));
    }

    @Override
    public PanelComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
//        Если делать Search будете то разлогайте
//        componentsList.addAll(Helper.moduleLayers(category, (e) -> e.getModuleName().getString().contains(searchValue)));

        backgroundComponent.position(getX(), getY()).size(getWidth(), getHeight()).render(context, mouseX, mouseY, delta);

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        componentsList.forEach(e -> {
            float height = Helper.moduleHeight(e.getComponents());
            e.getComponents().forEach(SettingComponent::init);
            e.position(getX() + 2.5f, getY() + 32 + offset.get()).size(240f / 2, height).render(context, mouseX, mouseY, delta);

            offset.set(offset.get() + height + 2.5f);
        });

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight()))
            componentsList.forEach(e -> e.mouseClicked(mouseX, mouseY, button));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        componentsList.forEach(e -> e.mouseReleased(mouseX, mouseY, button));

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
