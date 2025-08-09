package jaypasha.funpay.modules.settings.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
public class BindSetting extends SettingLayer {

    Integer key = -1;

    public BindSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);
    }

    public BindSetting set(Integer keyCode) {
        if (Objects.equals(keyCode, this.key)) return this;

        this.key = keyCode;
        this.getAnimation().reset();

        return this;
    }

    @Override
    public BindSetting register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public BindSetting collection(Collection collection) {
        collection.put(this);

        return this;
    }
}
