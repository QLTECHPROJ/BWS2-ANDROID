package com.qltech.bws.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "audio_table")
public class DownloadAudioDetails implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "ID")
    private String ID;

    @ColumnInfo(name = "Name")
    private String Name;

    @ColumnInfo(name = "AudioFile")
    private String AudioFile;

    @ColumnInfo(name = "AudioDirection")
    private String AudioDirection;

    @ColumnInfo(name = "Audiomastercat")
    private String Audiomastercat;

    @ColumnInfo(name = "AudioSubCategory")
    private String AudioSubCategory;

    @ColumnInfo(name = "ImageFile")
    private String ImageFile;

    @ColumnInfo(name = "Like")
    private String Like;

    @ColumnInfo(name = "Download")
    private String Download;

    @ColumnInfo(name = "AudioDuration")
    private String AudioDuration;

    @ColumnInfo(name = "EncodedBytes")
    private byte[] EncodedBytes;

    @ColumnInfo(name = "dirPath")
    private String dirPath;

    @ColumnInfo(name = "PlaylistId")
    private String PlaylistId;

    @ColumnInfo(name = "IsSingle")
    private String IsSingle;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getLike() {
        return Like;
    }

    public void setLike(String like) {
        Like = like;
    }

    public String getDownload() {
        return Download;
    }

    public void setDownload(String download) {
        Download = download;
    }

    public String getAudioDuration() {
        return AudioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        AudioDuration = audioDuration;
    }

    public byte[] getEncodedBytes() {
        return EncodedBytes;
    }

    public void setEncodedBytes(byte[] encodedBytes) {
        EncodedBytes = encodedBytes;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
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
}
