package jaypasha.funpay.modules.impl.combat;

import com.google.common.eventbus.Subscribe; import jaypasha.funpay.Api; import jaypasha.funpay.api.events.impl.PlayerEvent; import jaypasha.funpay.api.events.impl.TickEvent; import jaypasha.funpay.modules.impl.combat.auraModule.AuraUtils; import jaypasha.funpay.modules.impl.combat.auraModule.FallDetector; import jaypasha.funpay.modules.impl.combat.auraModule.configs.AttackConfiguration; import jaypasha.funpay.modules.impl.combat.auraModule.configs.RotationConfiguration; import jaypasha.funpay.modules.impl.combat.auraModule.configs.TargetFinderConfiguration; import jaypasha.funpay.modules.impl.combat.auraModule.services.AttackService; import jaypasha.funpay.modules.impl.combat.auraModule.services.RotationService; import jaypasha.funpay.modules.impl.combat.auraModule.services.TargetFinderService; import jaypasha.funpay.modules.more.Category; import jaypasha.funpay.modules.more.ModuleLayer; import jaypasha.funpay.modules.settings.impl.BooleanSetting; import jaypasha.funpay.modules.settings.impl.ModeListSetting; import jaypasha.funpay.modules.settings.impl.ModeSetting; import jaypasha.funpay.modules.settings.impl.SliderSetting; import lombok.AccessLevel; import lombok.Getter; import lombok.experimental.FieldDefaults; import lombok.experimental.NonFinal; import net.minecraft.entity.Entity; import net.minecraft.entity.LivingEntity; import net.minecraft.item.ItemStack; import net.minecraft.item.Items; import net.minecraft.text.Text; import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap; import java.util.Map; import java.util.Optional;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) public class AttackAuraModule extends ModuleLayer implements Api {

    public static AttackAuraModule INSTANCE;

    // Настройки
    ModeListSetting targets;
    ModeSetting priority;
    ModeSetting rotationMode;
    SliderSetting fov;
    SliderSetting attackDistance;
    SliderSetting addedDistance;

    BooleanSetting onlyCriticals;
    BooleanSetting smartCriticals;
    BooleanSetting rayCasting;
    BooleanSetting attackThroughWall;
    BooleanSetting dontAttackIfUsing;
    BooleanSetting onlyWithWeapon;

    // Сервисы
    AttackService attackService;
    RotationService rotationService;
    TargetFinderService targetFinderService;
    FallDetector fallDetector;

    // Состояние
    @NonFinal Entity target = null;

    public AttackAuraModule() {
        super(Text.of("AttackAura"), Text.of("Автоматическая атака (Aura)"), Category.Combat);
        INSTANCE = this;

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

        onlyCriticals = new BooleanSetting(Text.of("Только Критами"), Text.empty(), () -> true).set(false).register(this);
        smartCriticals = new BooleanSetting(Text.of("Умные криты"), Text.empty(), () -> true).set(true).register(this);
        rayCasting = new BooleanSetting(Text.of("Проверка луча"), Text.empty(), () -> true).set(true).register(this);
        attackThroughWall = new BooleanSetting(Text.of("Бить через стены"), Text.empty(), () -> true).set(false).register(this);
        dontAttackIfUsing = new BooleanSetting(Text.of("Не бить при использовании"), Text.empty(), () -> true).set(true).register(this);
        onlyWithWeapon = new BooleanSetting(Text.of("Только с оружием"), Text.empty(), () -> true).set(true).register(this);

        fallDetector = new FallDetector();
        attackService = new AttackService();
        rotationService = new RotationService();
        targetFinderService = new TargetFinderService();

        setKey(GLFW.GLFW_KEY_R);
    }

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

    @Subscribe
    public void onTick(TickEvent e) {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            clearTargetAndRotation();
            return;
        }
        if (shouldPause()) {
            clearTargetAndRotation();
            return;
        }

        target = selectTarget().orElse(null);

        if (target == null) {
            rotationService.resetRotation();
        }
    }

    @Subscribe
    public void onPlayerMove(PlayerEvent.MovementEvent e) {
        if (!isEnabled() || mc.player == null || mc.world == null) return;
        if (target == null) return;

        TargetFinderConfiguration tfc = AuraUtils.generateTargetFindConfiguration(this);
        if (!tfc.isValid(target)) {
            target = null;
            rotationService.resetRotation();
            return;
        }
        if (!validateTargetBeforeAttack()) {
            // цель валидна для сервиса, но запрещена текущими “мягкими” условиями
            return;
        }

        RotationConfiguration rotCfg = AuraUtils.generateRotationConfiguration(this);
        AttackConfiguration atkCfg = AuraUtils.generateAttackConfiguration(this);

        rotationService.applyRotation(rotCfg);
        attackService.attack(atkCfg);
    }

    public static LivingEntity getCurrentTarget() {
        return (INSTANCE != null && INSTANCE.target instanceof LivingEntity le) ? le : null;
    }

    public Map<String, Boolean> getTargetsMap() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        targets.asStringList().forEach(name -> map.put(name, targets.get(name).getEnabled()));
        return map;
    }

// -------- Внутренняя логика --------

    private Optional<Entity> selectTarget() {
        TargetFinderConfiguration tfc = AuraUtils.generateTargetFindConfiguration(this);
        String pr = priority.getSelected();

        if ("На кого смотрю".equals(pr)) {
            return (Optional<Entity>) targetFinderService.lookingAt(tfc, e -> true);
        } else if ("Меньше HP".equals(pr)) {
            return (Optional<Entity>) targetFinderService.minByHealth(tfc, e -> true);
        } else {
            return (Optional<Entity>) targetFinderService.each(tfc, e -> true);
        }
    }

    private boolean shouldPause() {
        if (dontAttackIfUsing.getEnabled() && mc.player.isUsingItem()) return true;
        if (onlyWithWeapon.getEnabled() && !isHoldingWeapon()) return true;
        return false;
    }

    private boolean validateTargetBeforeAttack() {
        // Дополнительные «мягкие» фильтры поверх tfc.isValid(...).
        // Например, небольшой гистерезис по дистанции: разрешаем выход за дистанцию на буфер addedDistance.
        if (target == null) return false;

        double reach = attackDistance.getValue() + addedDistance.getValue();
        double distSq = mc.player.squaredDistanceTo(target);
        if (distSq > reach * reach) return false;

        // Если запрещено через стены, а rayCasting выключен — можно ранне остановить.
        if (!attackThroughWall.getEnabled() && !rayCasting.getEnabled() && !hasLineOfSightRough()) {
            return false;
        }
        return true;
    }

    private boolean isHoldingWeapon() {
        ItemStack main = mc.player.getMainHandStack();
        return main.isOf(Items.NETHERITE_SWORD) ||
                main.isOf(Items.DIAMOND_SWORD)   ||
                main.isOf(Items.IRON_SWORD)      ||
                main.isOf(Items.GOLDEN_SWORD)    ||
                main.isOf(Items.STONE_SWORD)     ||
                main.isOf(Items.WOODEN_SWORD)    ||
                main.isOf(Items.TRIDENT)         ||
                main.isOf(Items.NETHERITE_AXE)   ||
                main.isOf(Items.DIAMOND_AXE)     ||
                main.isOf(Items.IRON_AXE)        ||
                main.isOf(Items.GOLDEN_AXE)      ||
                main.isOf(Items.STONE_AXE)       ||
                main.isOf(Items.WOODEN_AXE);
    }

    private boolean hasLineOfSightRough() {
        // Лёгкая эвристика без трассировки (по флагам ванили),
        // полноценную проверку делай в TargetFinderConfiguration/RayCast сервисе
        return mc.player.canSee(target);
    }

    private void clearTargetAndRotation() {
        target = null;
        rotationService.resetRotation();
    }

}