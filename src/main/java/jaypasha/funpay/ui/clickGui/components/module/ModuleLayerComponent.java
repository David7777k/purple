package jaypasha.funpay.ui.clickGui.components.module;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
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
import lombok.experimental.NonFinal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleLayerComponent extends Component {

    ModuleLayer moduleLayer;
    List<SettingComponent> components = new ArrayList<>();

    Animation settingsAnimation = new DecelerateAnimation()
            .setMs(250)
            .setValue(0f);

    @NonFinal
    boolean settingsExpanded = false;

    private float hoverProgress = 0f;
    private float cachedTotalHeight = -1f;
    private long lastHeightCalcTime = 0L;

    public ModuleLayerComponent(ModuleLayer moduleLayer) {
        this.moduleLayer = moduleLayer;
        initializeComponents();
        settingsAnimation.setDirection(Direction.BACKWARDS);
        settingsAnimation.reset();
    }

    private void initializeComponents() {
        this.components.clear();
        this.components.addAll(Helper.settingComponents(moduleLayer));

        for (SettingComponent component : components) {
            try {
                component.init();
            } catch (Exception e) {
                System.err.println("Failed to initialize setting component: " + e.getMessage());
            }
        }
    }

    @Override
    public Component render(DrawContext context, int mouseX, int mouseY, float delta) {
        float moduleAnimation = moduleLayer.getAnimation().getOutput().floatValue();

        boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), 20f);
        hoverProgress = MathHelper.lerp(delta * 10f, hoverProgress, hovered ? 1f : 0f);

        // Фон заголовка
        Api.border()
                .size(new SizeState(getWidth(), 20f))
                .radius(new QuadRadiusState(5f))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (20 + 35 * hoverProgress))))
                .thickness(-1f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .size(new SizeState(getWidth(), 20f))
                .radius(new QuadRadiusState(5f))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, (int) (10 + 25 * moduleAnimation + 30 * hoverProgress))))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Текст названия или бинда
        String text = moduleLayer.getBinding()
                ? moduleLayer.getKey() != -1
                ? "[" + KeyBoardUtil.translate(moduleLayer.getKey()) + "]"
                : "Press any key"
                : moduleLayer.getModuleName().getString();

        Api.text()
                .size(8.5f)
                .font(Api.inter())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (50 + 50 * moduleAnimation + 100 * hoverProgress)))
                .text(text)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + 5 + (5 * moduleAnimation),
                        getY() + (20f - Api.inter().getHeight(text, 8.5f)) / 2);

        // Иконка "B" вкл/выкл модуля
        Api.text()
                .text("B")
                .size(8)
                .font(Api.icons())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, (int) (100 * moduleAnimation + 100 * hoverProgress)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + getWidth() - 18 - (10 * moduleAnimation),
                        getY() + (20f - 8) / 2);

        // Стрелка раскрытия настроек
        if (!components.isEmpty()) {
            String arrowIcon = settingsExpanded ? "v" : ">";
            boolean arrowHovered = Math.isHover(mouseX, mouseY, getX() + getWidth() - 30, getY(), 15, 20);
            int arrowAlpha = (int) (70 + 30 * moduleAnimation + 60 * (arrowHovered ? 1 : 0));

            Api.text()
                    .text(arrowIcon)
                    .size(8)
                    .font(Api.inter())
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, arrowAlpha))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + getWidth() - 25,
                            getY() + (20f - 8) / 2);
        }

        // Рендер настроек
        if (settingsExpanded && !components.isEmpty()) {
            renderSettings(context, mouseX, mouseY, delta);
        }

        return null;
    }

    private void renderSettings(DrawContext context, int mouseX, int mouseY, float delta) {
        float currentY = getY() + 20f;
        float totalHeight = calculateSettingsHeight();

        // Рендерим фон для области настроек
        if (totalHeight > 0) {
            Api.rectangle()
                    .size(new SizeState(getWidth(), totalHeight))
                    .radius(new QuadRadiusState(0f, 0f, 5f, 5f))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 15)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX(), currentY);
        }

        // Рендерим каждый компонент настроек
        float offsetY = 2f;
        for (SettingComponent component : components) {
            if (!component.getSettingLayer().getVisible().get()) continue;

            try {
                component.init();
                float height = Math.max(15f, component.getHeight());

                component.position(getX() + 5f, currentY + offsetY)
                        .size(getWidth() - 10f, height);

                component.render(context, mouseX, mouseY, delta);

                offsetY += height + 3f;

            } catch (Exception e) {
                System.err.println("Error rendering setting component: " +
                        component.getSettingLayer().getName().getString() + " - " + e.getMessage());
                offsetY += 18f;
            }
        }
    }

    private float calculateSettingsHeight() {
        float totalHeight = 0f;
        for (SettingComponent component : components) {
            if (!component.getSettingLayer().getVisible().get()) continue;
            try {
                component.init();
                totalHeight += Math.max(15f, component.getHeight()) + 3f;
            } catch (Exception e) {
                totalHeight += 18f;
            }
        }
        return Math.max(totalHeight - 3f, 0f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (moduleLayer.getBinding()) {
            moduleLayer.setKey(button);
            moduleLayer.setBinding(false);
            return true;
        }

        // Обработка кликов по настройкам
        if (settingsExpanded && !components.isEmpty()) {
            float settingsStartY = getY() + 20f + 2f;
            float offsetY = 0f;

            for (SettingComponent component : components) {
                if (!component.getSettingLayer().getVisible().get()) continue;

                try {
                    component.init();
                    float height = Math.max(15f, component.getHeight());

                    component.position(getX() + 5f, settingsStartY + offsetY);

                    if (component.mouseClicked(mouseX, mouseY, button)) return true;

                    offsetY += height + 3f;
                } catch (Exception e) {
                    offsetY += 18f;
                }
            }
        }

        // Обработка кликов по заголовку модуля
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), 20f)) {
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

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (settingsExpanded) {
            components.forEach(c -> c.mouseReleased(mouseX, mouseY, button));
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (moduleLayer.getBinding()) {
            moduleLayer.setKey((keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_ESCAPE) ? -1 : keyCode);
            moduleLayer.setBinding(false);
            return true;
        }

        if (settingsExpanded) {
            components.forEach(c -> c.keyPressed(keyCode, scanCode, modifiers));
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (settingsExpanded) {
            components.forEach(c -> c.keyReleased(keyCode, scanCode, modifiers));
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void toggleSettings() {
        settingsExpanded = !settingsExpanded;
        settingsAnimation.setDirection(settingsExpanded ? Direction.FORWARDS : Direction.BACKWARDS);
        cachedTotalHeight = -1f;
    }

    public void closeSettings() {
        if (settingsExpanded) {
            settingsExpanded = false;
            settingsAnimation.setDirection(Direction.BACKWARDS);
            cachedTotalHeight = -1f;
        }
    }

    public void openSettings() {
        if (!settingsExpanded && !components.isEmpty()) {
            settingsExpanded = true;
            settingsAnimation.setDirection(Direction.FORWARDS);
            cachedTotalHeight = -1f;
        }
    }

    public float getTotalHeight() {
        long now = System.currentTimeMillis();
        if (now - lastHeightCalcTime > 100 || cachedTotalHeight < 0f) {
            cachedTotalHeight = calculateTotalHeight();
            lastHeightCalcTime = now;
        }
        return cachedTotalHeight;
    }

    private float calculateTotalHeight() {
        float baseHeight = 20f;
        if (!settingsExpanded || components.isEmpty()) return baseHeight;

        float settingsHeight = calculateSettingsHeight();
        return baseHeight + settingsHeight;
    }
}