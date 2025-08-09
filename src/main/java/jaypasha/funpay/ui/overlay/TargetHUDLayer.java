package jaypasha.funpay.ui.overlay;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.draggable.DraggableLayer;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.impl.render.HudModule;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

import static jaypasha.funpay.ui.overlay.OverlayRenderer.rect;

public class TargetHUDLayer extends DraggableLayer {

    float animationValue;

    static Supplier<HudModule> module = Suppliers.memoize(() -> (HudModule) Pasxalka.getInstance().getModuleRepository().find(HudModule.class));
    static Supplier<AttackAuraModule> aura = Suppliers.memoize(() -> (AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class));

    public TargetHUDLayer() {
        super(10f, 45f, 75f, 25f, () -> module.get().getEnabled() && module.get().getVisible().get("Target").getEnabled() && aura.get().getTarget() != null);
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickCounter) {
        PlayerEntity target = (PlayerEntity) aura.get().getTarget();
        if (target == null) target = mc.player;

        rect(context, getX(), getY(), getWidth(), getHeight());

        animationValue = MathHelper.lerp(.1f, animationValue, Math.clamp(target.getHealth() / target.getMaxHealth(), 0f, 1f));

        Api.text()
                .font(Api.hudIcons())
                .text("B")
                .size(14f)
                .color(0xFF878DFF)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 2.5f, getY() + 2.5f);

        Api.text()
                .font(Api.inter())
                .text(target.getName().getString())
                .color(0xFFC9C3FF)
                .size(6)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 19.5f, getY() + 3.5f);

        Api.text()
                .font(Api.inter())
                .text("Health: " + String.format("%.1f", target.getHealth()))
                .color(ColorUtility.applyOpacity(0xFFC9C3FF, 50))
                .size(6)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 19.5f, getY() + 10.5f);

        Api.rectangle()
                .radius(new QuadRadiusState(.2f))
                .color(new QuadColorState(0xFF878DFF, 0xFF878DFF, 0xFFC9C3FF, 0xFFC9C3FF))
                .size(new SizeState((getWidth() - 5f) * animationValue, 2.5f))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 2.5f, getY() + getHeight() - 5f);
    }
}
