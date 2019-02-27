/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.data.model;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;

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