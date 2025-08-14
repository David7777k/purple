package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe; import com.mojang.blaze3d.systems.RenderSystem; import jaypasha.funpay.api.events.impl.RenderEvent; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.utility.color.ColorUtility; import jaypasha.funpay.utility.math.MathProjection; import jaypasha.funpay.utility.math.MathVector; import net.minecraft.client.gl.ShaderProgramKeys; import net.minecraft.client.gui.DrawContext; import net.minecraft.client.render.*; import net.minecraft.entity.Entity; import net.minecraft.text.Text; import net.minecraft.util.math.Box; import net.minecraft.util.math.Vec3d; import org.joml.Matrix4f;

public class ESPModule extends ModuleLayer {

    public ESPModule() {
        super(Text.of("ESP"), null, Category.Render);
    }

    @Subscribe
    public void renderEvent(RenderEvent.AfterHud renderEvent) {
        if (!getEnabled() || mc.player == null || mc.player.getWorld() == null) return;

        DrawContext context = renderEvent.getContext();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        // Render state
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        // Batch begin
        BufferBuilder builder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        final int borderColor = ColorUtility.applyOpacity(0xFF000000, 75);
        final float thickness = 0.5f;

        mc.player.getWorld().getPlayers().forEach(e -> {
            if (e.equals(mc.player)) return;

            // Быстрый отсев по центру шапки
            Vec3d headProjected = MathProjection.projectCoordinates(
                    MathVector.lerpPosition(e).add(0, e.getHeight() + 0.5f, 0)
            );
            if (headProjected.z <= 0 || headProjected.z >= 1) return;

            drawBox(builder, e, matrix, borderColor, thickness);
        });

        // Flush once
        BufferRenderer.drawWithGlobalProgram(builder.end());

        // Restore state
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    private void drawBox(BufferBuilder builder, Entity ent, Matrix4f matrix, int color, float thickness) {
        Vec3d[] corners = getVectors(ent);

        // Находим screen-space bounding box из видимых углов
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Vec3d world : corners) {
            Vec3d p = MathProjection.projectCoordinates(world);
            if (p.z > 0 && p.z < 1) {
                if (p.x < minX) minX = p.x;
                if (p.y < minY) minY = p.y;
                if (p.x > maxX) maxX = p.x;
                if (p.y > maxY) maxY = p.y;
            }
        }

        if (!(minX < maxX && minY < maxY)) return;

        float x0 = (float) minX;
        float y0 = (float) minY;
        float x1 = (float) maxX;
        float y1 = (float) maxY;
        float t  = thickness;

        // Левые/правые/верх/низ рамки как четыре QUAD’а
        setRectPoints(builder, matrix, x0 - t, y0,     x0,     y1,     color); // left
        setRectPoints(builder, matrix, x1,     y0,     x1 + t, y1,     color); // right
        setRectPoints(builder, matrix, x0,     y0 - t, x1,     y0,     color); // top
        setRectPoints(builder, matrix, x0,     y1,     x1,     y1 + t, color); // bottom
    }

    private static Vec3d[] getVectors(Entity ent) {
        Vec3d lerp = MathVector.lerpPosition(ent);
        Box bbWorld = ent.getBoundingBox();

        // Переносим AABB в мир с учётом интерполяции и лёгким расширением
        Box bb = new Box(
                bbWorld.minX - ent.getX() + lerp.x - 0.05,
                bbWorld.minY - ent.getY() + lerp.y,
                bbWorld.minZ - ent.getZ() + lerp.z - 0.05,
                bbWorld.maxX - ent.getX() + lerp.x + 0.05,
                bbWorld.maxY - ent.getY() + lerp.y + 0.15,
                bbWorld.maxZ - ent.getZ() + lerp.z + 0.05
        );

        return new Vec3d[]{
                new Vec3d(bb.minX, bb.minY, bb.minZ),
                new Vec3d(bb.minX, bb.maxY, bb.minZ),
                new Vec3d(bb.maxX, bb.minY, bb.minZ),
                new Vec3d(bb.maxX, bb.maxY, bb.minZ),
                new Vec3d(bb.minX, bb.minY, bb.maxZ),
                new Vec3d(bb.minX, bb.maxY, bb.maxZ),
                new Vec3d(bb.maxX, bb.minY, bb.maxZ),
                new Vec3d(bb.maxX, bb.maxY, bb.maxZ)
        };
    }

    public static void setRectPoints(BufferBuilder bufferBuilder, Matrix4f matrix,
                                     float x, float y, float x1, float y1, int color) {
        bufferBuilder.vertex(matrix, x,  y1, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x1, y,  0.0F).color(color);
        bufferBuilder.vertex(matrix, x,  y,  0.0F).color(color);
    }

}