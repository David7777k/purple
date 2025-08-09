package jaypasha.funpay.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.api.events.impl.PlayerEvent;
import jaypasha.funpay.api.events.impl.TickEvent;
import jaypasha.funpay.modules.impl.combat.auraModule.AuraUtils;
import jaypasha.funpay.modules.impl.combat.auraModule.FallDetector;
import jaypasha.funpay.modules.impl.combat.auraModule.Vector;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.services.AttackService;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import jaypasha.funpay.modules.impl.combat.auraModule.services.TargetFinderService;
import jaypasha.funpay.modules.more.Category;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import jaypasha.funpay.modules.settings.impl.ModeSetting;
import jaypasha.funpay.modules.settings.impl.SliderSetting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttackAuraModule extends ModuleLayer {

    SliderSetting attackDistance = new SliderSetting(Text.of("Дистанция Аттаки"), null, () -> true)
            .set(.1f, 6.0f, .1f)
            .register(this);

    SliderSetting addedDistance = new SliderSetting(Text.of("Добавочная дистанция"), null, () -> true)
            .set(0.0f, 6.0f, .1f)
            .register(this);

    ModeListSetting options = new ModeListSetting(Text.of("Настройки"), null, () -> true)
            .set("Только Критами", "Умные криты", "Проверка луча", "Бить через стены", "Не бить при использовании")
            .register(this);

    ModeSetting randomizationMode = new ModeSetting(Text.of("Рандомизация"), null, () -> true)
            .set("Плавная", "Резкая")
            .register(this);

    AttackService attackService;
    RotationService rotationService;
    TargetFinderService targetFinderService;

    FallDetector fallDetector;

    @NonFinal
    Entity target = null;

    public AttackAuraModule() {
        super(Text.of("Aura"), Text.of("Автоматически аттакует игроков"), Category.Combat);

        fallDetector = new FallDetector();
        attackService = new AttackService();
        rotationService = new RotationService();
        targetFinderService = new TargetFinderService();

        setKey(GLFW.GLFW_KEY_R);
    }

    @Subscribe
    public void tickEvent(TickEvent tickEvent) {
        TargetFinderConfiguration targetFinderConfiguration = AuraUtils.generateTargetFindConfiguration(this);

        if (target == null || !targetFinderConfiguration.isValid(target)) {
            target = null;
            target = targetFinderService.each(targetFinderConfiguration, e -> e.isAlive() && !e.isRemoved()).orElse(null);
        }

        if (!getEnabled() || target == null || !targetFinderConfiguration.isValidDistance(target)) {
            target = null;
            rotationService.resetRotation();
        }
    }

    @Subscribe
    public void moveEvent(PlayerEvent.MovementEvent movementTickEvent) {
        if (!getEnabled() || target == null) return;

        RotationConfiguration rotationConfiguration = AuraUtils.generateRotationConfiguration(this);
        AttackConfiguration attackConfiguration = AuraUtils.generateAttackConfiguration(this);

        if (AuraUtils.generateTargetFindConfiguration(this).isValid(target)) {
            rotationService.applyRotation(rotationConfiguration);
            attackService.attack(attackConfiguration);
        }
    }

    @Subscribe
    public void velocityEvent(PlayerEvent.VelocityEvent velocityEvent) {
        if (!getEnabled() || target == null) return;

        if (AuraUtils.generateTargetFindConfiguration(this).isValid(target)) {
            velocityEvent.setVelocity(fixVelocity(velocityEvent.getVelocity(), velocityEvent.getInput(), velocityEvent.getSpeed()));
        }
    }

    private Vec3d fixVelocity(Vec3d currVelocity, Vec3d movementInput, float speed) {
        Vector rotation = rotationService.getCurrentVector();
        float yaw = rotation.getYaw();
        double d = movementInput.lengthSquared();

        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        } else {
            Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);

            float f = MathHelper.sin(yaw * 0.017453292f);
            float g = MathHelper.cos(yaw * 0.017453292f);

            return new Vec3d(
                    vec3d.getX() * g - vec3d.getZ() * f,
                    vec3d.getY(),
                    vec3d.getZ() * g + vec3d.getX() * f
            );
        }
    }
}
