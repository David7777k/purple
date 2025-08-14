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
    static Supplier<SearchSource> searchSource = Suppliers.memoize(
            () -> new SearchSource("Search...", Pasxalka.getInstance().getClickGuiScreen().getPanelsLayer()::initModules)
    );

    // внутреннее состояние для скролла
    @lombok.experimental.NonFinal
    float scrollX = 0f;

    @Override
    public SearchComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        var source = searchSource.get();
        boolean focused = source.isSelected();
        boolean hovered = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());

        // фон с блюром
        Api.blur()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(5))
                .blurRadius(16)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // подложка
        Api.rectangle()
                .radius(new QuadRadiusState(5))
                .color(new QuadColorState(ColorUtility.applyOpacity(0xFF000000, focused ? 40 : (hovered ? 30 : 20))))
                .size(new SizeState(getWidth(), getHeight()))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // рамка
        Api.border()
                .radius(new QuadRadiusState(5))
                .color(new QuadColorState(ColorUtility.applyOpacity(-1, focused ? 30 : (hovered ? 18 : 10))))
                .thickness(-.5f)
                .size(new SizeState(getWidth(), getHeight()))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // текст
        String fullText = source.getText().toString();
        boolean hasText = !fullText.isEmpty();
        String shown = focused || hasText ? fullText : source.getDefaultText();

        // горизонтальный скролл — держим каретку в видимой области
        float pad = 6f;
        float avail = getWidth() - pad * 2;
        float textW = Api.inter().getWidth(shown, 8);
        float caretX = Api.inter().getWidth(shown.substring(0, Math.min(source.getCaret(), shown.length())), 8);

        if (focused) {
            if (caretX - scrollX > avail) scrollX = caretX - avail;
            if (caretX - scrollX < 0) scrollX = caretX;
            scrollX = Math.max(0, Math.min(scrollX, Math.max(0, textW - avail)));
        } else {
            scrollX = 0;
        }

        // отрисовка текстовой области с ножницами
        Scissors.push(getX() + pad, getY(), avail, getHeight());
        Api.text()
                .color(ColorUtility.applyOpacity(-1, hasText ? 100 : 50))
                .font(Api.inter())
                .text(shown)
                .size(8)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + pad - scrollX,
                        getY() + (getHeight() - Api.inter().getHeight("A", 8)) / 2f);
        Scissors.pop();

        // каретка
        if (focused) {
            boolean blink = ((System.currentTimeMillis() / 500) % 2) == 0;
            if (blink) {
                float caretPx = getX() + pad - scrollX + caretX;
                Api.rectangle()
                        .size(new SizeState(1, Api.inter().getHeight("A", 8)))
                        .color(new QuadColorState(ColorUtility.applyOpacity(-1, 90)))
                        .build()
                        .render(context.getMatrices().peek().getPositionMatrix(),
                                caretPx,
                                getY() + (getHeight() - Api.inter().getHeight("A", 8)) / 2f);
            }
        }

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var source = searchSource.get();
        boolean inside = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
        if (inside) {
            source.focus(true);
            source.moveCaretToEnd();
            return true;
        } else if (source.isSelected()) {
            source.focus(false);
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
