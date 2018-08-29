package de.nisnagel.iogo.data.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {StateHistory.class}, version = 1, exportSchema = false)
@TypeConverters({ListConverters.class})
public abstract class HistoryDatabase extends RoomDatabase {

    public abstract StateHistoryDao getStateHistoryDao();

}