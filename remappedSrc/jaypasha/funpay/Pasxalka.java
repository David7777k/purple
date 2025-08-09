package jaypasha.funpay;

import com.google.common.eventbus.EventBus;
import jaypasha.funpay.api.draggable.data.DraggableRepository;
import jaypasha.funpay.modules.more.ModuleRepository;
import jaypasha.funpay.ui.clickGui.ClickGuiScreen;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.fabricmc.api.ModInitializer;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pasxalka implements ModInitializer, Api {

    @Getter
    static Pasxalka instance;

    EventBus eventBus = new EventBus();

    ModuleRepository moduleRepository;
    DraggableRepository draggableRepository;
    ClickGuiScreen clickGuiScreen;

    public Pasxalka() {
        instance = this;

        createObjects();
        initObjects();

        eventBus.register(this);
    }

    void createObjects() {
        this.moduleRepository = new ModuleRepository();
        this.draggableRepository = new DraggableRepository();
        this.clickGuiScreen = new ClickGuiScreen();
    }

    void initObjects() {
        this.moduleRepository.init();
    }

    @Override
    public void onInitialize() {
        System.out.println("Client was successfully initialized");
    }
}
