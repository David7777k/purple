package jaypasha.funpay.ui.clickGui.components.settings.collection;

import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;

import java.util.List;
import java.util.Objects;

public final class CollectionHelper {

    public static List<SettingComponent> childSettingComponents(jaypasha.funpay.modules.settings.impl.Collection collection) {
        return collection.getSettingLayers().stream()
                .map(jaypasha.funpay.ui.clickGui.Helper::find)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Высота коллекции с равномерным промежутком между видимыми дочерними элементами.
     * Гэп добавляется только между элементами (не после последнего).
     */
    public static float collectionHeight(List<SettingComponent> settingComponents) {
        final float gap = 4f;
        float total = 0f;
        boolean first = true;

        for (SettingComponent sc : settingComponents) {
            if (!sc.getSettingLayer().getVisible().get()) continue;
            if (!first) total += gap;
            total += sc.getHeight();
            first = false;
        }
        return total;
    }
}
