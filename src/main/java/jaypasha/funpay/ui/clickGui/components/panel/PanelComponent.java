package jaypasha.funpay.ui.clickGui.components.panel;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.BackgroundComponent;
import jaypasha.funpay.ui.clickGui.components.module.ModuleLayerComponent;
import jaypasha.funpay.ui.clickGui.components.search.SearchComponent;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.utility.Scissors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PanelComponent extends Component {

    @NonFinal
    List<ModuleLayerComponent> componentsList = new ArrayList<>();

    BackgroundComponent backgroundComponent;

    @NonFinal
    float scroll = 0f, animationScroll = 0f;

    Category category;

    public PanelComponent(Category category) {
        this.category = category;
        backgroundComponent = new BackgroundComponent(category);

        init();
    }

    @Override
    public void init() {
        // Получаем все модули для категории (Helper.moduleLayers(category) — метод с одним параметром)
        List<ModuleLayerComponent> all = Helper.moduleLayers(category);

        // Берём поисковую строку один раз (оптимизация)
        String search = SearchComponent.getSearchSource().get().getText().toString().toLowerCase();

        // Фильтруем по началу названия
        componentsList = all.stream()
                .filter(e -> e.getModuleLayer().getModuleName().getString().toLowerCase().startsWith(search))
                .toList();
    }

    @Override
    public PanelComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        animationScroll = MathHelper.lerp(.02f, animationScroll, scroll);

        backgroundComponent.position(getX(), getY()).size(getWidth(), getHeight()).render(context, mouseX, mouseY, delta);

        // Scissors для обрезки содержимого панели
        Scissors.push(getX() + 2.5f, getY() + 32, getWidth() - 5, getHeight() - 32 - 14.5f);

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        AtomicReference<Float> totalContentHeight = new AtomicReference<>(0f);

        for (ModuleLayerComponent e : componentsList) {
            // Инициализируем компоненты настроек безопасно
            e.getComponents().forEach(component -> {
                try {
                    component.init();
                } catch (Exception ex) {
                    System.err.println("Failed to initialize setting component: " + ex.getMessage());
                }
            });

            // Получаем текущую высоту модуля (учёт анимации открытия настроек)
            float moduleHeight = Math.max(20f, e.getTotalHeight());

            // Позиционируем модуль; ширина фиксирована (240/2)
            e.position(getX() + 2.5f, getY() + 32 + offset.get() + animationScroll)
                    .size(240f / 2, moduleHeight);

            // Рендерим модуль
            try {
                e.render(context, mouseX, mouseY, delta);
            } catch (Exception ex) {
                System.err.println("Failed to render module: " + e.getModuleLayer().getModuleName().getString() + " - " + ex.getMessage());
            }

            // Обновляем оффсет и суммарную высоту
            float moduleSpacing = moduleHeight + 2.5f;
            offset.set(offset.get() + moduleSpacing);
            totalContentHeight.set(totalContentHeight.get() + moduleSpacing);
        }

        Scissors.pop();

        // Обновляем границы скролла на основе реального размера контента
        float maxScroll = min(getHeight() - totalContentHeight.get() - 45.5f, 0);
        scroll = clamp(scroll, maxScroll, 0);

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            // Обрабатываем клики по модулям
            for (ModuleLayerComponent component : componentsList) {
                if (component.mouseClicked(mouseX, mouseY, button)) {
                    return true; // Останавливаем обработку если клик был обработан
                }
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
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight()))
            scroll += verticalAmount * 10f;

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

    /**
     * Закрывает все открытые настройки модулей в этой панели
     */
    public void closeAllSettings() {
        componentsList.forEach(ModuleLayerComponent::closeSettings);
    }

    /**
     * Возвращает общую высоту всех модулей (для расчета скролла)
     */
    public float getTotalContentHeight() {
        return componentsList.stream()
                .map(ModuleLayerComponent::getTotalHeight)
                .reduce(0f, Float::sum) + (componentsList.size() - 1) * 2.5f;
    }
}
