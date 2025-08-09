package jaypasha.funpay.ui.clickGui.components;

import jaypasha.funpay.Api;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BackgroundComponent extends Component {

    Category category;

    Animation animation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    @Override
    public BackgroundComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight()))
            animation.setDirection(Direction.FORWARDS);
        else animation.setDirection(Direction.BACKWARDS);

        Api.border()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(25f / 2))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFFFFFFFF, 25)))
                .thickness(-0.1f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.blur()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(25f / 2))
                .blurRadius(24f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(25f / 2))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 90)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.text()
                .size(10)
                .font(Api.inter())
                .text(category.name())
                .color(0xFFFFFFFF)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 10 + (2.5f * animation.getOutput().floatValue()), getY() + 10);

        return null;
    }
}
