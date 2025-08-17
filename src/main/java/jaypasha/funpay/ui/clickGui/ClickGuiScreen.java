package jaypasha.funpay.ui.clickGui;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.api.events.impl.KeyEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.ui.clickGui.components.panel.ModernPanel;
import jaypasha.funpay.ui.clickGui.components.search.ModernSearchField;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.windows.WindowRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE) // убрали makeFinal
public class ClickGuiScreen extends Screen implements Api {

    List<ModernPanel> panels = new ArrayList<>();
    WindowRepository windowRepository = new WindowRepository();
    ModernSearchField searchField;

    // Анимации
    Animation scaleAnimation = new DecelerateAnimation().setMs(250).setValue(1f);
    Animation gradientAnimation = new DecelerateAnimation().setMs(300).setValue(1f);
    Animation imageAnimation = new DecelerateAnimation().setMs(500).setValue(1f);

    // Размеры и позиции
    float panelWidth = 120f;
    float panelHeight = 300f;
    float panelSpacing = 10f;
    float scale = 1.0f;

    // Состояние
    boolean isClosing = false;

    Supplier<Float> centerX = () -> mc.getWindow().getScaledWidth() / 2f;
    Supplier<Float> centerY = () -> mc.getWindow().getScaledHeight() / 2f;

    public ClickGuiScreen() {
        super(Text.of("Modern ClickGUI"));

        // Создаем панели для каждой категории
        for (Category category : Category.values()) {
            panels.add(new ModernPanel(category));
        }

        // Создаем поле поиска
        searchField = new ModernSearchField();

        try {
            Pasxalka.getInstance().getEventBus().register(this);
        } catch (Exception e) {
            System.err.println("Failed to register ClickGuiScreen to event bus: " + e.getMessage());
        }
    }

    public static boolean hasControlDown() {
        return Screen.hasControlDown();
    }

    @Override
    protected void init() {
        panels.forEach(panel -> {
            try { panel.init(); }
            catch (Exception e) { System.err.println("Error initializing panel: " + e.getMessage()); }
        });

        if (searchField != null) searchField.init();

        scaleAnimation.setDirection(Direction.FORWARDS);
        scaleAnimation.reset();
        gradientAnimation.setDirection(Direction.FORWARDS);
        gradientAnimation.reset();
        imageAnimation.setDirection(Direction.FORWARDS);
        imageAnimation.reset();

        updateScale();
        super.init();
    }

    @Override
    public void close() {
        isClosing = true;

        scaleAnimation.setDirection(Direction.BACKWARDS);
        scaleAnimation.reset();
        gradientAnimation.setDirection(Direction.BACKWARDS);
        gradientAnimation.reset();
        imageAnimation.setDirection(Direction.BACKWARDS);
        imageAnimation.reset();

        if (windowRepository != null) windowRepository.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) { }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (scaleAnimation != null && scaleAnimation.isFinished(Direction.BACKWARDS)) {
            mc.setScreen(null);
            return;
        }

        delta = MathHelper.clamp(delta, 0f, 1f);

        try {
            float scaleValue = scaleAnimation != null ? scaleAnimation.getOutput().floatValue() : 1f;
            float gradientValue = gradientAnimation != null ? gradientAnimation.getOutput().floatValue() : 1f;
            float imageValue = imageAnimation != null ? imageAnimation.getOutput().floatValue() : 1f;

            renderBackground(context, gradientValue);
            renderImage(context, imageValue);

            context.getMatrices().push();
            context.getMatrices().translate(centerX.get(), centerY.get(), 0);
            context.getMatrices().scale(scaleValue * scale, scaleValue * scale, 1f);
            context.getMatrices().translate(-centerX.get(), -centerY.get(), 0);

            renderPanels(context, mouseX, mouseY, delta);
            renderSearchField(context, mouseX, mouseY, delta);
            renderWindows(context, mouseX, mouseY, delta);

            context.getMatrices().pop();
        } catch (Exception e) {
            System.err.println("Critical error in ClickGuiScreen render: " + e.getMessage());
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBackground(DrawContext context, float gradientValue) {
        int gradientColor = ColorUtility.applyOpacity(0xFF6A5ACD, (int)(255 * gradientValue * 0.3f));
        Api.rectangle()
                .size(new SizeState(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight()))
                .color(new QuadColorState(gradientColor, gradientColor, 0x00000000, 0x00000000))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), 0, 0);
    }

    private void renderImage(DrawContext context, float imageValue) { }

    private void renderPanels(DrawContext context, int mouseX, int mouseY, float delta) {
        float totalWidth = panels.size() * (panelWidth + panelSpacing) - panelSpacing;
        float startX = centerX.get() - totalWidth / 2f;
        float startY = centerY.get() - panelHeight / 2f;

        for (int i = 0; i < panels.size(); i++) {
            ModernPanel panel = panels.get(i);
            float x = startX + i * (panelWidth + panelSpacing);
            panel.position(x, startY).size(panelWidth, panelHeight);

            try { panel.render(context, mouseX, mouseY, delta); }
            catch (Exception e) { System.err.println("Error rendering panel: " + e.getMessage()); }
        }
    }

    private void renderSearchField(DrawContext context, int mouseX, int mouseY, float delta) {
        if (searchField == null) return;
        float x = centerX.get() - 60f;
        float y = centerY.get() + panelHeight / 2f + 30f;
        searchField.position(x, y).size(120f, 20f);
        try { searchField.render(context, mouseX, mouseY, delta); }
        catch (Exception e) { System.err.println("Error rendering search field: " + e.getMessage()); }
    }

    private void renderWindows(DrawContext context, int mouseX, int mouseY, float delta) {
        if (windowRepository != null) {
            try { windowRepository.render(context, mouseX, mouseY, delta); }
            catch (Exception e) { System.err.println("Error rendering windows: " + e.getMessage()); }
        }
    }

    @Subscribe
    public void keyListener(KeyEvent keyEvent) {
        try {
            if (Objects.isNull(mc.currentScreen) && keyEvent.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                mc.setScreen(Pasxalka.getInstance().getClickGuiScreen());
            }
        } catch (Exception e) { System.err.println("Error in key listener: " + e.getMessage()); }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        try {
            mouseX = adjustMouseX(mouseX);
            mouseY = adjustMouseY(mouseY);

            if (windowRepository != null && windowRepository.mouseClicked(mouseX, mouseY, button)) return true;
            if (searchField != null && searchField.mouseClicked(mouseX, mouseY, button)) return true;

            for (ModernPanel panel : panels) {
                if (panel.mouseClicked(mouseX, mouseY, button)) return true;
            }
        } catch (Exception e) { System.err.println("Critical error in mouseClicked: " + e.getMessage()); }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        try {
            mouseX = adjustMouseX(mouseX);
            mouseY = adjustMouseY(mouseY);

            if (windowRepository != null && windowRepository.mouseReleased(mouseX, mouseY, button)) return true;
            if (searchField != null) searchField.mouseReleased(mouseX, mouseY, button);

            for (ModernPanel panel : panels) panel.mouseReleased(mouseX, mouseY, button);
        } catch (Exception e) { System.err.println("Error in mouseReleased: " + e.getMessage()); }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!Double.isFinite(verticalAmount) || !Double.isFinite(horizontalAmount)) return false;
        try {
            mouseX = adjustMouseX(mouseX);
            mouseY = adjustMouseY(mouseY);

            if (windowRepository != null && windowRepository.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;

            for (ModernPanel panel : panels) {
                if (panel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
            }
        } catch (Exception e) { System.err.println("Error in mouseScrolled: " + e.getMessage()); }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (windowRepository != null && windowRepository.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (searchField != null && searchField.keyPressed(keyCode, scanCode, modifiers)) return true;

            for (ModernPanel panel : panels) {
                if (panel.keyPressed(keyCode, scanCode, modifiers)) return true;
            }

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                close();
                return true;
            }
        } catch (Exception e) { System.err.println("Error in keyPressed: " + e.getMessage()); }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        try {
            if (windowRepository != null && windowRepository.keyReleased(keyCode, scanCode, modifiers)) return true;
            if (searchField != null && searchField.keyReleased(keyCode, scanCode, modifiers)) return true;

            for (ModernPanel panel : panels) {
                if (panel.keyReleased(keyCode, scanCode, modifiers)) return true;
            }
        } catch (Exception e) { System.err.println("Error in keyReleased: " + e.getMessage()); }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        try {
            if (searchField != null && searchField.charTyped(chr, modifiers)) return true;
            for (ModernPanel panel : panels) {
                if (panel.charTyped(chr, modifiers)) return true;
            }
        } catch (Exception e) { System.err.println("Error in charTyped: " + e.getMessage()); }

        return super.charTyped(chr, modifiers);
    }

    private void updateScale() {
        float totalWidth = panels.size() * (panelWidth + panelSpacing);
        float screenWidth = mc.getWindow().getScaledWidth();

        if (totalWidth >= screenWidth * 0.9f) {
            scale = (screenWidth * 0.9f) / totalWidth;
            scale = MathHelper.clamp(scale, 0.5f, 1.0f);
        } else scale = 1.0f;
    }

    private double adjustMouseX(double mouseX) {
        return scale == 1.0f ? mouseX : (mouseX - centerX.get()) / scale + centerX.get();
    }

    private double adjustMouseY(double mouseY) {
        return scale == 1.0f ? mouseY : (mouseY - centerY.get()) / scale + centerY.get();
    }

    public boolean isSearching() {
        return searchField != null && !searchField.isEmpty();
    }

    public String getSearchText() {
        return searchField != null ? searchField.getText() : "";
    }

    public boolean searchCheck(String text) {
        return isSearching() && !text.toLowerCase().contains(getSearchText().toLowerCase());
    }

    @Override
    public void removed() {
        try { Pasxalka.getInstance().getEventBus().unregister(this); }
        catch (Exception e) { System.err.println("Error unregistering from event bus: " + e.getMessage()); }
        super.removed();
    }
}
