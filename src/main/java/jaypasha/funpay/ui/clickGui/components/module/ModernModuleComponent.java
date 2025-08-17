package jaypasha.funpay.ui.clickGui.components.module;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.ModuleEvent;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.keyboard.KeyBoardUtil;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.Scissors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModernModuleComponent extends Component {

    static final float MODULE_HEIGHT = 18f;
    static final float CORNER_RADIUS = 4f;
    static final float SETTINGS_PADDING = 4f;
    static final float SETTING_SPACING = 2f;

    static final int ENABLED_BG_COLOR = ColorUtility.rgba(45, 46, 53, 120);
    static final int DISABLED_BG_COLOR = ColorUtility.rgba(21, 21, 21, 80);
    static final int ENABLED_ACCENT = 0xFF6A5ACD;
    static final int DISABLED_ACCENT = ColorUtility.rgba(153, 153, 153, 100);
    static final int TEXT_COLOR_ENABLED = ColorUtility.rgba(255, 255, 255, 255);
    static final int TEXT_COLOR_DISABLED = ColorUtility.rgba(153, 153, 153, 255);
    static final int HOVER_OVERLAY = ColorUtility.rgba(255, 255, 255, 15);

    ModuleLayer moduleLayer;
    List<SettingComponent> settingComponents = new ArrayList<>();

    Animation enableAnimation = new DecelerateAnimation().setMs(200).setValue(0f);
    Animation hoverAnimation = new DecelerateAnimation().setMs(150).setValue(0f);
    Animation expandAnimation = new DecelerateAnimation().setMs(200).setValue(0f);
    Animation bindAnimation = new DecelerateAnimation().setMs(150).setValue(0f);

    boolean settingsExpanded = false;
    boolean bindMode = false;
    boolean hovered = false;
    long lastToggleTime = 0L;
    static final long TOGGLE_DEBOUNCE_MS = 100L;

    private static volatile boolean BUFFER_ERROR_LOGGED = false;

    public ModernModuleComponent(ModuleLayer moduleLayer) {
        this.moduleLayer = moduleLayer;
        this.settingComponents.addAll(Helper.settingComponents(moduleLayer));

        enableAnimation.setValue(moduleLayer.isEnabled() ? 1f : 0f);
        expandAnimation.setValue(settingsExpanded ? 1f : 0f);
        bindAnimation.setValue(0f);

        for (SettingComponent component : settingComponents) {
            try { if (component != null) component.init(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    public ModernModuleComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        try {
            updateAnimations(mouseX, mouseY);

            renderBackground(context);
            renderContent(context);
            renderExpandIndicator(context);
            renderSettings(context, mouseX, mouseY, delta);

        } catch (Exception e) {
            if (!BUFFER_ERROR_LOGGED) {
                BUFFER_ERROR_LOGGED = true;
                e.printStackTrace();
            }
        }
        return this;
    }

    private void updateAnimations(double mouseX, double mouseY) {
        // Не фиксируем value каждый кадр — оставляем только направление
        enableAnimation.setDirection(moduleLayer.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

        boolean isHovered = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), MODULE_HEIGHT);
        if (isHovered != hovered) {
            hovered = isHovered;
            hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        }

        expandAnimation.setDirection(settingsExpanded ? Direction.FORWARDS : Direction.BACKWARDS);
        bindAnimation.setDirection(bindMode ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    private void renderBackground(DrawContext context) {
        float enableValue = enableAnimation.getOutput().floatValue();
        float hoverValue = hoverAnimation.getOutput().floatValue();

        int bgColor = ColorUtility.interpolate(DISABLED_BG_COLOR, ENABLED_BG_COLOR, enableValue);

        Api.rectangle()
                .size(new SizeState(getWidth(), MODULE_HEIGHT))
                .radius(new QuadRadiusState(CORNER_RADIUS))
                .color(new QuadColorState(bgColor))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        if (hoverValue > 0.01f) {
            Api.rectangle()
                    .size(new SizeState(getWidth(), MODULE_HEIGHT))
                    .radius(new QuadRadiusState(CORNER_RADIUS))
                    .color(new QuadColorState(ColorUtility.applyOpacity(HOVER_OVERLAY, 255f * hoverValue)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());
        }

        if (enableValue > 0.01f) {
            Api.rectangle()
                    .size(new SizeState(2f, MODULE_HEIGHT))
                    .radius(new QuadRadiusState(1f, 0f, 0f, 1f))
                    .color(new QuadColorState(ColorUtility.applyOpacity(ENABLED_ACCENT, 255f * enableValue)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());
        }
    }

    private void renderContent(DrawContext context) {
        float enableValue = enableAnimation.getOutput().floatValue();
        float bindValue = bindAnimation.getOutput().floatValue();

        String displayText;
        int textColor;

        if (bindValue > 0.5f) {
            displayText = moduleLayer.getKey() != -1 ? "[" + KeyBoardUtil.translate(moduleLayer.getKey()) + "]" : "Press key...";
            textColor = ColorUtility.interpolate(TEXT_COLOR_DISABLED, ENABLED_ACCENT, bindValue);
        } else {
            displayText = moduleLayer.getModuleName().getString();
            textColor = ColorUtility.interpolate(TEXT_COLOR_DISABLED, TEXT_COLOR_ENABLED, enableValue);
        }

        if (moduleLayer.isEnabled()) {
            trySafeTextRender(context,
                    "✔",
                    6f,
                    ENABLED_ACCENT,
                    getX() + 6f,
                    (int) (getY() + MODULE_HEIGHT / 2f - Api.inter().getHeight("✔", 6f) / 2f));
        }

        if (displayText != null && !displayText.isEmpty()) {
            float x = getX() + (bindValue > 0.5f ? getWidth() / 2f - Api.inter().getWidth(displayText, 6.5f) / 2f : 12f);
            float y = getY() + MODULE_HEIGHT / 2f - Api.inter().getHeight(displayText, 6.5f) / 2f + 1f;
            trySafeTextRender(context, displayText, 6.5f, textColor, x, (int) y);
        }
    }

    private void renderExpandIndicator(DrawContext context) {
        if (settingComponents.isEmpty()) return;

        float enableValue = enableAnimation.getOutput().floatValue();
        float expandValue = expandAnimation.getOutput().floatValue();

        String arrow = settingsExpanded ? "v" : ">";
        int arrowColor = ColorUtility.interpolate(DISABLED_ACCENT, ENABLED_ACCENT, enableValue);
        arrowColor = ColorUtility.applyOpacity(arrowColor, 255f * (0.7f + 0.3f * expandValue));

        try {
            if (arrow != null && !arrow.isEmpty()) {
                Api.text()
                        .size(6f)
                        .font(Api.inter())
                        .text(arrow)
                        .color(arrowColor)
                        .build()
                        .render(context.getMatrices().peek().getPositionMatrix(),
                                getX() + getWidth() - 12f,
                                getY() + MODULE_HEIGHT / 2f - 3f);
            }
        } catch (Exception ignored) {}
    }

    private void renderSettings(DrawContext context, int mouseX, int mouseY, float delta) {
        if (settingComponents.isEmpty()) return;

        float expandValue = settingsExpanded ? 1f : expandAnimation.getOutput().floatValue();
        if (expandValue < 0.01f) return;

        float settingsHeight = getSettingsHeight() * expandValue;
        float settingsY = getY() + MODULE_HEIGHT;

        Api.rectangle()
                .size(new SizeState(getWidth(), settingsHeight))
                .radius(new QuadRadiusState(0f, 0f, CORNER_RADIUS, CORNER_RADIUS))
                .color(new QuadColorState(ColorUtility.applyOpacity(DISABLED_BG_COLOR, 255f * (expandValue * 0.8f))))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), settingsY);

        Scissors.push(getX(), settingsY, getWidth(),
                Math.max(settingsHeight, mc.getWindow().getScaledHeight() - settingsY));
        try {
            float yOffset = SETTINGS_PADDING;
            for (SettingComponent component : settingComponents) {
                if (!component.getSettingLayer().getVisible().get()) continue;

                float componentY = settingsY + yOffset;
                component.position(getX() + SETTINGS_PADDING, componentY)
                        .size(getWidth() - SETTINGS_PADDING * 2f, component.getHeight());

                if (component.getHeight() > 0) {
                    try { component.render(context, mouseX, mouseY, delta); }
                    catch (Exception e) {
                        if (!BUFFER_ERROR_LOGGED) {
                            BUFFER_ERROR_LOGGED = true;
                            System.err.println("Error rendering a setting component: " + e.getMessage());
                        }
                    }
                }
                yOffset += component.getHeight() + SETTING_SPACING;
            }
        } finally {
            Scissors.pop();
        }
    }

    private float getSettingsHeight() {
        float totalHeight = SETTINGS_PADDING * 2f;
        for (SettingComponent component : settingComponents) {
            if (component.getSettingLayer().getVisible().get()) {
                totalHeight += component.getHeight() + SETTING_SPACING;
            }
        }
        return totalHeight;
    }

    public float getTotalHeight() {
        float baseHeight = MODULE_HEIGHT;
        if (settingsExpanded) {
            baseHeight += getSettingsHeight();
        } else {
            baseHeight += getSettingsHeight() * expandAnimation.getOutput().floatValue();
        }
        return baseHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        long currentTime = System.currentTimeMillis();

        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), MODULE_HEIGHT)) {
            if (button == 0 && currentTime - lastToggleTime >= TOGGLE_DEBOUNCE_MS) {
                lastToggleTime = currentTime;
                EventManager.call(new ModuleEvent.ToggleEvent(moduleLayer));
                return true;
            } else if (button == 1 && !settingComponents.isEmpty()) {
                settingsExpanded = !settingsExpanded;
                try {
                    expandAnimation.setDirection(settingsExpanded ? Direction.FORWARDS : Direction.BACKWARDS);
                    expandAnimation.setValue(settingsExpanded ? 1f : 0f);
                } catch (Exception ignored) {}
                return true;
            } else if (button == 2) {
                bindMode = !bindMode;
                bindAnimation.setDirection(bindMode ? Direction.FORWARDS : Direction.BACKWARDS);
                bindAnimation.setValue(bindMode ? 1f : 0f);
                return true;
            }
        }

        if (settingsExpanded && expandAnimation.getOutput().floatValue() > 0.5f) {
            for (SettingComponent component : settingComponents) {
                if (!component.getSettingLayer().getVisible().get()) continue;
                if (component.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (settingsExpanded) {
            for (SettingComponent component : settingComponents) {
                component.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindMode) {
            if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                moduleLayer.setKey(-1);
            } else {
                moduleLayer.setKey(keyCode);
            }
            bindMode = false;
            bindAnimation.setValue(0f);
            bindAnimation.setDirection(Direction.BACKWARDS);
            return true;
        }

        if (settingsExpanded) {
            for (SettingComponent component : settingComponents) {
                if (component.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (settingsExpanded) {
            for (SettingComponent component : settingComponents) {
                component.keyReleased(keyCode, scanCode, modifiers);
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (settingsExpanded) {
            for (SettingComponent component : settingComponents) {
                if (component.charTyped(chr, modifiers)) return true;
            }
        }
        return false;
    }

    public void closeSettings() {
        settingsExpanded = false;
        expandAnimation.setValue(0f);
        expandAnimation.setDirection(Direction.BACKWARDS);
    }

    private void trySafeTextRender(DrawContext context, String text, float size, int color, float x, float y) {
        if (text == null || text.isEmpty() || Api.inter() == null) return;
        try {
            Api.text()
                    .size(size)
                    .font(Api.inter())
                    .text(text)
                    .color(color)
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), x, y);
        } catch (IllegalStateException ignored) {}
    }
}
