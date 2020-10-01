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

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId")// ORDER BY uid ASC
    List<DownloadAudioDetails> geAllData(String PlaylistId);

    @Query("SELECT * FROM audio_table")// ORDER BY uid ASC
    List<DownloadAudioDetails> geAllData1();

    @Insert
    void insertMedia(DownloadAudioDetails downloadAudioDetails);

    @Insert
    void insertPlaylist(DownloadPlaylistDetails downloadPlaylistDetails);

    @Query("DELETE FROM audio_table WHERE AudioFile =:AudioFile And PlaylistId =:PlaylistId")
    void deleteByAudioFile(String AudioFile, String PlaylistId);

    @Query("DELETE FROM audio_table WHERE PlaylistId = :PlaylistId")
    void deleteByPlaylistId(String PlaylistId);

    @Query("DELETE FROM playlist_table WHERE PlaylistId = :PlaylistId")
    void deletePlaylist(String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
    List<DownloadAudioDetails> getLastIdByuId(String AudioFile);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId")
    List<DownloadAudioDetails> getaudioByPlaylist(String AudioFile,String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
    List<DownloadAudioDetails> getAllAudioByPlaylist(String PlaylistId);

    @Query("SELECT * FROM playlist_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
    List<DownloadPlaylistDetails> getPlaylist(String PlaylistId);

    @Query("SELECT * FROM playlist_table ORDER BY uid ASC")
    List<DownloadPlaylistDetails> getAllPlaylist();

    @Query("UPDATE audio_table SET Download =:Download,IsSingle =:IsSingle,PlaylistId =:PlaylistId WHERE ID =:ID")
    void updateMediaByDownload(String Download,String IsSingle,String PlaylistId,String ID );

    @Query("DELETE FROM audio_table")
    void deleteAll();
    @Query("DELETE FROM playlist_table")
    void deleteAllPlalist();

}
