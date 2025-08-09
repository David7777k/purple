package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.api.events.impl.RenderEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.BooleanSetting;
import jaypasha.funpay.utility.math.MathVector;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.TextureUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class ArrowsModule extends ModuleLayer {

    BooleanSetting showNames = new BooleanSetting(Text.of("Показывать имя"), null, () -> true)
            .register(this);

    public ArrowsModule() {
        super(Text.of("Arrows"), null, Category.Render);
    }

    @Subscribe
    public void renderEvent(RenderEvent.AfterHud renderEvent) {
        if (!getEnabled()) return;

        DrawContext context = renderEvent.getContext();

        mc.world.getPlayers().forEach(entity -> {
            if (!entity.isAlive() || entity.equals(mc.player)) return;

            drawArrowTo(context, entity);
        });
    }

    private void drawArrowTo(DrawContext context, AbstractClientPlayerEntity player) {
        MatrixStack ms = context.getMatrices();

        float x = mc.getWindow().getScaledWidth() / 2f;
        float y = mc.getWindow().getScaledHeight() / 2f;

        ms.push();
        ms.translate(x, y, 0);
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathVector.rotationDifference(player) - mc.player.getYaw()));
        ms.translate(-x, -y, 0);

        Api.texture()
                .size(new SizeState(20, 20))
                .texture(0,0,1,1, TextureUtil.of("images/triangle.png"))
                .color(new QuadColorState(0xFFC9C3FF))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), x - 10, y - 10 - 40);

        if (showNames.getEnabled())
            Api.text()
                    .text(player.getName().getString())
                    .size(6)
                    .color(0xFFC9C3FF)
                    .font(Api.inter())
                    .build()
                    .render(context.getMatrices().peek().getPositionMatrix(), x - Api.inter().getWidth(player.getName().getString(), 6) / 2, y - 10 - 25);

        ms.pop();
    }
}
