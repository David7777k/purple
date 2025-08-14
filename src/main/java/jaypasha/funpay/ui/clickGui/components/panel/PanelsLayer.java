package jaypasha.funpay.ui.clickGui.components.panel;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.utility.math.Math;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PanelsLayer extends Component {

    private final List<PanelComponent> componentsList = new ArrayList<>();

    @Override
    public void init() {
        componentsList.clear();
        Arrays.stream(Category.values())
                .map(PanelComponent::new)
                .forEach(componentsList::add);
        initModules();
    }

    public void initModules() {
        componentsList.forEach(PanelComponent::init);
    }

    @Override
    public PanelsLayer render(DrawContext context, int mouseX, int mouseY, float delta) {
        float x = getX();
        final float panelWidth = 250f / 2f;
        final float gap = 5f;

        for (PanelComponent e : componentsList) {
            e.position(x, getY())
                    .size(panelWidth, getHeight())
                    .render(context, mouseX, mouseY, delta);
            x += panelWidth + gap;
        }
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            for (PanelComponent e : componentsList) {
                if (e.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        componentsList.forEach(e -> e.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        componentsList.forEach(e -> e.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        componentsList.forEach(e -> e.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        componentsList.forEach(e -> e.keyReleased(keyCode, scanCode, modifiers));
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}
