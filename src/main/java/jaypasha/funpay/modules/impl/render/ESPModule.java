package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import jaypasha.funpay.api.events.impl.RenderEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.MathProjection;
import jaypasha.funpay.utility.math.MathVector;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4d;

public class ESPModule extends ModuleLayer {

    public ESPModule() {
        super(Text.of("ESP"), null, Category.Render);
    }

    @Subscribe
    public void renderEvent(RenderEvent.AfterHud renderEvent) {
        if (!getEnabled()) return;

        DrawContext context = renderEvent.getContext();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        mc.player.getWorld().getPlayers().forEach(e -> {
            if (e.equals(mc.player)) return;

            Vec3d projected = MathProjection.projectCoordinates(MathVector.lerpPosition(e).add(0,e.getHeight() + .5f,0));

            if (projected.z <= 0 || projected.z >= 1) return;

            drawBox(builder, e, context);
        });

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public void drawBox(BufferBuilder builder, Entity ent, DrawContext context) {
        Vec3d[] vectors = getVectors(ent);

        Vector4d position = null;
        for (Vec3d vector : vectors) {
            vector = MathProjection.projectCoordinates(new Vec3d(vector.x, vector.y, vector.z));
            if (vector.z > 0 && vector.z < 1) {
                if (position == null) position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
        }

        if (position != null) {
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double endPosY = position.w;

            setRectPoints(builder, context.getMatrices().peek().getPositionMatrix(), (float) (posX - 0.5f), (float) posY, (float) (posX + 0.5 - 0.5), (float) endPosY, ColorUtility.applyOpacity(0xFF000000, 75));
            setRectPoints(builder, context.getMatrices().peek().getPositionMatrix(), (float) posX, (float) (endPosY - 0.5f), (float) endPosX, (float) endPosY, ColorUtility.applyOpacity(0xFF000000, 75));
            setRectPoints(builder, context.getMatrices().peek().getPositionMatrix(), (float) (posX - 0.5), (float) posY, (float) endPosX, (float) (posY + 0.5), ColorUtility.applyOpacity(0xFF000000, 75));
            setRectPoints(builder, context.getMatrices().peek().getPositionMatrix(), (float) (endPosX - 0.5), (float) posY, (float) endPosX, (float) endPosY, ColorUtility.applyOpacity(0xFF000000, 75));
        }

        BufferRenderer.drawWithGlobalProgram(builder.end());
    }

    private static Vec3d[] getVectors(Entity ent) {
        Vec3d lerp = MathVector.lerpPosition(ent);

        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + lerp.x - 0.05, axisAlignedBB2.minY - ent.getY() + lerp.y, axisAlignedBB2.minZ - ent.getZ() + lerp.z - 0.05, axisAlignedBB2.maxX - ent.getX() + lerp.x + 0.05, axisAlignedBB2.maxY - ent.getY() + lerp.y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + lerp.z + 0.05);
        return new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
    }

    public static void setRectPoints(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float x1, float y1, int color) {
        bufferBuilder.vertex(matrix, x, y1, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x1, y, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(color);
    }

}
