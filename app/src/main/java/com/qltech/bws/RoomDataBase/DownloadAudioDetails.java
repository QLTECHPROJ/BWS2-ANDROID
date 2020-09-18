package com.qltech.bws.RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "item_table")
public class DownloadAudioDetails implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "ID")
    private String ID;

    @ColumnInfo(name = "Name")
    private String name;

    @ColumnInfo(name = "AudioFile")
    private String audioFile;

    @ColumnInfo(name = "AudioDirection")
    private String audioDirection;

    @ColumnInfo(name = "Audiomastercat")
    private String audiomastercat;

    @ColumnInfo(name = "AudioSubCategory")
    private String audioSubCategory;

    @ColumnInfo(name = "ImageFile")
    private String imageFile;

    @ColumnInfo(name = "Like")
    private String like;

    @ColumnInfo(name = "Download")
    private String download;

    @ColumnInfo(name = "AudioDuration")
    private String audioDuration;

    @ColumnInfo(name = "EncodedBytes")
    private byte[] encodedBytes;

    @ColumnInfo(name = "dirPath")
    private String dirPath;

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

    public byte[] getEncodedBytes() {
        return encodedBytes;
    }

    public void setEncodedBytes(byte[] encodedBytes) {
        this.encodedBytes = encodedBytes;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }
}
