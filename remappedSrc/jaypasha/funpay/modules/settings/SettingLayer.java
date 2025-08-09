package jaypasha.funpay.modules.settings;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class SettingLayer implements SettingLayerBuilder {

    Text name;
    Text description;
    Supplier<Boolean> visible;
    Animation animation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    public void reg(ModuleLayer provider) {
        provider.getSettingLayers().add(this);
    }
}
