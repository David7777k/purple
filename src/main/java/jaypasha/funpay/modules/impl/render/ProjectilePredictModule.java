package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe; import jaypasha.funpay.Api; import jaypasha.funpay.api.events.impl.RenderEvent; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.utility.color.ColorUtility; import jaypasha.funpay.utility.math.MathProjection; import jaypasha.funpay.utility.math.MathVector; import jaypasha.funpay.utility.render.utility.VertexUtils; import net.minecraft.client.render.VertexConsumer; import net.minecraft.client.render.VertexConsumerProvider; import net.minecraft.client.util.math.MatrixStack; import net.minecraft.entity.Entity; import net.minecraft.entity.projectile.ProjectileEntity; import net.minecraft.text.Text; import net.minecraft.util.hit.BlockHitResult; import net.minecraft.util.hit.HitResult; import net.minecraft.util.math.ColorHelper; import net.minecraft.util.math.Vec3d; import net.minecraft.world.RaycastContext;

public class ProjectilePredictModule extends ModuleLayer {

    // Параметры визуала
    private static final int COLOR_START = ColorUtility.applyOpacity(0xFFFFFFFF, 0);   // прозрачное начало
    private static final int COLOR_END   = 0xFFFF0000;                                  // красный конец
    private static final int MAX_STEPS   = 160;                                        // максимум итераций
    private static final double MAX_DIST_SQ = 128 * 128;                               // ограничение по дистанции (мир)

    public ProjectilePredictModule() {
        super(Text.of("Projectile Predict"), null, Category.Render);
    }

    @Subscribe
    public void renderEvent(RenderEvent.AfterHand event) {
        if (!getEnabled() || mc.world == null || mc.player == null) return;

        MatrixStack ms = event.getStack();
        Vec3d cam = mc.gameRenderer.getCamera().getPos();

        ms.push();
        ms.translate(-cam.x, -cam.y, -cam.z);
        final var matrix = ms.peek().getPositionMatrix();

        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vc = immediate.getBuffer(VertexUtils.LINES);

        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof ProjectileEntity proj) || !e.isAlive()) continue;
            if (proj.squaredDistanceTo(cam) > MAX_DIST_SQ) continue;

            renderTrajectory(proj, vc, matrix);
        }

        // Полный flush, чтобы гарантированно вывести слой
        immediate.draw();
        ms.pop();
    }

    private void renderTrajectory(ProjectileEntity entity, VertexConsumer vc, org.joml.Matrix4f matrix) {
        // Локальные копии, чтобы не мутировать сущность
        Vec3d vel = entity.getVelocity();
        Vec3d pos = entity.getPos();

        if (vel.equals(Vec3d.ZERO)) return;

        final double gravity = entity.getFinalGravity();
        final double drag = entity.isTouchingWater() ? 0.8 : 0.99;

        // Будем рисовать отрезки prev -> curr, без промежуточного списка
        Vec3d prev = pos;
        int step = 0;

        while (step < MAX_STEPS) {
            Vec3d nextPos = pos.add(vel);

            BlockHitResult blockHit = mc.world.raycast(new RaycastContext(
                    pos, nextPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    entity
            ));

            boolean hit = blockHit.getType() != HitResult.Type.MISS;
            Vec3d curr = hit ? blockHit.getPos() : nextPos;

            // Градиент по шагам (0..1)
            float t = clamp01((float) step / (float) MAX_STEPS);
            int color = lerpArgb(t, COLOR_START, COLOR_END);

            // Вершина A
            vc.vertex(matrix, (float) prev.x, (float) prev.y, (float) prev.z)
                    .color(color)
                    .normal(0, 1, 0);
            // Вершина B
            vc.vertex(matrix, (float) curr.x, (float) curr.y, (float) curr.z)
                    .color(color)
                    .normal(0, 1, 0);

            if (hit) break;
            if (prev.squaredDistanceTo(curr) < 1.0e-6) break;         // защита от залипания
            if (entity.squaredDistanceTo(curr) > MAX_DIST_SQ) break;   // ограничение по дистанции

            // Следующий шаг интеграции
            pos = curr;
            vel = vel.multiply(drag).subtract(0, gravity, 0);

            prev = curr;
            step++;
        }
    }

// ——— Утилиты ———

    private static float clamp01(float v) {
        return v < 0f ? 0f : (v > 1f ? 1f : v);
    }

    // Лерп ARGB с учётом альфы (использует ColorHelper)
    private static int lerpArgb(float t, int a, int b) {
        // В новых версиях есть ColorHelper.Argb.lerp(t, a, b).
        // Делаем совместимо:
        int aa = (a >>> 24) & 0xFF;
        int ar = (a >>> 16) & 0xFF;
        int ag = (a >>> 8)  & 0xFF;
        int ab = (a)        & 0xFF;

        int ba = (b >>> 24) & 0xFF;
        int br = (b >>> 16) & 0xFF;
        int bg = (b >>> 8)  & 0xFF;
        int bb = (b)        & 0xFF;

        int ca = aa + Math.round((ba - aa) * t);
        int cr = ar + Math.round((br - ar) * t);
        int cg = ag + Math.round((bg - ag) * t);
        int cb = ab + Math.round((bb - ab) * t);

        return (ca << 24) | (cr << 16) | (cg << 8) | cb;
    }

}