package com.brainwellnessspa.roomDataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AudioDetailsDao {
    @Query("SELECT DISTINCT(ID),UserID,ID,Name,AudioFile,AudioDirection,Audiomastercat,AudioSubCategory,ImageFile,AudioDuration,PlaylistId,IsSingle,IsDownload,DownloadProgress FROM audio_table WHERE PlaylistId =:PlaylistId And UserID=:UserID ORDER BY uid ASC")
    fun geAllDataz(PlaylistId: String?, UserID: String?): LiveData<List<DownloadAudioDetailsUniq>>

    //    @Query("SELECT * FROM playlist_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")
    //    List<DownloadPlaylistDetails> getPlaylist(String PlaylistId);
    @Query("SELECT DISTINCT (PlaylistID),UserID,PlaylistID,PlaylistName,PlaylistDesc,IsReminder,PlaylistMastercat,PlaylistSubcat,PlaylistImage,PlaylistImageDetails,TotalAudio,TotalDuration,Totalhour,Totalminute,Created FROM playlist_table WHERE UserID=:UserID ORDER BY uid DESC")
    fun getAllPlaylist1(UserID: String?): LiveData<List<DownloadPlaylistDetailsUnique>>

    //    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid DESC")// ORDER BY uid ASC
    //    List<DownloadAudioDetails> geAllData(String PlaylistId);
    @Query("SELECT * FROM audio_table WHERE UserID=:UserID ORDER BY uid DESC")
    fun geAllData12(UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT * FROM audio_table ORDER BY uid DESC")
    fun geAllData1LiveForAll(): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT * FROM audio_table ORDER BY uid DESC")
    fun geAllData1ForAll(): List<DownloadAudioDetails?>

    @Query("SELECT * FROM audio_table ORDER BY uid DESC")
    fun geAllData1ForAllLive(): LiveData<List<DownloadAudioDetails>>
    //    @Query("SELECT * FROM audio_table  ORDER BY uid DESC")// ORDER BY uid ASC
    //    List<DownloadAudioDetails> geAllData1();
    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload And UserID=:UserID")
    fun geAllDataBYDownloaded(IsDownload: String?, UserID: String?): List<String?>

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload And UserID=:UserID")
    fun geAllLiveDataBYDownloaded(IsDownload: String?, UserID: String?): LiveData<List<String?>>

    @Query("SELECT * FROM audio_table WHERE IsDownload !=:IsDownload And UserID=:UserID")
    fun getNotDownloadData(IsDownload: String?, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT * FROM audio_table WHERE IsDownload !=:IsDownload And UserID=:UserID and PlaylistId =:PlaylistId")
    fun getNotDownloadPlayListData(IsDownload: String?, UserID: String?, PlaylistId: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload And UserID=:UserID")
    fun geAllDataBYDownloaded1(IsDownload: String?, UserID: String?): LiveData<List<String?>>

    @Query("SELECT DISTINCT Name FROM audio_table WHERE IsDownload =:IsDownload")
    fun geAllDataBYDownloadedForAll(IsDownload: String?): LiveData<List<String?>>

    @Insert
    fun insertMedia(downloadAudioDetails: DownloadAudioDetails?)

    @Insert
    fun insertPlaylist(downloadPlaylistDetails: DownloadPlaylistDetails?)

    @Query("DELETE FROM audio_table WHERE AudioFile =:AudioFile And PlaylistId =:PlaylistId And UserID=:UserID")
    fun deleteByAudioFile(AudioFile: String?, PlaylistId: String?, UserID: String?)

    @Query("DELETE FROM audio_table WHERE PlaylistId = :PlaylistId And UserID=:UserID")
    fun deleteByPlaylistId(PlaylistId: String?, UserID: String?)

    @Query("DELETE FROM playlist_table WHERE PlaylistID = :PlaylistId And UserID=:UserID")
    fun deletePlaylist(PlaylistId: String?, UserID: String?)

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile And UserID=:UserID")
    fun getLastIdByuId1(AudioFile: String?, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
    fun getLastIdByuIdForAll(AudioFile: String?): LiveData<List<DownloadAudioDetails?>>

    //    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile")
    //    List<DownloadAudioDetails> getLastIdByuId(String AudioFile);
    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId And UserID=:UserID")
    fun getaudioByPlaylist1(AudioFile: String?, PlaylistId: String?, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT * FROM audio_table WHERE AudioFile =:AudioFile and PlaylistId =:PlaylistId And UserID=:UserID")
    fun getaudioByPlaylist(AudioFile: String?, PlaylistId: String?, UserID: String?): List<DownloadAudioDetails?>

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId And UserID=:UserID ORDER BY uid ASC")
    fun getAllAudioByPlaylist1(PlaylistId: String?, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    //    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId ORDER BY uid ASC")
    //    List<DownloadAudioDetails> getAllAudioByPlaylist(String PlaylistId);
    @Query("SELECT * FROM playlist_table WHERE PlaylistID =:PlaylistId And UserID=:UserID ORDER BY uid DESC")
    fun getPlaylist1(PlaylistId: String?, UserID: String?): LiveData<List<DownloadPlaylistDetails?>>

    @Query("SELECT PlaylistID FROM playlist_table WHERE Created =:Created And UserID=:UserID ORDER BY uid DESC")
    fun getPlaylistIDByCreated(Created: String?, UserID: String?): String


    //    @Query("SELECT * FROM playlist_table ORDER BY uid DESC")
    //    List<DownloadPlaylistDetails> getAllPlaylist();
    @Query("UPDATE audio_table SET IsDownload =:IsDownload WHERE Name =:Name and PlaylistId =:PlaylistId And UserID=:UserID")
    fun updateMediaByDownload(IsDownload: String?, PlaylistId: String?, Name: String?, UserID: String?)

    @Query("UPDATE audio_table SET IsDownload =:IsDownload,DownloadProgress =:DownloadProgress WHERE Name =:Name and PlaylistId =:PlaylistId And UserID=:UserID")
    fun updateMediaByDownloadProgress(IsDownload: String?, DownloadProgress: Int, PlaylistId: String?, Name: String?, UserID: String?)

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId and IsDownload =:IsDownload And UserID =:UserID")
    fun getCountDownloadProgress1(IsDownload: String?, PlaylistId: String?, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT COUNT(Name) FROM audio_table WHERE PlaylistId =:PlaylistId and IsDownload =:IsDownload")
    fun getCountDownloadProgress(IsDownload: String?, PlaylistId: String?): Int

    @Query("SELECT * FROM audio_table WHERE PlaylistId =:PlaylistId and AudioFile =:AudioFile And UserID=:UserID")
    fun getDownloadProgress1(AudioFile: String?, PlaylistId: String?, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT * FROM audio_table WHERE DownloadProgress <=:DownloadProgress And UserID=:UserID")
    fun getDownloadProgressRemain(DownloadProgress: Int, UserID: String?): LiveData<List<DownloadAudioDetails?>>

    @Query("SELECT DownloadProgress FROM audio_table WHERE PlaylistId =:PlaylistId and AudioFile =:AudioFile And UserID=:UserID")
    fun getDownloadProgress(AudioFile: String?, PlaylistId: String?, UserID: String?): Int //    @Query("SELECT COUNT(DISTINCT ProductID) FROM item_table")
    //    int getunique();
    /*
    @Query("DELETE FROM audio_table")
    void deleteAll();

    @Query("DELETE FROM playlist_table")
    void deleteAllPlalist();*/
}