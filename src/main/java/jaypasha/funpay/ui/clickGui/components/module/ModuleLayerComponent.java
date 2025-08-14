package jaypasha.funpay.ui.clickGui.components.module;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.ModuleEvent;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.keyboard.KeyBoardUtil;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleLayerComponent extends Component {

    // Layout
    static final float HEADER_HEIGHT = 20f;
    static final float RADIUS = 5f;
    static final float SIDE_PADDING = 5f;
    static final float CONTENT_PADDING_Y = 2f;
    static final float ITEM_SPACING_Y = 3f;
    static final float MIN_ITEM_HEIGHT = 15f;
    static final float RIGHT_PADDING = 6f;
    static final float ICON_SIZE = 8f;
    static final float ARROW_GLYPH_WIDTH = 8f;
    static final float ICON_GAP = 12f;

    // Debug
    // private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    ModuleLayer moduleLayer;
    List<SettingComponent> components = new ArrayList<>();

    // animation
    float settingsProgress = 0f;
    float settingsTarget = 0f;
    boolean settingsExpanded = false;

    // scrolling
    float scrollOffset = 0f;
    float scrollTarget = 0f;
    float scrollSpeedFactor = 12f;
    float wheelScrollAmount = 12f;

    // cache heights
    float cachedSettingsHeight = -1f;
    long lastHeightCalc = 0L;

    // debounce to avoid opening-click leaking into child components
    long lastToggleTime = 0L;
    static final long TOGGLE_DEBOUNCE_MS = 150L;

    public ModuleLayerComponent(ModuleLayer moduleLayer) {
        this.moduleLayer = moduleLayer;
        this.components.addAll(Helper.settingComponents(moduleLayer));
        for (SettingComponent sc : components) {
            try { sc.init(); } catch (Exception ignored) {}
        }
        markHeightsDirty();
    }

    private void markHeightsDirty() {
        cachedSettingsHeight = -1f;
        lastHeightCalc = 0L;
        clampScrollTargets();
    }

    @Override
    public ModuleLayerComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        float animSpeed = 12f;
        settingsProgress = MathHelper.clamp(MathHelper.lerp(delta * animSpeed, settingsProgress, settingsTarget), 0f, 1f);

        // smooth scroll interpolation
        scrollOffset = MathHelper.clamp(MathHelper.lerp(delta * scrollSpeedFactor, scrollOffset, scrollTarget), 0f, getMaxScroll());

        float animForBg = moduleLayer.getAnimation() != null ? moduleLayer.getAnimation().getOutput().floatValue() : 1f;

        // header bg
        Api.border()
                .size(new SizeState(getWidth(), HEADER_HEIGHT))
                .radius(new QuadRadiusState(RADIUS))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 20)))
                .thickness(-1f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .size(new SizeState(getWidth(), HEADER_HEIGHT))
                .radius(new QuadRadiusState(RADIUS))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, (int) (10 + 25 * animForBg))))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // title / binding
        String text = moduleLayer.getBinding()
                ? (moduleLayer.getKey() != -1 ? "[" + KeyBoardUtil.translate(moduleLayer.getKey()) + "]" : "Press any key")
                : moduleLayer.getModuleName().getString();

        Api.text()
                .size(8.5f)
                .font(Api.inter())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (50 + 50 * animForBg)))
                .text(text)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + SIDE_PADDING + (5f * animForBg),
                        getY() + (HEADER_HEIGHT - Api.inter().getHeight(text, 8.5f)) / 2f);

        // icons
        float arrowX = getX() + getWidth() - RIGHT_PADDING - ARROW_GLYPH_WIDTH;
        float iconBX = arrowX - ICON_GAP;

        Api.text()
                .text("B")
                .size(ICON_SIZE)
                .font(Api.icons())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * animForBg)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), iconBX, getY() + (HEADER_HEIGHT - ICON_SIZE) / 2f);

        boolean hasSettings = !components.isEmpty();
        if (hasSettings) {
            Api.text()
                    .text(">")
                    .size(ICON_SIZE)
                    .font(Api.inter())
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * (1f - settingsProgress))))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), arrowX, getY() + (HEADER_HEIGHT - ICON_SIZE) / 2f);

            Api.text()
                    .text("v")
                    .size(ICON_SIZE)
                    .font(Api.inter())
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * settingsProgress)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), arrowX, getY() + (HEADER_HEIGHT - ICON_SIZE) / 2f);
        }

        // settings area
        float fullSettingsH = getSettingsHeightCached();
        float animatedH = fullSettingsH * settingsProgress;
        float settingsY = getY() + HEADER_HEIGHT;

        if (animatedH > 0.5f) {
            Api.rectangle()
                    .size(new SizeState(getWidth(), animatedH))
                    .radius(new QuadRadiusState(0f, 0f, RADIUS, RADIUS))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 15)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), settingsY);

            Api.rectangle()
                    .size(new SizeState(getWidth(), 1f))
                    .radius(new QuadRadiusState(0f))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 12)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), settingsY);
        }

        // render components (use passed mouseX/mouseY consistently!)
        float offsetY = CONTENT_PADDING_Y;
        float left = getX() + SIDE_PADDING;
        float width = getWidth() - SIDE_PADDING * 2f;

        for (SettingComponent comp : components) {
            if (!comp.getSettingLayer().getVisible().get()) continue;

            float compH = java.lang.Math.max(MIN_ITEM_HEIGHT, comp.getHeight());
            float compTop = settingsY + offsetY - scrollOffset;
            float compBottom = compTop + compH;

            if (compBottom < settingsY || compTop > settingsY + animatedH) {
                offsetY += compH + ITEM_SPACING_Y;
                continue;
            }

            comp.position(left, compTop).size(width, compH);
            try {
                comp.render(context, mouseX, mouseY, delta);
            } catch (Exception e) {
                System.err.println("Error rendering setting component: " + e.getMessage());
            }

            offsetY += compH + ITEM_SPACING_Y;
        }

        return this;
    }

    private float getSettingsHeightCached() {
        long now = System.currentTimeMillis();
        if (now - lastHeightCalc > 100 || cachedSettingsHeight < 0f) {
            float total = 0f;
            for (SettingComponent c : components) {
                if (!c.getSettingLayer().getVisible().get()) continue;
                total += java.lang.Math.max(MIN_ITEM_HEIGHT, c.getHeight()) + ITEM_SPACING_Y;
            }
            cachedSettingsHeight = total > 0f ? (total - ITEM_SPACING_Y + CONTENT_PADDING_Y * 2f) : 0f;
            lastHeightCalc = now;
            clampScrollTargets();
        }
        return cachedSettingsHeight;
    }

    private float getMaxScroll() {
        float full = getSettingsHeightCached();
        float visible = java.lang.Math.max(0f, getHeight() - HEADER_HEIGHT);
        return java.lang.Math.max(0f, full - visible + CONTENT_PADDING_Y);
    }

    private void clampScrollTargets() {
        float max = getMaxScroll();
        scrollTarget = MathHelper.clamp(scrollTarget, 0f, max);
        scrollOffset = MathHelper.clamp(scrollOffset, 0f, max);
    }

    // ---- input handling ----

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // binding mode
        if (moduleLayer.getBinding()) {
            moduleLayer.setKey(button);
            moduleLayer.setBinding(false);
            return true;
        }

        // arrow area: first priority
        float arrowX = getX() + getWidth() - RIGHT_PADDING - ARROW_GLYPH_WIDTH;
        boolean arrowHovered = Math.isHover(mouseX, mouseY, arrowX - 2f, getY(), 15f, HEADER_HEIGHT);
        if (arrowHovered && (button == 0 || button == 1) && !components.isEmpty()) {
            toggleSettings();
            return true;
        }

        // header clicks
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), HEADER_HEIGHT)) {
            if (button == 0) {
                EventManager.call(new ModuleEvent.ToggleEvent(moduleLayer));
                return true;
            }
            if (button == 1 && !components.isEmpty()) {
                toggleSettings();
                return true;
            }
            if (button == 2) {
                moduleLayer.setBinding(!moduleLayer.getBinding());
                return true;
            }
        }

        // route clicks to visible settings — debounce
        if (settingsProgress > 0f && !components.isEmpty()) {
            long now = System.currentTimeMillis();
            if (now - lastToggleTime < TOGGLE_DEBOUNCE_MS) return true;

            float startY = getY() + HEADER_HEIGHT;
            float animatedH = getSettingsHeightCached() * settingsProgress;

            if (Math.isHover(mouseX, mouseY, getX(), startY, getWidth(), animatedH)) {
                float offsetY = CONTENT_PADDING_Y;
                float left = getX() + SIDE_PADDING;
                float width = getWidth() - SIDE_PADDING * 2f;

                for (SettingComponent comp : components) {
                    if (!comp.getSettingLayer().getVisible().get()) continue;
                    float compH = java.lang.Math.max(MIN_ITEM_HEIGHT, comp.getHeight());
                    float compTop = startY + offsetY - scrollOffset;

                    if (compTop + compH < startY || compTop > startY + animatedH) {
                        offsetY += compH + ITEM_SPACING_Y;
                        continue;
                    }

                    comp.position(left, compTop).size(width, compH);
                    try {
                        if (comp.mouseClicked(mouseX, mouseY, button)) return true;
                    } catch (Exception ignored) {}
                    offsetY += compH + ITEM_SPACING_Y;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (settingsProgress > 0f) {
            components.forEach(c -> {
                try { c.mouseReleased(mouseX, mouseY, button); } catch (Exception ignored) {}
            });
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // совместимая сигнатура + работа только в области настроек
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (settingsProgress <= 0f) return false;

        float startY = getY() + HEADER_HEIGHT;
        float animatedH = getSettingsHeightCached() * settingsProgress;
        boolean insideSettings = Math.isHover(mouseX, mouseY, getX(), startY, getWidth(), animatedH);
        if (!insideSettings) return false;

        float delta = (float) (-verticalAmount * wheelScrollAmount);
        scrollTarget = MathHelper.clamp(scrollTarget + delta, 0f, getMaxScroll());
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (moduleLayer.getBinding()) {
            moduleLayer.setKey((keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_ESCAPE) ? -1 : keyCode);
            moduleLayer.setBinding(false);
            return true;
        }

        if (settingsProgress > 0f) {
            components.forEach(c -> {
                try { c.keyPressed(keyCode, scanCode, modifiers); } catch (Exception ignored) {}
            });
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (settingsProgress > 0f) {
            components.forEach(c -> {
                try { c.keyReleased(keyCode, scanCode, modifiers); } catch (Exception ignored) {}
            });
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void toggleSettings() {
        if (components.isEmpty()) return;
        settingsExpanded = !settingsExpanded;
        lastToggleTime = System.currentTimeMillis();

        if (settingsExpanded) {
            settingsTarget = 1f;
            if (settingsProgress < 0.05f) settingsProgress = 0f;
        } else {
            settingsTarget = 0f;
        }
        clampScrollTargets();
        markHeightsDirty();
    }

    public void openSettings() {
        if (components.isEmpty()) return;
        settingsExpanded = true;
        settingsTarget = 1f;
        settingsProgress = 0f;
        lastToggleTime = System.currentTimeMillis();
        clampScrollTargets();
        markHeightsDirty();
    }

    public void closeSettings() {
        if (!settingsExpanded) return;
        settingsExpanded = false;
        settingsTarget = 0f;
        markHeightsDirty();
    }

    public float getTotalHeight() {
        return HEADER_HEIGHT + getSettingsHeightCached() * settingsProgress;
    }
}
