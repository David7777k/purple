package jaypasha.funpay.api.draggable.data.impl;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.draggable.DraggableLayer;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;

public class WaterMarkLayer extends DraggableLayer {

    public WaterMarkLayer() {
        super(10.0f, 10.0f, 84.0f, 15.0f, () -> true);
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickCounter) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        Api.rectangle()
            .size(new SizeState(getWidth(),getHeight()))
            .color(new QuadColorState(0xFF000000))
            .radius(new QuadRadiusState(10f / 2))
            .build()
            .render(matrix4f, getX(), getY());

        Api.border()
            .size(new SizeState(getWidth(),getHeight()))
            .color(new QuadColorState(0xFFFFFFFF))
            .radius(new QuadRadiusState(10f / 2))
            .thickness(-.5f)
            .build()
            .render(matrix4f, getX(), getY());
    }

}
