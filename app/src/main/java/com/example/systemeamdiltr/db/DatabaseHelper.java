package com.example.systemeamdiltr.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.systemeamdiltr.dao.TemperatureDao;
import com.example.systemeamdiltr.entities.Temperature;
import com.example.systemeamdiltr.utils.Converters;

@Database(entities = {Temperature.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DatabaseHelper extends RoomDatabase {
    private static final String DB_NAME="gx.db" ;
    private static DatabaseHelper instance;
    public abstract TemperatureDao tempDao();

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance =  Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseHelper.class, DB_NAME).build();

        }
        return instance;
    }

}
