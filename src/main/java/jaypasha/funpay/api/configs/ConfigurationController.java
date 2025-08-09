package jaypasha.funpay.api.configs;

import com.google.gson.*;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.api.configs.layers.ModuleObjectLayer;

import java.io.*;
import java.util.List;

public class ConfigurationController implements Configuration {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(String name) {
        File file = new File(mc.runDirectory.getAbsolutePath() + "/pasxalka/configs/" + name);

        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        JsonArray mainArray = new JsonArray();
        JsonObject descriptionObject = new JsonObject();

        descriptionObject.addProperty("Author", "jaypasha - https://funpay.com/users/7571071/");
        mainArray.add(descriptionObject);

        Pasxalka.getInstance().getModuleRepository().forEach(module -> mainArray.add(ModuleObjectLayer.asElement(module)));

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(mainArray, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(String name) {
        File file = new File(mc.runDirectory.getAbsolutePath() + "/pasxalka/configs/" + name);

        try (Reader reader = new FileReader(file)) {
            ModuleObjectLayer.parseJson(gson, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(String name) {
        File file = new File(mc.runDirectory.getAbsolutePath() + "/pasxalka/configs/" + name);

        if (file.exists()) {
            file.deleteOnExit();
        }
    }

    @Override
    public List<String> asList() {
        return List.of();
    }
}
