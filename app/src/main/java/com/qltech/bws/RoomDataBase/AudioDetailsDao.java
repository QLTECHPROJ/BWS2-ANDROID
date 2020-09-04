package com.qltech.bws.RoomDataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface AudioDetailsDao {

    @Query("SELECT * FROM item_table  ORDER BY uid ASC")
    List<DownloadAudioDetails> getbyUIDAll();

}
