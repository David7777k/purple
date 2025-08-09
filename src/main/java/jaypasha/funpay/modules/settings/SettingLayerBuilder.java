package jaypasha.funpay.modules.settings;

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.Collection;

public interface SettingLayerBuilder {

    SettingLayerBuilder register(ModuleLayer provider);

    SettingLayerBuilder collection(Collection collection);

}
