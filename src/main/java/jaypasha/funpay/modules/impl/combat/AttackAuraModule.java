package jaypasha.funpay.modules.impl.combat;

import jaypasha.funpay.Api;
import jaypasha.funpay.modules.more.ModuleLayer;
import jaypasha.funpay.modules.impl.combat.auraModule.AuraUtils;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration;
import jaypasha.funpay.modules.impl.combat.auraModule.services.AttackService;
import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService;
import jaypasha.funpay.modules.impl.combat.auraModule.services.TargetFinderService;
import jaypasha.funpay.modules.settings.impl.ModeListSetting;
import jaypasha.funpay.modules.settings.impl.ModeSetting;
import jaypasha.funpay.modules.settings.impl.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import jaypasha.funpay.modules.more.Category;

import java.util.LinkedHashMap;
import java.util.Map;

public class AttackAuraModule extends ModuleLayer implements Api {

    // Настройки
    private ModeListSetting targets;
    private ModeSetting priority;
    private ModeSetting rotationMode;
    private SliderSetting fov;
    private SliderSetting attackDistance;
    private SliderSetting addedDistance;
    private ToggleOptions options;

    // Сервисы и состояние
    private final TargetFinderService targetFinderService = new TargetFinderService();
    private final RotationService rotationService = RotationService.getInstance();
    private final AttackService attackService = new AttackService();
    private final FallDetector fallDetector = new FallDetector();

    private Entity target;

    public AttackAuraModule() {
        super(Text.of("AttackAura"), Category.Combat); // убедись, что enum значение — COMBAT

        // Цели
        targets = new ModeListSetting(text("Цели"), Text.empty(), () -> true)
                .set("Игроки", "Мобы", "Инвиз")
                .enable("Игроки")
                .register(this);

        // Приоритет
        priority = new ModeSetting(text("Приоритет"), Text.empty(), () -> true)
                .set("Ближайший", "Меньше HP", "На кого смотрю")
                .select("Ближайший")
                .register(this);

        // Тип наводки
        rotationMode = new ModeSetting(text("Тип наводки"), Text.empty(), () -> true)
                .set("Обычный", "Тестовый")
                .select("Обычный")
                .register(this);

        // Угол обзора (FOV)
        fov = new SliderSetting(text("Угол обзора"), Text.empty(), () -> true)
                .range(15.0f, 180.0f, 1.0f)
                .value(90.0f)
                .register(this);

        // Дистанция атаки
        attackDistance = new SliderSetting(text("Дистанция атаки"), Text.empty(), () -> true)
                .range(2.0f, 6.0f, 0.05f)
                .value(3.0f)
                .register(this);

        // Буфер дистанции
        addedDistance = new SliderSetting(text("Буфер дистанции"), Text.empty(), () -> true)
                .range(0.0f, 2.0f, 0.05f)
                .value(0.25f)
                .register(this);

        // Дополнительные опции
        options = new ToggleOptions()
                .add("Только Критами", false)
                .add("Умные криты", true)
                .add("Проверка луча", true)
                .add("Бить через стены", false)
                .add("Не бить при использовании", true)
                .add("Только с оружием", true);

        registerOptions(options);
    }

    private static Text text(String s) {
        return Text.of(s);
    }

    // override activate/deactivate (ModuleLayer вызывает их при toggleEnabled)
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

    // onTick — вызывается системой мода (не объявлен в ModuleLayer), поэтому без @Override
    public void onTick() {
        // используем публичный isEnabled() из ModuleLayer
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

        var rotCfg = AuraUtils.generateRotationConfiguration(this);
        rotationService.aimAt(target, rotCfg);

        tryAttack();

        fallDetector.onTick();
    }

    private void tryAttack() {
        AttackConfiguration cfg = AuraUtils.generateAttackConfiguration(this);
        if (!cfg.isValid()) return;

        // используем сервис атаки, чтобы соблюдать тайминги/рандом/события
        attackService.attack(cfg);
    }

    // Геттеры и вспомогательные методы
    public ModeListSetting getTargets() { return targets; }
    public ModeSetting getPriority() { return priority; }
    public ModeSetting getRotationMode() { return rotationMode; }
    public SliderSetting getFov() { return fov; }
    public SliderSetting getAttackDistance() { return attackDistance; }
    public SliderSetting getAddedDistance() { return addedDistance; }
    public ToggleOptions getOptions() { return options; }
    public Entity getTarget() { return target; }
    public FallDetector getFallDetector() { return fallDetector; }
    public RotationService getRotationService() { return rotationService; }

    // Регистрация опций — оставь под свою логику UI
    private void registerOptions(ToggleOptions opts) {
        // TODO: добавить регистрацию в GUI
    }

    // Вложенный класс — детектор падения (для умных критов)
    public static class FallDetector implements Api {
        private boolean falling;

        public void onTick() {
            if (mc.player != null) {
                falling = !mc.player.isOnGround() && mc.player.getVelocity().y < 0.0;
            } else {
                falling = false;
            }
        }

        public boolean isFalling() {
            return falling;
        }
    }

    // в конце списка геттеров
    // public RotationService getRotationService() { return rotationService; } // уже объявлен выше

    // Вложенный класс — набор переключателей
    public static class ToggleOptions {
        private final Map<String, Boolean> values = new LinkedHashMap<>();

        public ToggleOptions add(String name, boolean enabled) {
            values.put(name, enabled);
            return this;
        }

        public boolean isEnabled(String name) {
            return values.getOrDefault(name, false);
        }

        public void set(String name, boolean enabled) {
            values.put(name, enabled);
        }

        public Map<String, Boolean> all() {
            return values;
        }
    }
}
