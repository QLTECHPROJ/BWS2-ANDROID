package com.brainwellnessspa.RoomDataBase;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;

public class DatabaseClient {
    private Context Ctx;
    private static DatabaseClient Instance;

    //our app database object
    private AudioDatabase cart;

    private DatabaseClient(Context Ctx) {
        this.Ctx = Ctx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        cart = Room.databaseBuilder(Ctx,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context Ctx) {
        if (Instance == null) {
            Instance = new DatabaseClient(Ctx);
        }
        return Instance;
    }

    public AudioDatabase getaudioDatabase() {
        return cart;
    }
}
