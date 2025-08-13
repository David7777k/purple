package jaypasha.funpay.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import jaypasha.funpay.Api;
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
import jaypasha.funpay.modules.settings.impl.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * AttackAuraModule — теперь использует реальные настройки, регистрируемые в GUI.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttackAuraModule extends ModuleLayer implements Api {

    // Статический инстанс для доступа извне (например, TargetHudRenderer)
    public static AttackAuraModule INSTANCE;

    // Клиент
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // Настройки
    ModeListSetting targets;
    ModeSetting priority;
    ModeSetting rotationMode;
    SliderSetting fov;
    SliderSetting attackDistance;
    SliderSetting addedDistance;

    // Булевы опции (можно менять в GUI)
    BooleanSetting onlyCriticals;
    BooleanSetting smartCriticals;
    BooleanSetting rayCasting;
    BooleanSetting attackThroughWall;
    BooleanSetting dontAttackIfUsing;
    BooleanSetting onlyWithWeapon;

    // Сервисы и состояние
    AttackService attackService;
    RotationService rotationService;
    TargetFinderService targetFinderService;
    FallDetector fallDetector;

    @NonFinal
    Entity target = null;

    public AttackAuraModule() {
        super(Text.of("AttackAura"), Text.of("Автоматическая атака (Aura)"), Category.Combat);

        // Устанавливаем инстанс
        INSTANCE = this;

        // Создаем настройки и регистрируем
        targets = new ModeListSetting(Text.of("Цели"), Text.empty(), () -> true)
                .set("Игроки", "Мобы", "Инвиз")
                .enable("Игроки")
                .register(this);

        priority = new ModeSetting(Text.of("Приоритет"), Text.empty(), () -> true)
                .set("Ближайший", "Меньше HP", "На кого смотрю")
                .select("Ближайший")
                .register(this);

        rotationMode = new ModeSetting(Text.of("Тип наводки"), Text.empty(), () -> true)
                .set("Обычный", "Тестовый")
                .select("Обычный")
                .register(this);

        fov = new SliderSetting(Text.of("Угол обзора"), Text.empty(), () -> true)
                .range(15.0f, 180.0f, 1.0f)
                .value(90.0f)
                .register(this);

        attackDistance = new SliderSetting(Text.of("Дистанция атаки"), Text.empty(), () -> true)
                .range(2.0f, 6.0f, 0.05f)
                .value(3.0f)
                .register(this);

        addedDistance = new SliderSetting(Text.of("Буфер дистанции"), Text.empty(), () -> true)
                .range(0.0f, 2.0f, 0.05f)
                .value(0.25f)
                .register(this);

        // Булевые опции — теперь можно включать/выключать в GUI
        onlyCriticals = new BooleanSetting(Text.of("Только Критами"), Text.empty(), () -> true)
                .set(false)
                .register(this);

        smartCriticals = new BooleanSetting(Text.of("Умные криты"), Text.empty(), () -> true)
                .set(true)
                .register(this);

        rayCasting = new BooleanSetting(Text.of("Проверка луча"), Text.empty(), () -> true)
                .set(true)
                .register(this);

        attackThroughWall = new BooleanSetting(Text.of("Бить через стены"), Text.empty(), () -> true)
                .set(false)
                .register(this);

        dontAttackIfUsing = new BooleanSetting(Text.of("Не бить при использовании"), Text.empty(), () -> true)
                .set(true)
                .register(this);

        onlyWithWeapon = new BooleanSetting(Text.of("Только с оружием"), Text.empty(), () -> true)
                .set(true)
                .register(this);

        // Сервисы
        fallDetector = new FallDetector();
        attackService = new AttackService();
        rotationService = new RotationService();
        targetFinderService = new TargetFinderService();

        // Горячая клавиша (можешь изменить)
        setKey(GLFW.GLFW_KEY_R);
    }

    // Переопределяем activate/deactivate, ModuleLayer будет вызывать их
    @Override
    public void activate() {
        target = null;
        rotationService.resetRotation();
    }

    @Override
    public void deactivate() {
        target = null;
        rotationService.resetRotation();
    }

    // Тик — подписан в EventBus; если у тебя другой механизм, используй @Subscribe
    @Subscribe
    public void tickEvent(TickEvent tickEvent) {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            target = null;
            rotationService.resetRotation();
            return;
        }

        TargetFinderConfiguration tfc = AuraUtils.generateTargetFindConfiguration(this);

        String pr = priority.getSelected();
        if ("На кого смотрю".equals(pr)) {
            target = targetFinderService.lookingAt(tfc, e -> true).orElse(null);
        } else if ("Меньше HP".equals(pr)) {
            target = targetFinderService.minByHealth(tfc, e -> true).orElse(null);
        } else {
            target = targetFinderService.each(tfc, e -> true).orElse(null);
        }

        if (target == null) {
            rotationService.resetRotation();
            return;
        }
    }

    @Subscribe
    public void moveEvent(PlayerEvent.MovementEvent movementTickEvent) {
        if (!isEnabled() || target == null) return;

        TargetFinderConfiguration tfc = AuraUtils.generateTargetFindConfiguration(this);
        if (!tfc.isValid(target)) return;

        RotationConfiguration rotCfg = AuraUtils.generateRotationConfiguration(this);
        AttackConfiguration atkCfg = AuraUtils.generateAttackConfiguration(this);

        rotationService.applyRotation(rotCfg); // applyRotation uses module's rotation service implementation
        attackService.attack(atkCfg);

        // также можно корректировать скорость/velocity, если нужно
    }

    public static LivingEntity getCurrentTarget() {
        if (INSTANCE != null && INSTANCE.target instanceof LivingEntity) {
            return (LivingEntity) INSTANCE.target;
        }
        return null;
    }

    // Геттеры для доступа извне (если нужны)
    public ModeListSetting getTargets() { return targets; }
    public ModeSetting getPriority() { return priority; }
    public ModeSetting getRotationMode() { return rotationMode; }
    public SliderSetting getFov() { return fov; }
    public SliderSetting getAttackDistance() { return attackDistance; }
    public SliderSetting getAddedDistance() { return addedDistance; }
    public BooleanSetting getOnlyCriticals() { return onlyCriticals; }
    public BooleanSetting getSmartCriticals() { return smartCriticals; }
    public BooleanSetting getRayCasting() { return rayCasting; }
    public BooleanSetting getAttackThroughWall() { return attackThroughWall; }
    public BooleanSetting getDontAttackIfUsing() { return dontAttackIfUsing; }
    public BooleanSetting getOnlyWithWeapon() { return onlyWithWeapon; }

    public RotationService getRotationService() { return rotationService; }
    public FallDetector getFallDetector() { return fallDetector; }

    // Вспомогательный метод (если нужно) — разбор выбранных целей в виде карты
    public java.util.Map<String, Boolean> getTargetsMap() {
        java.util.Map<String, Boolean> map = new java.util.LinkedHashMap<>();
        targets.asStringList().forEach(name -> map.put(name, targets.get(name).getEnabled()));
        return map;
    }
}
