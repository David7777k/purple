package jaypasha.funpay.ui.overlay;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.draggable.DraggableLayer;
import jaypasha.funpay.modules.impl.render.HudModule;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.utility.Scissors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static jaypasha.funpay.ui.overlay.OverlayRenderer.rect;

public class StaffListLayer extends DraggableLayer {

    static List<String> staffsPrefixes = Lists.newArrayList(
            "eternity", "dhelper", "d helper", "moder", "moderator", "admin", "owner", "administrator"
    );

    static Supplier<List<AbstractClientPlayerEntity>> list = () -> {
        if (mc == null || mc.world == null) return Collections.emptyList();
        return mc.world.getPlayers().stream()
                .filter(player -> {
                    var team = player.getScoreboardTeam();
                    if (team == null || team.getPrefix() == null) return false;
                    String prefix = team.getPrefix().getString().toLowerCase();
                    for (String s : staffsPrefixes) {
                        if (prefix.contains(s.toLowerCase())) return true;
                    }
                    return false;
                })
                .toList();
    };

    static Supplier<HudModule> module = Suppliers.memoize(() ->
            (HudModule) Pasxalka.getInstance().getModuleRepository().find(HudModule.class));

    public StaffListLayer() {
        super(10f, 95f, 60f, 15f, () ->
                module.get().getEnabled() && !list.get().isEmpty()
        );
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickCounter) {
        rect(context, getX(), getY(), getWidth(), getHeight());

        Api.text()
                .font(Api.inter())
                .text("Staffs")
                .color(0xFFC9C3FF)
                .size(7)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + 2.5f,
                        getY() - 1 + (15 - Api.inter().getHeight("Keybinds", 7)) / 2);

        float total = 0f;
        for (var ignored : list.get()) total += 6f;
        setHeight(15f + total);

        AtomicReference<Float> offset = new AtomicReference<>(0f);
        Scissors.push(getX(), getY(), getWidth(), getHeight());
        list.get().forEach(e -> {
            Api.text()
                    .size(5)
                    .font(Api.inter())
                    .text(e.getName().getString())
                    .thickness(.1f)
                    .color(ColorUtility.applyOpacity(0xFFC9C3FF, 50))
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(),
                            getX() + 2.5f,
                            getY() + 12.5f + offset.get());
            offset.set(offset.get() + 6f);
        });
        Scissors.pop();
    }
}
