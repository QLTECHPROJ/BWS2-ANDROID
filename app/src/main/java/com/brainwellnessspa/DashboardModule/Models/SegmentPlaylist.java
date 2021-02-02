package com.brainwellnessspa.DashboardModule.Models;

public class SegmentPlaylist {
    String playlistId,playlistName,playlistType,playlistDuration,audioCount;

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }

    public String getPlaylistDuration() {
        return playlistDuration;
    }

    public void setPlaylistDuration(String playlistDuration) {
        this.playlistDuration = playlistDuration;
    }

    public String getAudioCount() {
        return audioCount;
    }

    public void setAudioCount(String audioCount) {
        this.audioCount = audioCount;
    }
}
