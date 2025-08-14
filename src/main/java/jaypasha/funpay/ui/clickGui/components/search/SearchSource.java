package jaypasha.funpay.ui.clickGui.components.search;

import jaypasha.funpay.ui.clickGui.ClickGuiScreen;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.lwjgl.glfw.GLFW;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchSource {

    String defaultText;
    Runnable runnable;

    StringBuilder text = new StringBuilder();

    @NonFinal
    boolean selected = false;

    // позиция каретки (символьная), всегда от 0..text.length()
    @NonFinal
    int caret = 0;

    public void toggle() {
        this.selected = !this.selected;
        if (this.selected) {
            moveCaretToEnd();
        }
    }

    public void focus(boolean value) {
        this.selected = value;
        if (value) moveCaretToEnd();
    }

    public void moveCaretToEnd() {
        this.caret = text.length();
    }

    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_F && ClickGuiScreen.hasControlDown()) {
            toggle();
            return;
        }

        if (!selected) return;

        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (text.length() > 0 && caret > 0) {
                text.deleteCharAt(caret - 1);
                caret--;
                runnable.run();
            }
            return;
        }

        if (key == GLFW.GLFW_KEY_DELETE) {
            if (caret < text.length()) {
                text.deleteCharAt(caret);
                runnable.run();
            }
            return;
        }

        if (key == GLFW.GLFW_KEY_LEFT) {
            if (caret > 0) caret--;
            return;
        }

        if (key == GLFW.GLFW_KEY_RIGHT) {
            if (caret < text.length()) caret++;
            return;
        }

        if (key == GLFW.GLFW_KEY_HOME) {
            caret = 0;
            return;
        }

        if (key == GLFW.GLFW_KEY_END) {
            moveCaretToEnd();
        }
    }

    public void charTyped(char chr) {
        if (!selected) return;
        if (Character.isISOControl(chr)) return;

        text.insert(caret, chr);
        caret++;
        runnable.run();
    }
}