package jaypasha.funpay.ui.clickGui.components.settings.modeListSetting;

import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import java.util.List;

public class ModeListSettingHelper {

    public static List<ModeListSettingValueComponent> values(ModeListSetting modeListSetting) {
        return modeListSetting.asStringList().stream()
                .map(e -> new ModeListSettingValueComponent(modeListSetting, e))
                .toList();
    }

}
