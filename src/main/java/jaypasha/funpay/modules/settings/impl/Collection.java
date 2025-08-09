package jaypasha.funpay.modules.settings.impl;

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.SettingLayerBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Collection extends SettingLayer {

    List<SettingLayer> settingLayers = new ArrayList<>();

    public Collection(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);
    }

    public Collection put(SettingLayer settingLayer) {
        settingLayers.add(settingLayer);

        return this;
    }

    @Override
    public Collection register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public SettingLayer collection(Collection collection) {
        collection.put(this);

        return this;
    }

}
