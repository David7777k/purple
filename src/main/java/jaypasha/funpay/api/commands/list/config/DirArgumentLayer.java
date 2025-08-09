package jaypasha.funpay.api.commands.list.config;

import jaypasha.funpay.api.commands.ArgumentLayer;

import java.io.IOException;
import java.util.List;

public class DirArgumentLayer extends ArgumentLayer {

    public DirArgumentLayer() {
        super("dir", 0);
    }

    @Override
    public void execute(List<String> arguments) {
        try {
            Runtime.getRuntime().exec("explorer " + mc.runDirectory.getAbsolutePath() + "\\pasxalka\\configs");
            print("Папка открыта.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
