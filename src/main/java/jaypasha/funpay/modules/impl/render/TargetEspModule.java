package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.events.impl.RenderEvent;
import jaypasha.funpay.modules.impl.combat.AttackAuraModule;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.MathVector;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import static jaypasha.funpay.utility.render.utility.VertexUtils.IMAGE;
import static jaypasha.funpay.utility.render.utility.VertexUtils.drawImageQuad;

public class TargetEspModule extends ModuleLayer {

    public TargetEspModule() {
        super(Text.of("Target ESP"), null, Category.Render);
    }

    @Subscribe
    public void render(RenderEvent.AfterHand renderEvent) {
        Entity target = ((AttackAuraModule) Pasxalka.getInstance().getModuleRepository().find(AttackAuraModule.class)).getTarget();

        if (!getEnabled() || target == null) return;

        MatrixStack ms = renderEvent.getStack();
        Vec3d targetPos = MathVector.lerpPosition(target).add(0,1f,0).subtract(mc.gameRenderer.getCamera().getPos());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        ms.push();
        ms.translate(targetPos);
        ms.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(mc.player.getYaw(1.0f)));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.player.getPitch(1.0f)));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((System.currentTimeMillis() % 3600) / 5f));
        ms.translate(-targetPos.x, -targetPos.y, -targetPos.z);

        VertexConsumerProvider.Immediate vertexConsumerProvider = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(IMAGE);

        RenderSystem.setShaderTexture(0, Identifier.of("pasxalka", "images/target.png"));

        drawImageQuad(vertexConsumer, ms.peek().getPositionMatrix(), (float) targetPos.x, (float) targetPos.y, (float) targetPos.z, .5f, ColorUtility.applyOpacity(0xFFC9C3FF, (int) (100 * getAnimation().getOutput().floatValue())));

        vertexConsumerProvider.drawCurrentLayer();

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}
