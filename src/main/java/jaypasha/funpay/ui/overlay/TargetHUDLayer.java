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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

import static jaypasha.funpay.ui.overlay.OverlayRenderer.rect;

public class TargetHUDLayer extends DraggableLayer {

    float animationValue;

    static Supplier<HudModule> module = Suppliers.memoize(() -> (HudModule) Pasxalka.getInstance().getModuleRepository().find(HudModule.class));
    static Supplier<AttackAuraModule> aura = Suppliers.memoize(() -> (AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class));

    // Клиент
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public TargetHUDLayer() {
        // Видимость: HUD виден когда включён модуль/HUD Target ON и есть либо цель Aura, либо цель под прицелом
        super(10f, 45f, 75f, 25f, () -> module.get().getEnabled()
                && module.get().getVisible().get("Target").getEnabled()
                && (aura.get().getTarget() != null || getEntityUnderCrosshairStatic() != null));
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickCounter) {
        // Сначала пробуем цель из Aura
        LivingEntity target = null;
        Entity auraTarget = aura.get().getTarget();
        if (auraTarget instanceof LivingEntity) target = (LivingEntity) auraTarget;

        // Если в Aura цели нет — возьмём сущность под прицелом, если она живая
        if (target == null) {
            target = getEntityUnderCrosshairStatic();
        }

        // Если нет цели — ничего не рисуем
        if (target == null) return;

        rect(context, getX(), getY(), getWidth(), getHeight());

        float healthRatio = target.getMaxHealth() > 0 ? target.getHealth() / target.getMaxHealth() : 0f;
        animationValue = MathHelper.lerp(.1f, animationValue, MathHelper.clamp(healthRatio, 0f, 1f));

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

    // Получаем живую сущность под прицелом (или null)
    private static LivingEntity getEntityUnderCrosshairStatic() {
        if (mc == null) return null;
        HitResult hr = mc.crosshairTarget;
        if (hr instanceof EntityHitResult) {
            Entity e = ((EntityHitResult) hr).getEntity();
            if (e instanceof LivingEntity) return (LivingEntity) e;
        }
        return null;
    }
}
