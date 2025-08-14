package jaypasha.funpay.modules.impl.render;

import com.google.common.eventbus.Subscribe; import jaypasha.funpay.Api; import jaypasha.funpay.api.events.impl.RenderEvent; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.modules.settings.impl.BooleanSetting; import jaypasha.funpay.utility.color.ColorUtility; import jaypasha.funpay.utility.math.MathProjection; import jaypasha.funpay.utility.math.MathVector; import jaypasha.funpay.utility.render.builders.states.QuadColorState; import jaypasha.funpay.utility.render.builders.states.QuadRadiusState; import jaypasha.funpay.utility.render.builders.states.SizeState; import net.minecraft.client.gui.DrawContext; import net.minecraft.client.render.entity.state.EntityRenderState; import net.minecraft.entity.Entity; import net.minecraft.entity.player.PlayerEntity; import net.minecraft.item.ItemStack; import net.minecraft.scoreboard.Team; import net.minecraft.text.Text; import net.minecraft.util.math.Vec3d; import org.joml.Matrix4f;

public class NameTagsModule extends ModuleLayer {

    private final BooleanSetting armor = new BooleanSetting(Text.of("Показывать броню"), null, () -> true)
            .register(this);

    private static final int TEXT_SIZE = 6;
    private static final float LETTER_SPACING = 0.05f;
    private static final float BG_HEIGHT = 16.5f;
    private static final float BG_RADIUS = 2f;
    private static final int BG_COLOR = ColorUtility.applyOpacity(0xFF000000, 50);
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PADDING_X = 5;
    private static final int GAP_PREFIX_NAME = 6;
    private static final double TAG_Y_OFFSET = 0.5;
    private static final double ARMOR_Y_OFFSET = 0.9;
    private static final int ARMOR_SLOT_SIZE = 14;

    public NameTagsModule() {
        super(Text.of("Name Tags"), null, Category.Render);
    }

    @Subscribe
    public void renderLabelsReceived(RenderEvent.RenderLabelsEvent<? extends Entity, ? extends EntityRenderState> event) {
        if (!getEnabled()) return;
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity) {
            event.cancel();
        }
    }

    @Subscribe
    public void drawEvent(RenderEvent.AfterHud event) {
        if (!getEnabled() || mc.player == null || mc.world == null) return;

        DrawContext ctx = event.getContext();
        Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();

        for (PlayerEntity e : mc.world.getPlayers()) {
            if (e.equals(mc.player)) continue;

            Team team = e.getScoreboardTeam();
            if (team != null && team.getPrefix() != null && !team.getPrefix().getString().isEmpty()) {
                renderNameTagWithPrefix(ctx, matrix, e, team);
            } else {
                renderWithoutPrefix(ctx, matrix, e);
            }

            if (armor.getEnabled()) {
                renderArmorList(ctx, matrix, e);
            }
        }
    }

    private void renderArmorList(DrawContext ctx, Matrix4f matrix, PlayerEntity entity) {
        Vec3d projected = MathProjection.projectCoordinates(
                MathVector.lerpPosition(entity).add(0, entity.getHeight() + ARMOR_Y_OFFSET, 0)
        );
        if (projected.z <= 0 || projected.z >= 1) return;

        int nonEmpty = Math.toIntExact(
                entity.getInventory().armor.stream().filter(s -> s != null && !s.isEmpty()).count()
        );
        if (nonEmpty == 0) return;

        float totalWidth = nonEmpty * ARMOR_SLOT_SIZE;
        float startX = (float) projected.x - totalWidth / 2f;
        float y = (float) projected.y;

        float x = startX;
        for (ItemStack stack : entity.getInventory().armor) {
            if (stack == null || stack.isEmpty()) continue;
            ctx.drawItem(stack, Math.round(x), Math.round(y));
            x += ARMOR_SLOT_SIZE;
        }
    }

    private void renderNameTagWithPrefix(DrawContext ctx, Matrix4f matrix, PlayerEntity e, Team team) {
        String playerName = e.getName().getString();
        String prefix = team.getPrefix().getString();

        int nameWidth = (int) Api.inter().getWidth(playerName, TEXT_SIZE, LETTER_SPACING, 0);
        int prefixWidth = mc.textRenderer.getWidth(prefix);
        int totalWidth = PADDING_X + prefixWidth + GAP_PREFIX_NAME + nameWidth + PADDING_X;

        Vec3d tagPos = MathProjection.projectCoordinates(
                MathVector.lerpPosition(e).add(0, e.getHeight() + TAG_Y_OFFSET, 0)
        );
        if (tagPos.z <= 0 || tagPos.z >= 1) return;

        float bgX = (float) tagPos.x - totalWidth / 2f;
        float bgY = (float) tagPos.y - BG_HEIGHT / 2f;

        Api.rectangle()
                .size(new SizeState(totalWidth, BG_HEIGHT))
                .radius(new QuadRadiusState(BG_RADIUS))
                .color(new QuadColorState(BG_COLOR))
                .build()
                .render(matrix, bgX, bgY);

        int prefixX = Math.round(bgX + PADDING_X);
        int textBaseY = Math.round((float) tagPos.y - mc.textRenderer.fontHeight / 2f);
        ctx.drawText(mc.textRenderer, prefix, prefixX, textBaseY, 0xFFFFFFFF, true);

        float nameX = bgX + PADDING_X + prefixWidth + GAP_PREFIX_NAME;
        Api.text()
                .size(TEXT_SIZE)
                .color(TEXT_COLOR)
                .text(playerName)
                .font(Api.inter())
                .build()
                .render(matrix, nameX, (float) tagPos.y - TEXT_SIZE / 2f);
    }

    private void renderWithoutPrefix(DrawContext ctx, Matrix4f matrix, PlayerEntity e) {
        String playerName = e.getName().getString();
        int nameWidth = (int) Api.inter().getWidth(playerName, TEXT_SIZE, LETTER_SPACING, 0);
        int totalWidth = PADDING_X + nameWidth + PADDING_X;

        Vec3d tagPos = MathProjection.projectCoordinates(
                MathVector.lerpPosition(e).add(0, e.getHeight() + TAG_Y_OFFSET, 0)
        );
        if (tagPos.z <= 0 || tagPos.z >= 1) return;

        float bgX = (float) tagPos.x - totalWidth / 2f;
        float bgY = (float) tagPos.y - BG_HEIGHT / 2f;

        Api.rectangle()
                .size(new SizeState(totalWidth, BG_HEIGHT))
                .radius(new QuadRadiusState(BG_RADIUS))
                .color(new QuadColorState(BG_COLOR))
                .build()
                .render(matrix, bgX, bgY);

        float nameX = (float) tagPos.x - nameWidth / 2f;
        Api.text()
                .size(TEXT_SIZE)
                .color(TEXT_COLOR)
                .font(Api.inter())
                .text(playerName)
                .build()
                .render(matrix, nameX, (float) tagPos.y - TEXT_SIZE / 2f);
    }

}