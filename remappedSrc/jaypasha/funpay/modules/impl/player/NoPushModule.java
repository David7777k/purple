package jaypasha.funpay.modules.impl.player;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import net.minecraft.text.Text;

public class NoPushModule extends ModuleLayer {

    public NoPushModule() {
        super(Text.of("No Push"), null, Category.Player);
    }
}
