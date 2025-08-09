package jaypasha.funpay.ui.overlay;

import jaypasha.funpay.Api;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import net.minecraft.client.gui.DrawContext;

public class OverlayRenderer {

    public static void rect(DrawContext context, float x, float y, float width, float height) {
        Api.blur()
                .blurRadius(16)
                .radius(new QuadRadiusState(2.5f))
                .size(new SizeState(width, height))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), x, y);

        Api.shadow()
                .size(new SizeState(width + 1, height + 1))
                .radius(new QuadRadiusState(3f))
                .shadow(6)
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF00042C, 20)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), x - .5, y - .5);

        Api.rectangle()
                .size(new SizeState(width, height))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF00042C, 70), ColorUtility.applyOpacity(0xFF00042C, 70), ColorUtility.applyOpacity(0xFF000537, 70), ColorUtility.applyOpacity(0xFF000537, 70)))
                .radius(new QuadRadiusState(2.5f))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), x, y);
    }

}
