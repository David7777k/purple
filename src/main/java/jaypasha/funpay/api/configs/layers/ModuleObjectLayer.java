package jaypasha.funpay.api.configs.layers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jaypasha.funpay.Pasxalka;
import jaypasha.funpay.modules.more.ModuleLayer;

import java.io.Reader;

public class ModuleObjectLayer {

    public static JsonElement asElement(ModuleLayer module) {
        JsonObject moduleObject = new JsonObject();

        moduleObject.addProperty("Module-Name", module.getModuleName().getString());
        moduleObject.addProperty("Module-Enabled", module.getEnabled());
        moduleObject.addProperty("Module-Key", module.getKey());
        moduleObject.addProperty("Module-Toggle-Action", module.getAction());
        moduleObject.add("Module-Settings", SettingsObjectLayer.asElement(module.getSettingLayers()));

        return moduleObject;
    }

    public static void parseJson(Gson gson, Reader reader) {
        JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);

        if (!jsonElement.isJsonArray()) return;

        jsonElement.getAsJsonArray().asList().forEach(e -> {
            if (!e.isJsonObject()) return;

            JsonObject jsonObject = e.getAsJsonObject();

            if (!jsonObject.has("Module-Name")) return;

            ModuleLayer moduleLayer = Pasxalka.getInstance().getModuleRepository().filter(module -> module.getModuleName().getString().equalsIgnoreCase(jsonObject.get("Module-Name").getAsString())).getFirst();

            moduleLayer.setEnabled(jsonObject.get("Module-Enabled").getAsBoolean());
            moduleLayer.setKey(jsonObject.get("Module-Key").getAsInt());
            moduleLayer.setAction(jsonObject.get("Module-Toggle-Action").getAsInt());

            if (!jsonObject.has("Module-Settings")) return;

            jsonObject.getAsJsonArray("Module-Settings").asList().forEach(settingElement ->
                    SettingsObjectLayer.parseSetting(moduleLayer, settingElement));
        });
    }
}
