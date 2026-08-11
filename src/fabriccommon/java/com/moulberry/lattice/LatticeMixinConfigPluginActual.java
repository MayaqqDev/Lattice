package com.moulberry.lattice;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.msrandom.multiplatform.annotations.Actual;

import java.io.InputStream;
import java.io.InputStreamReader;

public class LatticeMixinConfigPluginActual {
    @Actual
    public static String getVersion() {
        try {
            try (InputStream inputStream = LatticeMixinConfigPlugin.class.getResourceAsStream("/version.json")) {
                if (inputStream == null) {
                    throw new RuntimeException("/version.json is not present!");
                }
                try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                    JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
                    return jsonObject.get("id").getAsString();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse Minecraft version information", e);
        }
    }
}
