package jaypasha.funpay.modules.impl.movement;

import com.google.common.eventbus.Subscribe; import jaypasha.funpay.api.events.impl.TickEvent; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import net.minecraft.client.option.KeyBinding; import net.minecraft.entity.player.HungerManager; import net.minecraft.text.Text;

public class SprintModule extends ModuleLayer {

    public SprintModule() {
        super(Text.of("Sprint"), null, Category.Movement);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!getEnabled() || mc.player == null || mc.world == null) return;

        KeyBinding forward = mc.options.forwardKey;
        boolean forwardHeld = forward.isPressed(); // корректнее, чем isKeyPressed(handle, code)

        boolean sneaking = mc.player.isSneaking();
        boolean usingItem = mc.player.isUsingItem();
        boolean strafing = mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed();
        boolean movingBackward = mc.options.backKey.isPressed();

        // Энергия/еда: не форсим спринт, если не можем
        HungerManager hunger = mc.player.getHungerManager();
        boolean canSprint = mc.player.isSubmergedInWater() || // в воде логика иная, но оставим возможность
                (hunger.getFoodLevel() > 6 || mc.player.getAbilities().allowFlying);

        // Классическое поведение: только при удержании "вперёд", без шифта и использования предметов
        boolean shouldSprint = forwardHeld && !sneaking && !usingItem && canSprint && !movingBackward;

        // При желании можно разрешить спринт при диагональном движении:
        // shouldSprint = (forwardHeld || strafing) && ...

        mc.player.setSprinting(shouldSprint);
    }

}