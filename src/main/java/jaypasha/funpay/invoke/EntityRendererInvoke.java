package jaypasha.funpay.invoke;

import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.RenderEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererInvoke<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresentInvoke(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        RenderEvent.RenderLabelsEvent<T, S> renderLabelsEvent = new RenderEvent.RenderLabelsEvent<>(state);
        EventManager.call(renderLabelsEvent);

        if (renderLabelsEvent.isCanceled()) ci.cancel();
    }

}
