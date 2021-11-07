package com.newproject.tamagotchi_foad;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Player.class, PetData.class, Pet.class}, version = 2, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static String DATABASE_NAME = "tempName";
    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract PlayerDAO playerDAO();
    public abstract PetDataDAO petDataDAO();
    public abstract PetDAO petDAO();
}
