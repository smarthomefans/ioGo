package de.nisnagel.iogo.data.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;

@Database(entities = { Enum.class, EnumState.class, State.class}, version = 9, exportSchema = false)
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
            database.execSQL("ALTER TABLE enum ADD COLUMN color TEXT");
            database.execSQL("ALTER TABLE enum ADD COLUMN icon TEXT");
        }
    };
    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE state ADD COLUMN history INTEGER NOT NULL DEFAULT 0");
        }
    };
    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE enum RENAME TO enum_old");
            database.execSQL("DROP INDEX index_enum_id");
            database.execSQL("CREATE TABLE enum (" +
                    "id TEXT NOT NULL PRIMARY KEY, " +
                    "name TEXT, " +
                    "type TEXT, " +
                    "color TEXT, " +
                    "icon TEXT, " +
                    "favorite INTEGER NOT NULL DEFAULT 0, " +
                    "rank INTEGER DEFAULT 0, " +
                    "hidden INTEGER NOT NULL DEFAULT 0 " +
                    ")");
            database.execSQL("CREATE UNIQUE INDEX index_enum_id ON enum(id)");
            database.execSQL("INSERT INTO enum(id,name,type,color,icon,favorite,rank,hidden) SELECT id,name,type,color,icon,CASE favorite WHeN 'true' then 1 else 0 END,0,0 FROM enum_old");
            database.execSQL("DROP TABLE enum_old");
        }
    };
}