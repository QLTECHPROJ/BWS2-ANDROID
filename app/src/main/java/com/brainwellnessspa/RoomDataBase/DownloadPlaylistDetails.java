package com.brainwellnessspa.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "playlist_table")
public class DownloadPlaylistDetails implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "PlaylistID")
    private String playlistID;

    @ColumnInfo(name = "PlaylistName")
    private String playlistName;

    @ColumnInfo(name = "PlaylistDesc")
    private String playlistDesc;

    @ColumnInfo(name = "IsReminder")
    private String IsReminder;

    @ColumnInfo(name = "PlaylistMastercat")
    private String playlistMastercat;

    @ColumnInfo(name = "PlaylistSubcat")
    private String playlistSubcat;

    @ColumnInfo(name = "PlaylistImage")
    private String playlistImage;

    @ColumnInfo(name = "PlaylistImageDetails")
    private String PlaylistImageDetails;

    @ColumnInfo(name = "TotalAudio")
    private String totalAudio;

    @ColumnInfo(name = "TotalDuration")
    private String totalDuration;

    @ColumnInfo(name = "Totalhour")
    private String totalhour;

    @ColumnInfo(name = "Totalminute")
    private String totalminute;

    @ColumnInfo(name = "Created")
    private String created;

    @ColumnInfo(name = "Download")
    private String download;

    @ColumnInfo(name = "Like")
    private String like;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

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
