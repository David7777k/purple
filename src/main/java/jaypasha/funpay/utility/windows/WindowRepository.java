package jaypasha.funpay.utility.windows;

import jaypasha.funpay.api.animations.Direction;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

import static jaypasha.funpay.utility.math.Math.scale;

public class WindowRepository {

    List<WindowLayer> windowLayerList = new ArrayList<>();

    public void push(WindowLayer windowLayer) {
        windowLayerList.add(windowLayer);

        windowLayer.getAnimation().setDirection(Direction.FORWARDS);
        windowLayer.getAnimation().reset();
    }

    public void pop(WindowLayer windowLayer) {
        if (windowLayer.getAnimation().getDirection().equals(Direction.BACKWARDS)) return;

        windowLayer.getAnimation().setDirection(Direction.BACKWARDS);
        windowLayer.getAnimation().reset();
    }

    public void close() {
        windowLayerList.forEach(this::pop);
    }

    public boolean contains(WindowLayer windowLayer) {
        return windowLayerList.contains(windowLayer);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        windowLayerList.removeIf(e -> e.getAnimation().isFinished(Direction.BACKWARDS));
        windowLayerList.forEach(e -> scale(context.getMatrices(), e.getX() + e.getWidth() / 2, e.getY() + e.getHeight() / 2, e.getAnimation().getOutput().floatValue(),
                () -> e.render(context, mouseX, mouseY, delta)));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return windowLayerList.stream().anyMatch(e -> e.mouseClicked(mouseX, mouseY, button));
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return windowLayerList.stream().anyMatch(e -> e.mouseReleased(mouseX, mouseY, button));
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return windowLayerList.stream().anyMatch(e -> e.keyPressed(keyCode, scanCode, modifiers));
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return windowLayerList.stream().anyMatch(e -> e.keyReleased(keyCode, scanCode, modifiers));
    }

    public boolean charTyped(char chr, int modifiers) {
        return windowLayerList.stream().anyMatch(e -> e.charTyped(chr, modifiers));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return windowLayerList.stream().anyMatch(e -> e.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
    }
}
