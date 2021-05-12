package com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models;

public class MainPlayModel {
    private String ID;
    private String name;
    private String audioFile;
    private String PlaylistID;
    private String audioDirection;
    private String audiomastercat;
    private String audioSubCategory;
    private String imageFile;
    private String like;
    private String download;
    private String audioDuration;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAudioDirection() {
        return audioDirection;
    }

    public void setAudioDirection(String audioDirection) {
        this.audioDirection = audioDirection;
    }

    public String getAudiomastercat() {
        return audiomastercat;
    }

    public void setAudiomastercat(String audiomastercat) {
        this.audiomastercat = audiomastercat;
    }

    public String getAudioSubCategory() {
        return audioSubCategory;
    }

    public void setAudioSubCategory(String audioSubCategory) {
        this.audioSubCategory = audioSubCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getPlaylistID() {
        return PlaylistID;
    }

    public void setPlaylistID(String playlistID) {
        PlaylistID = playlistID;
    }
}
