package com.example.nagel.io1.data.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = { State.class, com.example.nagel.io1.data.model.Room.class, Function.class}, version = 1)
@TypeConverters({TimeConverters.class, ListConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "appDatabase.db";
    private static volatile AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME).build();
    }

    public abstract StateDao getStateDao();
    public abstract RoomDao getRoomDao();
    public abstract FunctionDao getFunctionDao();
}