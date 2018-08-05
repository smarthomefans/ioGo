package de.nisnagel.iogo.data.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = { Enum.class, EnumState.class, State.class}, version = 7, exportSchema = false)
@TypeConverters({ListConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract StateDao getStateDao();
    public abstract EnumDao getEnumDao();
    public abstract EnumStateDao getEnumStateDao();

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE state ADD COLUMN sync INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE state ADD COLUMN min REAL");
            database.execSQL("ALTER TABLE state ADD COLUMN max REAL");
        }
    };
    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE enum ADD COLUMN color STRING");
            database.execSQL("ALTER TABLE enum ADD COLUMN icon STRING");
        }
    };
}