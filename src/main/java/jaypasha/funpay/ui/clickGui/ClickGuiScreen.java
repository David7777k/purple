package jaypasha.funpay.ui.clickGui;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.api.events.impl.KeyEvent;
import jaypasha.funpay.ui.clickGui.components.panel.PanelsLayer;
import jaypasha.funpay.ui.clickGui.components.search.SearchComponent;
import jaypasha.funpay.utility.windows.WindowRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static jaypasha.funpay.utility.math.Math.scale;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClickGuiScreen extends Screen implements Api {

    List<Component> componentsList = new ArrayList<>();

    @Getter
    WindowRepository windowRepository = new WindowRepository();

    PanelsLayer panelsLayer = new PanelsLayer();
    SearchComponent searchComponent = new SearchComponent();

    Animation animation = new DecelerateAnimation()
            .setMs(150)
            .setValue(1f);

    final float width = 645f;
    final float height = 550f / 2;

    Supplier<Float> x = () -> (mc.getWindow().getScaledWidth() - width) / 2;
    Supplier<Float> y = () -> (mc.getWindow().getScaledHeight() - height) / 2;

    public ClickGuiScreen() {
        super(Text.of("pasxalka.click_gui"));

        componentsList.addAll(List.of(
                panelsLayer,
                searchComponent
        ));

        Pasxalka.getInstance().getEventBus().register(this);
    }

    @Override
    protected void init() {
        componentsList.forEach(Component::init);
        animation.setDirection(Direction.FORWARDS);
        animation.reset();

        super.init();
    }

    @Override
    public void close() {
        animation.setDirection(Direction.BACKWARDS);
        animation.reset();
        windowRepository.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (animation.isFinished(Direction.BACKWARDS))
            mc.setScreen(null);

        panelsLayer.position(x.get(), y.get()).size(width, height);
        searchComponent.position(x.get() + width / 2 - 50, y.get() + height + 25).size(100, 20);

        scale(context.getMatrices(), x.get() + width / 2, y.get() + height / 2, animation.getOutput().floatValue(), () -> {
            componentsList.forEach(e -> e.render(context, mouseX, mouseY, delta));
            windowRepository.render(context, mouseX, mouseY, delta);
        });

        super.render(context, mouseX, mouseY, delta);
    }

    @Subscribe
    public void keyListener(KeyEvent keyEvent) {
        if (Objects.isNull(mc.currentScreen) && keyEvent.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT)
            mc.setScreen(Pasxalka.getInstance().getClickGuiScreen());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!windowRepository.mouseClicked(mouseX, mouseY, button))
            componentsList.forEach(e -> e.mouseClicked(mouseX, mouseY, button));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!windowRepository.mouseReleased(mouseX, mouseY, button))
            componentsList.forEach(e -> e.mouseReleased(mouseX, mouseY, button));

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!windowRepository.keyPressed(keyCode, scanCode, modifiers))
            componentsList.forEach(e -> e.keyPressed(keyCode, scanCode, modifiers));

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // фикс: раньше тут ошибочно вызывали keyPressed(...)
        if (!windowRepository.keyReleased(keyCode, scanCode, modifiers))
            componentsList.forEach(e -> e.keyReleased(keyCode, scanCode, modifiers));

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        componentsList.forEach(e -> e.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!windowRepository.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
            componentsList.forEach(e -> e.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
