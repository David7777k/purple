package jaypasha.funpay.modules.more;

/*
 * Create by puzatiy
 * At 03.06.2025
 */

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
import jaypasha.funpay.api.animations.Animation;
import jaypasha.funpay.api.animations.Direction;
import jaypasha.funpay.api.animations.implement.DecelerateAnimation;
import jaypasha.funpay.api.events.impl.KeyEvent;
import jaypasha.funpay.api.events.impl.ModuleEvent;
import jaypasha.funpay.modules.settings.SettingLayer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ModuleLayer implements Api {

    Text moduleName;
    Text moduleDescription;
    Category category;

    @Setter
    Integer key = GLFW.GLFW_KEY_UNKNOWN;
    Boolean enabled = false;

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

    public void toggleEnabled() {
        this.enabled = !this.enabled;
        this.animation.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Subscribe
    private void toggleEventListener(ModuleEvent.ToggleEvent toggleEvent) {
        if (toggleEvent.getModuleLayer().equals(this)) {
            toggleEnabled();
        }
    }

    @Subscribe
    private void keyEventListener(KeyEvent keyEvent) {
        if (keyEvent.getKey() == this.getKey() && keyEvent.getAction() == 1) {
            this.toggleEnabled();
        }
    }
}
