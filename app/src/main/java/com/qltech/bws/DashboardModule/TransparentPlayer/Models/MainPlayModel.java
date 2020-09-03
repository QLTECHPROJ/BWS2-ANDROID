package com.qltech.bws.DashboardModule.TransparentPlayer.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;

import java.util.ArrayList;

public class MainPlayModel {
    public static class ResponseData implements Parcelable{
        @SerializedName("HomeID")
        @Expose
        private String homeID;
        @SerializedName("View")
        @Expose
        private String view;
        @SerializedName("Type")
        @Expose
        private String type;
        @SerializedName("UserID")
        @Expose
        private String userID;
        @SerializedName("Details")
        @Expose
        private ArrayList<ResponseData.Detail> details = null;


        public String getHomeID() {
            return homeID;
        }

        public void setHomeID(String homeID) {
            this.homeID = homeID;
        }

        public String getView() {
            return view;
        }

        public void setView(String view) {
            this.view = view;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public ArrayList<ResponseData.Detail> getDetails() {
            return details;
        }

        public void setDetails(ArrayList<ResponseData.Detail> details) {
            this.details = details;
        }


        public static class Detail implements Parcelable {
            @SerializedName("ID")
            @Expose
            private String iD;
            @SerializedName("Name")
            @Expose
            private String name;
            @SerializedName("AudioFile")
            @Expose
            private String audioFile;
            @SerializedName("AudioDirection")
            @Expose
            private String audioDirection;
            @SerializedName("Audiomastercat")
            @Expose
            private String audiomastercat;
            @SerializedName("AudioSubCategory")
            @Expose
            private String audioSubCategory;
            @SerializedName("ImageFile")
            @Expose
            private String imageFile;
            @SerializedName("Like")
            @Expose
            private String like;
            @SerializedName("Download")
            @Expose
            private String download;
            @SerializedName("AudioDuration")
            @Expose
            private String audioDuration;

            protected Detail(Parcel in) {
                iD = in.readString();
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

            public static final Creator<ResponseData.Detail> CREATOR = new Creator<ResponseData.Detail>() {
                @Override
                public ResponseData.Detail createFromParcel(Parcel in) {
                    return new ResponseData.Detail(in);
                }

                @Override
                public ResponseData.Detail[] newArray(int size) {
                    return new ResponseData.Detail[size];
                }
            };

            public String getID() {
                return iD;
            }

            public void setID(String iD) {
                this.iD = iD;
            }

            public String getiD() {
                return iD;
            }

            public void setiD(String iD) {
                this.iD = iD;
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
                parcel.writeString(iD);
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
    }
}
