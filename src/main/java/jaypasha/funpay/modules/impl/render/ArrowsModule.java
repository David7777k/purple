package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe; import jaypasha.funpay.Api; import jaypasha.funpay.api.events.impl.RenderEvent; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.modules.settings.impl.BooleanSetting; import jaypasha.funpay.utility.math.MathVector; import jaypasha.funpay.utility.render.builders.states.QuadColorState; import jaypasha.funpay.utility.render.builders.states.SizeState; import jaypasha.funpay.utility.render.utility.TextureUtil; import net.minecraft.client.gui.DrawContext; import net.minecraft.client.network.AbstractClientPlayerEntity; import net.minecraft.client.util.math.MatrixStack; import net.minecraft.text.Text; import net.minecraft.util.math.RotationAxis; import org.joml.Matrix4f;

public class ArrowsModule extends ModuleLayer {

    private final BooleanSetting showNames = new BooleanSetting(Text.of("Показывать имя"), null, () -> true)
            .register(this);

    // Можно добавить в настройки:
    private static final int ARROW_SIZE = 20;
    private static final int ARROW_OFFSET_Y = 40;
    private static final int NAME_OFFSET_Y = 25;
    private static final int NAME_FONT_SIZE = 6;
    private static final int COLOR_MAIN = 0xFFC9C3FF;

    public ArrowsModule() {
        super(Text.of("Arrows"), null, Category.Render);
    }

    @Subscribe
    public void renderEvent(RenderEvent.AfterHud event) {
        if (!getEnabled() || mc.player == null || mc.world == null) return;

        DrawContext context = event.getContext();
        float centerX = mc.getWindow().getScaledWidth() / 2f;
        float centerY = mc.getWindow().getScaledHeight() / 2f;

        mc.world.getPlayers().forEach(target -> {
            if (!target.isAlive() || target.equals(mc.player)) return;
            drawArrowTo(context, target, centerX, centerY);
        });
    }

    private void drawArrowTo(DrawContext context, AbstractClientPlayerEntity target, float cx, float cy) {
        MatrixStack ms = context.getMatrices();
        Matrix4f matrix = ms.peek().getPositionMatrix();

        float rotation = MathVector.rotationDifference(target) - mc.player.getYaw();

        ms.push();
        ms.translate(cx, cy, 0);
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
        ms.translate(-cx, -cy, 0);

        // Стрелка
        Api.texture()
                .size(new SizeState(ARROW_SIZE, ARROW_SIZE))
                .texture(0, 0, 1, 1, TextureUtil.of("images/triangle.png"))
                .color(new QuadColorState(COLOR_MAIN))
                .build()
                .render(matrix, cx - ARROW_SIZE / 2f, cy - ARROW_SIZE / 2f - ARROW_OFFSET_Y);

        ms.pop();

        // Имя игрока
        if (showNames.getEnabled()) {
            String name = target.getName().getString();
            float textWidth = Api.inter().getWidth(name, NAME_FONT_SIZE);
            Api.text()
                    .text(name)
                    .size(NAME_FONT_SIZE)
                    .color(COLOR_MAIN)
                    .font(Api.inter())
                    .build()
                    .render(matrix, cx - textWidth / 2f, cy - ARROW_OFFSET_Y + (ARROW_SIZE / 2f) - NAME_OFFSET_Y);
        }
    }

}

