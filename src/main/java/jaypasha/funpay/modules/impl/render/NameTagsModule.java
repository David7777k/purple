package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.api.events.impl.RenderEvent;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.BooleanSetting;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.MathProjection;
import jaypasha.funpay.utility.math.MathVector;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class NameTagsModule extends ModuleLayer {

    public NameTagsModule() {
        super(Text.of("Name Tags"), null, Category.Render);
    }

    BooleanSetting armor = new BooleanSetting(Text.of("Показывать броню"), null, () -> true)
            .register(this);

    @Subscribe
    public void renderLabelsReceived(RenderEvent.RenderLabelsEvent<? extends Entity, ? extends EntityRenderState> event) {
        if (!getEnabled()) return;

        event.cancel();
    }

    @Subscribe
    public void drawEvent(RenderEvent.AfterHud renderEvent) {
        if (!getEnabled()) return;

        DrawContext context = renderEvent.getContext();

        mc.player.getWorld().getPlayers().forEach(e -> {
            if (e.equals(mc.player)) return;

            if (e.getScoreboardTeam() != null && e.getScoreboardTeam().getPrefix() != null && !e.getScoreboardTeam().getPrefix().getString().isEmpty()) {
                renderNameTagWithPrefix(context, e);
            } else {
                renderWithoutPrefix(context, e);
            }

            if (armor.getEnabled()) {
                renderArmorList(context, e);
            }
        });
    }

    public void renderArmorList(DrawContext context, PlayerEntity entity) {
        float offset = 0f;

        Vec3d projected = MathProjection.projectCoordinates(MathVector.lerpPosition(entity).add(0, entity.getHeight() + 0.9, 0));;

        for (ItemStack stack : mc.player.getInventory().armor) {
            if (stack == null || stack.isEmpty()) return;

            context.drawItem(stack, (int) ((int) projected.x + offset - (mc.player.getInventory().armor.stream().filter(e -> e != null && !e.isEmpty()).count() * 7)), (int) projected.y);

            offset += 14;
        }
    }

    public void renderNameTagWithPrefix(DrawContext context, PlayerEntity e) {
        String playerName = e.getName().getString();
        String prefix = e.getScoreboardTeam().getPrefix().getString();

        int nameWidth = (int) Api.inter().getWidth(playerName, 6, 0.05f, 0);
        int prefixWidth = mc.textRenderer.getWidth(prefix) - 5;
        int totalWidth = prefixWidth + nameWidth + 15;

        Vec3d tagPosition = MathProjection.projectCoordinates(MathVector.lerpPosition(e).add(0, e.getHeight() + 0.5, 0));

        if (tagPosition.z <= 0 || tagPosition.z >= 1) return;

        Api.rectangle()
                .size(new SizeState(totalWidth, 16.5f))
                .radius(new QuadRadiusState(1))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 50)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), tagPosition.getX() - ((double) totalWidth / 2), tagPosition.getY() - 5f);

        context.drawText(mc.textRenderer, prefix, (int) (tagPosition.getX() - (double) totalWidth / 2) + 2, (int) tagPosition.getY(), -1, true);

        Api.text()
                .size(6)
                .color(0xFFFFFFFF)
                .text(playerName)
                .font(Api.inter())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), (int) (tagPosition.getX() + (double) (prefixWidth - nameWidth) / 2), tagPosition.y);
    }

    public void renderWithoutPrefix(DrawContext context, PlayerEntity e) {
        String playerName = e.getName().getString();

        int nameWidth = (int) Api.inter().getWidth(playerName, 6, 0.05f, 0);

        Vec3d tagPosition = MathProjection.projectCoordinates(MathVector.lerpPosition(e).add(0, e.getHeight() + 0.5, 0));

        if (tagPosition.z <= 0 || tagPosition.z >= 1) return;

        Api.rectangle()
                .size(new SizeState(nameWidth + 10, 16.5f))
                .radius(new QuadRadiusState(1))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 50)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), tagPosition.getX() - ((double) nameWidth / 2) - 5, tagPosition.getY() - 5f);

        Api.text()
                .size(6)
                .color(0xFFFFFFFF)
                .font(Api.inter())
                .text(playerName)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), tagPosition.getX() - ((double) nameWidth / 2), tagPosition.getY());
    }
}
