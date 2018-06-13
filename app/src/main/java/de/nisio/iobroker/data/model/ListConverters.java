package de.nisio.iobroker.data.model;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ListConverters {

    @TypeConverter
    public static List<String> stringToList(String value) {
        List<String> list = new ArrayList();
        if(value == null) return list;
        else list = Arrays.asList(value.split("#"));
        return list;
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        return list == null ? null : TextUtils.join("#", list);
    }

    @TypeConverter
    public static Map<String,String> stringToMap(String value) {


        Type type = new TypeToken<Map<String, String>>(){}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Map<String, String> map = gson.fromJson(value, type);

        return map;
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