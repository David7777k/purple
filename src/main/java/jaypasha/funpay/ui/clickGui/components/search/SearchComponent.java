package jaypasha.funpay.ui.clickGui.components.search;

import com.google.common.base.Suppliers;
import jaypasha.funpay.Api;
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
            () -> new SearchSource("Search...",  // placeholder
                    0xFFFFFFFF,                 // текст
                    ColorUtility.applyOpacity(0xFFFFFFFF, 30), // плейсхолдер
                    ColorUtility.applyOpacity(0xFF000000, 60), // фон
                    ColorUtility.applyOpacity(0xFFFFFFFF, 25)  // рамка/подсветка
            )
    );

    @Override
    public SearchComponent render(DrawContext context, int mouseX, int mouseY, float delta) {
        SearchSource src = searchSource.get();

        // фон
        Api.rectangle()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(6f))
                .color(new QuadColorState(src.bgColor()))
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // лёгкий контур/подсветка
        Api.border()
                .size(new SizeState(getWidth(), getHeight()))
                .radius(new QuadRadiusState(6f))
                .color(new QuadColorState(src.borderColor()))
                .thickness(1f)
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(), getX(), getY());

        // текст + каретка
        final boolean drawPlaceholder = src.getText().length() == 0 && !src.typing();
        final String caret = (src.typing() && (System.currentTimeMillis() % 1000 > 500)) ? "_" : "";
        final String draw = (drawPlaceholder ? src.placeholder() : src.getText().toString()) + caret;

        Scissors.push(getX() + 5, getY(), getWidth() - 10, getHeight());
        Api.text()
                .size(6.5f)
                .font(Api.inter())
                .text(draw)
                .color(drawPlaceholder ? src.placeholderColor() : src.textColor())
                .build()
                .render(context.getMatrices().peek().getPositionMatrix(),
                        getX() + 6, getY() + (getHeight() - Api.inter().getHeight("A", 6.5f)) / 2f);
        Scissors.pop();

        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        SearchSource src = searchSource.get();
        boolean inside = Math.isHover(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
        src.setFocused(inside);
        src.setTyping(inside);
        return inside || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        SearchSource src = searchSource.get();
        if (!src.focused()) return super.charTyped(chr, modifiers);

        // простая фильтрация управляющих символов
        if (chr >= 32 && chr != 127) {
            src.getText().append(chr);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        SearchSource src = searchSource.get();
        if (!src.focused()) return super.keyPressed(keyCode, scanCode, modifiers);

        // backspace
        if (keyCode == 259 /* GLFW_KEY_BACKSPACE */) {
            if (src.getText().length() > 0) {
                src.getText().deleteCharAt(src.getText().length() - 1);
                return true;
            }
        }
        // enter/escape — прекратить ввод
        if (keyCode == 257 /* ENTER */ || keyCode == 256 /* ESC */) {
            src.setTyping(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // источник поиска (синглтон)
    public static final class SearchSource {
        private final String placeholder;
        private final int textColor;
        private final int placeholderColor;
        private final int bgColor;
        private final int borderColor;

        private final StringBuilder text = new StringBuilder();
        private boolean focused = false;
        private boolean typing = false;

        public SearchSource(String placeholder, int textColor, int placeholderColor, int bgColor, int borderColor) {
            this.placeholder = placeholder;
            this.textColor = textColor;
            this.placeholderColor = placeholderColor;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
        }

        public StringBuilder getText() { return text; }
        public boolean focused() { return focused; }
        public boolean typing() { return typing; }
        public void setFocused(boolean f) { focused = f; }
        public void setTyping(boolean t) { typing = t; }

        public String placeholder() { return placeholder; }
        public int textColor() { return textColor; }
        public int placeholderColor() { return placeholderColor; }
        public int bgColor() { return bgColor; }
        public int borderColor() { return borderColor; }
    }
}
