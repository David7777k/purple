package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.modules.settings.SettingLayerBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeSetting extends SettingLayer {

    List<String> values = new ArrayList<>();

    @Getter
    @NonFinal
    String value = null;

    public ModeSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);
    }

    public ModeSetting set(String... strings) {
        values.addAll(Arrays.asList(strings));

        return this;
    }

    public ModeSetting set(String value) {
        this.value = value;
        this.getAnimation().reset();

        return this;
    }

    @Override
    public ModeSetting register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public ModeSetting collection(Collection collection) {
        collection.put(this);

        return this;
    }
}
