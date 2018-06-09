package com.example.nagel.io1.data.model;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.util.StringUtil;
import android.text.TextUtils;

import com.example.nagel.io1.data.io.IoState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        /*
        Map<String,String> map = new HashMap<>();

        if(value == null || !value.contains(",")) {
            return map;
        }
        else{
            String[] pairs = value.split(";");
            for (int i=0;i<pairs.length;i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split(":");
                map.put(keyValue[0], keyValue[1]);
            }
        }*/

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

       /* StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
        }
        return sb.toString();*/

    }
}