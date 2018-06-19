package de.nisio.iobroker.data.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = { Enum.class, EnumState.class, State.class}, version = 3, exportSchema = false)
@TypeConverters({ListConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract StateDao getStateDao();
    public abstract EnumDao getEnumDao();
    public abstract EnumStateDao getEnumStateDao();
}