package com.example.nagel.io1.data.model;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}