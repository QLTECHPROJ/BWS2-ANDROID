package com.brainwellnessspa.RoomDataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AudioDetailsDao {

    @Query("SELECT DISTINCT(ID),UserID,ID,Name,AudioFile,AudioDirection,Audiomastercat,AudioSubCategory,ImageFile,AudioDuration,PlaylistId,IsSingle,IsDownload,DownloadProgress FROM audio_table WHERE PlaylistId =:PlaylistId And UserID=:UserID ORDER BY uid ASC")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetailsUniq>> geAllDataz(String PlaylistId,String UserID);

//    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")// ORDER BY uid ASC
//    List<DownloadAudioDetails> geAllData(String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE UserID=:UserID ORDER BY uid DESC")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetails>> geAllData12(String UserID);
    @Query("SELECT * FROM audio_table ORDER BY uid DESC")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetails>> geAllData1LiveForAll();

    @Query("SELECT * FROM audio_table ORDER BY uid DESC")
// ORDER BY uid ASC
    List<DownloadAudioDetails> geAllData1ForAll();

//    @Query("SELECT * FROM audio_table  ORDER BY uid DESC")// ORDER BY uid ASC
//    List<DownloadAudioDetails> geAllData1();

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload And UserID=:UserID")
// ORDER BY uid ASC
    List<String> geAllDataBYDownloaded(String IsDownload,String UserID);

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload And UserID=:UserID")
// ORDER BY uid ASC
    LiveData<List<String>> geAllLiveDataBYDownloaded(String IsDownload,String UserID);

   @Query("SELECT * FROM audio_table WHERE IsDownload !=:IsDownload And UserID=:UserID")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetails>> getNotDownloadData(String IsDownload,String UserID);

   @Query("SELECT * FROM audio_table WHERE IsDownload !=:IsDownload And UserID=:UserID and PlaylistId =:PlaylistId")
// ORDER BY uid ASC
    LiveData<List<DownloadAudioDetails>> getNotDownloadPlayListData(String IsDownload,String UserID, String PlaylistId);

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload And UserID=:UserID")
// ORDER BY uid ASC
    LiveData<List<String>> geAllDataBYDownloaded1(String IsDownload,String UserID);

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload")
// ORDER BY uid ASC
    LiveData<List<String>> geAllDataBYDownloadedForAll(String IsDownload);

    @Insert
    void insertMedia(DownloadAudioDetails downloadAudioDetails);

    @Insert
    void insertPlaylist(DownloadPlaylistDetails downloadPlaylistDetails);

    @Query("DELETE FROM audio_table WHERE AudioFile =:AudioFile And PlaylistId =:PlaylistId And UserID=:UserID")
    void deleteByAudioFile(String AudioFile, String PlaylistId,String UserID);

    @Query("DELETE FROM audio_table WHERE PlaylistId = :PlaylistId And UserID=:UserID")
    void deleteByPlaylistId(String PlaylistId,String UserID);

    @Query("DELETE FROM playlist_table WHERE PlaylistId = :PlaylistId And UserID=:UserID")
    void deletePlaylist(String PlaylistId,String UserID);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile And UserID=:UserID")
    LiveData<List<DownloadAudioDetails>> getLastIdByuId1(String AudioFile,String UserID);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
    LiveData<List<DownloadAudioDetails>> getLastIdByuIdForAll(String AudioFile);

//    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
//    List<DownloadAudioDetails> getLastIdByuId(String AudioFile);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId And UserID=:UserID")
    LiveData<List<DownloadAudioDetails>> getaudioByPlaylist1(String AudioFile, String PlaylistId,String UserID);

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId And UserID=:UserID")
    List<DownloadAudioDetails> getaudioByPlaylist(String AudioFile, String PlaylistId,String UserID);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId And UserID=:UserID ORDER BY uid ASC")
    LiveData<List<DownloadAudioDetails>> getAllAudioByPlaylist1(String PlaylistId,String UserID);

//    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
//    List<DownloadAudioDetails> getAllAudioByPlaylist(String PlaylistId);

    @Query("SELECT * FROM playlist_table WHERE PlaylistId =:PlaylistId And UserID=:UserID ORDER BY uid DESC")
    LiveData<List<DownloadPlaylistDetails>> getPlaylist1(String PlaylistId,String UserID);

//    @Query("SELECT * FROM playlist_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")
//    List<DownloadPlaylistDetails> getPlaylist(String PlaylistId);

    @Query("SELECT DISTINCT (PlaylistID),PlaylistID,PlaylistName,PlaylistDesc,IsReminder,PlaylistMastercat,PlaylistSubcat,PlaylistImage,PlaylistImageDetails,TotalAudio,TotalDuration,Totalhour,Totalminute,Created FROM playlist_table WHERE UserID=:UserID ORDER BY uid DESC")
    LiveData<List<DownloadPlaylistDetailsUnique>> getAllPlaylist1(String UserID);

//    @Query("SELECT * FROM playlist_table ORDER BY uid DESC")
//    List<DownloadPlaylistDetails> getAllPlaylist();

    @Query("UPDATE audio_table SET IsDownload =:IsDownload WHERE Name =:Name and PlaylistId =:PlaylistId And UserID=:UserID")
    void updateMediaByDownload(String IsDownload, String PlaylistId, String Name,String UserID);

    @Query("UPDATE audio_table SET IsDownload =:IsDownload,DownloadProgress =:DownloadProgress WHERE Name =:Name and PlaylistId =:PlaylistId And UserID=:UserID")
    void updateMediaByDownloadProgress(String IsDownload, int DownloadProgress, String PlaylistId, String Name,String UserID);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId and IsDownload =:IsDownload And UserID =:UserID")
    LiveData<List<DownloadPlaylistDetails>> getCountDownloadProgress1(String IsDownload, String PlaylistId,String UserID);

    @Query("SELECT COUNT(Name) FROM audio_table WHERE PlaylistId =:PlaylistId and IsDownload =:IsDownload")
    int getCountDownloadProgress(String IsDownload, String PlaylistId);

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId and AudioFile =:AudioFile And UserID=:UserID")
    LiveData<List<DownloadAudioDetails>> getDownloadProgress1(String AudioFile, String PlaylistId,String UserID);

    @Query("SELECT * FROM audio_table WHERE DownloadProgress <=:DownloadProgress And UserID=:UserID")
    LiveData<List<DownloadAudioDetails>> getDownloadProgressRemain(int DownloadProgress,String UserID);

    @Query("SELECT DownloadProgress FROM audio_table WHERE PlaylistId =:PlaylistId and AudioFile =:AudioFile And UserID=:UserID")
    int getDownloadProgress(String AudioFile, String PlaylistId,String UserID);

//    @Query("SELECT COUNT(DISTINCT ProductID) FROM item_table")
//    int getunique();
/*
    @Query("DELETE FROM audio_table")
    void deleteAll();

    @Query("DELETE FROM playlist_table")
    void deleteAllPlalist();*/

}
