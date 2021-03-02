package com.example.cu1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FotoBBDD.class}, version = 1, exportSchema = false)
public abstract class FotoBBDDRoom extends RoomDatabase {
    public abstract FotoDao fotoDao();

    private static volatile FotoBBDDRoom INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static FotoBBDDRoom getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (FotoBBDDRoom.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FotoBBDDRoom.class, "foto_database").build();
                }
            }
        }
        return  INSTANCE;
    }
}
