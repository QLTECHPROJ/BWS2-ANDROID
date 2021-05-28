package com.brainwellnessspa.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.io.Serializable;

public class DownloadAudioDetailsUniq  implements Serializable {
    private String ID;
    private String UserId;
    private String Name;
    private String AudioFile;
    private String AudioDirection;
    private String Audiomastercat;
    private String AudioSubCategory;
    private String ImageFile;
    private String AudioDuration;
    private String PlaylistId;
    private String IsSingle;
    private String IsDownload;
    private int DownloadProgress;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAudioFile() {
        return AudioFile;
    }

    public void setAudioFile(String audioFile) {
        AudioFile = audioFile;
    }

    public String getAudioDirection() {
        return AudioDirection;
    }

    public void setAudioDirection(String audioDirection) {
        AudioDirection = audioDirection;
    }

    public String getAudiomastercat() {
        return Audiomastercat;
    }

    public void setAudiomastercat(String audiomastercat) {
        Audiomastercat = audiomastercat;
    }

    public String getAudioSubCategory() {
        return AudioSubCategory;
    }

    public void setAudioSubCategory(String audioSubCategory) {
        AudioSubCategory = audioSubCategory;
    }

    public String getImageFile() {
        return ImageFile;
    }

    public void setImageFile(String imageFile) {
        ImageFile = imageFile;
    }

    public String getAudioDuration() {
        return AudioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        AudioDuration = audioDuration;
    }

    public String getPlaylistId() {
        return PlaylistId;
    }

    public void setPlaylistId(String playlistId) {
        PlaylistId = playlistId;
    }

    public String getIsSingle() {
        return IsSingle;
    }

    public void setIsSingle(String isSingle) {
        IsSingle = isSingle;
    }

    public String getIsDownload() {
        return IsDownload;
    }

    public void setIsDownload(String isDownload) {
        IsDownload = isDownload;
    }

    public int getDownloadProgress() {
        return DownloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        DownloadProgress = downloadProgress;
    }
}