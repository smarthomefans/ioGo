package de.nisnagel.iogo.data.io;

import com.google.gson.JsonDeserializer;

import java.util.Locale;

import timber.log.Timber;

public class IoName {

    private String language;
    private String name;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static JsonDeserializer<IoName> getDeserializer(){
        JsonDeserializer<IoName> deserializer = (json, typeOfT, context) -> {

            IoName ioName = new IoName();
            try {
                if (json.isJsonPrimitive()) {
                    ioName.setName(json.getAsString());
                } else if (json.isJsonObject()) {
                    String language = Locale.getDefault().getLanguage();
                    if(json.getAsJsonObject().has(language)){
                        ioName.setName(json.getAsJsonObject().get(language).getAsString());
                    }else if(json.getAsJsonObject().has("en")){
                        ioName.setName(json.getAsJsonObject().get("en").getAsString());
                    }else if(json.getAsJsonObject().has("de")){
                        ioName.setName(json.getAsJsonObject().get("de").getAsString());
                    }
                }
            } catch (Throwable t) {
                Timber.e(t);
            }

            return ioName;
        };

        return deserializer;
    }
}
