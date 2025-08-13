package jaypasha.funpay.modules.more;

import com.google.common.annotations.Beta;
import jaypasha.funpay.Api;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.modules.settings.SettingLayer;
import jaypasha.funpay.ui.NotificationManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public class ModuleLayer implements Api {

    Text moduleName;
    Text moduleDescription;
    Category category;

    @Setter
    Integer key = GLFW.GLFW_KEY_UNKNOWN;

    @Setter
    Integer action = 0;

    // protected чтобы подклассы могли использовать поле напрямую
    protected Boolean enabled = false;
    protected Boolean binding = false;

    List<SettingLayer> settingLayers = new ArrayList<>();

    Animation animation = new DecelerateAnimation()
            .setMs(250)
            .setValue(1);

    public ModuleLayer(Text moduleName, Text moduleDescription, Category category) {
        this.moduleName = moduleName;
        this.moduleDescription = moduleDescription;
        this.category = category;
        this.animation.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public ModuleLayer(Text moduleName, Category category) {
        this.moduleName = moduleName;
        this.moduleDescription = Text.of("Description missing.");
        this.category = category;
        this.animation.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    // Публичный геттер состояния
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    // Сеттер с сохранением прежней логики toggle
    public void setEnabled(Boolean enabled) {
        if (enabled != this.enabled)
            toggleEnabled();
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
        this.animation.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);

        boolean beta = this.getClass().isAnnotationPresent(Beta.class);
        Formatting color = enabled ? Formatting.GREEN : Formatting.RED;

        Text title = Text.of((beta ? "(beta) " : "") + moduleName.getString());
        Text message = Text.of(enabled ? "модуль включён" : "модуль выключен");

        NotificationManager.post(title, message, color, 2500); // время в миллисекундах

        if (enabled) activate();
        else deactivate();
    }

    // Публичный сеттер binding — раньше был пустым, теперь работает.
    public void setBinding(boolean b) {
        this.binding = b;
    }

    public boolean getBinding() {
        return binding != null && binding;
    }

    public List<SettingLayer> filter(Predicate<SettingLayer> predicate) {
        return settingLayers.stream().filter(predicate).toList();
    }

    public void forEach(Consumer<SettingLayer> action) {
        settingLayers.forEach(action);
    }

    // Hooks — переопределяй в дочерних классах
    public void activate() {
    }

    public void deactivate() {
    }
}
