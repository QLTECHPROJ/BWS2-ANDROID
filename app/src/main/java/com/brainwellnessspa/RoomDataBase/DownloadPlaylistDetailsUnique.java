package com.brainwellnessspa.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.io.Serializable;

public class DownloadPlaylistDetailsUnique implements Serializable {
    private String playlistID;
    private String playlistName;
    private String playlistDesc;
    private String IsReminder;
    private String playlistMastercat;
    private String playlistSubcat;
    private String playlistImage;
    private String PlaylistImageDetails;
    private String totalAudio;
    private String totalDuration;
    private String totalhour;
    private String totalminute;
    private String created;
    private String download;
    private String like;
    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistDesc() {
        return playlistDesc;
    }

    public void setPlaylistDesc(String playlistDesc) {
        this.playlistDesc = playlistDesc;
    }

    public String getIsReminder() {
        return IsReminder;
    }

    public void setIsReminder(String isReminder) {
        IsReminder = isReminder;
    }

    public String getPlaylistMastercat() {
        return playlistMastercat;
    }

    public void setPlaylistMastercat(String playlistMastercat) {
        this.playlistMastercat = playlistMastercat;
    }

    public String getPlaylistSubcat() {
        return playlistSubcat;
    }

    public void setPlaylistSubcat(String playlistSubcat) {
        this.playlistSubcat = playlistSubcat;
    }

    public String getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(String playlistImage) {
        this.playlistImage = playlistImage;
    }

    public String getPlaylistImageDetails() {
        return PlaylistImageDetails;
    }

    public void setPlaylistImageDetails(String playlistImageDetails) {
        PlaylistImageDetails = playlistImageDetails;
    }

    public String getTotalAudio() {
        return totalAudio;
    }

    public void setTotalAudio(String totalAudio) {
        this.totalAudio = totalAudio;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getTotalhour() {
        return totalhour;
    }

    public void setTotalhour(String totalhour) {
        this.totalhour = totalhour;
    }

    public String getTotalminute() {
        return totalminute;
    }

    public void setTotalminute(String totalminute) {
        this.totalminute = totalminute;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }
}