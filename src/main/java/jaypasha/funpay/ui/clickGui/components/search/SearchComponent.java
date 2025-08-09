package jaypasha.funpay.ui.clickGui.components.search;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.ui.clickGui.Component;
import jaypasha.funpay.utility.color.ColorUtility;
import jaypasha.funpay.utility.math.Math;
import jaypasha.funpay.utility.render.builders.states.QuadColorState;
import jaypasha.funpay.utility.render.builders.states.QuadRadiusState;
import jaypasha.funpay.utility.render.builders.states.SizeState;
import jaypasha.funpay.utility.render.utility.Scissors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchComponent extends Component {

    @Getter
    static Supplier<SearchSource> searchSource = Suppliers.memoize(() -> new SearchSource("Search...",Pasxalka.getInstance().getClickGuiScreen().getPanelsLayer()::initModules));

    @Override
    public SearchComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        Api.blur()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(5))
                .blurRadius(16)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.rectangle()
                .radius(new QuadRadiusState(5))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, searchSource.get().isSelected() ? 40 : 20)))
                .size(new SizeState(getWidth(), getHeight()))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        Api.border()
                .radius(new QuadRadiusState(5))
                .color(new QuadColorState(ColorUtility.applyOpacity(-1, searchSource.get().isSelected() ? 30 : 10)))
                .thickness(-.5f)
                .size(new SizeState(getWidth(), getHeight()))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        String text = searchSource.get().isSelected() && !searchSource.get().getText().isEmpty() ? searchSource.get().getText().toString() : searchSource.get().getDefaultText();

        Scissors.push(getX(), getY(), getWidth(), getHeight());
        Api.text()
                .color(-1)
                .font(Api.inter())
                .text(text)
                .size(8)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX() + 5, getY() + getHeight() / 4);
        Scissors.pop();

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            searchSource.get().toggle();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        searchSource.get().keyPressed(keyCode);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        searchSource.get().charTyped(chr);

        return super.charTyped(chr, modifiers);
    }
}
