package jaypasha.funpay.ui.clickGui.components;

import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
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
import jaypasha.funpay.utility.render.utility.Scissors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.join;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BackgroundComponent extends Component {

    Category category;

    final Animation animation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    @Override
    public BackgroundComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        animation.setDirection(
                Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                        ? Direction.FORWARDS
                        : Direction.BACKWARDS
        );

        Api.blur()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(7.5f))
                .blurRadius(24f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.shadow()
                .radius(new QuadRadiusState(7.5f))
                .softness(1)
                .shadow(12)
                .size(new SizeState(getWidth() + 1, getHeight() + 1))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 35)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() - 0.5f, getY() - 0.5f);

        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(7.5f))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 65)))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.text()
                .size(10)
                .font(Api.inter())
                .text(category.name())
                .color(0xFFFFFFFF)
                .build()
                .render(
                        context.getMatrices().peek().getPositionMatrix(),
                        getX() + 10 + (2.5f * animation.getOutput().floatValue()),
                        getY() + 10
                );

        Api.rectangle()
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, 25)))
                .size(new SizeState(getWidth(), 15))
                .radius(new QuadRadiusState(2, 7.5f, 7.5f, 2))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY() + getHeight() - 15);

        List<String> modulesList = Pasxalka.getInstance().getModuleRepository()
                .getModuleLayers().stream()
                .filter(e -> e.getCategory().equals(category))
                .limit(3)
                .map(e -> e.getModuleName().getString())
                .toList();

        if (!modulesList.isEmpty()) {
            String modulesString = join(modulesList, ", ");

            Scissors.push(getX(), getY() + getHeight() - 15, getWidth(), 15);
            Api.text()
                    .color(ColorUtility.applyOpacity(0xFFFFFFFF, 50))
                    .size(7)
                    .text(modulesString)
                    .font(Api.inter())
                    .build()
                    .render(
                            context.getMatrices().peek().getPositionMatrix(),
                            getX() + 5,
                            getY() + getHeight() - 15 + Api.inter().getHeight(modulesString, 7) / 2
                    );
            Scissors.pop();
        }

        return this;
    }
}
