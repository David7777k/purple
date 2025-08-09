package jaypasha.funpay.ui.clickGui;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.impl.KeyEvent;
import jaypasha.funpay.ui.clickGui.components.panel.PanelsLayer;
import jaypasha.funpay.utility.math.Math;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClickGuiScreen extends Screen implements Api {

    List<Component> componentsList = new ArrayList<>();

    PanelsLayer panelsLayer = new PanelsLayer();

    final float width = 645f;
    final float height = 550f / 2;

    Supplier<Float> x = () -> (mc.getWindow().getScaledWidth() - width) / 2;
    Supplier<Float> y = () -> (mc.getWindow().getScaledHeight() - height) / 2;

    public ClickGuiScreen() {
        super(Text.of("pasxalka.click_gui"));

        componentsList.addAll(List.of(
            panelsLayer
        ));

        Pasxalka.getInstance().getEventBus().register(this);
    }

    @Override
    protected void init() {
        componentsList.forEach(Component::init);

        super.init();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        panelsLayer.position(x.get(), y.get()).size(width, height);

        componentsList.forEach(e -> e.render(context, mouseX, mouseY, delta));

        super.render(context, mouseX, mouseY, delta);
    }

    @Subscribe
    public void keyListener(KeyEvent keyEvent) {
        if (Objects.isNull(mc.currentScreen) && keyEvent.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT)
            mc.setScreen(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        componentsList.forEach(e -> e.mouseClicked(mouseX, mouseY, button));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        componentsList.forEach(e -> e.mouseReleased(mouseX, mouseY, button));

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
