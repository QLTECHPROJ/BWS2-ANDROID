package com.brainwellnessspa.roomDataBase;

import java.io.Serializable;

public class DownloadPlaylistDetailsUnique implements Serializable {
    private String UserId;
    private String PlaylistID;
    private String PlaylistName;
    private String PlaylistDesc;
    private String IsReminder;
    private String PlaylistMastercat;
    private String PlaylistSubcat;
    private String PlaylistImage;
    private String PlaylistImageDetails;
    private String TotalAudio;
    private String TotalDuration;
    private String Totalhour;
    private String Totalminute;
    private String Created;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPlaylistID() {
        return PlaylistID;
    }

    public void setPlaylistID(String playlistID) {
        PlaylistID = playlistID;
    }

    public String getPlaylistName() {
        return PlaylistName;
    }

    public void setPlaylistName(String playlistName) {
        PlaylistName = playlistName;
    }

    public String getPlaylistDesc() {
        return PlaylistDesc;
    }

    public void setPlaylistDesc(String playlistDesc) {
        PlaylistDesc = playlistDesc;
    }

    public String getIsReminder() {
        return IsReminder;
    }

    public void setIsReminder(String isReminder) {
        IsReminder = isReminder;
    }

    public String getPlaylistMastercat() {
        return PlaylistMastercat;
    }

    public void setPlaylistMastercat(String playlistMastercat) {
        PlaylistMastercat = playlistMastercat;
    }

    public String getPlaylistSubcat() {
        return PlaylistSubcat;
    }

    public void setPlaylistSubcat(String playlistSubcat) {
        PlaylistSubcat = playlistSubcat;
    }

    public String getPlaylistImage() {
        return PlaylistImage;
    }

    public void setPlaylistImage(String playlistImage) {
        PlaylistImage = playlistImage;
    }

    public String getPlaylistImageDetails() {
        return PlaylistImageDetails;
    }

    public void setPlaylistImageDetails(String playlistImageDetails) {
        PlaylistImageDetails = playlistImageDetails;
    }

    public String getTotalAudio() {
        return TotalAudio;
    }

    public void setTotalAudio(String totalAudio) {
        TotalAudio = totalAudio;
    }

    public String getTotalDuration() {
        return TotalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        TotalDuration = totalDuration;
    }

    public String getTotalhour() {
        return Totalhour;
    }

    public void setTotalhour(String totalhour) {
        Totalhour = totalhour;
    }

    public String getTotalminute() {
        return Totalminute;
    }

    public void setTotalminute(String totalminute) {
        Totalminute = totalminute;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }
}