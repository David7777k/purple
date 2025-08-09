package jaypasha.funpay.modules.impl.miscellaneous;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import net.minecraft.text.Text;

public class AutoLeaveModule extends ModuleLayer {

    public AutoLeaveModule() {
        super(Text.of("Auto Leave"), null, Category.Miscellaneous);
    }

}
