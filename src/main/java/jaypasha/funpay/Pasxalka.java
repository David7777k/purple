package jaypasha.funpay;

import com.google.common.eventbus.EventBus;
import jaypasha.funpay.api.commands.CommandsListener;
import jaypasha.funpay.api.commands.CommandsRepository;
import jaypasha.funpay.api.configs.ConfigurationService;
import jaypasha.funpay.api.draggable.data.DraggableRepository;
import jaypasha.funpay.modules.more.ModuleRepository;
import jaypasha.funpay.ui.clickGui.ClickGuiScreen;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.Screen;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pasxalka implements ModInitializer, Api {

    @Getter
    static Pasxalka instance;

    EventBus eventBus = new EventBus();

    ModuleRepository moduleRepository;
    DraggableRepository draggableRepository;
    ConfigurationService configurationService;

    /** Click GUI screen — лениво создаётся при первом вызове геттера */
    ClickGuiScreen clickGuiScreen;

    CommandsListener commandsListener;

    public Pasxalka() {
        instance = this;

        createObjects();
        initObjects();

        try {
            eventBus.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createObjects() {
        this.moduleRepository = new ModuleRepository();
        this.draggableRepository = new DraggableRepository();
        this.configurationService = new ConfigurationService();
        this.commandsListener = new CommandsListener();
        // ClickGuiScreen не инициализируем тут жёстко — сделаем лениво в геттере,
        // чтобы избежать возможных проблем с порядком инициализации графики/ресурсов.
    }

    void initObjects() {
        try {
            this.moduleRepository.init();
            this.draggableRepository.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialize() {
        System.out.println("Client was successfully initialized");
        try {
            this.configurationService.save("bot");
            this.configurationService.load("bot");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает экран GUI. Ленивая инициализация — если объект ещё не создан,
     * создаём новый экземпляр. Синхронизировано на случай многопоточного доступа.
     */
    public synchronized Screen getModernClickGuiScreen() {
        if (this.clickGuiScreen == null) {
            try {
                this.clickGuiScreen = new ClickGuiScreen();
            } catch (Exception e) {
                e.printStackTrace();
                // В худшем случае возвращаем null — вызывающий код должен это обработать.
            }
        }
        return this.clickGuiScreen;
    }
}
