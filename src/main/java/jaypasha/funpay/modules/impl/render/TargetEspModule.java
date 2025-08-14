package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe; import com.mojang.blaze3d.systems.RenderSystem; import jaypasha.funpay.Pasxalka; import jaypasha.funpay.api.events.impl.RenderEvent; import jaypasha.funpay.modules.impl.combat.AttackAuraModule; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.utility.color.ColorUtility; import jaypasha.funpay.utility.math.MathVector; import net.minecraft.client.render.VertexConsumer; import net.minecraft.client.render.VertexConsumerProvider; import net.minecraft.client.util.math.MatrixStack; import net.minecraft.entity.Entity; import net.minecraft.text.Text; import net.minecraft.util.Identifier; import net.minecraft.util.math.RotationAxis; import net.minecraft.util.math.Vec3d;

import static jaypasha.funpay.utility.render.utility.VertexUtils.IMAGE; import static jaypasha.funpay.utility.render.utility.VertexUtils.drawImageQuad;

public class TargetEspModule extends ModuleLayer {

    private static final Identifier TEXTURE = Identifier.of("pasxalka", "images/target.png");
    private static final float IMAGE_SIZE = 0.5f;
    private static final int BASE_COLOR = 0xFFC9C3FF;

    public TargetEspModule() {
        super(Text.of("Target ESP"), null, Category.Render);
    }

    @Subscribe
    public void render(RenderEvent.AfterHand event) {
        if (!getEnabled() || mc.player == null || mc.world == null) return;

        AttackAuraModule aura = (AttackAuraModule) Pasxalka.getInstance()
                .getModuleRepository()
                .find(AttackAuraModule.class);
        if (aura == null) return;

        Entity target = aura.getTarget();
        if (target == null) return;

        // Позиция цели относительно камеры
        Vec3d relPos = MathVector.lerpPosition(target)
                .add(0, 1f, 0)
                .subtract(mc.gameRenderer.getCamera().getPos());

        // Настройка рендер состояния
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture(0, TEXTURE);

        MatrixStack ms = event.getStack();
        ms.push();

        // Перенос в позицию цели
        ms.translate(relPos.x, relPos.y, relPos.z);
        // Повороты под игрока + вращение картинки
        ms.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(mc.player.getYaw(1.0f)));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.player.getPitch(1.0f)));
        float rotation = (System.currentTimeMillis() % 3600) / 5f;
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

        // Отрисовка
        VertexConsumerProvider.Immediate vcp = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vc = vcp.getBuffer(IMAGE);
        int alpha = (int) (100 * getAnimation().getOutput().floatValue());
        int color = ColorUtility.applyOpacity(BASE_COLOR, alpha);

        drawImageQuad(vc, ms.peek().getPositionMatrix(),
                0, 0, 0, IMAGE_SIZE, color);

        ms.pop();

        // Финальный flush
        vcp.draw();

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

}