package com.example.nagel.io1.service.model;

import android.arch.persistence.room.TypeConverter;

import java.sql.Timestamp;

public class TimeConverters {
    @TypeConverter
    public static Timestamp fromTimestamp(Long value) {
        return value == null ? null : new Timestamp(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Timestamp date) {
        return date == null ? null : date.getTime();
    }
}