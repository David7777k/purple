package jaypasha.funpay.modules.impl.render;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import lombok.Getter;
import net.minecraft.text.Text;

@Getter
public class HudModule extends ModuleLayer {

    ModeListSetting visible = new ModeListSetting(Text.of("Показывать"), null, () -> true)
            .set("Watermark", "Keybinds", "Target")
            .register(this);

    public HudModule() {
        super(Text.of("HUD"), null, Category.Render);
    }
}
