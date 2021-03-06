package com.brainwellnessspa.roomDataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DownloadAudioDetails.class, DownloadPlaylistDetails.class}, version = 3, exportSchema = false)
public abstract class AudioDatabase extends RoomDatabase {

    private static volatile AudioDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final int NUMBER_OF_THREADS1 = 1;
    public static final ExecutorService databaseWriteExecutor1 = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract AudioDetailsDao taskDao();
}
