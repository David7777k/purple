package jaypasha.funpay.modules.impl.combat;

import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.BooleanSetting;
import jaypasha.funpay.modules.settings.impl.Collection;
import jaypasha.funpay.modules.settings.impl.SliderSetting;
import net.minecraft.text.Text;

public class AttackAuraModule extends ModuleLayer {

    Collection sliders = new Collection(Text.of("Sliders"), null, () -> true)
            .register(this);

    Collection booleans = new Collection(Text.of("Booleans"), null, () -> true)
            .register(this);

    SliderSetting attackRange = new SliderSetting(Text.of("Attack Range"), Text.of("bla bal bal alba labal ab la lablab lablalbalbal l lbalba l abla"), () -> true)
            .set(.5f, 6.f, .1f)
            .collection(sliders);

    SliderSetting incrementsRange = new SliderSetting(Text.of("Increments Range"), null, () -> true)
            .set(.0f, 3.f, .1f)
            .collection(sliders);

    BooleanSetting onlyCrits = new BooleanSetting(Text.of("Only Crits"), Text.of("if boolean setting is enabled then aura module was due hits with Только Критами"), () -> true)
            .collection(booleans);

    BooleanSetting smartCrits = new BooleanSetting(Text.of("Smart Crits"), Text.of("if boolean setting is enabled then aura module was due hits with Только Критами"), () -> true)
            .register(this);

    public AttackAuraModule() {
        super(Text.of("Aura Module"), null, Category.Combat);
    }
}
