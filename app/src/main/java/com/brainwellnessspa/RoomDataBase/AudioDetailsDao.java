package com.brainwellnessspa.RoomDataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AudioDetailsDao {

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetails>> geAllData1(String PlaylistId);

    @Query("SELECT DISTINCT(ID),ID,Name,AudioFile,AudioDirection,Audiomastercat,AudioSubCategory,ImageFile,`Like`,Download,AudioDuration,PlaylistId,IsSingle,IsDownload,DownloadProgress FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetailsUniq>> geAllDataz(String PlaylistId);

//    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")// ORDER BY uid ASC
//    List<DownloadAudioDetails> geAllData(String PlaylistId);

    @Query("SELECT * FROM audio_table  ORDER BY uid DESC")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetails>> geAllData12();

//    @Query("SELECT * FROM audio_table  ORDER BY uid DESC")// ORDER BY uid ASC
//    List<DownloadAudioDetails> geAllData1();

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload")
// ORDER BY uid ASC
    List<String> geAllDataBYDownloaded(String IsDownload);

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload")
// ORDER BY uid ASC
        LiveData<List<String>> geAllDataBYDownloaded1(String IsDownload);

    @Query("SELECT DISTINCT Name FROM audio_table")
// ORDER BY uid ASC
    List<String> geAllDataBYDownloaded1();

    @Insert
    void insertMedia(DownloadAudioDetails downloadAudioDetails);
//    void insertMedia(DownloadAudioDetails downloadAudioDetails);  download

    @Insert
    void insertPlaylist(DownloadPlaylistDetails downloadPlaylistDetails);

    @Query("DELETE FROM audio_table WHERE AudioFile =:AudioFile And PlaylistId =:PlaylistId")
    void deleteByAudioFile(String AudioFile, String PlaylistId);

    @Query("DELETE FROM audio_table WHERE PlaylistId = :PlaylistId")
    void deleteByPlaylistId(String PlaylistId);

    @Query("DELETE FROM playlist_table WHERE PlaylistId = :PlaylistId")
    void deletePlaylist(String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
    LiveData<List<DownloadAudioDetails>> getLastIdByuId1(String AudioFile);

//    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
//    List<DownloadAudioDetails> getLastIdByuId(String AudioFile);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId")
    LiveData<List<DownloadAudioDetails>> getaudioByPlaylist1(String AudioFile, String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId")
    List<DownloadAudioDetails> getaudioByPlaylist(String AudioFile, String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
    LiveData<List<DownloadAudioDetails>> getAllAudioByPlaylist1(String PlaylistId);

//    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
//    List<DownloadAudioDetails> getAllAudioByPlaylist(String PlaylistId);

    @Query("SELECT * FROM playlist_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")
    LiveData<List<DownloadPlaylistDetails>> getPlaylist1(String PlaylistId);

//    @Query("SELECT * FROM playlist_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")
//    List<DownloadPlaylistDetails> getPlaylist(String PlaylistId);

    @Query("SELECT * FROM playlist_table ORDER BY uid DESC")
    LiveData<List<DownloadPlaylistDetails>> getAllPlaylist1();

//    @Query("SELECT * FROM playlist_table ORDER BY uid DESC")
//    List<DownloadPlaylistDetails> getAllPlaylist();

    @Query("UPDATE audio_table SET IsDownload =:IsDownload WHERE Name =:Name and PlaylistId =:PlaylistId")
    void updateMediaByDownload(String IsDownload, String PlaylistId, String Name);

    @Query("UPDATE audio_table SET IsDownload =:IsDownload,DownloadProgress =:DownloadProgress WHERE Name =:Name and PlaylistId =:PlaylistId")
    void updateMediaByDownloadProgress(String IsDownload, int DownloadProgress, String PlaylistId, String Name);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId and IsDownload =:IsDownload")
    LiveData<List<DownloadPlaylistDetails>>  getCountDownloadProgress1(String IsDownload, String PlaylistId);

    @Query("SELECT COUNT(Name) FROM audio_table WHERE PlaylistId =:PlaylistId and IsDownload =:IsDownload")
    int getCountDownloadProgress(String IsDownload,String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId and AudioFile =:AudioFile")
        LiveData<List<DownloadAudioDetails>> getDownloadProgress1(String AudioFile, String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE DownloadProgress <=:DownloadProgress")
        LiveData<List<DownloadAudioDetails>> getDownloadProgressRemain(int DownloadProgress);

    @Query("SELECT DownloadProgress FROM audio_table WHERE PlaylistId =:PlaylistId and AudioFile =:AudioFile")
    int getDownloadProgress(String AudioFile, String PlaylistId);

//    @Query("SELECT COUNT(DISTINCT ProductID) FROM item_table")
//    int getunique();

    @Query("DELETE FROM audio_table")
    void deleteAll();

    @Query("DELETE FROM playlist_table")
    void deleteAllPlalist();

}
