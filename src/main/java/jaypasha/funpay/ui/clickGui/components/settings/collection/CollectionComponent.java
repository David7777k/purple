package jaypasha.funpay.ui.clickGui.components.settings.collection;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.impl.Collection;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class CollectionComponent extends SettingComponent {

    private static final float CHILD_GAP = 4f;
    private final List<SettingComponent> childSettingsComponents = new ArrayList<>();

    public CollectionComponent(Collection collection) {
        super(collection);
        childSettingsComponents.addAll(CollectionHelper.childSettingComponents(collection));
    }

    @Override
    public void init() {
        childSettingsComponents.forEach(SettingComponent::init);

        float titleHeight = Api.inter().getHeight(getSettingLayer().getName().getString(), 7.5f);
        float childrenHeight = CollectionHelper.collectionHeight(childSettingsComponents);

        size((240f / 2) - 10, titleHeight + 5 + childrenHeight);
    }

    @Override
    public CollectionComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        String title = getSettingLayer().getName().getString();
        float titleWidth = Api.inter().getWidth(title, 7.5f);
        boolean hovered = jaypasha.funpay.utility.math.Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(),
                Api.inter().getHeight(title, 7.5f) + 2);

        // Фон контейнера
        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(3))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 25)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // Заголовок
        Api.text()
                .font(Api.inter())
                .color(ColorUtility.applyOpacity(0xFFFFFFFF, hovered ? 100 : 85))
                .text(title)
                .size(7.5f)
                .build()
                .render(
                        context.getMatrices().peek().getPositionMatrix(),
                        getX() + (getWidth() - titleWidth) / 2f,
                        getY()
                );

        // Рендер дочерних
        float yBase = getY() + Api.inter().getHeight(title, 7.5f) + 5f;
        List<SettingComponent> visible = childSettingsComponents.stream()
                .filter(c -> c.getSettingLayer().getVisible().get())
                .toList();

        float offset = 0f;
        for (int i = 0; i < visible.size(); i++) {
            SettingComponent child = visible.get(i);
            float yPos = yBase + offset;
            child.position(getX(), yPos).render(context, mouseX, mouseY, delta);

            offset += child.getHeight();
            if (i < visible.size() - 1) offset += CHILD_GAP;
        }
        return this;
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return childSettingsComponents.stream().anyMatch(e -> e.mouseReleased(mouseX, mouseY, button));
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return childSettingsComponents.stream().anyMatch(e -> e.mouseClicked(mouseX, mouseY, button));
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return childSettingsComponents.stream().anyMatch(e -> e.keyPressed(keyCode, scanCode, modifiers));
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return childSettingsComponents.stream().anyMatch(e -> e.keyReleased(keyCode, scanCode, modifiers));
    }
}
