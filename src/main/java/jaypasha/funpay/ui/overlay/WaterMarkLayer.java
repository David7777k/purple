package jaypasha.funpay.ui.overlay;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.draggable.DraggableLayer;
import jaypasha.funpay.modules.impl.render.HudModule;
import jaypasha.funpay.utility.color.ColorUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.function.Supplier;

import static jaypasha.funpay.ui.overlay.OverlayRenderer.rect;

public class WaterMarkLayer extends DraggableLayer {

    static Supplier<HudModule> module = Suppliers.memoize(() -> (HudModule) Pasxalka.getInstance().getModuleRepository().find(HudModule.class));

    public WaterMarkLayer() {
        super(10f, 10f, 128f, 15f, () -> module.get().getEnabled() && module.get().getVisible().get("Watermark").getEnabled());
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickCounter) {
        final String fpsString = String.valueOf(mc.getCurrentFps());
        final String nameString = mc.player.getName().getString();
        final String roleString = "developer";

        final float fpsWidth = Api.inter().getWidth(fpsString, 7);
        final float nameWidth = Api.inter().getWidth(nameString, 7);
        final float roleWidth = Api.inter().getWidth(roleString, 7);

        setWidth(50.5f + fpsWidth + nameWidth + roleWidth);

        rect(context, getX(), getY(), getWidth(), getHeight());

        Api.text()
                .text("A")
                .font(Api.hudIcons())
                .color(0xFF878DFF)
                .size(8f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 2.5, getY() + (getHeight() - Api.hudIcons().getHeight("A", 8)) / 2);

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFC9C3FF, 50))
                .text(fpsString)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 14, getY() - 1 + getHeight() / 2 - Api.inter().getHeight(fpsString, 7) / 2);

        Api.text()
                .text("B")
                .font(Api.hudIcons())
                .color(0xFF878DFF)
                .size(8f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 18 + fpsWidth, getY() + (getHeight() - Api.hudIcons().getHeight("A", 8)) / 2);

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFC9C3FF, 50))
                .text(nameString)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 29.5 + fpsWidth, getY() - 1 + getHeight() / 2 - Api.inter().getHeight(nameString, 7) / 2);

        Api.text()
                .text("C")
                .font(Api.hudIcons())
                .color(0xFF878DFF)
                .size(8f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 34 + fpsWidth + nameWidth, getY() + (getHeight() - Api.hudIcons().getHeight("A", 8)) / 2);

        Api.text()
                .size(7)
                .color(ColorUtility.applyOpacity(0xFFC9C3FF, 50))
                .text(roleString)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 45.5 + fpsWidth + nameWidth, getY() - 1 + getHeight() / 2 - Api.inter().getHeight(roleString, 7) / 2);

    }
}
