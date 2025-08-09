package jaypasha.funpay.ui;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public final class NotificationManager implements HudRenderCallback, ClientTickEvents.EndTick {
    private static final NotificationManager INSTANCE = new NotificationManager();
    private final Deque<Notification> queue = new ArrayDeque<>();

    private NotificationManager() {}

    public static void init() {
        HudRenderCallback.EVENT.register(INSTANCE);
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE);
    }

    public static void post(Text title, Text message, Formatting color, long durationMs) {
        INSTANCE.queue.addLast(new Notification(title, message, color, durationMs));
    }

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;

        int screenWidth = mc.getWindow().getScaledWidth();
        int startY = 8;
        int maxVisible = 5;
        int i = 0;

        Iterator<Notification> it = queue.iterator();
        while (it.hasNext() && i < maxVisible) {
            Notification n = it.next();
            if (!n.isAlive()) {
                it.remove();
                continue;
            }
            drawNotification(ctx, mc, n, screenWidth, startY + i * 36);
            i++;
        }
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        queue.removeIf(n -> !n.isAlive());
    }

    private void drawNotification(DrawContext ctx, MinecraftClient mc, Notification n, int screenWidth, int y) {
        String title = n.title.getString();
        String message = n.message.getString();

        int titleWidth = mc.textRenderer.getWidth(title);
        int msgWidth = mc.textRenderer.getWidth(message);
        int w = Math.max(titleWidth, msgWidth) + 20 + 16 + 10;
        int h = 30;

        float in = easeOut(n.progressIn());
        float out = easeOut(n.progressOut());
        float alpha = clamp01(1f - out);
        int x = (int) (screenWidth - w - 10 + (1 - in + out) * 24);

        // Основные цвета
        int bgTop = argb((int)(alpha * 210), 26, 26, 28);
        int borderColor = applyAlpha(formattingColor(n.color), alpha);
        int textColor = applyAlpha(0xFFFFFFFF, alpha);
        int subTextColor = applyAlpha(0xFFCCCCCC, alpha);
        int progressBg = applyAlpha(0xFF505050, alpha * 0.5f);
        int progressFg = borderColor;

        // Иконка
        String icon = switch (n.color) {
            case GREEN -> "✔";
            case RED -> "✖";
            case YELLOW -> "⚠";
            default -> "ℹ";
        };

        // Основная карточка
        drawRoundedRect(ctx, x, y, w, h, 6, bgTop);
        ctx.fill(x, y, x + 4, y + h, borderColor); // цветной бордер слева

        // Текст и иконка
        ctx.drawTextWithShadow(mc.textRenderer, icon, x + 6, y + 8, borderColor);
        ctx.drawTextWithShadow(mc.textRenderer, title, x + 22, y + 6, textColor);
        ctx.drawTextWithShadow(mc.textRenderer, message, x + 22, y + 6 + 11, subTextColor);

        // Прогресс‑бар
        int px = x + 4;
        int py = y + h - 3;
        int pw = w - 8;
        int fillW = (int)(pw * n.lifeRatio());
        ctx.fill(px, py, px + pw, py + 2, progressBg);
        ctx.fill(px, py, px + fillW, py + 2, progressFg);
    }

    private void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        // центр
        ctx.fill(x + r, y, x + w - r, y + h, color);
        // боковые
        ctx.fill(x, y + r, x + r, y + h - r, color);
        ctx.fill(x + w - r, y + r, x + w, y + h - r, color);
        // углы (approximate)
        ctx.fill(x + 1, y + 1, x + r, y + r, color);
        ctx.fill(x + w - r, y + 1, x + w - 1, y + r, color);
        ctx.fill(x + 1, y + h - r, x + r, y + h - 1, color);
        ctx.fill(x + w - r, y + h - r, x + w - 1, y + h - 1, color);
    }

    private static float easeOut(float t) {
        t = clamp01(t);
        return 1f - (float)Math.pow(1f - t, 3);
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private static int formattingColor(Formatting fmt) {
        Integer rgb = fmt.getColorValue();
        return rgb != null ? (0xFF000000 | rgb) : 0xFFFFFFFF;
    }

    private static int applyAlpha(int argb, float alphaFactor) {
        int a = Math.max(0, Math.min(255, (int)(alphaFactor * ((argb >>> 24) & 0xFF))));
        return (a << 24) | (argb & 0xFFFFFF);
    }

    private static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private static final class Notification {
        final Text title;
        final Text message;
        final Formatting color;
        final long durationMs;
        final long createdAt;

        Notification(Text title, Text message, Formatting color, long durationMs) {
            this.title = title;
            this.message = message;
            this.color = color;
            this.durationMs = durationMs;
            this.createdAt = System.currentTimeMillis();
        }

        boolean isAlive() {
            return System.currentTimeMillis() < createdAt + durationMs + 300;
        }

        float progressIn() {
            return clamp01((System.currentTimeMillis() - createdAt) / 200f);
        }

        float progressOut() {
            long end = createdAt + durationMs;
            return clamp01((System.currentTimeMillis() - end) / 300f);
        }

        float lifeRatio() {
            long now = System.currentTimeMillis();
            long elapsed = Math.max(0, now - createdAt);
            return 1f - Math.min(1f, (float)elapsed / durationMs);
        }
    }
}
