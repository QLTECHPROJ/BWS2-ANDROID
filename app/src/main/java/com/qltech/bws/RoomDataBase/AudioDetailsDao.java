package com.qltech.bws.RoomDataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AudioDetailsDao {

    @Query("SELECT * FROM item_table")// ORDER BY uid ASC
    List<DownloadAudioDetails> geAllData();

    @Insert
    void insertMedia(DownloadAudioDetails downloadAudioDetails);

    @Query("DELETE FROM item_table WHERE AudioFile = :AudioFile")
    void deleteByAudioFile(String AudioFile);

    @Query("SELECT * FROM item_table WHERE ID =:ID ORDER BY uid DESC LIMIT 1")
    List<DownloadAudioDetails> getLastIdByuId(String ID);

    @Query("UPDATE item_table SET Download =:Download,IsSingle =:IsSingle,PlaylistId =:PlaylistId WHERE ID =:ID")
    void updateMediaByDownload(String Download,String IsSingle,String PlaylistId,String ID );
}
