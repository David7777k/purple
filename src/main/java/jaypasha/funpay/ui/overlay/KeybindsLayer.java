package jaypasha.funpay.ui.overlay;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.draggable.DraggableLayer;
import jaypasha.funpay.modules.impl.render.HudModule;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.keyboard.KeyBoardUtil;
import jaypasha.funpay.utility.render.utility.Scissors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static jaypasha.funpay.ui.overlay.OverlayRenderer.rect;

public class KeybindsLayer extends DraggableLayer {

    static Supplier<List<ModuleLayer>> modules = () -> {
        Pasxalka pas = Pasxalka.getInstance();
        if (pas == null) return Collections.emptyList();
        return pas.getModuleRepository().getModuleLayers().stream()
                .filter(e -> e.getAnimation().getOutput().floatValue() > 0f)
                .filter(e -> e.getKey() != -1)
                .toList();
    };

    static Supplier<HudModule> module = Suppliers.memoize(() ->
            (HudModule) Pasxalka.getInstance().getModuleRepository().find(HudModule.class));

    public KeybindsLayer() {
        super(10f, 25f, 60f, 15f, () ->
                module.get().getEnabled() &&
                        module.get().getVisible().get("Keybinds").getEnabled() &&
                        !modules.get().isEmpty()
        );
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickCounter) {
        rect(context, getX(), getY(), getWidth(), getHeight());

        Api.text()
                .font(Api.inter())
                .text("Keybinds")
                .color(0xFFC9C3FF)
                .size(7)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + 2.5f,
                        getY() - 1 + (15 - Api.inter().getHeight("Keybinds", 7)) / 2);

        AtomicReference<Float> height = new AtomicReference<>(0f);
        modules.get().forEach(e -> height.set(height.get() + 6 * e.getAnimation().getOutput().floatValue()));
        setHeight(15f + height.get());

        AtomicReference<Float> offset = new AtomicReference<>(0f);

        Scissors.push(getX(), getY(), getWidth(), getHeight());
        modules.get().forEach(e -> {
            float alpha = 50f * e.getAnimation().getOutput().floatValue();
            Api.text()
                    .size(5)
                    .font(Api.inter())
                    .text(e.getModuleName().getString())
                    .thickness(.1f)
                    .color(ColorUtility.applyOpacity(0xFFC9C3FF, alpha))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() - 5 + (7.5f * e.getAnimation().getOutput().floatValue()),
                            getY() + 12.5f + offset.get());

            String keyText = "[" + KeyBoardUtil.translate(e.getKey()) + "]";
            Api.text()
                    .size(5)
                    .font(Api.inter())
                    .text(keyText)
                    .thickness(.1f)
                    .color(ColorUtility.applyOpacity(0xFFC9C3FF, alpha))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + getWidth() + 5
                                    - (7.5f * e.getAnimation().getOutput().floatValue())
                                    - Api.inter().getWidth(keyText, 5, 0.1f, 0),
                            getY() + 12.5f + offset.get());

            offset.set(offset.get() + 6 * e.getAnimation().getOutput().floatValue());
        });
        Scissors.pop();
    }
}
