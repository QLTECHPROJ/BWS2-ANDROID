package com.qltech.bws.DashboardModule.TransparentPlayer.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class MainPlayModel implements Parcelable {
        private String ID;
        private String name;
        private String audioFile;
        private String audioDirection;
        private String audiomastercat;
        private String audioSubCategory;
        private String imageFile;
        private String like;
        private String download;
        private String audioDuration;

    public MainPlayModel() {
    }
    public MainPlayModel(Parcel in) {
        ID = in.readString();
        name = in.readString();
        audioFile = in.readString();
        audioDirection = in.readString();
        audiomastercat = in.readString();
        audioSubCategory = in.readString();
        imageFile = in.readString();
        like = in.readString();
        download = in.readString();
        audioDuration = in.readString();
    }

    public static final Creator<MainPlayModel> CREATOR = new Creator<MainPlayModel>() {
        @Override
        public MainPlayModel createFromParcel(Parcel in) {
            return new MainPlayModel(in);
        }

        @Override
        public MainPlayModel[] newArray(int size) {
            return new MainPlayModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(name);
        parcel.writeString(audioFile);
        parcel.writeString(audioDirection);
        parcel.writeString(audiomastercat);
        parcel.writeString(audioSubCategory);
        parcel.writeString(imageFile);
        parcel.writeString(like);
        parcel.writeString(download);
        parcel.writeString(audioDuration);
    }
}
