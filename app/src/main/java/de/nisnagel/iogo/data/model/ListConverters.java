package de.nisnagel.iogo.data.model;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class ListConverters {

    @TypeConverter
    public static Map<String,String> stringToMap(String value) {

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        return gson.fromJson(value, type);
    }

    @TypeConverter
    public static String mapToString(Map<String,String> map) {

        if(map == null || map.size() == 0){
            return "";
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(map);

    }
}