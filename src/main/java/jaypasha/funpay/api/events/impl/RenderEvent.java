package jaypasha.funpay.api.events.impl;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import jaypasha.funpay.api.events.EventLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class RenderEvent extends EventLayer {

    @Getter
    @AllArgsConstructor
    public static class AfterHand extends RenderEvent {
        MatrixStack stack;
        RenderTickCounter tickCounter;
    }

    @Getter
    @AllArgsConstructor
    public static class BeforeHud extends RenderEvent {
        DrawContext context;
        RenderTickCounter tickCounter;
    }

    @Getter
    @AllArgsConstructor
    public static class AfterHud extends RenderEvent {
        DrawContext context;
        RenderTickCounter tickCounter;
    }

    @Getter
    @AllArgsConstructor
    public static class AfterChat extends RenderEvent {
        DrawContext context;
        int mouseX;
        int mouseY;
        float delta;
    }

    @Getter
    @AllArgsConstructor
    public static class RenderLabelsEvent<T extends Entity, S extends EntityRenderState> extends RenderEvent {
        S state;
    }

}
