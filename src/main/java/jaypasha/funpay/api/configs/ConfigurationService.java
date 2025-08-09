package jaypasha.funpay.api.configs;

import java.util.List;

public class ConfigurationService implements Configuration{

    final ConfigurationController configurationController = new ConfigurationController();

    @Override
    public void save(String name) {
        configurationController.save(name + ".json");
    }

    @Override
    public void load(String name) {
        configurationController.load(name + ".json");
    }

    @Override
    public void remove(String name) {
        configurationController.remove(name);
    }

    @Override
    public List<String> asList() {
        return configurationController.asList();
    }
}
