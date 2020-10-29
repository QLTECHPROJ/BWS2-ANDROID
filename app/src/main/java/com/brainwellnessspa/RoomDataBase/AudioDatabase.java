package com.brainwellnessspa.RoomDataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DownloadAudioDetails.class,DownloadPlaylistDetails.class}, version = 2, exportSchema = false)
public abstract class AudioDatabase extends RoomDatabase {
    public abstract AudioDetailsDao taskDao();
}
