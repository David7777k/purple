package jaypasha.funpay.utility.windows;

import jaypasha.funpay.api.animations.Direction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindowRepository {

    private final List<WindowLayer> windows = new ArrayList<>();

    private static final float MIN_SCALE = 0.05f;
    private static final float MIN_SIZE = 1.0f;
    private static final float EPS = 1e-4f;

    public List<WindowLayer> getWindowLayers() {
        return Collections.unmodifiableList(windows);
    }

    public boolean contains(WindowLayer w) {
        return windows.contains(w);
    }

    public void push(WindowLayer w) {
        if (w == null) return;
        windows.remove(w);
        windows.add(w);
    }

    public void pop(WindowLayer w) {
        if (w == null) return;
        windows.remove(w);
    }

    public void clear() {
        windows.clear();
    }

    public void close() {
        for (WindowLayer w : windows) {
            if (w.getAnimation() != null) {
                try { w.getAnimation().setDirection(Direction.BACKWARDS); }
                catch (Throwable ignored) {}
            }
        }
    }

    private static float getScale(WindowLayer w) {
        if (w.getAnimation() == null) return 1f;
        var out = w.getAnimation().getOutput();
        return out == null ? 1f : out.floatValue();
    }

    private static boolean isRenderable(WindowLayer w) {
        if (w == null) return false;
        float s = getScale(w);
        return w.getWidth() >= MIN_SIZE && w.getHeight() >= MIN_SIZE && s >= MIN_SCALE;
    }

    private static float cx(WindowLayer w) { return w.getX() + w.getWidth() / 2f; }
    private static float cy(WindowLayer w) { return w.getY() + w.getHeight() / 2f; }

    private static double inv(double coord, float pivot, float scale) {
        return pivot + (coord - pivot) / Math.max(scale, EPS);
    }

    private static boolean hit(double x, double y, float rx, float ry, float rw, float rh) {
        return x >= rx && y >= ry && x <= rx + rw && y <= ry + rh;
    }

    private static void withScale(MatrixStack ms, float px, float py, float s, Runnable r) {
        ms.push();
        ms.translate(px, py, 0);
        ms.scale(s, s, 1f);
        ms.translate(-px, -py, 0);
        try { r.run(); } finally { ms.pop(); }
    }

    private void gcClosed() {
        windows.removeIf(w -> {
            try {
                return w != null && w.getAnimation() != null && w.getAnimation().isFinished(Direction.BACKWARDS);
            } catch (Throwable t) {
                return false;
            }
        });
    }

    // ----- Render -----

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        gcClosed();
        if (windows.isEmpty()) return;

        MatrixStack ms = ctx.getMatrices();

        for (int i = 0; i < windows.size(); i++) {
            WindowLayer w = windows.get(i);
            if (!isRenderable(w)) continue;

            float s = getScale(w);
            float px = cx(w), py = cy(w);
            int adjX = (int) Math.round(inv(mouseX, px, s));
            int adjY = (int) Math.round(inv(mouseY, py, s));

            withScale(ms, px, py, s, () -> {
                try { w.render(ctx, adjX, adjY, delta); }
                catch (Throwable ignored) {}
            });
        }
    }

    // ----- Mouse -----

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        gcClosed();
        // Найдём верхнее окно под курсором
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowLayer w = windows.get(i);
            if (!isRenderable(w)) continue;

            float s = getScale(w);
            double x = inv(mouseX, cx(w), s);
            double y = inv(mouseY, cy(w), s);

            if (hit(x, y, w.getX(), w.getY(), w.getWidth(), w.getHeight())) {
                boolean consumed = false;
                try { consumed = w.mouseClicked(x, y, button); } catch (Throwable ignored) {}
                bringToFront(i);
                return consumed;
            }
        }
        // Клик вне всех окон — закрыть все
        clear();
        return false;
    }

    private void bringToFront(int i) {
        if (i < 0 || i >= windows.size()) return;
        WindowLayer w = windows.remove(i);
        windows.add(w);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        gcClosed();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowLayer w = windows.get(i);
            if (!isRenderable(w)) continue;

            float s = getScale(w);
            double x = inv(mouseX, cx(w), s);
            double y = inv(mouseY, cy(w), s);

            boolean consumed = false;
            try { consumed = w.mouseReleased(x, y, button); } catch (Throwable ignored) {}
            if (consumed) return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double h, double v) {
        gcClosed();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowLayer w = windows.get(i);
            if (!isRenderable(w)) continue;

            float s = getScale(w);
            double x = inv(mouseX, cx(w), s);
            double y = inv(mouseY, cy(w), s);
            if (!hit(x, y, w.getX(), w.getY(), w.getWidth(), w.getHeight())) continue;

            boolean consumed = false;
            try { consumed = w.mouseScrolled(x, y, h, v); } catch (Throwable ignored) {}
            if (consumed) return true;
        }
        return false;
    }

    // ----- Keyboard -----

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        gcClosed();
        // ESC — закрыть верхнее окно
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !windows.isEmpty()) {
            pop(windows.get(windows.size() - 1));
            return true;
        }
        return passKeyToWindows(keyCode, scanCode, modifiers);
    }

    private boolean passKeyToWindows(int keyCode, int scanCode, int modifiers) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowLayer w = windows.get(i);
            try {
                if (w.keyPressed(keyCode, scanCode, modifiers)) {
                    bringToFront(i);
                    return true;
                }
            } catch (Throwable ignored) {}
        }
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        gcClosed();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowLayer w = windows.get(i);
            try { if (w.keyReleased(keyCode, scanCode, modifiers)) return true; }
            catch (Throwable ignored) {}
        }
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        gcClosed();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowLayer w = windows.get(i);
            try { if (w.charTyped(chr, modifiers)) return true; }
            catch (Throwable ignored) {}
        }
        return false;
    }
}
