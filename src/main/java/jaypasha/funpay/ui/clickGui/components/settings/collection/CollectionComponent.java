package jaypasha.funpay.ui.clickGui.components.settings.collection;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.settings.impl.Collection;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CollectionComponent extends SettingComponent {

    List<SettingComponent> childSettingsComponents = new ArrayList<>();

    public CollectionComponent(Collection collection) {
        super(collection);
        childSettingsComponents.addAll(CollectionHelper.childSettingComponents(collection));
    }

    @Override
    public void init() {
        childSettingsComponents.forEach(SettingComponent::init);
        size((240f / 2) - 10, Api.inter().getHeight(getSettingLayer().getName().getString(), 7.5f) + 5 + CollectionHelper.collectionHeight(childSettingsComponents) + 2.5f);
    }

    @Override
    public CollectionComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        Api.text()
                .font(Api.inter())
                .color(0xFFFFFFFF)
                .text(getSettingLayer().getName().getString())
                .size(7.5f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + getWidth() / 2 - Api.inter().getWidth(getSettingLayer().getName().getString(), 7.5f) / 2, getY());

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        childSettingsComponents.forEach(e -> {
            e.position(getX(), getY() + offset.get() + Api.inter().getHeight(getSettingLayer().getName().getString(), 7.5f) + 5).render(context, mouseX, mouseY, delta);

            offset.set(offset.get() + e.getHeight() + 4f);
        });

        return null;
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return childSettingsComponents.stream().anyMatch(e -> e.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return childSettingsComponents.stream().anyMatch(e -> e.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return childSettingsComponents.stream().anyMatch(e -> e.keyPressed(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return childSettingsComponents.stream().anyMatch(e -> e.keyReleased(keyCode, scanCode, modifiers));
    }
}
