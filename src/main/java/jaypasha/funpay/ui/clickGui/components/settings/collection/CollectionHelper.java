package jaypasha.funpay.ui.clickGui.components.settings.collection;

import jaypasha.funpay.modules.settings.impl.Collection;
import jaypasha.funpay.ui.clickGui.Helper;
import jaypasha.funpay.ui.clickGui.components.settings.SettingComponent;

import java.util.List;
import java.util.Objects;

public final class CollectionHelper {

    public static List<SettingComponent> childSettingComponents(Collection collection) {
        return collection.getSettingLayers().stream()
                .map(Helper::find)
                .filter(Objects::nonNull)
                .toList();
    }

    public static float collectionHeight(List<SettingComponent> settingComponents) {
        return settingComponents.stream()
                .filter(e -> e.getSettingLayer().getVisible().get())
                .map(e -> e.getHeight() + 2.5f)
                .reduce(0f, Float::sum);
    }

}
