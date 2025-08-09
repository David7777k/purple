package jaypasha.funpay.ui.clickGui.components.settings;

import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.ui.clickGui.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor()
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class SettingComponent extends Component {

    SettingLayer settingLayer;

}
