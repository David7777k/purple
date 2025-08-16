package jaypasha.funpay.ui.clickGui.components.panel;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.BackgroundComponent;
import jaypasha.funpay.ui.clickGui.components.module.ModuleLayerComponent;
import jaypasha.funpay.ui.clickGui.components.search.SearchComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.Scissors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PanelComponent extends Component {

    private List<ModuleLayerComponent> componentsList = new ArrayList<>();
    private final BackgroundComponent backgroundComponent;
    private float scroll = 0f;
    private float animationScroll = 0f;

    private final Category category;

    // визуальные константы, заточенные под «узкий/аккуратный» вид
    private static final float PADDING_X = 2.5f;
    private static final float HEADER_H = 32f;
    private static final float BOTTOM_PAD = 14.5f;
    private static final float ITEM_WIDTH = 240f / 2f;
    private static final float ITEM_SPACING = 2.5f;
    private static final float MIN_ITEM_H = 20f;

    public PanelComponent(Category category) {
        this.category = category;
        this.backgroundComponent = new BackgroundComponent(category);
        init();
    }

    @Override
    public void init() {
        // список модулей с учётом поиска
        final String search = SearchComponent.getSearchSource().get().getText().toString().toLowerCase();
        List<ModuleLayerComponent> all = Helper.moduleLayers(category);

        componentsList = all.stream()
                .filter(e -> {
                    String name = e.getModuleLayer().getModuleName().getString().toLowerCase();
                    return search.isEmpty() || name.contains(search);
                })
                .toList();

        componentsList.forEach(m -> {
            try { m.init(); } catch (Exception ignored) {}
        });

        // сброс скролла при смене списка
        scroll = animationScroll = 0f;
    }

    @Override
    public PanelComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        // фон/каркас панели
        backgroundComponent.position(getX(), getY()).size(getWidth(), getHeight()).render(context, mouseX, mouseY, delta);

        // сглаживание скролла, как в исходнике
        animationScroll = MathHelper.lerp(0.15f, animationScroll, scroll);

        // контентная область
        float contentX = getX() + PADDING_X;
        float contentY = getY() + HEADER_H;
        float contentW = getWidth() - PADDING_X * 2f - 2f; // -2f под правую полосу скролла
        float contentH = getHeight() - HEADER_H - BOTTOM_PAD;

        // клиппинг
        Scissors.push(contentX, contentY, contentW, contentH);

        // раскладка карточек/строк модулей «узкой колонкой» (высота модуля считает сам компонент)
        float y = contentY - animationScroll;
        for (ModuleLayerComponent m : componentsList) {
            float itemH = max(MIN_ITEM_H, m.getTotalHeight());
            m.position(contentX, y).size(contentW, itemH).render(context, mouseX, mouseY, delta);
            y += itemH + ITEM_SPACING;
        }

        Scissors.pop();

        // скроллбар
        float totalContentH = getTotalContentHeight();
        float visibleH = contentH;
        if (totalContentH > visibleH + 1f) {
            float trackH = visibleH;
            float thumbH = max(20f, (visibleH / totalContentH) * trackH);
            float maxScroll = totalContentH - visibleH;
            float scroll01 = max(0f, min(1f, animationScroll / maxScroll));
            float thumbY = contentY + (trackH - thumbH) * scroll01;

            // трек
            Api.rectangle()
                    .size(new SizeState(2f, visibleH))
                    .radius(new QuadRadiusState(1f))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 10)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - 4f, contentY);

            // ползунок
            Api.rectangle()
                    .size(new SizeState(2f, thumbH))
                    .radius(new QuadRadiusState(1f))
                    .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 45)))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() - 4f, thumbY);
        }

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            for (ModuleLayerComponent component : componentsList) {
                if (component.mouseClicked(mouseX, mouseY, button)) return true;
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
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            // отзывчивый скролл
            scroll += (float) (verticalAmount * 18f);
            // кламп по содержимому
            float maxScroll = Math.max(0f, getTotalContentHeight() - (getHeight() - HEADER_H - BOTTOM_PAD));
            if (scroll < 0f) scroll = 0f;
            if (scroll > maxScroll) scroll = maxScroll;
        }
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

    public void closeAllSettings() {
        componentsList.forEach(ModuleLayerComponent::closeSettings);
    }

    public float getTotalContentHeight() {
        float h = 0f;
        for (ModuleLayerComponent m : componentsList) {
            h += max(MIN_ITEM_H, m.getTotalHeight()) + ITEM_SPACING;
        }
        return Math.max(0f, h - ITEM_SPACING);
    }
}
