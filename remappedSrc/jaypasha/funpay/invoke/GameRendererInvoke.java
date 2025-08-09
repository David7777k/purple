package jaypasha.funpay.invoke;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import com.mojang.blaze3d.systems.RenderSystem;
import jaypasha.funpay.api.events.EventManager;
import jaypasha.funpay.api.events.impl.RenderEvent;
import jaypasha.funpay.utility.math.MathProjection;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static jaypasha.funpay.Api.mc;

@Mixin(GameRenderer.class)
public class GameRendererInvoke {

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void render3dHook(RenderTickCounter tickCounter, CallbackInfo ci) {
        Camera camera = mc.gameRenderer.getCamera();
        MatrixStack matrixStack = new MatrixStack();
        RenderSystem.getModelViewStack().pushMatrix().mul(matrixStack.peek().getPositionMatrix());
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));

        MathProjection.lastModMat.set(RenderSystem.getModelViewMatrix());
        MathProjection.lastProjMat.set(RenderSystem.getProjectionMatrix());
        MathProjection.lastWorldSpaceMatrix.set(matrixStack.peek().getPositionMatrix());

        RenderEvent.AfterHand afterHand = new RenderEvent.AfterHand(matrixStack, tickCounter);
        EventManager.call(afterHand);

        RenderSystem.getModelViewStack().popMatrix();
    }

}
