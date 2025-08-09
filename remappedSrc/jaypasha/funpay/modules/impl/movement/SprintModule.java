package jaypasha.funpay.modules.impl.movement;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import net.minecraft.text.Text;

public class SprintModule extends ModuleLayer {

    public SprintModule() {
        super(Text.of("Sprint"), null, Category.Movement);
    }
}
