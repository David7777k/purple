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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeListSetting extends SettingLayer {

    List<BooleanSetting> values = new ArrayList<>();

    public ModeListSetting(Text name, Text description, Supplier<Boolean> visible) {
        super(name, description, visible);
    }

    public void set(String... list) {
        Arrays.stream(list).map(this::create).forEach(values::add);
    }

    public List<String> asStringList() {
        return values.stream().map(BooleanSetting::getName).map(Text::getString).toList();
    }

    public Boolean asBoolean(String text) {
        return Objects.requireNonNull(values.stream()
                .filter(e -> e.getName().getString().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null)).getEnabled();
    }

    public BooleanSetting create(String text) {
        return new BooleanSetting(Text.of(text), Text.empty(), () -> true);
    }

    @Override
    public ModeListSetting register(ModuleLayer provider) {
        super.reg(provider);

        return this;
    }

    @Override
    public ModeListSetting collection(Collection collection) {
        collection.put(this);

        return this;
    }
}
